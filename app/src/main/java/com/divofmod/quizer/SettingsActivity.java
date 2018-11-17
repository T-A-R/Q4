package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.model.Config.Phone;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences , setting;
    SeekBar seekBar;

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
        setting = getSharedPreferences("setting_" + mSharedPreferences.getInt("CurrentUserId",0),MODE_PRIVATE);

        initPhoneSpinner();
        textSizeQuestion();
        intervalBetweenAnswer();
    }

    private void intervalBetweenAnswer() {
        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(setting.getInt("interval_between_answer",10));
        final TextView interval_example = findViewById(R.id.interval_example);
        interval_example.setText(String.valueOf(setting.getInt("interval_between_answer",10)));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int interval = 10;


                i = ((int) Math.round(i/interval)* interval);
                interval_example.setText(String.valueOf(i));

                SharedPreferences.Editor editor = setting.edit();
                editor.putInt("interval_between_answer",i);
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
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.text_size_maket,getResources().getTextArray(R.array.text_size));
        final TextView size_example = findViewById(R.id.size_example);
        Spinner spinner = findViewById(R.id.switch_size);
        spinner.setAdapter(adapter);
        spinner.setSelection(setting.getInt("spinner_position",0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = setting.edit();
                switch (i)
                {
                    case 0:
                        size_example.setTextSize(16);
                        editor.putFloat("scale",(float) 1.2);
                        editor.putInt("text_size",16);
                        break;
                    case 1:
                        size_example.setTextSize(20);
                        editor.putFloat("scale",(float) 1.4);
                        editor.putInt("text_size",20);
                        break;
                    case 2:
                        size_example.setTextSize(24);
                        editor.putFloat("scale",(float) 1.6);
                        editor.putInt("text_size",24);
                        break;
                    case 3:
                        size_example.setTextSize(28);
                        editor.putFloat("scale",(float) 1.8);
                        editor.putInt("text_size",28);
                        break;
                    case 4:
                        size_example.setTextSize(32);
                        editor.putFloat("scale",(float) 2);
                        editor.putInt("text_size",32);
                        break;

                }
                editor.putInt("spinner_position",i);
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
}