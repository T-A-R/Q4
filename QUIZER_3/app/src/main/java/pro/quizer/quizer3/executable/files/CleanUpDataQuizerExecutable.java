package pro.quizer.quizer3.executable.files;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.executable.BaseExecutable;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.utils.FileUtils;

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

        files.addAll(FileUtils.getFilesRecursion(FileUtils.JSON));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.AMR));
        files.addAll(FileUtils.getFilesRecursion(FileUtils.JPEG));

        for (final File file : files) {
            final boolean isDeleted = file.delete();
            Log.d("Clean up file", (!isDeleted ? "NOT " : "") + "DELETED: " + file.getAbsolutePath());
        }
        onSuccess();
    }
}
