package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.DeleteUsersExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.UploadingExecutable;
import pro.quizer.quizerexit.executable.files.AllAudiosSendingExecutable;
import pro.quizer.quizerexit.executable.files.AllPhotosSendingExecutable;
import pro.quizer.quizerexit.executable.SendAllQuestionnairesExecutable;
import pro.quizer.quizerexit.executable.ServiceInfoExecutable;
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

        mUsersCount = findViewById(R.id.users_count);
        mUnsendedQuestionaires = findViewById(R.id.unsended_questionnaires_count);
        mUnsendedAudio = findViewById(R.id.unsended_audio_files_count);
        mUnsendePhoto = findViewById(R.id.unsended_photo_files_count);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startAuthActivity();
            }
        });
    }

    private void initStrings() {
        mUsersCountString = getString(R.string.count_users_on_this_device);
        mUnsendedQuestionairesString = getString(R.string.count_unsended_questionnaires);
        mUnsendedAudioString = getString(R.string.count_unsended_audio_files);
        mUnsendedPhotoString = getString(R.string.count_unsended_photo_files);
    }

    private void updateData(final ServiceViewModel pServiceViewModel) {
        final List<File> notSentAudio = getAllAudio();
        final List<File> notSentPhoto = getAllPhotos();
        final int notSentAudioCount = notSentAudio.size();
        final int notSentPhotoCount = notSentPhoto.size();
        final int notSentQuestionnairesCount = pServiceViewModel.getQuestionnaireModels().size();
        final int usersCount = pServiceViewModel.getUserModels().size();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mUsersCount, String.format(mUsersCountString, usersCount));
                UiUtils.setTextOrHide(mUnsendedQuestionaires, String.format(mUnsendedQuestionairesString, notSentQuestionnairesCount));
                UiUtils.setTextOrHide(mUnsendedAudio, String.format(mUnsendedAudioString, notSentAudioCount));
                UiUtils.setTextOrHide(mUnsendePhoto, String.format(mUnsendedPhotoString, notSentPhotoCount));

                mSendDataButton.setVisibility(notSentQuestionnairesCount > 0 ? View.VISIBLE : View.GONE);
                mSendAudioButton.setVisibility(notSentAudioCount > 0 ? View.VISIBLE : View.GONE);
                mSendPhotoButton.setVisibility(notSentPhotoCount > 0 ? View.VISIBLE : View.GONE);

                if (usersCount > 0 && notSentQuestionnairesCount <= 0 && notSentAudioCount <= 0 && notSentPhotoCount <= 0) {
                    mDeleteUsersButton.setVisibility(View.VISIBLE);
                } else {
                    mDeleteUsersButton.setVisibility(View.GONE);
                }

                if (notSentQuestionnairesCount > 0 || notSentAudioCount > 0 || notSentPhotoCount > 0) {
                    mUploadDataButton.setVisibility(View.VISIBLE);
                } else {
                    mUploadDataButton.setVisibility(View.GONE);
                }
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
                    showToast(getString(R.string.please_send_q));

                    return;
                }

                new AllPhotosSendingExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mSendAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (notSentQuestionnairesCount > 0) {
                    showToast(getString(R.string.please_send_q));

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
    }

    @Override
    public void onStarting() {
//        showProgressBar();
        if (!isFinishing()) {
            showToast(getString(R.string.sending));
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