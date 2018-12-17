package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.DeleteUsersExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendAllQuestionnairesExecutable;
import pro.quizer.quizerexit.executable.ServiceInfoExecutable;
import pro.quizer.quizerexit.model.view.ServiceViewModel;
import pro.quizer.quizerexit.utils.OkHttpUtils;
import pro.quizer.quizerexit.view.Toolbar;

public class ServiceActivity extends BaseActivity implements ICallback {

    private String serverUrl;

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

        serverUrl = getCurrentUser().getConfig().getServerUrl();

        initViews();
        initStrings();

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
            public void onClick(View v) {
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
                mUsersCount.setText(String.format(mUsersCountString, usersCount));
                mUnsendedQuestionaires.setText(String.format(mUnsendedQuestionairesString, notSentQuestionnairesCount));
                mUnsendedAudio.setText(String.format(mUnsendedAudioString, notSentAudioCount));
                mUnsendePhoto.setText(String.format(mUnsendedPhotoString, notSentPhotoCount));

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
            public void onClick(View view) {
                new SendAllQuestionnairesExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });

        mSendPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notSentQuestionnairesCount > 0) {
                    showToast(getString(R.string.please_send_q));

                    return;
                }


                final Call.Factory client = new OkHttpClient();
                client.newCall(OkHttpUtils.postPhoto(notSentPhoto, getServer()))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                hideProgressBar();

                            }

                            @Override
                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                hideProgressBar();

                                final ResponseBody responseBody = response.body();

                                if (responseBody == null) {
                                    showToast(getString(R.string.incorrect_server_response));

                                    return;
                                }

//                                final String responseJson = responseBody.string();
                            }
                        });

//                OkHttpUtils.sendPhotos(serverUrl, notSentPhoto);
            }
        });

        mDeleteUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteUsersExecutable(ServiceActivity.this, ServiceActivity.this).execute();
            }
        });
    }

    @Override
    public void onStarting() {
        showProgressBar();
    }

    @Override
    public void onSuccess() {
        updateData(new ServiceInfoExecutable().execute());
        hideProgressBar();
    }

    @Override
    public void onError(Exception pException) {
        updateData(new ServiceInfoExecutable().execute());
        hideProgressBar();
    }
}