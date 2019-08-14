package pro.quizer.quizerexit.broadcast;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Collections;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.utils.SmsUtils;

public class StartSmsReminder extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Отправка SMS...", Toast.LENGTH_SHORT).show();
        //TODO Show dialog

        Intent i = new Intent(context,BaseActivity.class).putExtra("fromNotificationClick",true);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), i, 0);

    }
}
