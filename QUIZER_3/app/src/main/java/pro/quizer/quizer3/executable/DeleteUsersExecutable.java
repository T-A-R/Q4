package pro.quizer.quizer3.executable;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.SmsAnswersR;
import pro.quizer.quizer3.utils.FileUtils;

import static pro.quizer.quizer3.CoreApplication.getQuizerDatabase;
import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.utils.FileUtils.moveFile;

public class DeleteUsersExecutable extends BaseExecutable {

    private final Context mContext;
    private final ICallback mCallback;

    public DeleteUsersExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        try {
            List<SmsAnswersR> smsAnswers = getQuizerDatabase().getQuizerDao().getAllSmsAnswers();
            moveFiles();

            getQuizerDatabase().clearAllTables();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (smsAnswers != null && smsAnswers.size() > 0)
                            getQuizerDatabase().getQuizerDao().insertSmsAnswersList(smsAnswers);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onSuccess();
                }
            }, 5000);


        } catch (Exception e) {
            Log.d(TAG, mContext.getString(R.string.db_clear_error));
            onError(e);
        }

    }

    private void moveFiles() {

//        FileUtils.createFolderIfNotExist(UPLOADING_PATH);

        final List<File> files = new ArrayList<>();

        files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG, FileUtils.getPhotosStoragePath(mContext)));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR, FileUtils.getAudioStoragePath(mContext)));

        for (final File file : files) {
            moveFile(file);
        }
    }
}
