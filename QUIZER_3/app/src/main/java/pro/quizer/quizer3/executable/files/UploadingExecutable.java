package pro.quizer.quizer3.executable.files;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.QuestionnaireListRequestModelExecutable;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.logs.Crash;
import pro.quizer.quizer3.API.models.request.CrashRequestModel;
import pro.quizer.quizer3.API.models.request.LogsRequestModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireListRequestModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.FileUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.utils.FileUtils.FOLDER_DIVIDER;
import static pro.quizer.quizer3.utils.FileUtils.moveFile;

public class UploadingExecutable extends BaseExecutable {

    private final String UPLOADING_QUESTIONNAIRE_FILE_NAME = "data_%1$s_%2$s" + FileUtils.JSON;
    private final String UPLOADING_CRASH_FILE_NAME = "quizer_crashlog_%1$s_%2$s" + FileUtils.JSON;
    private final String UPLOADING_LOGS_FILE_NAME = "quizer_applogs_%1$s_%2$s" + FileUtils.JSON;
    public static final String UPLOADING_FOLDER_NAME = "data_quizer";
    public static final String UPLOADING_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_DIVIDER + UPLOADING_FOLDER_NAME + FOLDER_DIVIDER;

    private final MainActivity mContext;

    public UploadingExecutable(final MainActivity pContext, final ICallback pCallback) {
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
            try {
                mContext.runOnUiThread(() ->  Toast.makeText(mContext, R.string.notification_uploading, Toast.LENGTH_SHORT).show());
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
    }

    private void moveQuestionnaires() {

        final List<UserModelR> users = MainActivity.getStaticDao().getAllUsers();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.notification_sending_empty_users_list)));
            return;
        }

        for (final UserModelR user : users) {
            final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModelExecutable(user, true).execute();

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

        if (MainActivity.getStaticDao().getCrashLogs().size() > 0) {

            List<Crash> crashList = new ArrayList<>();
            List<CrashLogs> crashLogsList = null;
            try {
                crashLogsList = MainActivity.getStaticDao().getCrashLogs();
            } catch (Exception e) {
                MainActivity.addLog("android", Constants.LogType.DATABASE, Constants.LogObject.LOG, mContext.getString(R.string.load_crashlog_from_db), Constants.LogResult.ERROR, mContext.getString(R.string.db_load_error), e.getMessage());
            }
            if (crashLogsList != null && crashLogsList.size() > 0) {
                for (CrashLogs crash : crashLogsList) {
                    crashList.add(new Crash("android", crash.getLog(), crash.isFrom_questionnaire()));
                }
            }

            Log.d(TAG, "Sending Crash Logs: " + crashList.size());
            CrashRequestModel crashRequestModel = new CrashRequestModel("android", crashList);

            Gson gson = new Gson();
            String json = gson.toJson(crashRequestModel);

            try {
                Log.d(TAG, "moveCrashLogs: " + json);
                FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_CRASH_FILE_NAME, "android", DateUtils.getCurrentTimeMillis()), json);
                MainActivity.getStaticDao().clearCrashLogs();
            } catch (final IOException pE) {
                MainActivity.addLog("android", Constants.LogType.FILE, Constants.LogObject.LOG, mContext.getString(R.string.save_crashlog_to_file), Constants.LogResult.ERROR, mContext.getString(R.string.save_ctashlog_error), pE.getMessage());
            }
        }
    }

    private void moveLogs() {

        List<AppLogsR> logs = MainActivity.getStaticDao().getAllLogsWithStatus(Constants.LogStatus.NOT_SENT);

        if (logs.size() > 0) {

            LogsRequestModel crashRequestModel = new LogsRequestModel("android", logs);
            Gson gson = new Gson();
            String json = gson.toJson(crashRequestModel);

            try {
                Log.d(TAG, "moveLogs: " + json);
                FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_LOGS_FILE_NAME, "android", DateUtils.getCurrentTimeMillis()), json);
//                BaseActivity.getDao().setLogsStatus(Constants.LogStatus.SENT);
                MainActivity.getStaticDao().clearAppLogsR();
            } catch (final IOException pE) {
                Log.d(TAG, "Не удалось сформировать файл журнала событий при ручной выгрузке\n" + pE.getMessage());
            }
        }
    }

    private void setSentStatusForUserQuestionnaires(final UserModelR pUserModel) {
        try {
//            MainActivity.getStaticDao().setQuestionnaireStatusByUserId(QuestionnaireStatus.SENT, pUserModel.getUser_id());
            MainActivity.getStaticDao().deleteQuestionnaireStatusByUserId(pUserModel.getUser_id());
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
