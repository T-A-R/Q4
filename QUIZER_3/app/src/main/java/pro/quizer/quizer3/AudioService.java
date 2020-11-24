package pro.quizer.quizer3;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import androidx.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pro.quizer.quizer3.utils.FileUtils;

import static pro.quizer.quizer3.utils.FileUtils.FOLDER_DIVIDER;

public class AudioService extends MediaBrowserServiceCompat implements Serializable {

    public static final String SOURCE_NONE = "SOURCE_NONE";
    public static final String SOURCE_MIC = "SOURCE_MIC";
    public static final String SOURCE_AUDIO = "SOURCE_AUDIO";
    public static final String VOICE_REC_MIME = "audio/mp4";
    public static final String EXTRA_KEY_AMPL = "METADATA_KEY_AMPL";
    public static final String EXTRA_KEY_AR_FREQ = "EXTRA_KEY_AR_FREQ";
    public static final String EXTRA_KEY_AR_AMPL = "EXTRA_KEY_AR_AMPL";
    public static final String EXTRA_KEY_AR_MAGN = "EXTRA_KEY_AR_MAGN";
    public static final String EXTRA_KEY_AR_DENS = "EXTRA_KEY_AR_DENS";

    private static final String EXT_STORAGE_DIR_NAME = "Voice";
    private static final String VOICE_REC_PREF = "rec-";
    private static final String VOICE_REC_EXT = ".m4a";
    private static final String VSL_JSON_FILE_NAME = "Visualization.json";
    private static final String VSL_XML_FILE_NAME = "Visualization.xml";
    private static final String VSL_PROP_WAVEFORM_NAME = "waveform";
    private static final String VSL_PROP_FFT_NAME = "fft";
    private static final String VSL_PROP_ENTRY_NAME = "entry";
    private static final String LOG_TAG = "bv_log";
    private static final String MEDIA_ROOT_ID = "MEDIA_ROOT_ID";
    private static final int SERVICE_ID = 1;
    private static final int IC_NOTIF_PLAY = R.drawable.ic_notif_play;
    private static final int IC_NOTIF_RECORD = R.drawable.ic_notif_record;
    private static final int IC_NOTIF_PAUSE = R.drawable.ic_notif_pause;
    private static final int IC_NOTIF_STOP = R.drawable.ic_notif_stop;
    private static final int VISUALIZER_CAPTURE_SIZE = 512; // default is max 1024, min 128 (2^n)
    private static final int VISUALIZER_SAMPLE_RATE = 8000;
    private static final int AMPLITUDE_CHK_RATE = 500;
    public static String mFileName = "unknown.m4a";

    public File audioFilesPath;


    private enum ServiceState {
        Ready,
        /*PreparePlaying, PrepareRecording,*/ // redundant
        Playing, PausedPlaying, StoppedPlaying,
        Recording, PausedRecording, StoppedRecording
    }

    private ServiceState servState = ServiceState.Ready;

    private MediaSessionCompat mediaSes;
    private MediaControllerCompat.TransportControls transCntrl;
    private PlaybackStateCompat.Builder stateBuilderImplFacility; // use method instead
    private MediaMetadataCompat.Builder metadataBuilder;

    private boolean isRecorderInitialized = false;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private Visualizer vslr;
    private ArrayList<Byte> alWaveFormVal = new ArrayList<>();
    private ArrayList<Byte> alFftVal = new ArrayList<>();

    private String recFilePath;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private FileFilter audioFileFilter = pathname -> pathname.exists() && pathname.isFile() && pathname.getName().endsWith(VOICE_REC_EXT);

    private SaveVslDataATask saveVslData;
    private HandlerThread hThread = new HandlerThread("AudioServiceHandlerThread");
    private Handler hndlrUI = new Handler(); // to post delayed
    // provides volume/mic control and oth functions
    private AudioManager audioManager;

    private AsyncTask<Void, Double, Void> aTaskAmplitude;

