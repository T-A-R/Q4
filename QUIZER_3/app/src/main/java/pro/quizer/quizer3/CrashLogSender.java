package pro.quizer.quizer3;

import android.content.Context;
import android.os.Environment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;

public class CrashLogSender implements Thread.UncaughtExceptionHandler {

    private final DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
    private String stacktraceDir;

    Thread.UncaughtExceptionHandler oldHandler;

    private CrashLogSender(Context context, boolean chained) {

        if (chained)
            oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        else
            oldHandler = null;
        stacktraceDir = String.format("/Android/data/%s/files/", context.getPackageName());
    }

    static CrashLogSender inContext(Context context) {
        return new CrashLogSender(context, true);
    }

    static CrashLogSender reportOnlyHandler(Context context) {
        return new CrashLogSender(context, false);
    }


    public CrashLogSender() {
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
            if (userList != null && userList.size() > 0) {
                flag = true;
                for (UserModelR user : userList) {
                    CoreApplication.getQuizerDatabase().getQuizerDao().updateQuestionnaireStart(false, user.getUser_id());
                    CoreApplication.getQuizerDatabase().getQuizerDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "BEFORE uncaughtException: " + CoreApplication.getQuizerDatabase().getQuizerDao().getCrashLogs().size());
        CoreApplication.getQuizerDatabase().getQuizerDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(), log, flag));
//        Log.d(TAG, "AFTER uncaughtException: " + CoreApplication.getQuizerDatabase().getQuizerDao().getCrashLogs().size());

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
