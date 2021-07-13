package pro.quizer.quizer3.executable.files;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.utils.FileUtils;

import static pro.quizer.quizer3.executable.files.UploadingExecutable.UPLOADING_PATH;

public class CleanUpDataQuizerExecutable extends BaseExecutable {

    private final ICallback mCallback;

    public CleanUpDataQuizerExecutable(final ICallback pCallback) {
        super(pCallback);

        mCallback = pCallback;
    }

    @Override
    public void execute() {
        onStarting();

        final List<File> files = new ArrayList<>();

        files.addAll(FileUtils.getFilesRecursion(FileUtils.JSON, UPLOADING_PATH));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR, UPLOADING_PATH));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG, UPLOADING_PATH));

        for (final File file : files) {
            final boolean isDeleted = file.delete();
            Log.d("Clean up file", (!isDeleted ? "NOT " : "") + "DELETED: " + file.getAbsolutePath());
        }
        onSuccess();
    }
}
