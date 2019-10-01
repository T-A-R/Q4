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

import pro.quizer.quizer3.MainActivity;

public class FileUtils {

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
