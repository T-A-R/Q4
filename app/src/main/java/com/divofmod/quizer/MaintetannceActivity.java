package com.divofmod.quizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MaintetannceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MaintetannceActivity";
    TextView countUsers,notSendForm,notSendAudio;
    Button sendData,saveDate,deleteUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintetannce);

        countUsers = findViewById(R.id.countUsers);
        notSendForm = findViewById(R.id.notSendForm);
        notSendAudio = findViewById(R.id.notSendAudio);

        sendData = findViewById(R.id.sendData);
        sendData.setOnClickListener(this);
        saveDate = findViewById(R.id.saveData);
        saveDate.setOnClickListener(this);
        deleteUser = findViewById(R.id.deleteUser);
        deleteUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
