package pro.quizer.quizerexit.broadcast;

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
import android.widget.Toast;
import android.support.v4.app.NotificationManagerCompat;
import android.media.MediaPlayer;
import android.os.Vibrator;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.MainActivity;
import pro.quizer.quizerexit.service.NotificationService;

public class StartSmsSender extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 3;
    private String CHANNEL_ID = null;
    private Vibrator vib;
    private MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {

//        Intent intent1 = new Intent(context, NotificationService.class);
//        context.startService(intent1);

//        mp = MediaPlayer.create(this, R.raw.sound_clip);
//        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        vib.vibrate(500);
//        mp.start();

//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setContentTitle("Стадия закончена");
//        builder.setContentText("Пожалуйста отправьте волну СМС");
//        builder.setSmallIcon(R.drawable.q_icon);
//        Intent notifyIntent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        //to be able to launch your activity from the notification
//        builder.setContentIntent(pendingIntent);
//        Notification notificationCompat = builder.build();
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
//        notificationCompat.defaults |= Notification.DEFAULT_SOUND;
//        notificationCompat.defaults |= Notification.DEFAULT_VIBRATE;
//        managerCompat.notify(NOTIFICATION_ID, notificationCompat);

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
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.q_icon)
                .setContentTitle("Стадия закончена")
                .setContentText("Пожалуйста отправьте волну СМС");

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        Notification notificationCompat = builder.build();
        notificationCompat.defaults |= Notification.DEFAULT_SOUND;
        notificationCompat.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(NOTIFICATION_ID, notificationCompat);

//        Notification notificationCompat = builder.build();
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
//        notificationCompat.defaults |= Notification.DEFAULT_SOUND;
//        notificationCompat.defaults |= Notification.DEFAULT_VIBRATE;
//        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
