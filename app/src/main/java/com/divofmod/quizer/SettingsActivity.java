package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.model.Config.Phone;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;

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

        initPhoneSpinner();
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