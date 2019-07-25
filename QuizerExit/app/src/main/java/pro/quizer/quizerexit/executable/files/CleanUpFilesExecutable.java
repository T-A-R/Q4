package pro.quizer.quizerexit.executable.files;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Select;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.QuestionnaireDatabaseModelR;
import pro.quizer.quizerexit.executable.BaseExecutable;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.utils.FileUtils;

import static pro.quizer.quizerexit.utils.FileUtils.FILE_NAME_DIVIDER;

public class CleanUpFilesExecutable extends BaseExecutable {

    private final Context mContext;
    private final ICallback mCallback;

    public CleanUpFilesExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        if (mContext instanceof  BaseActivity) {
            final List<File> files = new ArrayList<>();

            files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG, FileUtils.getPhotosStoragePath(mContext)));
            files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR, FileUtils.getAudioStoragePath(mContext)));

            // GOOD select
//            final List<QuestionnaireDatabaseModel> allQuestionnaires = new Select()
//                    .from(QuestionnaireDatabaseModel.class)
//                    .execute();
            final List<QuestionnaireDatabaseModelR> allQuestionnaires = BaseActivity.getDao().getAllQuestionnaires();

            final List<String> allTokens = new ArrayList<>();

//            for (final QuestionnaireDatabaseModel model : allQuestionnaires) {
//                allTokens.add(model.token);
//            }

            for (final QuestionnaireDatabaseModelR model : allQuestionnaires) {
                allTokens.add(model.getToken());
            }

            for (final File file : files) {
                final String token = extractToken(file.getName());

                if (!allTokens.contains(token)) {
                    final boolean isDeleted = file.delete();

                    Log.d("Clean up file", (isDeleted ? "NOT" : "") + " DELETED: " + file.getAbsolutePath());
                }
            }
        } else {
            onError(new Exception("Внутренняя ошибка приложения CleanUpFilesExecutable 1"));
        }
    }

    private String extractToken(final String pFileName) {
        final String[] array = pFileName.split("\\" + FILE_NAME_DIVIDER);

        return array[3];
    }
}
