package pro.quizer.quizerexit.executable.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.OkHttpUtils;
import pro.quizer.quizerexit.utils.StringUtils;

public abstract class AbstractFilesSendingByUserModelExecutable extends BaseExecutable {

    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;
    private final int mUserId;
    private final String mServerUrl;
    private final Context mContext;

    public AbstractFilesSendingByUserModelExecutable(final Context pContext, final UserModel pUserModel, final ICallback pCallback) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfig();

        mContext = pContext;
        mLoginAdmin = configModel.getLoginAdmin();
        mServerUrl = configModel.getServerUrl();

        mUserId = pUserModel.user_id;
        mLogin = pUserModel.login;
        mPassword = pUserModel.password;
    }

    public Context getContext() {
        return mContext;
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

        final List<File> notSendFiles = getFiles();

        if (notSendFiles == null || notSendFiles.isEmpty()) {
            return;
        }

        final Call.Factory client = new OkHttpClient();
        client.newCall(OkHttpUtils.postFiles(notSendFiles, mServerUrl, getNameForm(), getMediaType()))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                        final ResponseBody responseBody = response.body();

                        if (responseBody == null) {
                            onError(new Exception(mContext.getString(R.string.incorrect_server_response)));

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
                                    onError(new Exception(mContext.getString(R.string.empty_list_of_accepted_questionnairies)));
                                } else {
                                    for (final String token : tokensToRemove) {
                                        final String path = FileUtils.getFullPathByFileName(notSendFiles, token);

                                        if (StringUtils.isNotEmpty(path)) {
                                            final boolean isDeleted = new File(path).delete();

                                            Log.d("Deleting audio", (isDeleted ? "NOT" : "") + " DELETED: " + path);
                                        }
                                    }

                                    onSuccess();
                                }
                            } else {
                                onError(new Exception(deletingListResponseModel.getError()));
                            }
                        } else {
                            onError(new Exception(mContext.getString(R.string.server_error)));
                        }
                    }
                });

    }
}
