package com.divofmod.quizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;

import java.io.File;
import java.util.ArrayList;

public class QuotaActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ListView mListView;
    QuotaAdapter mQuotaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(QuotaActivity.this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        ArrayList<String[]> tableQuota = DBReader.read(mSQLiteDatabase,
                "answer",
                new String[]{"id", "title"});

        String quota = mSharedPreferences.getString("quota", "1");
        String[] quotasTemp = quota.substring(1, quota.length() - 1).split(";");
        for (String re : quotasTemp) {
            System.out.println(re);
            if (re.equals("1")) {
                startActivity(new Intent(QuotaActivity.this, QuestionsActivity.class));
                finish();
                return;
            }
        }

        quota = quota.substring(1, quota.length() - 1).replaceAll(",", " >> ");

        for (String[] tQ : tableQuota)
            quota = quota.replaceAll(tQ[0], tQ[1]);

        quotasTemp = quota.split(";");
        ArrayList<Quota> quotas = new ArrayList<>();
        for (String aQuotasTemp : quotasTemp)
            if (!aQuotasTemp.split("\\|")[2].equals("0"))
                quotas.add(new Quota(aQuotasTemp.split("\\|")[0],
                        aQuotasTemp.split("\\|")[1],
                        aQuotasTemp.split("\\|")[2]));

        setContentView(R.layout.activity_quota);

        findViewById(R.id.start_button).setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listView);
        mQuotaAdapter = new QuotaAdapter(QuotaActivity.this, quotas);
        mListView.setAdapter(mQuotaAdapter);

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(QuotaActivity.this, QuestionsActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSQLiteDatabase.close();
    }

    private Quota getModel(int position) {
        return mQuotaAdapter.getItem(position);
    }

    private class QuotaAdapter extends ArrayAdapter<Quota> {

        private LayoutInflater mInflater;

        QuotaAdapter(Context context, ArrayList<Quota> list) {
            super(context, R.layout.quota_item, list);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {
            QuotaAdapter.ViewHolder holder;
            View row = convertView;
            if (row == null) {

                row = mInflater.inflate(R.layout.quota_item, parent, false);
                holder = new QuotaAdapter.ViewHolder();
                holder.quotaSequence = (TextView) row.findViewById(R.id.quota_sequence);
                holder.quotaQuantity = (TextView) row.findViewById(R.id.quota_quantity);
                holder.quotaProgress = (ProgressBar) row.findViewById(R.id.quota_progress);

                row.setTag(holder);
            } else

                holder = (QuotaAdapter.ViewHolder) row.getTag();

            Quota quota = getModel(position);

            holder.quotaSequence.setText(quota.getSequence());

            holder.quotaProgress.setMax(Integer.parseInt(quota.getQuantity2()));
            holder.quotaProgress.setProgress(Integer.parseInt(quota.getQuantity1()));

            if (Integer.parseInt(quota.getQuantity1()) >= Integer.parseInt(quota.getQuantity2()))
                holder.quotaProgress.getProgressDrawable().setColorFilter(
                        ContextCompat.getColor(QuotaActivity.this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            else
                holder.quotaProgress.getProgressDrawable().setColorFilter(
                        ContextCompat.getColor(QuotaActivity.this, R.color.colorRed), PorterDuff.Mode.SRC_ATOP);

            holder.quotaQuantity.setText(quota.getQuantity1() + "/" + quota.getQuantity2());

            return row;
        }

        class ViewHolder {
            TextView quotaSequence;
            TextView quotaQuantity;
            ProgressBar quotaProgress;
        }
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                QuotaActivity.this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.ico)
                .setTitle("Выход из опроса")
                .setMessage("Выйти из опроса?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(QuotaActivity.this, ProjectActivity.class));
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
