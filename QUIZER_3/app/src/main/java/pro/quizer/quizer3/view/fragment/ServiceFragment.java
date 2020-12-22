package pro.quizer.quizer3.view.fragment;

import androidx.appcompat.app.AlertDialog;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.executable.DeleteUsersExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SendAllQuestionnairesExecutable;
import pro.quizer.quizer3.executable.ServiceInfoExecutable;
import pro.quizer.quizer3.executable.files.AllAudiosSendingExecutable;
import pro.quizer.quizer3.executable.files.AllPhotosSendingExecutable;
import pro.quizer.quizer3.executable.files.CleanUpFilesExecutable;
import pro.quizer.quizer3.executable.files.UploadingExecutable;
import pro.quizer.quizer3.executable.files.UploadingFTPExecutable;
import pro.quizer.quizer3.model.view.ServiceViewModel;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class ServiceFragment extends ScreenFragment {

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mClearFiles;
    private Button mClearDbButton;
    private Button mUploadDataButton;
    private Button mUploadFTPDataButton;
    private Button mLogsButton;

    private TextView mUsersCount;
    private String mUsersCountString;
    private TextView mUnsendedQuestionaires;
    private String mUnsendedQuestionairesString;
    private TextView mUnsendedAudio;
    private TextView mUnsendePhoto;
    private TextView mDeviceId;
    private String mUnsendedAudioString;
    private String mUnsendedPhotoString;
    private MainActivity activity;

    public ServiceFragment() {
        super(R.layout.fragment_service);
    }

    @Override
    protected void onReady() {

        activity = getMainActivity();
        initViews();
        MainFragment.disableSideMenu();
        initStrings();
        updateData(new ServiceInfoExecutable(activity).execute());
    }

    public void initViews() {
        mSendDataButton = findViewById(R.id.send_data);
        mSendAudioButton = findViewById(R.id.send_audio);
        mSendPhotoButton = findViewById(R.id.send_photo);
        mClearDbButton = findViewById(R.id.clear_db);
        mClearFiles = findViewById(R.id.clear_files);
        mUploadDataButton = findViewById(R.id.upload_data);
        mUploadFTPDataButton = findViewById(R.id.upload_ftp_data);
        mLogsButton = findViewById(R.id.logs_btn);

        mUsersCount = findViewById(R.id.users_count);
        mUnsendedQuestionaires = findViewById(R.id.unsended_questionnaires_count);
        mUnsendedAudio = findViewById(R.id.unsended_audio_files_count);
        mUnsendePhoto = findViewById(R.id.unsended_photo_files_count);
        mDeviceId = findViewById(R.id.device_id);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.service_title));
        mToolbar.showCloseView(v -> replaceFragment(new AuthFragment()));

        RelativeLayout cont = findViewById(R.id.service_cont);
        cont.startAnimation(Anim.getAppear(getContext()));
        mLogsButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendAudioButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendPhotoButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mClearFiles.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mClearDbButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mUploadDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mUploadFTPDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
    }

    private void initStrings() {
        mUsersCountString = getString(R.string.view_user_count_on_device);
        mUnsendedQuestionairesString = getString(R.string.view_unsent_count_quiz);
        mUnsendedAudioString = getString(R.string.view_unsent_count_audio);
        mUnsendedPhotoString = getString(R.string.view_unsent_count_photo);
    }

    private void updateData(final ServiceViewModel pServiceViewModel) {
        final List<File> notSentAudio = getAllAudio();
        final List<File> notSentPhoto = getAllPhotos();
        final int notSentAudioCount = notSentAudio.size();
        final int notSentPhotoCount = notSentPhoto.size();
        final int notSentQuestionnairesCount = pServiceViewModel.getQuestionnaireModels().size();
        final int notSentCrashCount = getDao().getCrashLogs().size();
        final int notSentLogsCount = getDao().getAllLogsWithStatus(Constants.LogStatus.NOT_SENT).size();
        final int usersCount = pServiceViewModel.getUserModels().size();
        final boolean hasUnfinishedQuiz = activity.getCurrentQuestionnaireForce() != null;

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mUsersCount, String.format(mUsersCountString, usersCount));
                UiUtils.setTextOrHide(mUnsendedQuestionaires, String.format(mUnsendedQuestionairesString, notSentQuestionnairesCount));
                UiUtils.setTextOrHide(mUnsendedAudio, String.format(mUnsendedAudioString, notSentAudioCount));
                UiUtils.setTextOrHide(mUnsendePhoto, String.format(mUnsendedPhotoString, notSentPhotoCount));
                UiUtils.setTextOrHide(mDeviceId, "Device ID: " + DeviceUtils.getDeviceId());

                UiUtils.setButtonEnabled(mSendDataButton, notSentQuestionnairesCount > 0);
                UiUtils.setButtonEnabled(mSendAudioButton, notSentAudioCount > 0);
                UiUtils.setButtonEnabled(mSendPhotoButton, notSentPhotoCount > 0);
                UiUtils.setButtonEnabled(mClearFiles, notSentPhotoCount > 0 || notSentAudioCount > 0);
                UiUtils.setButtonEnabled(mUploadDataButton, notSentQuestionnairesCount > 0 || notSentAudioCount > 0 || notSentPhotoCount > 0 || notSentCrashCount > 0 || notSentLogsCount > 0);
            }
        });

        mSendDataButton.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_send_quiz), null);
            new SendAllQuestionnairesExecutable((MainActivity) getActivity(), new ICallback() {
                @Override
                public void onStarting() {
                    showToast(getString(R.string.notification_sending));
                }

                @Override
                public void onSuccess() {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }

                @Override
                public void onError(Exception pException) {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }
            }).execute();
        });

        mSendPhotoButton.setOnClickListener(view -> {
            if (hasUnfinishedOrSend(notSentQuestionnairesCount, hasUnfinishedQuiz)) return;

//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_send_photo), null);
            new AllPhotosSendingExecutable((MainActivity) getActivity(), new ICallback() {
                @Override
                public void onStarting() {
                    showToast(getString(R.string.notification_sending));
                }

                @Override
                public void onSuccess() {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }

                @Override
                public void onError(Exception pException) {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }
            }).execute();
        });

        mSendAudioButton.setOnClickListener(view -> {
            if (hasUnfinishedOrSend(notSentQuestionnairesCount, hasUnfinishedQuiz)) return;

//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_send_audio), null);
            new AllAudiosSendingExecutable((MainActivity) getActivity(), new ICallback() {
                @Override
                public void onStarting() {
                    showToast(getString(R.string.notification_sending));
                }

                @Override
                public void onSuccess() {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }

                @Override
                public void onError(Exception pException) {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }
            }).execute();
        });

        mClearDbButton.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_clear_db), null);
            if (getActivity() != null && !getActivity().isFinishing()) {
                try {
                    showClearDbAlertDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        mClearFiles.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_clear_db), null);
            if (getActivity() != null && !getActivity().isFinishing()) {
                new CleanUpFilesExecutable(activity, new ICallback() {
                    @Override
                    public void onStarting() {

                    }

                    @Override
                    public void onSuccess() {
                        showToast("Файлы удалены");
                        updateData(new ServiceInfoExecutable(activity).execute());
                    }

                    @Override
                    public void onError(Exception pException) {
                        showToast("Ошибка удаления файлов");
                        updateData(new ServiceInfoExecutable(activity).execute());
                    }
                }).execute();
            }

        });

        mUploadDataButton.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_upload_data), null);
            new UploadingExecutable((MainActivity) getActivity(), new ICallback() {
                @Override
                public void onStarting() {
                    showToast(getString(R.string.notification_uploading));
                }

                @Override
                public void onSuccess() {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }

                @Override
                public void onError(Exception pException) {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }
            }).execute();
            showToast(getString(R.string.upload_complete));
        });

        mUploadFTPDataButton.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.ftp_upload), null);
            new UploadingFTPExecutable((MainActivity) getActivity(), new ICallback() {
                @Override
                public void onStarting() {
                    showToast(getString(R.string.notification_uploading));
                }

                @Override
                public void onSuccess() {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }

                @Override
                public void onError(Exception pException) {
                    updateData(new ServiceInfoExecutable(activity).execute());
                }
            }).execute();
        });

        mLogsButton.setOnClickListener(view -> {
//                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.LOG, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_logs), null);
            replaceFragment(new LogsFragment());
        });
    }

    public void showClearDbAlertDialog() {
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.clear_db_title)
                    .setMessage(R.string.dialog_clear_db_warning)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {
                        showScreensaver(getString(R.string.notification_clear_db),true);
                        new DeleteUsersExecutable(activity, new ICallback() {
                            @Override
                            public void onStarting() {
                            }

                            @Override
                            public void onSuccess() {
                                hideScreensaver();
                                replaceFragment(new KeyFragment());
                            }

                            @Override
                            public void onError(Exception pException) {
                                updateData(new ServiceInfoExecutable(activity).execute());
                            }
                        }).execute();
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new AuthFragment());
        return true;
    }
}

