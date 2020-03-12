package pro.quizer.quizer3.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import pro.quizer.quizer3.Constants;

import pro.quizer.quizer3.MainActivity;

public class FileUtils {

    public static String FILE_NAME_DIVIDER = "^";
    private static final String PHOTO_NAME_JPEG_TEMPLATE = "%1$s" + FILE_NAME_DIVIDER + "%2$s" + FILE_NAME_DIVIDER + "%3$s" + FILE_NAME_DIVIDER + "%4$s" + FILE_NAME_DIVIDER + "%5$s.jpeg";
    private static final String AUDIO_NAME_AMR_TEMPLATE = "%1$s/%2$s" + FILE_NAME_DIVIDER + "%3$s" + FILE_NAME_DIVIDER + "%4$s" + FILE_NAME_DIVIDER + "%5$s" + FILE_NAME_DIVIDER + "%6$s.amr";

    public static final String JSON = ".json";
    public static final String AMR = ".amr";
    public static final String JPEG = ".jpeg";

    public static final String FOLDER_DIVIDER = "/";
    private static final String FOLDER_PHOTOS = "photos";
    private static final String FOLDER_AUDIO = "audios";
    private static final String MEDIA_FILES = "media_files";
    private static final String CACHE = "cache";

    public static String getFileName(final String url) {
        final int end = url.length();
        final int start = url.lastIndexOf(FileUtils.FOLDER_DIVIDER);
        return url.substring(start + 1, end);
    }

    public static void createTxtFile(final String pFilePath, final String pFileName, final CharSequence pBody) throws IOException {
        final File root = new File(pFilePath);

        if (!root.exists()) {
            root.mkdirs();
        }

        final File gpxfile = new File(root, pFileName);
        final FileWriter writer = new FileWriter(gpxfile);

        writer.append(pBody);
        writer.flush();
        writer.close();

    }

    public static String getPhotosStoragePath(final Context pContext) {
        final String dataStoragePath = getDataStoragePath(pContext);
        final String path;

        if (StringUtils.isEmpty(dataStoragePath)) {
            path = Constants.Strings.EMPTY;
        } else {
            path = dataStoragePath + FileUtils.FOLDER_PHOTOS;
        }

        createFolderIfNotExist(path);

        return path;
    }

    public static String getAudioStoragePath(final Context pContext) {
        final String dataStoragePath = getDataStoragePath(pContext);
        final String path;

        if (StringUtils.isEmpty(dataStoragePath)) {
            path = Constants.Strings.EMPTY;
        } else {
            path = dataStoragePath + FileUtils.FOLDER_AUDIO;
        }

        createFolderIfNotExist(path);

        return path;
    }

    public static String getMediaFillesStoragePath(final Context pContext) {
        final String dataStoragePath = getDataStoragePath(pContext);
        final String path;

        if (StringUtils.isEmpty(dataStoragePath)) {
            path = Constants.Strings.EMPTY;
        } else {
            path = dataStoragePath + FileUtils.MEDIA_FILES;
        }

        createFolderIfNotExist(path);

        return path;
    }

    public static String getAudioFileStoragePath(final Context pContext, final String pAudioFileName) {
        final String audioStoragePath = getAudioStoragePath(pContext);

        if (StringUtils.isEmpty(audioStoragePath)) {
            return Constants.Strings.EMPTY;
        } else {
            return audioStoragePath + FOLDER_DIVIDER + pAudioFileName;
        }
    }

    private static String getDataStoragePath(final Context pContext) {
        File file = null;
        if (pContext != null)
            file = pContext.getExternalCacheDir();

        if (file != null) {
            final String path = file.getAbsolutePath();
            return path.replace(CACHE, Constants.Strings.EMPTY);
        } else {
            return Constants.Strings.EMPTY;
        }
    }

    public static String getFilesStoragePath(final Context pContext) {
        File file = null;
        if (pContext != null)
        file = pContext.getExternalFilesDir(null);

        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return Constants.Strings.EMPTY;
        }
    }

    public static boolean moveFile(final File pFile, final String pNewFilePath) {
        final File newFile = new File(pNewFilePath, pFile.getName());

        return pFile.renameTo(newFile);
    }

