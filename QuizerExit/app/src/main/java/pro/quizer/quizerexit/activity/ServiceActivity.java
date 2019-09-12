package pro.quizer.quizerexit.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.DeleteUsersExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendAllQuestionnairesExecutable;
import pro.quizer.quizerexit.executable.ServiceInfoExecutable;
import pro.quizer.quizerexit.executable.UploadingExecutable;
import pro.quizer.quizerexit.executable.UploadingFTPExecutable;
import pro.quizer.quizerexit.executable.files.AllAudiosSendingExecutable;
import pro.quizer.quizerexit.executable.files.AllPhotosSendingExecutable;
import pro.quizer.quizerexit.executable.files.CleanUpFilesExecutable;
import pro.quizer.quizerexit.model.view.ServiceViewModel;
import pro.quizer.quizerexit.utils.FontUtils;
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.Toolbar;

import static com.activeandroid.Cache.getContext;

public class ServiceActivity extends BaseActivity implements ICallback {

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
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
    private String mUnsendedAudioString;
    private String mUnsendedPhotoString;
    private Toolbar mToolbar;
    private boolean isDeletingDB = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initViews();
        initStrings();

        new CleanUpFilesExecutable(getContext(), null).execute();
        updateData(new ServiceInfoExecutable().execute());

        setChangeFontCallback(new BaseActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                showToast(getString(R.string.SETTED) + " " + FontUtils.getCurrentFontName(getFontSizePosition()));
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });
    }

    private void initViews() {
        mSendDataButton = findViewById(R.id.send_data);
        mSendAudioButton = findViewById(R.id.send_audio);
        mSendPhotoButton = findViewById(R.id.send_photo);
        mClearDbButton = findViewById(R.id.clear_db);
        mUploadDataButton = findViewById(R.id.upload_data);
        mUploadFTPDataButton = findViewById(R.id.upload_ftp_data);
        mLogsButton = findViewById(R.id.logs_btn);

        mUsersCount = findViewById(R.id.users_count);
        mUnsendedQuestionaires = findViewById(R.id.unsended_questionnaires_count);
        mUnsendedAudio = findViewById(R.id.unsended_audio_files_count);
        mUnsendePhoto = findViewById(R.id.unsended_photo_files_count);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.VIEW_SERVICE_TITLE));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startAuthActivity();
            }
        });
    }

    private void initStrings() {
        mUsersCountString = getString(R.string.VIEW_USERS_COUNT_ON_DEVICE);
        mUnsendedQuestionairesString = getString(R.string.VIEW_UNSENT_COUNT_QUIZ);
        mUnsendedAudioString = getString(R.string.VIEW_UNSENT_COUNT_AUDIO);
        mUnsendedPhotoString = getString(R.string.VIEW_UNSENT_COUNT_PHOTO);
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

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mUsersCount, String.format(mUsersCountString, usersCount));
                UiUtils.setTextOrHide(mUnsendedQuestionaires, String.format(mUnsendedQuestionairesString, notSentQuestionnairesCount));
                UiUtils.setTextOrHide(mUnsendedAudio, String.format(mUnsendedAudioString, notSentAudioCount));
                UiUtils.setTextOrHide(mUnsendePhoto, String.format(mUnsendedPhotoString, notSentPhotoCount));

                UiUtils.setButtonEnabled(mSendDataButton, notSentQuestionnairesCount > 0);
                UiUtils.setButtonEnabled(mSendAudioButton, notSentAudioCount > 0);
                UiUtils.setButtonEnabled(mSendPhotoButton, notSentPhotoCount > 0);
                UiUtils.setButtonEnabled(mUploadDataButton, notSentQuestionnairesCount > 0 || notSentAudioCount > 0 || notSentPhotoCount > 0 || notSentCrashCount > 0 || notSentLogsCount > 0);
            }
        });

        mSendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.QUESTIONNAIRE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.SEND_QUEST_BUTTON));
                new SendAllQuestionnairesExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mSendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (notSentQuestionnairesCount > 0) {
                    showToast(getString(R.string.NOTIFICATION_PLEASE_SEND_QUIZ));

                    return;
                }
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.SEND_PHOTO_BUTTON));
                new AllPhotosSendingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mSendAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (notSentQuestionnairesCount > 0) {
                    showToast(getString(R.string.NOTIFICATION_PLEASE_SEND_QUIZ));

                    return;
                }
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.SEND_AUDIO_BUTTON));
                new AllAudiosSendingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mClearDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.CLEAR_DB_BUTTON));
                if (!isFinishing()) {
                    try {
                        showClearDbAlertDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mUploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.UPLOAD_BUTTON));
                new UploadingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mUploadFTPDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.FILE, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.VIEW_FTP_UPLOAD_DATA));
                new UploadingFTPExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                addLog("android", Constants.LogType.BUTTON, Constants.LogObject.LOG, getString(R.string.PRESS_BUTTON), Constants.LogResult.PRESSED, getString(R.string.VIEW_LOGS_TITLE));
                startLogsActivity();
            }
        });
    }

    @Override
    public void onStarting() {
//        showProgressBar();
        if (!isFinishing() && !isDeletingDB) {
            showToast(getString(R.string.NOTIFICATION_SENDING));
        }

        if (!isFinishing() && isDeletingDB) {
            showToast(getString(R.string.NOTIFICATION_DELETING_DB));
        }
    }

    @Override
    public void onSuccess() {

        if (isDeletingDB) {
            hideProgressBar();
            startActivity(new Intent(this, ActivationActivity.class));
            return;
        }

        if (!isFinishing()) {
            updateData(new ServiceInfoExecutable().execute());
        }
//        hideProgressBar();
    }

    @Override
    public void onError(final Exception pException) {
        isDeletingDB = false;
        if (!isFinishing()) {
            updateData(new ServiceInfoExecutable().execute());
        }
//        hideProgressBar();
    }

    public void showClearDbAlertDialog() {
        if (!isFinishing()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.VIEW_CLEAR_DB_TITLE)
                    .setMessage(R.string.DIALOG_CLEAR_DB_BODY)
                    .setPositiveButton(R.string.VIEW_YES, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            showProgressBar();
                            isDeletingDB = true;
                            new DeleteUsersExecutable(ServiceActivity.this, ServiceActivity.this).execute();
                        }
                    })
                    .setNegativeButton(R.string.VIEW_NO, null).show();
        }
    }
}