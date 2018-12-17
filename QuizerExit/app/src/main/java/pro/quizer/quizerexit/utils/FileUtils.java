package pro.quizer.quizerexit.utils;

import java.io.File;

public final class FileUtils {

    private static final String FOLDER_DIVIDER = "/";
    private static final String FOLDER_PHOTOS = "photos";

    public static boolean renameFile(final File pOldFile, final int pUserId, final String pNewNameName) {
        final String pOldFilePath = pOldFile.getAbsolutePath();
        final String pCurrentFilePath = pOldFilePath.substring(0, pOldFilePath.lastIndexOf(FOLDER_DIVIDER) + 1) + FOLDER_PHOTOS + FOLDER_DIVIDER + pUserId + FOLDER_DIVIDER;

        if (!createFolderIfNotExist(pCurrentFilePath)) {
            return false;
        }

        final String pNewFilePath = pCurrentFilePath + pNewNameName;

        File file2 = new File(pNewFilePath);

        return pOldFile.renameTo(file2);
    }

    private static boolean createFolderIfNotExist(final String pPath) {
        final File file = new File(pPath);

        if (!file.exists()) {
            return file.mkdirs();
        }

        return true;
    }
}