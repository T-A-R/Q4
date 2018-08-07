package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;

import java.io.File;
import java.util.ArrayList;

public class PassportBlockActivity extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase mSQLiteDatabase;
    SharedPreferences mSharedPreferences;
    PassportAdapter mPassportAdapter;
    ListView mListView;

    String[] mFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_block);

        mSharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        ArrayList<String[]> mConfig = DBReader.read(mSQLiteDatabase,
                "config",
                new String[]{"title", "value"});

        for (int i = 0; i < mConfig.size(); i++)
            if (mConfig.get(i)[0].equals("passport_block_list_fields")) {
                mFields = mConfig.get(i)[1].split(",");
                break;
            }
        ArrayList<Passport> passports = new ArrayList<>();
        for (String mField : mFields) {
            passports.add(new Passport(translateTitle(mField.split("\\|")[0]), mField.split("\\|")[1]));
        }

        mListView = (ListView) findViewById(R.id.passportListView);
        findViewById(R.id.passport_btn).setOnClickListener(this);
        mPassportAdapter = new PassportAdapter(this, passports);
        mListView.setAdapter(mPassportAdapter);
    }

    private Passport getModel(int position) {
        return mPassportAdapter.getItem(position);
    }

    private class PassportAdapter extends ArrayAdapter<Passport> {

        private LayoutInflater mInflater;

        PassportAdapter(Context context, ArrayList<Passport> list) {
            super(context, R.layout.passport_item, list);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder holder;
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.passport_item, parent, false);
                holder = new ViewHolder();
                holder.passportTitle = (TextView) row.findViewById(R.id.passport_text);
                holder.passportEditText = (EditText) row.findViewById(R.id.passport_edit);
                row.setTag(holder);
            } else {

                holder = (ViewHolder) row.getTag();
            }

            final Passport passport = getModel(position);

            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.passport_text:
                        case R.id.passport_edit:
                            AlertDialog.Builder builder = new AlertDialog.Builder(PassportBlockActivity.this);

                            View view = getLayoutInflater().inflate(R.layout.passport_dialog, null);

                            final EditText passportEdit = (EditText) view.findViewById(R.id.passport_edit);
                            passportEdit.setText(passport.getEditText());

                            AlertDialog d = builder.setIcon(R.drawable.edit)
                                    .setTitle(holder.passportTitle.getText())
                                    .setCancelable(false)
                                    .setView(view)

                                    .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                             passport.setEditText(passportEdit.getText().toString());
                                            if (!passport.getEditText().isEmpty())
                                                passport.setPassportError(null);
                                            notifyDataSetChanged();
                                        }
                                    })

                                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create();
                            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            d.show();
                            break;
                    }
                }

            };
            holder.passportTitle.setText(passport.getTitle());
            holder.passportTitle.setOnClickListener(onClick);
            holder.passportEditText.setError(passport.getPassportError());
            holder.passportEditText.setText(passport.getEditText());
            holder.passportEditText.setOnClickListener(onClick);
            return row;
        }

        class ViewHolder {
            TextView passportTitle;
            EditText passportEditText;
        }

    }

    private String translateTitle(String title) {
        String temp = "";
        switch (title) {
            case "region":
                temp = "Регион";
                break;
            case "city":
                temp = "Город";
                break;
            case "district":
                temp = "Округ";
                break;
            case "street":
                temp = "Улица";
                break;
            case "house":
                temp = "Дом";
                break;
            case "housing":
                temp = "Дом";
                break;
            case "flat":
                temp = "Квартира";
                break;
            case "lastname":
                temp = "Фамилия";
                break;
            case "firstname":
                temp = "Имя";
                break;
            case "middlename":
                temp = "Отчество";
                break;
            case "phone":
                temp = "Телефон";
                break;
            case "contact":
                temp = "Телефон";
                break;
            case "passport_data":
                temp = "Пасспорт";
                break;

        }
        return temp;
    }

    @Override
    public void onClick(View v) {
        if (mSharedPreferences.getString("passport_block_location", "").equals("1")) {
            startActivity(new Intent(this, QuestionnaireActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, ProjectActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.exit)
                .setTitle("Выход из приложения")
                .setMessage("Выйти из приложения?")

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
