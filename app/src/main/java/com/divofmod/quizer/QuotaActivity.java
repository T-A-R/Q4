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
import com.divofmod.quizer.Utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class QuotaActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ListView mListView;
    int currentUser;
    QuotaAdapter mQuotaAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);
        currentUser = mSharedPreferences.getInt("CurrentUserId",0);


        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file_" + currentUser , ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        final ArrayList<String[]> tableQuota = Utils.getAnswers(this);

//        String quota = mSharedPreferences.getString("quota", "1");
//        String[] quotasTemp = quota.substring(1, quota.length() - 1).split(";");

//         TODO: 8/8/18 SIMPLE VERSION WITHOUT QUOTA
//        for (final String re : quotasTemp) {
//            System.out.println(re);
//            if (re.equals("1")) {
                startActivity(new Intent(this, QuestionsActivity.class));
                finish();
                return;
//            }
//        }

//        quota = quota.substring(1, quota.length() - 1).replaceAll(",", " >> ");
//
//        for (final String[] tQ : tableQuota)
//            quota = quota.replaceAll(tQ[0], tQ[1]);
//
//        quotasTemp = quota.split(";");
//        final ArrayList<Quota> quotas = new ArrayList<>();
//        for (final String aQuotasTemp : quotasTemp)
//            // TODO: 8/8/18 WTF???
//            if (!aQuotasTemp.split("\\|")[2].equals("0"))
//                quotas.add(new Quota(aQuotasTemp.split("\\|")[0],
//                        aQuotasTemp.split("\\|")[1],
//                        aQuotasTemp.split("\\|")[2]));
//
//        setContentView(R.layout.activity_quota);
//
//        findViewById(R.id.start_button).setOnClickListener(this);
//
//        mListView = (ListView) findViewById(R.id.listView);
//        mQuotaAdapter = new QuotaAdapter(this, quotas);
//        mListView.setAdapter(mQuotaAdapter);

    }

    @Override
    public void onClick(final View v) {
        startActivity(new Intent(this, QuestionsActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSQLiteDatabase.close();
    }

    private Quota getModel(final int position) {
        return mQuotaAdapter.getItem(position);
    }

    private class QuotaAdapter extends ArrayAdapter<Quota> {

        private LayoutInflater mInflater;

        QuotaAdapter(final Context context, final ArrayList<Quota> list) {
            super(context, R.layout.quota_item, list);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(final int position, final View convertView,
                            final ViewGroup parent) {
            final QuotaAdapter.ViewHolder holder;
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

            final Quota quota = getModel(position);

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
        final AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                this);
        quitDialog.setCancelable(true)
                .setIcon(R.drawable.ico)
                .setTitle("Выход из опроса")
                .setMessage("Выйти из опроса?")

                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        startActivity(new Intent(QuotaActivity.this, ProjectActivity.class));
                        finish();
                    }
                })

                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                })

                .show();
    }
}
