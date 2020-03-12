package pro.quizer.quizer3.executable.files;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.utils.FileUtils;

import static pro.quizer.quizer3.utils.FileUtils.FILE_NAME_DIVIDER;

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

        if (mContext instanceof MainActivity) {
            final List<File> files = new ArrayList<>();

            files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG, FileUtils.getPhotosStoragePath(mContext)));
            files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR, FileUtils.getAudioStoragePath(mContext)));

            final List<QuestionnaireDatabaseModelR> allQuestionnaires = ((MainActivity) mContext).getMainDao().getAllQuestionnaires();

            final List<String> allTokens = new ArrayList<>();

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
