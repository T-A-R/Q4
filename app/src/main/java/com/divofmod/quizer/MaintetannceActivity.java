package com.divofmod.quizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MaintetannceActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MaintetannceActivity";
    TextView countUsers,notSendForm,notSendAudio;
    Button sendData, saveData,deleteUser;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintetannce);

        mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        countUsers = findViewById(R.id.countUsers);
        notSendForm = findViewById(R.id.notSendForm);
        notSendAudio = findViewById(R.id.notSendAudio);

        countUsers.setText(getResources().getString(R.string.textCountUsers) + " " + 0);
        if (mSharedPreferences.getInt("lastUserId",0) != 0)
        {
            countUsers.setText(getResources().getString(R.string.textCountUsers) + " " + mSharedPreferences.getInt("lastUserId",0));
        }

        notSendForm.setText(getResources().getString(R.string.textNotSendForm) +  " " + "0");
        int countQuiz = Count("QuizzesRequest_");
        if (countQuiz !=0) {
            notSendForm.setText(getResources().getString(R.string.textNotSendForm ) + " " + String.valueOf(countQuiz));
        }

        int countAudio = Count("All_sended_audios_");
        notSendAudio.setText(getResources().getString(R.string.textSendAudioWithCurrentDevice) + " " + "0");
        if (countAudio != 0)
        {
            notSendAudio.setText(getResources().getString(R.string.textSendAudioWithCurrentDevice) + " " + String.valueOf(countAudio));
        }

        sendData = findViewById(R.id.sendData);
        sendData.setOnClickListener(this);
        saveData = findViewById(R.id.saveData);
        saveData.setOnClickListener(this);
        deleteUser = findViewById(R.id.deleteUser);
        deleteUser.setOnClickListener(this);



        if (notSended() == true)
        deleteUser.setEnabled(false);

    }

    private int Count(String field)
    {
        int i = 0;
        int count = 0;
        while (i < mSharedPreferences.getInt("lastUserId",0))
        {
            count = count + Integer.parseInt(mSharedPreferences.getString(field + i,"0"));
            i++;
        }

        return count;
    }

    private boolean notSended()
    {
        int i = 0;

        try {
            while (i < mSharedPreferences.getInt("lastUserId", 0)) {

                if (Integer.parseInt(mSharedPreferences.getString("Quizzes_audio_" + i, "0")) != 0 && Integer.parseInt(mSharedPreferences.getString("Quizzes_audio_" + i, "0")) != 0) {
                    Log.i(TAG, "notSended: ");
                    return true;
                }
                i++;
            }
        }
        catch (Exception ignore)
        {
            Log.i(TAG, "not: ");
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {


        }
    }
}
