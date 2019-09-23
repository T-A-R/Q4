package pro.quizer.quizerexit.utils;

import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Build;
import android.os.Environment;
//import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pro.quizer.quizerexit.CoreApplication;
//import pro.quizer.quizerexit.database.QuizerDatabase;
import pro.quizer.quizerexit.database.model.CrashLogs;
import pro.quizer.quizerexit.database.model.UserModelR;

public class TryMe implements Thread.UncaughtExceptionHandler {

    private final DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
//    private final DateFormat fileFormatter = new SimpleDateFormat("dd-MM-yy");
//    private String versionName = "0";
//    private int versionCode = 0;
    private String stacktraceDir;
//    private final Thread.UncaughtExceptionHandler previousHandler;

    Thread.UncaughtExceptionHandler oldHandler;

    private TryMe(Context context, boolean chained) {

//        PackageManager mPackManager = context.getPackageManager();
//        PackageInfo mPackInfo;
//        try {
//            mPackInfo = mPackManager.getPackageInfo(context.getPackageName(), 0);
//            versionName = mPackInfo.versionName;
//            versionCode = mPackInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // ignore
//        }
        if (chained)
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        else
            oldHandler = null;
        stacktraceDir = String.format("/Android/data/%s/files/", context.getPackageName());
    }

    static TryMe inContext(Context context) {
        return new TryMe(context, true);
    }

    static TryMe reportOnlyHandler(Context context) {
        return new TryMe(context, false);
    }


    public TryMe() {
        oldHandler = Thread.getDefaultUncaughtExceptionHandler(); // сохраним ранее установленный обработчик
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        final String state = Environment.getExternalStorageState();
        final Date dumpDate = new Date(System.currentTimeMillis());


        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder
//                .append("\n")
                .append(formatter.format(dumpDate)).append("\n")
                .append(String.format("Version: %s \n", DeviceUtils.getAppVersion()))
                .append(String.format("Device: %s \n", DeviceUtils.getDeviceInfo()))
                .append(thread.toString()).append("\n");
        processThrowable(throwable, reportBuilder);

        String log = reportBuilder.toString();

        List<UserModelR> userList;
        boolean flag = false;

        try {
            userList = CoreApplication.getQuizerDatabase().getQuizerDao().getUserWithAbortedQUestionnaire(true);
            if(userList != null && userList.size() > 0) {
                flag = true;
                for(UserModelR user : userList) {
                    CoreApplication.getQuizerDatabase().getQuizerDao().updateQuestionnaireStart(false, user.getUser_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CoreApplication.getQuizerDatabase().getQuizerDao().insertCrashLog(new CrashLogs(log, flag));


        if (oldHandler != null) // если есть ранее установленный...
            oldHandler.uncaughtException(thread, throwable); // ...вызовем его
    }

    private void processThrowable(Throwable exception, StringBuilder builder) {
        if (exception == null)
            return;
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        builder
                .append("Exception: ").append(exception.getClass().getName()).append("\n")
                .append("Message: ").append(exception.getMessage()).append("\nStacktrace:\n");
        for (StackTraceElement element : stackTraceElements) {
            builder.append("\t").append(element.toString()).append("\n");
        }
        processThrowable(exception.getCause(), builder);
    }
}
