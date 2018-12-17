package pro.quizer.quizerexit.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class FileUtils {

    public static final String FOLDER_DIVIDER = "/";
    public static final String FOLDER_PHOTOS = "photos";
    public static final String FOLDER_AUDIO = "audio";
    public static final String JPEG = ".jpeg";

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

    public static boolean createFolderIfNotExist(final String pPath) {
        final File file = new File(pPath);

        if (!file.exists()) {
            return file.mkdirs();
        }

        return true;
    }

    public static List<String> getFiles(final String pPath) {
        List<String> inFiles = new ArrayList<>();

        if (StringUtils.isEmpty(pPath)) {
            return inFiles;
        }

        final File parentDir = new File(pPath);

        if (!parentDir.exists()) {
            return inFiles;
        }

        File[] files = parentDir.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFiles(file.getAbsolutePath()));
            } else {
                if (file.getName().endsWith(JPEG)) {
                    inFiles.add(file.getAbsolutePath());
                }
            }
        }

        return inFiles;
    }

    public static List<String> getFilesRecursion(final String pPath) {
        List<String> inFiles = new ArrayList<>();

        if (StringUtils.isEmpty(pPath)) {
            return inFiles;
        }

        Queue<File> files = new LinkedList<>();

        final File parentDir = new File(pPath);

        if (!parentDir.exists()) {
            return inFiles;
        }

        files.addAll(Arrays.asList(parentDir.listFiles()));

        while (!files.isEmpty()) {
            File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else if (file.getName().endsWith(JPEG)) {
                inFiles.add(file.getAbsolutePath());
            }
        }

        return inFiles;
    }
}