    private final AudioManager.OnAudioFocusChangeListener afchListener = new AudioManager.OnAudioFocusChangeListener() {
        private int curVolume = -1;

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(LOG_TAG, "AudioManager.OnAudioFocusChangeListener.onAudioFocusChange() : "
                    + focusChange + isUiMsg());

            switch (servState) {
                //if recording - do nothing
                case Playing:
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // should pause and if focus was not returned - stop
                        transCntrl.pause();
                        hndlrUI.postDelayed(stopRunnable, TimeUnit.SECONDS.toMillis(30));
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // should pause
                        transCntrl.pause();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // should pause or decrease volume
                        if (audioManager == null) {
                            Log.w(LOG_TAG, "AudioManager.OnAudioFocusChangeListener.onAudioFocusChange() : audioManager == null");
                        } else {
                            curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume / 2, AudioManager.FLAG_SHOW_UI);
                        }
                    }
                    break;
                case PausedPlaying:
                case StoppedPlaying:
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // can resume and restore volume
                        if (audioManager == null) {
                            Log.w(LOG_TAG, "AudioManager.OnAudioFocusChangeListener.onAudioFocusChange() : audioManager == null");
                        } else if (curVolume == -1) { // haven't changed yet
                            curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        } else {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, AudioManager.FLAG_SHOW_UI);
                        }
                        transCntrl.play();
                    }
                    break;
            }
        }
    };

    private final Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            transCntrl.stop();
        }
    };

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    final private BroadcastReceiver bnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "ACTION_AUDIO_BECOMING_NOISY BroadcastReceiver onReceive() : " + intent);
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (servState == ServiceState.Playing) {
                    transCntrl.pause();
                }
            }
        }
    };

    private final MediaSessionCompat.Callback mediaSesCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            Log.d(LOG_TAG, "MediaSession.Callback.onCommand() : " + command + "; " + extras
                    + "; service state == " + servState);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.d(LOG_TAG, "MediaSession.Callback.onPlayFromMediaId() : " + mediaId + "; " + extras
                    + "; service state == " + servState + isUiMsg());

            switch (mediaId) {
                case SOURCE_NONE:
                    switch (servState) {
                        case Playing:
                        case PausedPlaying:
//                            actionStopPlaying();
                            actionSetReady();
                            break;
                        case Recording:
                        case PausedRecording:
                            actionStopRecording();
                            actionSetReady();
                            break;
                        case StoppedPlaying:
                        case StoppedRecording:
                        case Ready:
                        default:
                            actionSetReady();
                            break;
                    }
                    break;
                case SOURCE_AUDIO: // now is redundant, need to review states
                    switch (servState) {
                        case Playing:
                            break;
                        case PausedPlaying:
                        case StoppedPlaying:
                        case StoppedRecording:
                        case Ready:
                        default:
//                            actionPlay(null); // should be an error
                            break;
                        case Recording:
                        case PausedRecording:
                            actionStopRecording();
//                            actionPlay(null); // should be an error
                            break;
                    }
                    break;
                case SOURCE_MIC:
                    switch (servState) {
                        case Playing:
                        case PausedPlaying:
//                            actionStopPlaying();
                            actionRecord(extras);
                            break;
                        case Recording:
                            break;
                        case StoppedPlaying:
                        case PausedRecording:
                        case StoppedRecording:
                        case Ready:
                        default:
                            actionRecord(extras);
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            Log.d(LOG_TAG, "MediaSession.Callback.onPlayFromUri() : " + uri + "; " + extras
                    + "; service state == " + servState + isUiMsg());

            switch (servState) {
                case Playing:
//                    actionStopPlaying();
//                    actionPlay(uri);
                    break;
                case Ready:
                case PausedPlaying:
                case StoppedPlaying:
                case StoppedRecording:
//                    actionPlay(uri);
                    break;
                case Recording:
                case PausedRecording:
                    actionStopRecording();
//                    actionPlay(uri);
                    break;
                default:
                    Log.e(LOG_TAG, "MediaSession.Callback.onPlayFromUri() : wrong service state : " + servState);
                    break;
            }
        }

        @Override
        public void onPlay() {
            Log.d(LOG_TAG, "MediaSession.Callback.onPlay() : service state == " + servState);

            switch (servState) {
                case Playing:
                    break;
                case PausedPlaying:
                case StoppedPlaying:
//                    actionPlay(null);
                    break;
                case Recording:
                    break;
                case PausedRecording:
                case StoppedRecording:
                    actionRecord(null);
                    break;
                case Ready:
                default:
                    Log.e(LOG_TAG, "MediaSession.Callback.onPlay() : wrong service state : " + servState);
                    break;
            }
        }

        @Override
        public void onPause() {
            Log.d(LOG_TAG, "MediaSession.Callback.onPause() : service state == " + servState);

            switch (servState) {
                case Playing:
//                    actionPausePlaying();
                    break;
                case PausedPlaying:
                case StoppedPlaying:
                    break;
                case Recording:
                    actionPauseRecording();
                    break;
                case PausedRecording:
                case StoppedRecording:
                case Ready:
                default:
                    break;
            }

        }

        @Override
        public void onStop() {
            Log.d(LOG_TAG, "MediaSession.Callback.onStop() : service state == " + servState + isUiMsg());

            switch (servState) {
                case Playing:
                case PausedPlaying:
//                    actionStopPlaying();
                    break;
                case StoppedPlaying:
                    break;
                case Recording:
                case PausedRecording:
                    actionStopRecording();
                    break;
                case StoppedRecording:
                case Ready:
                default:
                    break;
            }
        }
    };

    private class RecAmplitudeChkAsyncTask extends AsyncTask<Void, Double, Void> {
        @Override
        protected void onProgressUpdate(Double... values) {
            if (values == null || values.length == 0) {
                Log.e(LOG_TAG, "aTaskAmplitude.onProgressUpdate() : wrong args ");
                return;
            }
        }

        // in release version not always works
        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                if (recorder != null && isRecorderInitialized) {
                    try {
                        Thread.sleep(AMPLITUDE_CHK_RATE);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                    if (recorder != null) {
                        double amplitude = recorder.getMaxAmplitude();
                        double dbSki = 20 * Math.log10(amplitude / 51805.5336 / 0.0002);
                        publishProgress(dbSki);
                    }
                }
            }
            return null;
        }
    }

    public static boolean isPauseRecordingSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            audioFilesPath = new File(FileUtils.getAudioStoragePath(this));
            Log.d(LOG_TAG, "AudioService created" + isUiMsg());

            hThread.start();
            Handler hndlrBG = new Handler(hThread.getLooper());
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            // MediaSession:
            //error without receiver declared in manifest: java.lang.IllegalArgumentException: MediaButtonReceiver component may not be null
            mediaSes = new MediaSessionCompat(getApplicationContext(), LOG_TAG);
            // callbacks for MediaButtons and TransportControls
            mediaSes.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            metadataBuilder = new MediaMetadataCompat.Builder();
            mediaSes.setMetadata(metadataBuilder
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, SOURCE_NONE).build());
            stateBuilderImplFacility = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);
            mediaSes.setPlaybackState(stateBuilderImplFacility.build());
            // callback from media controller
            mediaSes.setCallback(mediaSesCallback, hndlrBG); //Handler to set execution thread
            // token through which to communicate with client
            setSessionToken(mediaSes.getSessionToken());

            transCntrl = mediaSes.getController().getTransportControls();

            actionSetReady();
        } catch (Exception e) {
            Log.d("AUDIO Service", "onCreate AUDIO: ERROR!");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSes, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (getClass().getPackage().getName().equals(clientPackageName)) {
            return new BrowserRoot(MEDIA_ROOT_ID, null);
        } else {
            Log.i(LOG_TAG, "AudioService onGetRoot() : clientPackageName == " + clientPackageName);
            return new BrowserRoot("", null); // can connect but not browse
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(LOG_TAG, "AudioService onLoadChildren()" + isUiMsg());
        // can't browse
        if (TextUtils.isEmpty(parentId)) {
            result.sendResult(null);
            return;
        }

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        if (MEDIA_ROOT_ID.equals(parentId)) { // root
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {

                Log.w(LOG_TAG, "AudioService onLoadChildren() : External Storage not available : "
                        + Environment.getExternalStorageState());
                result.sendResult(mediaItems);
                return;
            }

            if (!audioFilesPath.exists() || !audioFilesPath.isDirectory()) {
                Log.w(LOG_TAG, "AudioService onLoadChildren() : Audio files dir not available : "
                        + audioFilesPath);
                result.sendResult(mediaItems);
                return;
            }

            File[] files = audioFilesPath.listFiles(audioFileFilter);
            if (files == null || files.length == 0) {
                Log.i(LOG_TAG, "AudioService onLoadChildren() : Audio files not present : "
                        + audioFilesPath);
                result.sendResult(mediaItems);
                return;
            }

            for (File file : files) {
                mediaItems.add(
                        new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                                .setMediaId(SOURCE_AUDIO)
                                .setTitle(getString(R.string.app_name))
                                .setSubtitle(getString(R.string.record_audio_playing))
                                .setDescription(file.getAbsolutePath())
                                .setIconBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic))
                                .setMediaUri(new Uri.Builder().encodedPath(file.getAbsolutePath()).build())
                                .build(),
                                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
            }
        }

        result.sendResult(mediaItems);
    }

    private String isUiMsg() {
        return " / is UI == " + (Looper.myLooper() == Looper.getMainLooper());
    }

    // generate new record name
    private String genNewRecordName() {
        return VOICE_REC_PREF + sDateFormat.format(new Date()) + VOICE_REC_EXT;
    }

    /**
     * request focus for audio playback
     *
     * @return true if granted
     */
    private boolean requestFocus() {
        boolean granted = false;
        if (audioManager != null) {
            granted = audioManager.requestAudioFocus(afchListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            Log.println((granted ? Log.DEBUG : Log.WARN), LOG_TAG,
                    "requestFocus() : audio focus request" + (granted ? "" : " NOT") + " granted");
        } else {
            Log.w(LOG_TAG, "requestFocus() : audioManager == null");
        }
        return granted;
    }

    private void setMetaAndPBState(MediaMetadataCompat meta, int pbState) {
        // first metadata, then pbState
        setMetaAndPBState(meta, pbState, 0, null, null);
    }

    private void setMetaAndPBState(MediaMetadataCompat meta, int pbState, int errCode,
                                   CharSequence errMsg, Bundle extra) {
        // first metadata, then pbState
        mediaSes.setMetadata(meta);
        setPBState(pbState, errCode, errMsg, extra);
        //mediaSes.sendSessionEvent(); // could be used
    }

    private void setPBState(int pbState) {
        setPBState(pbState, 0, null, null);
    }

    private void setPBState(int pbState, Bundle extra) {
        setPBState(pbState, 0, null, extra);
    }

    private void setPBState(int pbState, int errCode, CharSequence errMsg, Bundle extra) {
        try {
            mediaSes.setPlaybackState(stateBuilderImplFacility
                    .setState(pbState, -1, 1)
                    .setErrorMessage(errCode, errMsg)
                    .setExtras(extra)
                    .build());
        } catch (IllegalStateException ise) {
            //java.lang.IllegalStateException: beginBroadcast() called while already in a broadcast
            Log.e(LOG_TAG, "AudioService.setPBState() : ", ise);
        }
    }

    /**
     * Starts audio session : start service, make foreground notification, register receiver, ...
     */
    private void startSession() {
        Log.d(LOG_TAG, "startSession()");
        // start this MediaBrowserService, to make it run when UI will be closed
        try {
            startService(new Intent(getApplicationContext(), this.getClass()));
        } catch (Exception e) {
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }

        mediaSes.setActive(true);
        registerReceiver(bnReceiver, intentFilter);

    }

//    private void pauseSession() {
//        Log.d(LOG_TAG, "pauseSession()");
//        try {
//            unregisterReceiver(bnReceiver);
//        } catch (IllegalArgumentException iae) { // receiver not registered
//            Log.w(LOG_TAG, "Exception @ pauseSession : can't unregister receiver ", iae);
//        }
//        if (notifBuilder != null) {
//            notifBuilder.setContentText(getString(R.string.RECORD_AUDIO_PAUSED));
//            notifBuilder.mActions.clear();
//            String mediaId = mediaSes.getController().getMetadata().getDescription().getMediaId();
//            if (SOURCE_AUDIO.equals(mediaId)) {
//                notifBuilder.addAction(new NotificationCompat.Action(IC_NOTIF_PLAY,
//                        getString(R.string.RECORD_AUDIO_PLAY),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(AudioService.this,
//                                PlaybackStateCompat.ACTION_PLAY)));
//            } else if (SOURCE_MIC.equals(mediaId)) {
//                notifBuilder.addAction(new NotificationCompat.Action(IC_NOTIF_RECORD,
//                        getString(R.string.RECORD_AUDIO_RECORD),
//                        MediaButtonReceiver.buildMediaButtonPendingIntent(AudioService.this,
//                                PlaybackStateCompat.ACTION_PLAY)));
//            }
//            notifBuilder.addAction(new NotificationCompat.Action(IC_NOTIF_STOP,
//                    getString(R.string.RECORD_AUDIO_STOP),
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(AudioService.this,
//                            PlaybackStateCompat.ACTION_STOP)));
//
//            startForeground(SERVICE_ID, notifBuilder.build()); // to update notification
//        }
//        stopForeground(false); // stop but leave notification
//    }

    private void stopSession() {
        Log.d(LOG_TAG, "stopSession()");
        if (audioManager != null) {
            audioManager.abandonAudioFocus(afchListener);
        } else {
            Log.w(LOG_TAG, "stopSession() : audioManager == null");
        }

        try {
            unregisterReceiver(bnReceiver);
        } catch (IllegalArgumentException iae) { // receiver not registered or unregistered onPause
            //Log.w(LOG_TAG, "Exception @ stopSession : can't unregister receiver ", iae);
        }
        // stop service to enable it be terminated when all clients will be unbound
        stopSelf();
        mediaSes.setActive(false);
//        notifBuilder = null;
        stopForeground(true); // stop and remove notification
    }

    private void stopVisualizer() {
        Log.d(LOG_TAG, "stopVisualizer()");
        final ArrayList<Byte> listWf = alWaveFormVal; // to save ref
        final ArrayList<Byte> listFft = alFftVal;
        try {
            if (vslr != null) {
                //vslr.setDataCaptureListener(null, 0, false, false); // could be used
                vslr.setEnabled(false);
                // vslr.release(); // produces crashes when called often
                vslr = null;
                // write data
                if (saveVslData != null) {
                    saveVslData.cancel(true);
                }
                saveVslData = new SaveVslDataATask();
                // to run another (amplitude) at the same time
                saveVslData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new VSLData(listWf, listFft));
            } else {
                Log.d(LOG_TAG, "stopVisualizer() : visualizer == null");
            }
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "stopVisualizer() : Exception ", e);
        }
    }

    private class VSLData {
        private ArrayList<Byte> wf;
        private ArrayList<Byte> fft;

        public VSLData(ArrayList<Byte> wf, ArrayList<Byte> fft) {
            this.wf = wf;
            this.fft = fft;
        }

        public ArrayList<Byte> getWf() {
            return wf;
        }

        public ArrayList<Byte> getFft() {
            return fft;
        }
    }

    private class SaveVslDataATask extends AsyncTask<VSLData, Void, Integer> {
        final StringBuilder msg = new StringBuilder("");

        @Override
        protected Integer doInBackground(VSLData... params) {
            Log.d(LOG_TAG, "SaveVslDataATask.doInBackground()");
            if (params == null || params.length < 1) {
                Log.e(LOG_TAG, "SaveVslDataATask.doInBackground() : wrong params");
                return -1;
            }
            if (isCancelled()) {
                return 1;
            }
            try {
                if (readyToWrite(params[0].getWf(), params[0].getFft())) {
                    if (isCancelled()) {
                        return 1;
                    }
                    writeJSON(params[0].getWf(), params[0].getFft()); // IllegalStateException, IOE
                    if (isCancelled()) {
                        return 1;
                    }
                    writeXML(params[0].getWf(), params[0].getFft()); // IllegalStateException, IOE
                    if (isCancelled()) {
                        return 1;
                    }
                    final String filesPath = "" + getExternalFilesDir(null);
                    msg.append(getString(R.string.record_audio_vsl_data_written)).append(filesPath);
                    Log.i(LOG_TAG, "Visualization data written to JSON and XML : "
                            + filesPath);
                } else {
                    Log.w(LOG_TAG, "visualization data isn't ready to write ");
                    msg.append(getString(R.string.record_audio_error_vsl_data));
                }
            } catch (IllegalStateException | IllegalArgumentException | IOException e) {
                Log.e(LOG_TAG, "writing virtualization data exception ", e);
                msg.append(getString(R.string.record_audio_error_vsl_data));
                return 2;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.d(LOG_TAG, "SaveVslDataATask.onPostExecute() ");
        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
            Log.d(LOG_TAG, "SaveVslDataATask.onCancelled() " + msg);
        }
    }

    /**
     * ready to write JSON or XML data to file
     *
     * @return true if ready
     */
    private boolean readyToWrite(ArrayList<Byte> alWaveFormIn, ArrayList<Byte> alFftIn) {
        Log.d(LOG_TAG, "readyToWrite() " + isUiMsg());
        if (alWaveFormIn == null || alWaveFormIn.isEmpty() || alFftIn == null || alFftIn.isEmpty()) {
            Log.w(LOG_TAG, "readyToWrite() : data is empty : \n" + alWaveFormIn + "; \n" + alFftIn);
            return false;
        }
        if (alWaveFormIn.size() != alFftIn.size()) {
            Log.w(LOG_TAG, "readyToWrite() : data lists not match : WaveForm.size = " + alWaveFormIn.size()
                    + "; Fft.size = " + alFftIn.size() + " \n " + alWaveFormIn + " \n " + alFftIn);
            return false;
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.w(LOG_TAG, "readyToWrite() : media is not mounted");
            return false;
        }
        return true;
    }


    /**
     * writes visualization data to JSON file in external storage.
     * JSON format is:
     * <p>
     * [
     * {
     * VSL_PROP_WAVEFORM_NAME : alWaveForm.get(li),
     * VSL_PROP_FFT_NAME : alFft.get(li)
     * },
     * ]
     */
    private void writeJSON(ArrayList<Byte> alWaveFormIn, ArrayList<Byte> alFftIn)
            throws IllegalStateException, IOException {

        JsonWriter writer = new JsonWriter(new FileWriter(
                new File(getExternalFilesDir(null), VSL_JSON_FILE_NAME)));
        writer.setIndent("\t"); // for human readability

        writer.beginArray();
        for (int li = 0; li < alWaveFormIn.size(); li++) {
            writer.beginObject();
            writer.name(VSL_PROP_WAVEFORM_NAME).value(alWaveFormIn.get(li));
            writer.name(VSL_PROP_FFT_NAME).value(alFftIn.get(li));
            writer.endObject();
        }
        writer.endArray();
        writer.close();
    }

    /**
     * writes visulaizaton data to XML file
     * file structure :
     *
     * <DOCUMENT>
     * <VLS_PROP_ENTRY_NAME>
     * <VSL_PROP_WAVEFORM_NAME>
     * data
     * </VSL_PROP_WAVEFORM_NAME>
     * <VSL_PROP_FFT_NAME>
     * data
     * </VSL_PROP_FFT_NAME>
     * </VLS_PROP_ENTRY_NAME>
     * </DOCUMENT>
     */
    private void writeXML(ArrayList<Byte> alWaveFormIn, ArrayList<Byte> alFftIn)
            throws IllegalStateException, IllegalArgumentException, IOException {

        XmlSerializer xml = Xml.newSerializer();
        xml.setOutput(new FileWriter(new File(getExternalFilesDir(null), VSL_XML_FILE_NAME)));
        xml.startDocument(Xml.Encoding.UTF_8.name(), true);
        for (int li = 0; li < alWaveFormIn.size(); li++) {
            xml.startTag(null, VSL_PROP_ENTRY_NAME)
                    .startTag(null, VSL_PROP_WAVEFORM_NAME)
                    .text(alWaveFormIn.get(li).toString())
                    .endTag(null, VSL_PROP_WAVEFORM_NAME)
                    .startTag(null, VSL_PROP_FFT_NAME)
                    .text(alFftIn.get(li).toString())
                    .endTag(null, VSL_PROP_FFT_NAME)
                    .endTag(null, VSL_PROP_ENTRY_NAME);
        }
        xml.endDocument();
        xml.flush();
    }

    private void actionRecord(final Bundle extras) {
        recFilePath = audioFilesPath.getAbsolutePath() + FOLDER_DIVIDER + mFileName;

        //TODO Проверить надо или нет!!! =======================================

        setMetaAndPBState(metadataBuilder
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, SOURCE_MIC)
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, getString(R.string.app_name))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, getString(R.string.record_audio_recording))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, recFilePath)
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
                                BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic))
                        .build(),
                PlaybackStateCompat.STATE_PLAYING);
        //TODO ==================================================================

        startSession();
        servState = ServiceState.Recording;

        try {
            if (isPauseRecordingSupported() && recorder != null) {
                // can and should resume
                recorder.resume();
            } else {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //TODO better and more reliable to use ogg / vorbis
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                isRecorderInitialized = true;
                if (recorder != null) {
                    aTaskAmplitude = new RecAmplitudeChkAsyncTask();
                    // to run another AT (save data) at the same time
                    aTaskAmplitude.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);

                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        audioFilesPath.mkdirs();
                        recorder.setOutputFile(recFilePath);
                        try {
                            recorder.prepare();
                            recorder.start();
                        } catch (IOException e) {
                            actionStopRecording();
                            setPBState(PlaybackStateCompat.STATE_ERROR,
                                    PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR,
                                    getString(R.string.record_audio_error_microphone), null);
                        } catch (IllegalStateException e) {
                            actionStopRecording();
                            setPBState(PlaybackStateCompat.STATE_ERROR,
                                    PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR,
                                    getString(R.string.record_audio_error_microphone), null);
                        }
                    } else {
                        throw new IOException("Wrong External Storage State: "
                                + Environment.getExternalStorageState());
                    }
                }
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "actionRecord() : can't start recorder ", e);
            actionStopRecording();
            setPBState(PlaybackStateCompat.STATE_ERROR,
                    PlaybackStateCompat.ERROR_CODE_UNKNOWN_ERROR,
                    getString(R.string.record_audio_error_recorder_start), null);
        }
    }

    private void actionPauseRecording() {
//        pauseSession();
        setPBState(PlaybackStateCompat.STATE_PAUSED);
        servState = ServiceState.PausedRecording;
        try {
            if (isPauseRecordingSupported() && recorder != null) {
                recorder.pause();
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "actionPauseRecording() : IllegalArgumentException ", e);
            actionStopRecording();
            setPBState(PlaybackStateCompat.STATE_ERROR);
        }
    }

    private void actionStopRecording() {
        stopSession();
        setPBState(PlaybackStateCompat.STATE_STOPPED);
        servState = ServiceState.StoppedRecording;
        try {
            if (recorder != null) {
                try {
                    recorder.stop();
                    if (aTaskAmplitude != null) {
                        aTaskAmplitude.cancel(true);
                        aTaskAmplitude = null;
                    }
                    isRecorderInitialized = false;
                } catch (RuntimeException stopFailed) {
                    Log.e(LOG_TAG, "actionStopRecording() : MediaRecorder stop Failed" +
                            "(could be no valid audio/video data has been received) ", stopFailed);
                    setPBState(PlaybackStateCompat.STATE_ERROR,
                            PlaybackStateCompat.ERROR_CODE_NOT_SUPPORTED,
                            getString(R.string.record_audio_error_recorder_stop), null);
                    if (recFilePath != null) {
                        File file = new File(recFilePath);
                        if (file.exists()) {
                            if (!file.delete()) {
                                Log.w(LOG_TAG, "actionStopRecording : can't delete " + file);
                            } else {
                                Log.i(LOG_TAG, "actionStopRecording : file deleted " + file);
                            }
                        }
                    }
                }
                recorder.release();
                recorder = null;
            }
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "actionPauseRecording() : IllegalArgumentException ", e);
            setPBState(PlaybackStateCompat.STATE_ERROR);
        }
        notifyChildrenChanged(MEDIA_ROOT_ID);
        recFilePath = null;
    }

    private void actionSetReady() {
        setMetaAndPBState(metadataBuilder
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, SOURCE_NONE).build(),
                PlaybackStateCompat.STATE_NONE);
        servState = ServiceState.Ready;
    }
}
