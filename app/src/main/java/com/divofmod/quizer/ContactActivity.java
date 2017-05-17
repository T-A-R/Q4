package com.divofmod.quizer;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.ico)
                .setTitle("Вернуться к активации?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

                .show();
    }

}
