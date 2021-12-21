package pro.quizer.quizer3.executable.files;

import android.content.Context;
import android.os.Build;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.ISendingFileCallback;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.API.models.response.DeletingListResponseModel;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public abstract class AbstractFilesSendingByUserModelExecutable extends BaseExecutable {

    public static final int FIRST_FILE_POSITION = 0;
    private final String mLoginAdmin;
    private final String mLogin;
    private final String mPassword;
    private final int mUserId;
    private final String mServerUrl;
    private final MainActivity mBaseActivity;

    List<File> mNotSendFiles;

    AlertDialog.Builder mDialogBuilder;
    AlertDialog mAlertDialog;

    public static Comparator<File> FILE_COMPARATOR = (s1, s2) -> {
        long firstFile = s1.length();
        long secondFile = s2.length();

        return (int) (firstFile - secondFile);
    };

    public AbstractFilesSendingByUserModelExecutable(final MainActivity pContext, final UserModelR pUserModel, final ICallback pCallback) {
        super(pCallback);

        final ConfigModel configModel = pContext.getConfig();

        mBaseActivity = pContext;
        mLoginAdmin = configModel.getLoginAdmin();
        mServerUrl = configModel.getServerUrl();
        mUserId = pUserModel.getUser_id();
        mLogin = pUserModel.getLogin();
        mPassword = pUserModel.getPassword();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!mBaseActivity.isDestroyed() && mAlertDialog != null) {
                try {
                    mAlertDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (!mBaseActivity.isFinishing() && mAlertDialog != null) {
                try {
                    mAlertDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getFileNameByPosition(final int position) {
        return mNotSendFiles.get(position).getName();
    }

    private void showAlert(final int position) {
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mDialogBuilder = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                mAlertDialog = mDialogBuilder.create();

                final String number = String.valueOf(position + 1);
                final String listSize = String.valueOf(mNotSendFiles.size());
                final String header = String.format(mBaseActivity.getString(R.string.dialog_sending_file_x_from_y), number, listSize);
                final String message = getFileNameByPosition(position);

                mAlertDialog.setTitle(header);
                mAlertDialog.setMessage(message);

                if (!mBaseActivity.isFinishing()) {
                    try {
                        mAlertDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

        QuizerAPI.sendFiles(mServerUrl, file, getNameForm(), getMediaType(), responseBody -> {
            if (responseBody == null) {
                pSendingFileCallback.onError(position);
                onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_301)));
                return;
            }

            String responseJson = null;
            try {
                responseJson = responseBody.string();
                Log.d("T-L.AbstractFilesSendin", "sendFile: " + responseJson);
            } catch (IOException e) {
                e.printStackTrace();
                onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_302)));
            }

            DeletingListResponseModel deletingListResponseModel = null;

            try {
                deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
            } catch (Exception pE) {
                onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_303)));
                Log.d("T-L.AbstractFilesSendin", "sendFile: DeletingListResponseModel - ERROR");
            }
            if (deletingListResponseModel != null) {
                if(deletingListResponseModel.isProjectActive() != null) {
                    try {
                        mBaseActivity.getMainDao().setProjectActive(deletingListResponseModel.isProjectActive());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (deletingListResponseModel.getResult() != 0) {
                    final List<String> tokensToRemove = deletingListResponseModel.getAccepted();
                    if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                        Log.d(TAG, "sendFile: TOKEN = NULL");
                        pSendingFileCallback.onError(position);
                        onError(new Exception(mBaseActivity.getString(R.string.empty_tokens_list_error) + " " + mBaseActivity.getString(R.string.error_304)));
                    } else {
                        for (final String token : tokensToRemove) {
                            final String path = FileUtils.getFullPathByFileName(file, token);

                            if (StringUtils.isNotEmpty(path)) {
                                final boolean isDeleted = new File(path).delete();
                                Log.d("Deleting audio", (isDeleted ? "NOT" : "") + " DELETED: " + path);
                            }
                            mBaseActivity.getMainDao().clearPhotoAnswersByName(token);

                        }
                        pSendingFileCallback.onSuccess(position);
                        onSuccess();
                    }
                } else {
                    pSendingFileCallback.onError(position);
                    onError(new Exception(deletingListResponseModel.getError() + " " + mBaseActivity.getString(R.string.error_305)));
                }
            } else {
                pSendingFileCallback.onError(position);
                onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_306)));
            }
        });
    }
}
