package pro.quizer.quizerexit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

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
import pro.quizer.quizerexit.utils.UiUtils;
import pro.quizer.quizerexit.view.Toolbar;

import static com.activeandroid.Cache.getContext;

public class ServiceActivity extends BaseActivity implements ICallback {

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mDeleteUsersButton;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initViews();
        initStrings();

        new CleanUpFilesExecutable(getContext(), null).execute();
        updateData(new ServiceInfoExecutable().execute());
    }

    private void initViews() {
        mSendDataButton = findViewById(R.id.send_data);
        mSendAudioButton = findViewById(R.id.send_audio);
        mSendPhotoButton = findViewById(R.id.send_photo);
        mDeleteUsersButton = findViewById(R.id.delete_users);
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
                UiUtils.setButtonEnabled(mDeleteUsersButton, usersCount > 0 && notSentQuestionnairesCount <= 0 && notSentAudioCount <= 0 && notSentPhotoCount <= 0);
                UiUtils.setButtonEnabled(mUploadDataButton, notSentQuestionnairesCount > 0 || notSentAudioCount > 0 || notSentPhotoCount > 0 || notSentCrashCount >0);
            }
        });

        mSendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
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

                new AllAudiosSendingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mDeleteUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new DeleteUsersExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mUploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new UploadingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mUploadFTPDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new UploadingFTPExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startLogsActivity();
            }
        });
    }

    @Override
    public void onStarting() {
//        showProgressBar();
        if (!isFinishing()) {
            showToast(getString(R.string.NOTIFICATION_SENDING));
        }
    }

    @Override
    public void onSuccess() {
        if (!isFinishing()) {
            updateData(new ServiceInfoExecutable().execute());
        }
//        hideProgressBar();
    }

    @Override
    public void onError(final Exception pException) {
        if (!isFinishing()) {
            updateData(new ServiceInfoExecutable().execute());
        }
//        hideProgressBar();
    }
}