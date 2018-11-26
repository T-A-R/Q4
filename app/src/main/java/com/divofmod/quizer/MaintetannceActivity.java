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
    String [] Users;
    int CountUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintetannce);

        mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        countUsers = findViewById(R.id.countUsers);
        notSendForm = findViewById(R.id.notSendForm);
        notSendAudio = findViewById(R.id.notSendAudio);
        Users = mSharedPreferences.getString("lastUserId","0;").split(";");
        int i = 0;
        while (i < Users.length)
        {
            Log.i(TAG, "userid: " + Users[i]);
            i++;
        }
        CountUsers = Users.length;

        countUsers.setText(getResources().getString(R.string.textCountUsers) + " " + 0);
        if (CountUsers != 0)
        {
            countUsers.setText(getResources().getString(R.string.textCountUsers) + " " + String.valueOf(CountUsers-1));
        }

        notSendForm.setText(getResources().getString(R.string.textNotSendForm) +  " " + "0");
        int countQuiz = Count("QuizzesRequest_");
        if (countQuiz !=0) {
            notSendForm.setText(getResources().getString(R.string.textNotSendForm ) + " " + String.valueOf(countQuiz));
        }

        int countAudio = Count("Quizzes_audio_");
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
        while (i < CountUsers)
        {
            Log.i("Count I0", field + mSharedPreferences.getString(field + Users[i],"0"));
            if (!mSharedPreferences.getString(field + Users[i], "0").equals("0"))
            {
            int audiosOrquizzers = mSharedPreferences.getString(field + Users[i], "0").split(";").length;
                Log.i(TAG, "Count II: " + String .valueOf(audiosOrquizzers));
            count = count + audiosOrquizzers;
            }
            i++;
        }

        return count;
    }

    private boolean notSended()
    {
        int i = 0;

        try {
            while (i <= CountUsers) {

                Log.i(TAG, "Quizze: " + mSharedPreferences.getString("QuizzesRequest_" + Users[i], "0;"));
                Log.i(TAG, "Audio: " + mSharedPreferences.getString("Quizzes_audio_" + Users[i], "0;"));
                Integer audio = Integer.parseInt(mSharedPreferences.getString("Quizzes_audio_" + Users[i], "0"));
                Integer quizzes = Integer.parseInt(mSharedPreferences.getString("QuizzesRequest_" + Users[i], "0"));

                if (!audio.equals(0) || quizzes.equals(0))
                {
                    Log.i(TAG, "rrrrrr1: " + String .valueOf(quizzes));
                    if (!audio.equals(0))
                    {
                        audio = mSharedPreferences.getString("Quizzes_audio_" + Users[i], "0").split(";").length;
                        Log.i(TAG, "rrrrrr:2 " + String .valueOf(quizzes));
                    }
                    if (!quizzes.equals(0))
                    {
                        quizzes = mSharedPreferences.getString("QuizzesRequest_" + Users[i], "0").split(";").length;
                        Log.i(TAG, "rrrrrr3: " + String .valueOf(quizzes));
                    }
                }


                if (!audio.equals(0) || !quizzes.equals(0)) {
                    Log.i(TAG, "true");
                    return true;
                }
                i++;
            }
        }
        catch (Exception ignore)
        {
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
