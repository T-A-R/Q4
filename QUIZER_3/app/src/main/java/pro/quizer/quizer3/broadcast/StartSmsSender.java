package pro.quizer.quizer3.broadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.CoreApplication;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.database.models.OptionsR;

public class StartSmsSender extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 3;
    private String CHANNEL_ID = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.q_icon)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.sms_notification_title))
                .setContentText(context.getString(R.string.sms_notification_text));

        OptionsR option = null;
        try {
            option = CoreApplication.getQuizerDatabase().getQuizerDao().getOption(Constants.OptionName.QUIZ_STARTED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (option != null && !option.getData().equals("true")) {

            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra("AfterNotification", true);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);


            builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
            Notification notificationCompat = builder.build();
            notificationCompat.defaults |= Notification.DEFAULT_SOUND;
            notificationCompat.defaults |= Notification.DEFAULT_VIBRATE;

            if (notificationManager != null) {
                notificationManager.notify(NOTIFICATION_ID, notificationCompat);
            }
        }

    }
}
