package com.divofmod.quizer.QuizHelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

public abstract class AudioRecorder {

    private static MediaRecorder sMediaRecorder;
    private static String sFileName;

    //Начинаем аудиозапись
    public static void Start(Context context, String fileName, String maxDuration, String samplingRate) {
        //Проверяем наличие разрешения на аудиозапись.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            return;
        try {

            sFileName = fileName;
            System.out.println(sFileName + " Start");
            releaseRecorder();
            sMediaRecorder = new MediaRecorder();
            sMediaRecorder.setAudioSamplingRate(Integer.parseInt(samplingRate));
            sMediaRecorder.setMaxDuration(Integer.parseInt(maxDuration) * 1000);
            sMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            sMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            sMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            sMediaRecorder.setOutputFile(context.getFilesDir() + "/files/" + fileName + ".amr");
            sMediaRecorder.prepare();
            sMediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Завершаем запись.
    public static String Stop() {
        try {
            if (sMediaRecorder != null) {
                System.out.println(sFileName + " Stop");
                sMediaRecorder.stop();
                sMediaRecorder.release();
                return sFileName;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //Освобождаем все ресурсы рекордера методом release.
    // После этого объект уже нельзя использовать и необходимо создавать и настраивать новый.
    private static void releaseRecorder() {
        if (sMediaRecorder != null) {
            sMediaRecorder.release();
            sMediaRecorder = null;
        }
    }
}