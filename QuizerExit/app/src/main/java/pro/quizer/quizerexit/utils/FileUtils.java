package pro.quizer.quizerexit.utils;

import java.io.File;

public final class FileUtils {

    private static final String FOLDER_DIVIDER = "/";
    private static final String FOLDER_PHOTOS = "";

    public static boolean renameFile(final File pOldFile, final String pNewNameName) {
        final String pOldFilePath = pOldFile.getAbsolutePath();
        final String pNewFilePath = pOldFilePath
                .substring(0, pOldFilePath.lastIndexOf(FOLDER_DIVIDER) + 1) + FOLDER_PHOTOS + pNewNameName;

        File file2 = new File(pNewFilePath);

        return pOldFile.renameTo(file2);
    }
}