package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.database.model.CrashLogs;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.logs.Crash;
import pro.quizer.quizerexit.model.request.CrashRequestModel;
import pro.quizer.quizerexit.model.request.LogsRequestModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.FileUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;
import static pro.quizer.quizerexit.utils.FileUtils.FOLDER_DIVIDER;
import static pro.quizer.quizerexit.utils.FileUtils.moveFile;

public class UploadingExecutable extends BaseExecutable {

    private final String UPLOADING_QUESTIONNAIRE_FILE_NAME = "data_%1$s_%2$s" + FileUtils.JSON;
    private final String UPLOADING_CRASH_FILE_NAME = "quizer_crashlog_%1$s_%2$s" + FileUtils.JSON;
    private final String UPLOADING_LOGS_FILE_NAME = "quizer_applogs_%1$s_%2$s" + FileUtils.JSON;
    public static final String UPLOADING_FOLDER_NAME = "data_quizer";
    public static final String UPLOADING_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_DIVIDER + UPLOADING_FOLDER_NAME + FOLDER_DIVIDER;

    private final Context mContext;

    public UploadingExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        onStarting();

        FileUtils.createFolderIfNotExist(UPLOADING_PATH);
        moveCrashLogs();
        moveLogs();
        moveFiles();
        moveQuestionnaires();

        if (mContext != null)
            Toast.makeText(mContext, R.string.NOTIFICATION_UPLOADING, Toast.LENGTH_SHORT).show();
//        onSuccess();
    }

    private void moveQuestionnaires() {

        final List<UserModelR> users = BaseActivity.getDao().getAllUsers();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_USERS_LIST)));
            return;
        }

        for (final UserModelR user : users) {
            final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModelExecutable(user).execute();

            if (requestModel != null) {
                try {
                    FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_QUESTIONNAIRE_FILE_NAME, user.getLogin(), DateUtils.getCurrentTimeMillis()), new Gson().toJson(requestModel));
                    setSentStatusForUserQuestionnaires(user);
                } catch (final IOException pE) {

                }
            }
        }
    }

    private void moveCrashLogs() {

        if (BaseActivity.getDao().getCrashLogs().size() > 0) {
//            String crashLog = BaseActivity.getDao().getCrashLogs().get(BaseActivity.getDao().getCrashLogs().size() - 1).getLog();
//            Log.d(TAG, "Sending Crash Log: " + crashLog);
//            CrashRequestModel crashRequestModel = new CrashRequestModel("android", new Crash("android", crashLog));

            List<Crash> crashList = new ArrayList<>();
            List<CrashLogs> crashLogsList = null;
            try {
                crashLogsList = BaseActivity.getDao().getCrashLogs();
            } catch (Exception e) {
                BaseActivity.addLogWithData("android", Constants.LogType.DATABASE, Constants.LogObject.LOG, mContext.getString(R.string.LOAD_CRASHLOG_FROM_DB), Constants.LogResult.ERROR, mContext.getString(R.string.DB_LOAD_ERROR), e.getMessage());
            }
            if (crashLogsList != null && crashLogsList.size() > 0) {
                for (CrashLogs crash : crashLogsList) {
                    crashList.add(new Crash("android", crash.getLog()));
                }
            }

            Log.d(TAG, "Sending Crash Logs: " + crashList.size());
            CrashRequestModel crashRequestModel = new CrashRequestModel("android", crashList);

            Gson gson = new Gson();
            String json = gson.toJson(crashRequestModel);

            try {
                Log.d(TAG, "moveCrashLogs: " + json);
                FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_CRASH_FILE_NAME, "android", DateUtils.getCurrentTimeMillis()), json);
                BaseActivity.getDao().clearCrashLogs();
            } catch (final IOException pE) {
                BaseActivity.addLogWithData("android", Constants.LogType.FILE, Constants.LogObject.LOG, mContext.getString(R.string.SAVE_CRASH_FILE), Constants.LogResult.ERROR, mContext.getString(R.string.SAVE_CRASH_ERROR), pE.getMessage());
            }
        }
    }

    private void moveLogs() {

        List<AppLogsR> logs = BaseActivity.getDao().getAllLogsWithStatus(Constants.LogStatus.NOT_SENT);

        if (logs.size() > 0) {

            LogsRequestModel crashRequestModel = new LogsRequestModel("android", logs);
            Gson gson = new Gson();
            String json = gson.toJson(crashRequestModel);

            try {
                Log.d(TAG, "moveLogs: " + json);
                FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_LOGS_FILE_NAME, "android", DateUtils.getCurrentTimeMillis()), json);
//                BaseActivity.getDao().setLogsStatus(Constants.LogStatus.SENT);
                BaseActivity.getDao().clearAppLogsR();
            } catch (final IOException pE) {
                Log.d(TAG, "Не удалось сформировать файл журнала событий при ручной выгрузке\n" + pE.getMessage());
            }
        }
    }

    private void setSentStatusForUserQuestionnaires(final UserModelR pUserModel) {
        try {
            BaseActivity.getDao().setQuestionnaireStatusByUserId(QuestionnaireStatus.SENT, pUserModel.getUser_id());
        } catch (Exception e) {
            Log.d(TAG, "Ошибка записи в базу данных.");
        }
    }

    private void moveFiles() {
        final List<File> files = new ArrayList<>();

        files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG, FileUtils.getPhotosStoragePath(mContext)));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR, FileUtils.getAudioStoragePath(mContext)));

        for (final File file : files) {
            moveFile(file, UPLOADING_PATH);
        }
    }
}
