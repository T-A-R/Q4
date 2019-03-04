package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.os.Environment;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.FileUtils;

import static pro.quizer.quizerexit.utils.FileUtils.FOLDER_DIVIDER;
import static pro.quizer.quizerexit.utils.FileUtils.moveFile;

public class UploadingExecutable extends BaseExecutable {

    private final String UPLOADING_QUESTIONNAIRE_FILE_NAME = "data_%1$s_%2$s.json";
    private final String UPLOADING_FOLDER_NAME = "data_quizer";
    private final String UPLOADING_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_DIVIDER + UPLOADING_FOLDER_NAME + FOLDER_DIVIDER;

    private final Context mContext;

    public UploadingExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        onStarting();

        FileUtils.createFolderIfNotExist(UPLOADING_PATH);

        moveFiles();
        moveQuestionnaires();

        onSuccess();
    }

    private void moveQuestionnaires() {
        final List<UserModel> users = new Select().from(UserModel.class).execute();

        if (users == null || users.isEmpty()) {
            onError(new Exception(mContext.getString(R.string.users_not_found_for_send_data)));

            return;
        }

        for (final UserModel user : users) {
            final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModelExecutable(user).execute();

            if (requestModel != null) {
                try {
                    FileUtils.createTxtFile(UPLOADING_PATH, String.format(UPLOADING_QUESTIONNAIRE_FILE_NAME, user.login, DateUtils.getCurrentTimeMillis()), new Gson().toJson(requestModel));

                    setSentStatusForUserQuestionnaires(user);
                } catch (final IOException pE) {

                }
            }
        }
    }

    private void setSentStatusForUserQuestionnaires(final UserModel pUserModel) {
        new Update(QuestionnaireDatabaseModel.class)
                .set(QuestionnaireDatabaseModel.STATUS + " = ?", QuestionnaireStatus.SENT)
                .where(QuestionnaireDatabaseModel.USER_ID + " = ?", pUserModel.user_id)
                .execute();
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
