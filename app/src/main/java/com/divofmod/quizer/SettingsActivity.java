package com.divofmod.quizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.model.Config.Phone;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSharedPreferences, setting;
    SeekBar seekBar;
    Button setting_reset, setting_delete_user;
    int currentUser;

    public void onBackClick(final View view) {
        onBackPressed();
    }

    public void onExit(final View view) {
        openQuitDialog();
    }

    private void openQuitDialog() {
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.exit)
                .setTitle("Выход из приложения")
                .setMessage("Выйти из приложения?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        System.exit(0);
                    }
                })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                })

                .show();
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        currentUser = mSharedPreferences.getInt("CurrentUserId", 0);
        setting = getSharedPreferences("setting_" + currentUser, MODE_PRIVATE);


        setting_reset = findViewById(R.id.setting_reset);
        setting_delete_user = findViewById(R.id.setting_delete_user);

        setting_reset.setOnClickListener(this);
        setting_delete_user.setOnClickListener(this);

        String Audio = mSharedPreferences.getString("Quizzes_audio_" + currentUser,"0");
        String Quizzes = mSharedPreferences.getString("QuizzesRequest_" + currentUser,"0");

        if (!Quizzes.equals("0") && !Audio.equals("0")) {
            setting_delete_user.setEnabled(false);
            Log.i("log", "lock ");
            setting_delete_user.getContext().setTheme(R.style.ThemeOverlay_MyDarkButton);
        }
        else
        {
            Log.i("log", "unlock ");
        }

            initPhoneSpinner();
        textSizeQuestion();
        intervalBetweenAnswer();
        initDrawer();
    }

    @SuppressLint("NewApi")
    private void initDrawer() {
        final DrawerLayout drawer = findViewById(R.id.drawer);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        NavigationView navigation = findViewById(R.id.navigation);
        Button buttonDrawer = findViewById(R.id.openDrawer_toolbar);
        buttonDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.END);
            }
        });

        View view = navigation.getHeaderView(0);
        Button syns = view.findViewById(R.id.settings);
        syns.setEnabled(false);
        syns.setBackground(getResources().getDrawable(R.drawable.button_selector,getTheme()));
        view.findViewById(R.id.sync_button).setOnClickListener(clickHeader);
        view.findViewById(R.id.change_users).setOnClickListener(clickHeader);
    }

    private void intervalBetweenAnswer() {
        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(setting.getInt("interval_between_answer", 10));
        final TextView interval_example = findViewById(R.id.interval_example);
        interval_example.setText(String.valueOf(setting.getInt("interval_between_answer", 10)));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int interval = 10;


                i = ((int) Math.round(i / interval) * interval);
                interval_example.setText(String.valueOf(i));

                SharedPreferences.Editor editor = setting.edit();
                editor.putInt("interval_between_answer", i);
                editor.apply();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void textSizeQuestion() {
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.text_size_maket, getResources().getTextArray(R.array.text_size));
        final TextView size_example = findViewById(R.id.size_example);
        Spinner spinner = findViewById(R.id.switch_size);
        spinner.setAdapter(adapter);
        spinner.setSelection(setting.getInt("spinner_position", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = setting.edit();
                switch (i) {
                    case 0:
                        size_example.setTextSize(16);
                        editor.putFloat("scale", (float) 1.2);
                        editor.putInt("text_size", 16);
                        break;
                    case 1:
                        size_example.setTextSize(20);
                        editor.putFloat("scale", (float) 1.4);
                        editor.putInt("text_size", 20);
                        break;
                    case 2:
                        size_example.setTextSize(24);
                        editor.putFloat("scale", (float) 1.6);
                        editor.putInt("text_size", 24);
                        break;
                    case 3:
                        size_example.setTextSize(28);
                        editor.putFloat("scale", (float) 1.8);
                        editor.putInt("text_size", 28);
                        break;
                    case 4:
                        size_example.setTextSize(32);
                        editor.putFloat("scale", (float) 2);
                        editor.putInt("text_size", 32);
                        break;

                }
                editor.putInt("spinner_position", i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initPhoneSpinner() {
        final int selected = mSharedPreferences.getInt(Constants.Shared.NUMBER_POSITION, 0);

        final Spinner phoneSpinner = findViewById(R.id.spinner_phone);
        final List<String> list = new ArrayList<>();
        final List<Phone> phones = Utils.getConfig(this).getConfig().getProject_info().getReserve_channel().getPhone();

        for (final Phone phone : phones) {
            list.add(phone.getNumber());
        }

        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        phoneSpinner.setAdapter(dataAdapter);
        phoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                final SharedPreferences.Editor editor = mSharedPreferences.edit()
                        .putInt(Constants.Shared.NUMBER_POSITION, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });

        phoneSpinner.setSelection(selected);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_reset:
                finish();
                break;
            case R.id.setting_delete_user: {
                AuthActivity.deleteDirectory(new File(getFilesDir() + "/files/"));
                AuthActivity.deleteDirectory(new File(getFilesDir() + "/background/"));
                AuthActivity.deleteDirectory(new File(getFilesDir() + "/answerimages/"));
                deleteDatabase(mSharedPreferences.getString("name_file_" +  currentUser, ""));
                final SharedPreferences.Editor editor = mSharedPreferences.edit()
                        .putString("QuizzesRequest_" + currentUser,"0")
                        .putString("Sended_quizzes_" + currentUser,"0")
                        .putString("All_sended_quizzes_" + currentUser,"0")
                        .putString("Quizzes_audio_" + currentUser,"0")
                        .putString("Sended_audios_" + currentUser,"0")
                        .putString("Quizzes_audio_" + currentUser,"0")
                        .putString("All_sended_audios_" + currentUser,"0");
                editor.apply();

                String [] st = mSharedPreferences.getString("lastUserId","0;").split(";");
                int i =0;
                while (i<st.length)
                {

                    if (currentUser == i)
                    {
                        SharedPreferences.Editor edit = mSharedPreferences.edit();
                        edit.remove("login " + currentUser);
                        edit.remove("passw " + currentUser);
                        edit.remove("user_project_id " + currentUser);
                        edit.putString("lastUserId",mSharedPreferences.getString("lastUserId","0;").replaceFirst(st[i]+ ";",""));
                        edit.apply();

                    }

                i++;
                }

                startActivity(new Intent(SettingsActivity.this,AuthActivity.class));
                finish();
                 break;
            }
        }
    }

    View.OnClickListener clickHeader = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.sync_button:
                    startActivity(new Intent(SettingsActivity.this, SendQuizzesActivity.class));
                    finish();
                    break;
                case R.id.change_users:
                    startActivity(new Intent(SettingsActivity.this, AuthActivity.class));
                    finish();
                    break;
            }
        }
    };
}