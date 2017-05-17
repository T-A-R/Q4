package com.divofmod.quizer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.DataBase.DBReader;
import com.divofmod.quizer.QuizHelper.PhotoCamera;
import com.divofmod.quizer.QuizHelper.StopWatch;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ArrayList<String[]> statisticsQuestion;
    ArrayList<String[]> tableAnswer;


    ListView mListView;
    StatisticsAdapter mStatisticsAdapter;

    String mPhotoName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);

        mSQLiteDatabase = new DBHelper(StatisticsActivity.this,
                mSharedPreferences.getString("name_file", ""),
                new File(getFilesDir().toString() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file", "").substring(0, mSharedPreferences.getString("name_file", "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        statisticsQuestion = DBReader.read(mSQLiteDatabase,
                "statistic_data",
                new String[]{"questionnaire_id", "question_id", "answer_id", "percent_1", "percent_2", "percent_3"});
        tableAnswer = DBReader.read(mSQLiteDatabase,
                "answer",
                new String[]{"id", "title", "picture", "question_id", "next_question"});
        Statistics[] stats = new Statistics[statisticsQuestion.size()];

        // Min + (int)(Math.random() * ((Max - Min) + 1)) Random number pattern
        int randomNumber = 3 + (int) (Math.random() * ((5 - 3) + 1));
        for (int i = 0; i < stats.length; i++)
            stats[i] = new Statistics(setStat(statisticsQuestion.get(i)[2]), statisticsQuestion.get(i)[randomNumber]);

        setContentView(R.layout.activity_statistics);
        StopWatch.setStatisticsStart();
        mListView = (ListView) findViewById(R.id.listView);
        mStatisticsAdapter = new StatisticsAdapter(StatisticsActivity.this, stats);
        mListView.setAdapter(mStatisticsAdapter);

        findViewById(R.id.statistics_btn).setOnClickListener(this);


        mPhotoName = mSharedPreferences.getString("login", "") + "_" +
                mSharedPreferences.getString("user_project_id", "") + "_" +
                mSharedPreferences.getString("last_date_interview", "").replace(':', '-').replace(' ', '-');
        System.out.println(mPhotoName);

        new PhotoCamera(StatisticsActivity.this, (FrameLayout) findViewById(R.id.surfaceHolder), mPhotoName).createPhoto();

    }

    private String setStat(String question_id) {
        for (String[] temp : tableAnswer)
            if (temp[0].equals(question_id))
                return temp[1];
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.statistics_btn: {
                if (StopWatch.getStatisticsTime() >= 2500) {

                    String photoDate = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date());


                    mSQLiteDatabase.execSQL("create table if not exists photo_statistics_" + photoDate + "(names text);");
                    mSQLiteDatabase.execSQL("insert into photo_statistics_" + photoDate + "(names)" +
                            "values('" + mPhotoName + "')");

                    SharedPreferences.Editor editor = mSharedPreferences.edit()
                            .putString("Statistics_photo", mSharedPreferences.getString("Statistics_photo", "") + photoDate + ";");
                    editor.apply();

                } else
                    new File(getFilesDir(), "files/" + mPhotoName + ".jpg").delete();

                startActivity(new Intent(this, ProjectActivity.class));
                finish();
            }

            break;
        }

    }

    private Statistics getModel(int position) {
        return mStatisticsAdapter.getItem(position);
    }

    private class StatisticsAdapter extends ArrayAdapter<Statistics> {

        private LayoutInflater mInflater;

        StatisticsAdapter(Context context, Statistics[] list) {
            super(context, R.layout.statistics_item, list);
            mInflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder;
            View row = convertView;
            if (row == null) {

                row = mInflater.inflate(R.layout.statistics_item, parent, false);
                holder = new ViewHolder();
                holder.statisticsTitle = (TextView) row.findViewById(R.id.statistics_text);
                holder.statisticsPercent = (TextView) row.findViewById(R.id.statistics_numbers);
                holder.statisticsProgress = (ProgressBar) row.findViewById(R.id.statistics_progress);

                row.setTag(holder);
            } else {

                holder = (ViewHolder) row.getTag();
            }
            Statistics statistics = getModel(position);

            holder.statisticsTitle.setText(statistics.getTitle());

            holder.statisticsProgress.setMax(100);
            holder.statisticsProgress.setProgress(Integer.parseInt(statistics.getPercent()));
            holder.statisticsProgress.getProgressDrawable().setColorFilter(
                    statistics.getColor(), PorterDuff.Mode.SRC_ATOP);

            holder.statisticsPercent.setText(statistics.getPercent());

            return row;
        }

        class ViewHolder {
            TextView statisticsTitle;
            TextView statisticsPercent;
            ProgressBar statisticsProgress;
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SendQuizzesActivity.class));
        finish();
    }

}
