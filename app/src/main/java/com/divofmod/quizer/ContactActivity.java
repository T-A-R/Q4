package com.divofmod.quizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        final DrawerLayout drawer = findViewById(R.id.drawer);
        NavigationView navigation = findViewById(R.id.navigation);
        Button buttonDrawer = findViewById(R.id.openDrawer_toolbar);
        buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.END);
            }
        });

        View view = navigation.getHeaderView(0);
        view.findViewById(R.id.sync_button).setOnClickListener(clickHeader);
        view.findViewById(R.id.settings).setOnClickListener(clickHeader);
        view.findViewById(R.id.change_users).setOnClickListener(clickHeader);
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

    View.OnClickListener clickHeader = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.sync_button:
                    startActivity(new Intent(ContactActivity.this, SendQuizzesActivity.class));
                    break;
                case R.id.settings:
                    startActivity(new Intent(ContactActivity.this, SettingsActivity.class));
                    break;
                case R.id.change_users:
                    startActivity(new Intent(ContactActivity.this, AuthActivity.class));
                    break;
            }
        }
    };

}
