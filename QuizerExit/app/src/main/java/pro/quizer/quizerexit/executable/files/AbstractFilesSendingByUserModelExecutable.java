package pro.quizer.quizerexit.executable.files;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.ISendingFileCallback;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.utils.FileUtils;
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

    public AbstractFilesSendingByUserModelExecutable(final BaseActivity pContext, final UserModelR pUserModel, final ICallback pCallback) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfigR();

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
                mAlertDialog.cancel();
            }
        } else {
            if (!mBaseActivity.isFinishing() && mAlertDialog != null) {
                mAlertDialog.cancel();
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
                final String header = String.format(mBaseActivity.getString(R.string.DIALOG_SENDING_FILE_X_FROM_Y), number, listSize);
                final String message = getFileNameByPosition(position);

                mAlertDialog.setTitle(header);
                mAlertDialog.setMessage(message);

                if (!mBaseActivity.isFinishing()) {
                    mAlertDialog.show();
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

        BaseActivity.addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.SENT, "Попытка отправки " + getMediaType() + " на сервер");

        QuizerAPI.sendFiles(mServerUrl, file, getNameForm(), getMediaType(), responseBody -> {
            if (responseBody == null) {
                pSendingFileCallback.onError(position);
                BaseActivity.addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.ERROR, "Ошибка 3.01 (Не получен или отрицательный ответ от сервера)");
                onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 3.01"));
                return;
            }

            String responseJson = null;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                BaseActivity.addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.ERROR, "Ошибка 3.02 (Ошибка получения JSON из ответа сервера)");
                onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 3.02"));
            }

            DeletingListResponseModel deletingListResponseModel = null;

            try {
                deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
            } catch (Exception pE) {
                BaseActivity.addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.ERROR, "Ошибка 3.03 (Ошибка парсинга JSON)", responseJson);
                onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 3.03"));
            }

            if (deletingListResponseModel != null) {
                if (deletingListResponseModel.getResult() != 0) {
                    final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                    if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                        pSendingFileCallback.onError(position);
                        BaseActivity.addLogWithData(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.ERROR, "Ошибка 3.04 (Сервер не принял отправленные данные)", responseJson);
                        onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_TOKENS_LIST) + " Ошибка: 3.04"));
                    } else {
                        for (final String token : tokensToRemove) {
                            final String path = FileUtils.getFullPathByFileName(file, token);

                            if (StringUtils.isNotEmpty(path)) {
                                final boolean isDeleted = new File(path).delete();
                                BaseActivity.addLog(mLogin, Constants.LogType.FILE, Constants.LogObject.FILE, "Удаление " + getMediaType(), Constants.LogResult.SENT, (isDeleted ? "NOT" : "") + " DELETED: " + path);
                                Log.d("Deleting audio", (isDeleted ? "NOT" : "") + " DELETED: " + path);
                            }
                        }

                        pSendingFileCallback.onSuccess(position);
                        onSuccess();
                    }
                } else {
                    pSendingFileCallback.onError(position);
                    BaseActivity.addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, "Отправка " + getMediaType(), Constants.LogResult.ERROR, "Ошибка 3.05 (" + deletingListResponseModel.getError() + ")");
                    onError(new Exception(deletingListResponseModel.getError() + " Ошибка: 3.05"));
                }
            } else {
                pSendingFileCallback.onError(position);
                onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_ERROR) + " Ошибка: 3.06"));
            }
        });
    }


}