//    public static boolean removeFile(final Context pContext, final File pOldFile, final int pUserId, final String pNewNameName) {
//
//    }

    public static boolean renameFile(final File pOldFile, final String pNewName) {
        final String path = pOldFile.getPath();
        final String pCurrentFilePath = path.replace(pOldFile.getName(), Constants.Strings.EMPTY);

        if (!createFolderIfNotExist(pCurrentFilePath)) {
            return false;
        }

        final String pNewFilePath = pCurrentFilePath + pNewName;

        final File file2 = new File(pNewFilePath);

        return pOldFile.renameTo(file2);
    }

    public static boolean renameFile(final Context pContext, final File pOldFile, final int pUserId, final String pNewNameName) {
        final String pCurrentFilePath = getPhotosStoragePath(pContext) + FOLDER_DIVIDER + pUserId + FOLDER_DIVIDER;

        if (!createFolderIfNotExist(pCurrentFilePath)) {
            return false;
        }

        final String pNewFilePath = pCurrentFilePath + pNewNameName;

        final File file2 = new File(pNewFilePath);

        return pOldFile.renameTo(file2);
    }

    public static boolean createFolderIfNotExist(final String pPath) {
        final File file = new File(pPath);

        if (!file.exists()) {
            return file.mkdirs();
        }

        return true;
    }

    private static List<String> getFiles(final String pPath) {
        final List<String> inFiles = new ArrayList<>();

        if (StringUtils.isEmpty(pPath)) {
            return inFiles;
        }

        final File parentDir = new File(pPath);

        if (!parentDir.exists()) {
            return inFiles;
        }

        final File[] files = parentDir.listFiles();

        for (final File file : files) {
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

    public static List<File> getFilesRecursion(final String pType, final String pPath) {
        final List<File> inFiles = new ArrayList<>();

        if (StringUtils.isEmpty(pPath)) {
            return inFiles;
        }

        final Queue<File> files = new LinkedList<>();

        final File parentDir = new File(pPath);

        if (!parentDir.exists()) {
            return inFiles;
        }

        files.addAll(Arrays.asList(parentDir.listFiles()));

        while (!files.isEmpty()) {
            final File file = files.remove();
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else if (file.getName().endsWith(pType)) {
                inFiles.add(file);
            }
        }

        return inFiles;
    }

    public static String getFullPathByFileName(final Iterable<File> pFiles, final String pFileName) {
        for (final File file : pFiles) {
            final String path = file.getAbsolutePath();

            if (path.endsWith(pFileName)) {
                return path;
            }
        }

        return null;
    }

    public static String generatePhotoFileName(final String mLoginAdmin, final int mProjectId, final String mUserLogin, final String mToken, final int mRelativeId) {
        return String.format(PHOTO_NAME_JPEG_TEMPLATE, mLoginAdmin, mProjectId, mUserLogin, mToken, mRelativeId);
    }

    public static String generateAudioFileName(final Context pContext, final int pUserId, final String mLoginAdmin, final int mProjectId, final String mUserLogin, final String mToken) {
        return generateAudioFileName(pContext, pUserId, mLoginAdmin, mProjectId, mUserLogin, mToken, 0);
    }

    public static String generateAudioFileName(final Context pContext, final int pUserId, final String mLoginAdmin, final int mProjectId, final String mUserLogin, final String mToken, final int mRelativeId) {
        final String pCurrentFilePath = getAudioStoragePath(pContext) + FOLDER_DIVIDER + pUserId + FOLDER_DIVIDER;

        createFolderIfNotExist(pCurrentFilePath);

//        return String.format(AUDIO_NAME_AMR_TEMPLATE, pUserId, mLoginAdmin, mProjectId, mUserLogin, mToken, mRelativeId, DateUtils.getCurrentTimeMillis());
        return String.format(AUDIO_NAME_AMR_TEMPLATE, pUserId, mLoginAdmin, mProjectId, mUserLogin, mToken, mRelativeId);
    }

    public static void saveToFile(String data, String fileName) {
        File downloadsFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsFile, fileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(data);
            bw.close();
            Log.d(MainActivity.TAG, "FileUtils.saveToFile() file saved: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.d(MainActivity.TAG, "FileUtils.saveToFile() " + e);
        }
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }
}
