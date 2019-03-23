package pro.quizer.quizerexit.executable.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.ISendingFileCallback;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.OkHttpUtils;
import pro.quizer.quizerexit.utils.StringUtils;

public abstract class AbstractFilesSendingByUserModelExecutable extends BaseExecutable {

    public static final int FIRST_FILE_POSITION = 0;
    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;
    private final int mUserId;
    private final String mServerUrl;
    private final BaseActivity mBaseActivity;

    List<File> mNotSendFiles;

    AlertDialog.Builder mDialogBuilder;
    AlertDialog mAlertDialog;

    public static Comparator<File> FILE_COMPARATOR = new Comparator<File>() {

        public int compare(File s1, File s2) {
            long firstFile = s1.length();
            long secondFile = s2.length();

            return (int) (firstFile - secondFile);
        }
    };


    public AbstractFilesSendingByUserModelExecutable(final BaseActivity pContext, final UserModel pUserModel, final ICallback pCallback) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfig();

        mBaseActivity = pContext;
        mLoginAdmin = configModel.getLoginAdmin();
        mServerUrl = configModel.getServerUrl();

        mUserId = pUserModel.user_id;
        mLogin = pUserModel.login;
        mPassword = pUserModel.password;
    }

    public Context getContext() {
        return mBaseActivity;
    }

    public int getUserId() {
        return mUserId;
    }

    public abstract List<File> getFiles();

    public abstract String getMediaType();

    public abstract String getNameForm();

    @Override
    public void execute() {
        onStarting();

        mNotSendFiles = getFiles();

        if (mNotSendFiles == null || mNotSendFiles.isEmpty()) {
            return;
        }

        Collections.sort(mNotSendFiles, FILE_COMPARATOR);

        sendFile(FIRST_FILE_POSITION, new ISendingFileCallback() {
            @Override
            public void onSuccess(int position) {
                cancelAlert();

                final int next = position + 1;
                sendFile(next, this);
            }

            @Override
            public void onError(int position) {
                cancelAlert();

                final int next = position + 1;
                sendFile(next, this);
            }

            @Override
            public void onFinish() {
                cancelAlert();
            }
        });
    }

    private void cancelAlert() {
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
        }
    }

    private String getFileNameByPosition(final int position) {
        return mNotSendFiles.get(position).getName();
    }

    private void showAlert(final int position) {
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDialogBuilder = new AlertDialog.Builder(mBaseActivity);
                mAlertDialog = mDialogBuilder.create();

                final String number = String.valueOf(position + 1);
                final String listSize = String.valueOf(mNotSendFiles.size());
                final String header = String.format(mBaseActivity.getString(R.string.sending_file_x_from_y), number, listSize);
                final String message = getFileNameByPosition(position);

                mAlertDialog.setTitle(header);
                mAlertDialog.setMessage(message);

                mAlertDialog.show();
            }
        });
    }

    private void sendFile(final int position, final ISendingFileCallback pSendingFileCallback) {
        if (position >= mNotSendFiles.size()) {
            pSendingFileCallback.onFinish();

            return;
        }

        showAlert(position);

        final List<File> file = Collections.singletonList(mNotSendFiles.get(position));

        final Call.Factory client = new OkHttpClient();
        client.newCall(OkHttpUtils.postFiles(file, mServerUrl, getNameForm(), getMediaType()))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        pSendingFileCallback.onError(position);
                        onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                        final ResponseBody responseBody = response.body();

                        if (responseBody == null) {
                            pSendingFileCallback.onError(position);
                            onError(new Exception(mBaseActivity.getString(R.string.incorrect_server_response)));

                            return;
                        }

                        final String responseJson = responseBody.string();
                        DeletingListResponseModel deletingListResponseModel = null;

                        try {
                            deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
                        } catch (Exception pE) {
                            // empty
                        }

                        if (deletingListResponseModel != null) {
                            if (deletingListResponseModel.getResult() != 0) {
                                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                                    pSendingFileCallback.onError(position);
                                    onError(new Exception(mBaseActivity.getString(R.string.empty_list_of_accepted_questionnairies)));
                                } else {
                                    for (final String token : tokensToRemove) {
                                        final String path = FileUtils.getFullPathByFileName(file, token);

                                        if (StringUtils.isNotEmpty(path)) {
                                            final boolean isDeleted = new File(path).delete();

                                            Log.d("Deleting audio", (isDeleted ? "NOT" : "") + " DELETED: " + path);
                                        }
                                    }

                                    pSendingFileCallback.onSuccess(position);
                                    onSuccess();
                                }
                            } else {
                                pSendingFileCallback.onError(position);
                                onError(new Exception(deletingListResponseModel.getError()));
                            }
                        } else {
                            pSendingFileCallback.onError(position);
                            onError(new Exception(mBaseActivity.getString(R.string.server_error)));
                        }
                    }
                });
    }
}
