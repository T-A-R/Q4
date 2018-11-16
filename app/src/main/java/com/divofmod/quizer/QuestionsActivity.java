package com.divofmod.quizer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.divofmod.quizer.Constants.Constants;
import com.divofmod.quizer.DataBase.DBHelper;
import com.divofmod.quizer.Interfaces.ScrollViewListener;
import com.divofmod.quizer.QuizHelper.Audio;
import com.divofmod.quizer.QuizHelper.AudioRecorder;
import com.divofmod.quizer.QuizHelper.PhotoCamera;
import com.divofmod.quizer.QuizHelper.StopWatch;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.Utils.Utils;
import com.divofmod.quizer.model.API.QuizzesRequest;
import com.divofmod.quizer.model.Config.QuestionsMatchesField;
import com.divofmod.quizer.model.Config.StagesField;
import com.divofmod.quizer.model.Sms.SmsAnswerModel;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;
import com.divofmod.quizer.model.Sms.SmsFullAnswerModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class QuestionsActivity extends AppCompatActivity implements View.OnClickListener, ScrollViewListener, LocationListener {

    public static final String TAG = "QuestionsActivity";

    String mCurrentMessage;
    String mCurrentPhoneNumber;
    String mDateInterview;

    SharedPreferences mSharedPreferences;
    SQLiteDatabase mSQLiteDatabase;

    ArrayList<String[]> mTableQuestion;
    ArrayList<String[]> mTableAnswer;
    ArrayList<String[]> mTableSelectiveQuestion;
    ArrayList<String[]> mTableSelectiveAnswer;
    ArrayList<String[]> mConfig;

    HashMap<String, String> mConfigMap;

    String mUserId;

    SmsFullAnswerModel mSmsFullAnswerModel;
    ArrayList<Map<String, String[]>> answerSequenceInsideQuestions;
    ArrayList<String> goneNumbers;
    int num = -1;
    ArrayList<Integer> mQuestionSequence;

    int number = 1;
    String question_id;

    ArrayList<String> tableSequence;
    ArrayList<TableQuestion> tableQuestions;

    AnswerAdapter adapter;
    SelectiveQuestionAdapter selectiveQuestionAdapter;
    SelectiveAnswerAdapter selectiveAnswerAdapter;
    ListView selectiveAnswersListView;
    ArrayList<SelectiveAnswer> selectiveAnswersList;
    ListView listView;

    String[] currentQuestion = new String[1];
    ArrayList<String[]> currentAnswers;

    FrameLayout frameLayout;
    String photoName;
    int photoNumber = -1;

    Audio mAudio;

    LocationManager locationManager;

    TextView mQuestionTitle;
    ImageView questionPicture;

    ArrayList<TableQuestion> mTableQuestions;

    ObservableScrollView scrollView1;
    ObservableScrollView scrollView2;
    static boolean interceptScroll = true;
    int currentUser;

    public static int PERMISSION_SEND_SMS = 1001;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSmsFullAnswerModel = new SmsFullAnswerModel();
        mDateInterview = Utils.getCurrentTitme() + "";

        tableSequence = new ArrayList<>();
        answerSequenceInsideQuestions = new ArrayList<>();
        goneNumbers = new ArrayList<>();
        mQuestionSequence = new ArrayList<>();
        mQuestionSequence.add(number);

        mSharedPreferences = getSharedPreferences("data",
                Context.MODE_PRIVATE);
        currentUser = currentUser = mSharedPreferences.getInt("CurrentUserId",0);

        mSQLiteDatabase = new DBHelper(this,
                mSharedPreferences.getString("name_file_" + currentUser, ""),
                new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4)),
                getString(R.string.sql_file_name),
                getString(R.string.old_sql_file_name)).getWritableDatabase();

        mTableQuestion = Utils.getQuestions(this);

        mTableAnswer = Utils.getAnswers(this);

        mTableSelectiveQuestion = Utils.getSelectiveQuestions(this);

        mTableSelectiveAnswer = Utils.getSelectiveAnswers(this);

        mConfig = Utils.getConfigValues(this);

        mUserId = Utils.getQuestionnaireId(this);

        mConfigMap = new HashMap<>();
        for (int i = 0; i < mConfig.size(); i++) {
            mConfigMap.put(mConfig.get(i)[0], mConfig.get(i)[1]);
        }

        if (mConfigMap.get("audio") != null && mConfigMap.get("audio").equals("1")) {
            mAudio = new Audio(mConfigMap.get("audio_record_questions").split(","),
                    mConfigMap.get("audio_record_limit_time"),
                    mConfigMap.get("audio_speex_sample_rate"));
        }

        if (mConfigMap.get("photo_questionnaire") != null && mConfigMap.get("photo_questionnaire").equals("1")) {
            photoNumber = new Random().nextInt(Integer.parseInt(mConfigMap.get("count_questions_min"))) + 1;
        }

        String rsa;
        System.out.println(mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, ""));
        System.out.println(mSharedPreferences.getString("url", ""));
        try {
            final SharedPreferences.Editor editor = mSharedPreferences.edit();
            rsa = RSA.decrypt(mConfigMap.get("server"), this);
            editor.putString(Constants.Shared.LOGIN_ADMIN, rsa.split("\\|")[0]);
            editor.putString("url", rsa.split("\\|")[1]);
            editor.apply();
            System.out.println(mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, ""));
            System.out.println(mSharedPreferences.getString("url", ""));
        } catch (final Exception e) {
            rsa = mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, "") + "|" + mSharedPreferences.getString("url", "");
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        tableQuestions = new ArrayList<>();
        ArrayList<String[]> tableAnswers;
        for (int i = 0; i < mTableQuestion.size(); i++) {
            if (!mTableQuestion.get(i)[9].equals("0")) {
                tableAnswers = new ArrayList<>();
                for (int j = 0; j < mTableAnswer.size(); j++) {
                    if (mTableAnswer.get(j)[3].equals(mTableQuestion.get(i)[0])) {
                        tableAnswers.add(mTableAnswer.get(j));
                    }
                }

                tableQuestions.add(
                        new TableQuestion(
                                mTableQuestion.get(i)[0],
                                mTableQuestion.get(i)[1],
                                mTableQuestion.get(i)[2],
                                mTableQuestion.get(i)[9],
                                tableAnswers)
                );
            }
        }

        setContentView(R.layout.activity_questions);

        scrollView1 = (ObservableScrollView) findViewById(R.id.scrollview1);
        scrollView1.setScrollViewListener(this);
        scrollView2 = (ObservableScrollView) findViewById(R.id.scrollview2);
        scrollView2.setScrollViewListener(this);

        findViewById(R.id.activity_questions).setOnTouchListener(new OnSwipeTouchListener(QuestionsActivity.this) {

            public void onSwipeRight() {
                onClick(findViewById(R.id.question_previous_button));
            }

            public void onSwipeLeft() {
                onClick(findViewById(R.id.question_next_button));
            }

        });
        findViewById(R.id.question_next_button).setOnClickListener(this);
        findViewById(R.id.question_previous_button).setOnClickListener(this);
        findViewById(R.id.question_exit_button).setOnClickListener(this);

        frameLayout = (FrameLayout) findViewById(R.id.surfaceHolder);

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnTouchListener(new OnSwipeTouchListener(QuestionsActivity.this) {

            public void onSwipeRight() {
                onClick(findViewById(R.id.question_previous_button));
            }

            public void onSwipeLeft() {
                onClick(findViewById(R.id.question_next_button));
            }

        });



        mQuestionTitle = (TextView) findViewById(R.id.question_title);
        questionPicture = (ImageView) findViewById(R.id.question_picture);

        num++;
        nextQuestion();

        if (mAudio != null) {
            if (mAudio.getAudioRecordQuestions().containsKey(0)) {
                AudioRecorder.Start(this, mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, "") + "_" +
                        mSharedPreferences.getString("login" + currentUser, "") + "_" +
                        mSharedPreferences.getString("user_project_id" + currentUser, "") + "_" +
                        0 + "_" +
                        mDateInterview.replace(':', '-').replace(' ', '-'), mAudio.getAudioRecordLimitTime(), mAudio.getAudioSampleRate());
            }
        }

        StopWatch.setGlobalStart();
    }

    private void setCurrentQuestion() {

        for (final String[] temp : mTableQuestion) {
            if (temp[1].equals(Integer.toString(number))) {
                currentQuestion = temp;
                question_id = temp[0];
                return;
            }
        }
    }

    private void setCurrentAnswers() {
        currentAnswers = new ArrayList<>();
        for (final String[] temp : mTableAnswer) {
            if (temp[3].equals(question_id)) {
                currentAnswers.add(temp);
            }
        }
    }

    private String showQuestion(final String number) {
        final ArrayList<String[]> infoForQuestion = new ArrayList<>();
        String finalQ = "";
        String id = "";
        for (final String[] tempQ : mTableQuestion) {
            if (tempQ[1].equals(number)) {
                id = tempQ[0];
            }
        }

        for (final String[] temp : mTableAnswer) {
            if (temp[3].equals(id)) {
                infoForQuestion.add(temp);
            }
        }

        for (int i = 0; i < infoForQuestion.size(); i++) {
            for (int j = 0; j < answerSequenceInsideQuestions.size(); j++) {
                if (answerSequenceInsideQuestions.get(j).containsKey(infoForQuestion.get(i)[0])) {
                    if (answerSequenceInsideQuestions.get(j).get(infoForQuestion.get(i)[0])[0].equals(infoForQuestion.get(i)[0])) {
                        finalQ += " " + infoForQuestion.get(i)[1] + ",";
                    }
                    break;
                }
            }
        }

        return finalQ.substring(1, finalQ.length() - 1);
    }

    private String showAnswer(final String number, final String numbers) {
        final ArrayList<String[]> infoForAnswer = new ArrayList<>();
        String finalAnswer = "";
        String id = "";
        for (final String[] tempQ : mTableQuestion) {
            if (tempQ[1].equals(number)) {
                id = tempQ[0];
            }
        }

        for (final String[] temp : mTableAnswer) {
            if (temp[3].equals(id)) {
                infoForAnswer.add(temp);
            }
        }

        for (int i = 0; i < infoForAnswer.size(); i++) {


            if (Integer.parseInt(numbers.substring(0,1)) == i + 1) {
                finalAnswer = infoForAnswer.get(i)[1];
            }
        }
        return finalAnswer;
    }

    public void searchItem(final String textToSearch) {
        for (int i = 0; i < selectiveAnswersList.size(); i++) {
            if (!selectiveAnswersList.get(i).getTitle().toLowerCase().contains(textToSearch.toLowerCase())) {
                selectiveAnswersList.remove(selectiveAnswersList.get(i));
                i--;
            }
        }
        selectiveAnswerAdapter.notifyDataSetChanged();
    }

    public void initList(final int position) {
        selectiveAnswersList = new ArrayList<>();
        for (int i = 0; i < getModelSelectiveQuestion(position).getSelectiveAnswers().size(); i++) {
            if (getModelSelectiveQuestion(position).getSelectiveAnswers().get(i).getVisibility()) {
                selectiveAnswersList.add(getModelSelectiveQuestion(position).getSelectiveAnswers().get(i));
            }
        }

        selectiveAnswerAdapter = new SelectiveAnswerAdapter(this, selectiveAnswersList);
        selectiveAnswersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectiveAnswersListView.setAdapter(selectiveAnswerAdapter);
    }

    private void nextQuestion() {
        startAudio();
//        createPhoto();
        setCurrentQuestion();
        final String sForRe;
        if (currentQuestion[2].contains("#")) {
            sForRe = currentQuestion[2].substring(currentQuestion[2].indexOf('#'), currentQuestion[2].lastIndexOf('#') + 1);
            final String question = currentQuestion[2].substring(currentQuestion[2].indexOf('#') + 1, currentQuestion[2].indexOf('|'));
            mQuestionTitle.setText(currentQuestion[1] + ". " + currentQuestion[2].replace(sForRe, showQuestion(question)));
        } else {
            mQuestionTitle.setText(currentQuestion[1] + ". " + currentQuestion[2]);
        }

        if (!currentQuestion[5].equals("")) {
            questionPicture.setVisibility(View.VISIBLE);
            questionPicture.setImageURI(Uri.fromFile(new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4) + getString(R.string.separator_path) + "answerimages", currentQuestion[5])));
        } else {
            questionPicture.setVisibility(View.GONE);
        }

        if (currentQuestion[10].equals("0")) {
            if (!currentQuestion[9].equals("0")) {
                final ArrayList<TableQuestion> tempTableQuestions = new ArrayList<>();
                mTableQuestions = tempTableQuestions;

                for (int i = 0; i < tableQuestions.size(); i++) {
                    for (int j = 0; j < tableSequence.size(); j++) {
                        if (tableQuestions.get(i).getTableId() == Integer.parseInt(currentQuestion[9])) {
                            if (tableSequence.get(j).equals(Integer.toString(tableQuestions.get(i).getNumber()))) {
                                tempTableQuestions.add(tableQuestions.get(i));
                            }
                        }
                    }
                }

                if (tempTableQuestions.size() == 0) {
                    for (int i = 0; i < tableQuestions.size(); i++) {
                        if (tableQuestions.get(i).getTableId() == Integer.parseInt(currentQuestion[9])) {
                            tempTableQuestions.add(tableQuestions.get(i));
                        }
                    }
                }

                listView.setVisibility(View.GONE);
                mQuestionTitle.setVisibility(View.GONE);
                questionPicture.setVisibility(View.GONE);
                findViewById(R.id.question_underline).setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                findViewById(R.id.question_linear_with_table).setVisibility(View.VISIBLE);

                final TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TableRow row = new TableRow(this);

                final TextView tableTitle = (TextView) findViewById(R.id.question_table_title);
                tableTitle.setText(TableHelper.doShifts(tempTableQuestions.get(0).getTitle().contains("*")
                        ? tempTableQuestions.get(0).getTitle().split("\\*")[0] : "", this));

                //header (fixed vertically)
                final TableLayout header = (TableLayout) findViewById(R.id.table_header);
                header.removeAllViews();
                header.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryTextMaterialLight));
                row.setLayoutParams(wrapWrapTableRowParams);
                for (int i = 0; i < tempTableQuestions.get(0).getTableAnswers().size(); i++) {
                    final FrameLayout frameLayout = new FrameLayout(this);
                    frameLayout.setPadding(1, 0, 1, 1);
                    final TextView textView = TableHelper.makeTableRowWithText(tempTableQuestions.get(0).getTableAnswers().get(i).getTitle(), this);
                    textView.setId(i);
                    textView.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundMaterialLight));
                    tempTableQuestions.get(0).getTableAnswers().get(i).setTextViewId(textView.getId());

                    frameLayout.addView(textView);
                    row.addView(frameLayout);
                }
                header.addView(row);
                //header (fixed horizontally)
                final TableLayout fixedColumn = (TableLayout) findViewById(R.id.fixed_column);
                fixedColumn.removeAllViews();
                fixedColumn.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryTextMaterialLight));

                //rest of the table (within a scroll view)
                final TableLayout scrollablePart = (TableLayout) findViewById(R.id.scrollable_part);
                scrollablePart.removeAllViews();
                scrollablePart.setBackgroundColor(ContextCompat.getColor(this, R.color.secondaryTextMaterialLight));

                //Fixed part
                row = new TableRow(this);
                row.setLayoutParams(wrapWrapTableRowParams);

                for (int i = 0; i < tempTableQuestions.size(); i++) {
                    final FrameLayout fixedFrameLayout = new FrameLayout(this);
                    fixedFrameLayout.setPadding(0, 1, 1, 1);
                    final TextView fixedView = TableHelper.makeTableRowWithText(tempTableQuestions.get(i).getTitle().contains("*") ? tempTableQuestions.get(i).getTitle().split("\\*")[1] : tempTableQuestions.get(i).getTitle(), this);
                    fixedView.setId(i);
                    fixedView.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundMaterialLight));
                    tempTableQuestions.get(i).setTextViewId(fixedView.getId());

                    fixedFrameLayout.addView(fixedView);
                    fixedColumn.addView(fixedFrameLayout);
                    //Scrollable part
                    row = new TableRow(this);
                    row.setLayoutParams(wrapWrapTableRowParams);

                    for (int j = 0; j < tempTableQuestions.get(i).getTableAnswers().size(); j++) {
                        final FrameLayout frameLayout = new FrameLayout(this);
                        frameLayout.setPadding(1, 1, 1, 1);
                        final RelativeLayout relativeLayout = new RelativeLayout(this);
                        relativeLayout.setId(tempTableQuestions.get(i).getTableAnswers().get(j).getId() * 100);
                        relativeLayout.setGravity(Gravity.CENTER);
                        relativeLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundMaterialLight));
                        final RadioButton radioButton = new RadioButton(this);
                        radioButton.setId(tempTableQuestions.get(i).getTableAnswers().get(j).getId());
                        radioButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(final View v) {
                                if (answerSequenceInsideQuestions.size() > 0) {
                                    for (int i = answerSequenceInsideQuestions.size() - 1; i >= num; i--) {
                                        answerSequenceInsideQuestions.remove(i);
                                        goneNumbers.remove(i);
                                    }
                                }
                                final RadioButton clickedRadioButton = (RadioButton) v;
                                if (clickedRadioButton.isChecked()) {
                                    for (int i = 0; i < tempTableQuestions.size(); i++) {
                                        for (int j = 0; j < tempTableQuestions.get(i).getTableAnswers().size(); j++) {
                                            if (clickedRadioButton.getId() == tempTableQuestions.get(i).getTableAnswers().get(j).getId()) {
                                                tempTableQuestions.get(i).getTableAnswers().get(j).setChecked(true);
                                                for (int a = 0; a < tempTableQuestions.get(i).getTableAnswers().size(); a++) {
                                                    if (clickedRadioButton.getId() != tempTableQuestions.get(i).getTableAnswers().get(a).getId()) {
                                                        tempTableQuestions.get(i).getTableAnswers().get(a).setChecked(false);
                                                        final RadioButton radioButton = (RadioButton) scrollablePart.findViewById(tempTableQuestions.get(i).getTableAnswers().get(a).getId());
                                                        radioButton.setChecked(false);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        relativeLayout.addView(radioButton);
                        frameLayout.addView(relativeLayout);
                        row.addView(frameLayout);
                    }
                    scrollablePart.addView(row);
                }
                final int[] headerWidth = new int[tempTableQuestions.get(0).getTableAnswers().size()];
                final int[] headerHeight = new int[tempTableQuestions.get(0).getTableAnswers().size()];
                final int[] columnHeight = new int[tempTableQuestions.size()];
                final int[] columnWidth = new int[tempTableQuestions.size()];
                try {
                    Thread.sleep(600);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {

                    public void run() {

                        for (int i = 0; i < headerWidth.length; i++) {
                            if (header.findViewById(tempTableQuestions.get(0).getTableAnswers().get(i).getTextViewId()) != null) {
                                headerWidth[i] = header.findViewById(tempTableQuestions.get(0).getTableAnswers().get(i).getTextViewId()).getMeasuredWidth();
                            }
                            headerHeight[i] = header.findViewById(tempTableQuestions.get(0).getTableAnswers().get(i).getTextViewId()).getMeasuredHeight();

                            if (headerWidth[i] == 0 || headerHeight[i] == 0) {
                                i--;
                            }
                        }
                        for (int i = 0; i < columnHeight.length; i++) {
                            if (fixedColumn.findViewById(tempTableQuestions.get(i).getTextViewId()) != null) {
                                columnHeight[i] = fixedColumn.findViewById(tempTableQuestions.get(i).getTextViewId()).getMeasuredHeight();
                            }
                            columnWidth[i] = fixedColumn.findViewById(tempTableQuestions.get(i).getTextViewId()).getMeasuredWidth();

                            if (columnHeight[i] == 0 || columnWidth[i] == 0) {
                                i--;
                            }
                        }

                        runOnUiThread(new Runnable() {

                                          @Override
                                          public void run() {
                                              for (int i = 0; i < headerHeight.length; i++) {
                                                  if (findViewById(tempTableQuestions.get(0).getTableAnswers().get(i).getTextViewId()) != null && scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()) != null) {
                                                      header.findViewById(tempTableQuestions.get(0).getTableAnswers().get(i).getTextViewId()).setLayoutParams(new FrameLayout.LayoutParams(headerWidth[i] >= scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredWidth() ?
                                                              headerWidth[i] : scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredWidth(), TableHelper.getMaxValue(headerHeight)));
                                                  }

                                              }

                                              for (int j = 0; j < headerWidth.length; j++) {
                                                  if (findViewById(tempTableQuestions.get(0).getTableAnswers().get(j).getId() * 100) != null) {
                                                      scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(j).getId() * 100).setLayoutParams(new FrameLayout.LayoutParams(headerWidth[j] >= scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredWidth() ?
                                                              headerWidth[j] : scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredWidth(), TableHelper.getMaxValue(columnHeight) >= scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredHeight() ?
                                                              TableHelper.getMaxValue(columnHeight) : scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredHeight()));
                                                  }
                                              }

                                              for (int i = 0; i < columnHeight.length; i++) {
                                                  if (findViewById(tempTableQuestions.get(i).getTextViewId()) != null && scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()) != null) {
                                                      fixedColumn.findViewById(tempTableQuestions.get(i).getTextViewId()).setLayoutParams(new FrameLayout.
                                                              LayoutParams(tableTitle.getMeasuredWidth() > TableHelper.getMaxValue(columnWidth)
                                                              ? tableTitle.getMeasuredWidth() : TableHelper.getMaxValue(columnWidth)
                                                              , TableHelper.getMaxValue(columnHeight) >= scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredHeight() ?
                                                              TableHelper.getMaxValue(columnHeight) : scrollablePart.findViewById(tempTableQuestions.get(0).getTableAnswers().get(0).getId()).getMeasuredHeight()));
                                                  }
                                              }
                                              if (findViewById(R.id.question_table_title) != null) {
                                                  tableTitle.findViewById(R.id.question_table_title).setLayoutParams(new FrameLayout.LayoutParams(
                                                          tableTitle.getMeasuredWidth() > TableHelper.getMaxValue(columnWidth)
                                                                  ? tableTitle.getMeasuredWidth() : TableHelper.getMaxValue(columnWidth)
                                                          , TableHelper.getMaxValue(headerHeight)));
                                              }
                                          }
                                      }
                        );

                    }
                }).start();

                StopWatch.setStart();

            } else {
                setCurrentAnswers();
                final ArrayList<Answer> answers = new ArrayList<>();
                String answer = "";
                String answerNum = "";
                for (int i = 0; i < currentAnswers.size(); i++) {
                    if (currentAnswers.get(i)[1].contains("#")) {
                        answer = currentAnswers.get(i)[1].substring(currentAnswers.get(i)[1].indexOf('#') + 1, currentAnswers.get(i)[1].indexOf('|'));
                        Log.i(TAG, "currentAnswers: "  + currentAnswers.get(i)[1]);
                        answerNum = currentAnswers.get(i)[1].substring(currentAnswers.get(i)[1].indexOf('|') + 1, currentAnswers.get(i)[1].lastIndexOf('$'));
                    }
                    answers.add(new Answer(
                            currentAnswers.get(i)[0],
                            currentAnswers.get(i)[1].contains("#") ? showAnswer(answer, answerNum) :
                                    currentAnswers.get(i)[1],
                            currentAnswers.get(i)[2].equals("") ? null : new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4) + getString(R.string.separator_path) + "answerimages", currentAnswers.get(i)[2]),
                            currentQuestion[3],
                            currentQuestion[4],
                            currentAnswers.get(i)[5],
                            currentAnswers.get(i)[4],
                            currentAnswers.get(i)[6],
                            currentAnswers.get(i)[7]
                    ));
                }

                Collections.sort(answers, new Comparator<Answer>() {

                    public int compare(final Answer o1, final Answer o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });
                adapter = new AnswerAdapter(this, answers);
                findViewById(R.id.question_linear_with_table).setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mQuestionTitle.setVisibility(View.VISIBLE);
                findViewById(R.id.question_underline).setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
                StopWatch.setStart();
            }
        } else {
            ArrayList<String[]> currentSelectiveAnswers;

            final ArrayList<SelectiveQuestion> selectiveQuestions = new ArrayList<>();
            for (int i = 0; i < mTableSelectiveQuestion.size(); i++) {
                currentSelectiveAnswers = new ArrayList<>();
                for (int j = 0; j < mTableSelectiveAnswer.size(); j++) {
                    if (mTableSelectiveAnswer.get(j)[5].equals(mTableSelectiveQuestion.get(i)[0])) {
                        currentSelectiveAnswers.add(mTableSelectiveAnswer.get(j));
                    }
                }

                if (mTableSelectiveQuestion.get(i)[3].equals(currentQuestion[0])) {
                    selectiveQuestions.add(new SelectiveQuestion(
                            mTableSelectiveQuestion.get(i)[0],
                            mTableSelectiveQuestion.get(i)[2],
                            currentSelectiveAnswers,
                            currentQuestion[10]));
                }
            }

            Collections.sort(selectiveQuestions, new Comparator<SelectiveQuestion>() {

                public int compare(final SelectiveQuestion o1, final SelectiveQuestion o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            selectiveQuestionAdapter = new SelectiveQuestionAdapter(this, selectiveQuestions);
            findViewById(R.id.question_linear_with_table).setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mQuestionTitle.setVisibility(View.VISIBLE);
            findViewById(R.id.question_underline).setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(selectiveQuestionAdapter);
            StopWatch.setStart();
            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(final AdapterView<?> parent, final View itemClicked, final int position,
                                                final long id) {
                            final SelectiveQuestion selectiveQuestion = getModelSelectiveQuestion(position);

                            final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);

                            final View view = getLayoutInflater().inflate(R.layout.selective_answers_dialog, null);

                            selectiveAnswersListView = (ListView) view.findViewById(R.id.selective_answers);
                            initList(position);

                            final EditText selectiveAnswersSearch = (EditText) view.findViewById(R.id.selective_answers_search);

                            selectiveAnswersSearch.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                                }

                                @Override
                                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                                    if (s.toString().equals("")) {
                                        // reset listview
                                        initList(position);
                                    } else {
                                        // perform search
                                        initList(position);
                                        searchItem(s.toString());
                                    }
                                }

                                @Override
                                public void afterTextChanged(final Editable s) {
                                }

                            });

                            builder.setTitle(selectiveQuestion.getTitle())
                                    .setIcon(R.drawable.list)
                                    .setCancelable(false)
                                    .setView(view)
                                    .setPositiveButton("Ок",
                                            new DialogInterface.OnClickListener() {

                                                public void onClick(final DialogInterface dialog, final int id) {
                                                    for (int i = 0; i < listView.getAdapter().getCount() - 1; i++) {
                                                        for (int j = 0; j < getModelSelectiveQuestion(i).getSelectiveAnswers().size(); j++) {
                                                            for (int b = 0; b < getModelSelectiveQuestion(i + 1).getSelectiveAnswers().size(); b++) {
                                                                if (getModelSelectiveQuestion(i).getSelectiveAnswers().get(j).getNum() == getModelSelectiveQuestion(i + 1).getSelectiveAnswers().get(b).getParentNum()) {
                                                                    getModelSelectiveQuestion(i + 1).getSelectiveAnswers().get(b).setVisibility(getModelSelectiveQuestion(i).getSelectiveAnswers().get(j).getCheck());
                                                                    if (!getModelSelectiveQuestion(i + 1).getSelectiveAnswers().get(b).getVisibility()) {
                                                                        getModelSelectiveQuestion(i + 1).getSelectiveAnswers().get(b).setCheck(false);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    dialog.cancel();
                                                    selectiveQuestionAdapter.notifyDataSetChanged();
                                                }
                                            }
                                    );
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
            );
        }

    }

    private Answer getModelAnswer(final int position) {
        return ((adapter).getItem(position));
    }

    private SelectiveQuestion getModelSelectiveQuestion(final int position) {
        return ((selectiveQuestionAdapter).getItem(position));
    }

    private SelectiveAnswer getModelSelectiveAnswer(final int position) {
        return ((selectiveAnswerAdapter).getItem(position));
    }

    //Обрабатывваем нажатие НАЗАД/ДАЛЕЕ
    @Override
    public void onClick(final View v) {
        Answer answer;
        SelectiveQuestion selectiveQuestion;
        switch (v.getId()) {
            case R.id.question_next_button:
                int count;

                if (currentQuestion[10].equals("0")) {
                    if (!currentQuestion[9].equals("0")) {
                        count = 0;
                        int countBeforeAnswerCheck;
                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            countBeforeAnswerCheck = count;
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                if (mTableQuestions.get(i).getTableAnswers().get(j).isChecked()) {
                                    count++;
                                }
                            }
                            if (count == countBeforeAnswerCheck) {
                                break;
                            }
                        }
                        if (count != mTableQuestions.size()) {
                            return;
                        }

                        //0 - answer_id
                        //1 - duration_time_question
                        //2 - text_open_answer
                        final Map<String, String[]> userAnswers = new HashMap<>();

                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                if (mTableQuestions.get(i).getTableAnswers().get(j).isChecked()) {
                                    userAnswers.put(Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId()), new String[]{
                                            Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId()),
                                            StopWatch.getTime(),
                                            ""});
                                }
                            }
                        }

                        saveAudio();

                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                if (!mTableQuestions.get(i).getTableAnswers().get(j).isChecked()) {
                                    mQuestionSequence.removeAll(Arrays.asList(mTableQuestions.get(i).getTableAnswers().get(j).getNextQuestion()));
                                }
                            }
                        }

                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                if (mTableQuestions.get(i).getTableAnswers().get(j).isChecked()) {
                                    if (!mQuestionSequence.contains(mTableQuestions.get(i).getTableAnswers().get(j).getNextQuestion())) {
                                        mQuestionSequence.add(mTableQuestions.get(i).getTableAnswers().get(j).getNextQuestion());
                                    }
                                }
                            }
                        }

                        Collections.sort(mQuestionSequence);

                        for (int i = 0; i < mQuestionSequence.size(); i++) {
                            if (mQuestionSequence.get(i).equals(number)) {
                                number = i + 1 == mQuestionSequence.size() ? mTableQuestions.get(0).getTableAnswers().get(0).getNextQuestion() : mQuestionSequence.get(i + 1);
                                break;
                            }
                        }

                        answerSequenceInsideQuestions.add(userAnswers);
                        goneNumbers.add(currentQuestion[1]);

//                        smsAnswerSequenceInsideQuestions.add();

                        //Если вопрос последний, переходим в следующую активность.
                        addToDB(number);

                    } else {
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            if (answer.getCheck()) {
                                if (answer.getIsOpenAnswer()) {
                                    if (answer.getOpenAnswer().isEmpty()) {
                                        answer.setOpenAnswerError("Введите текст!");
                                    }
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();

                        count = 0;
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            if (answer.getCheck()) {
                                if (answer.getIsOpenAnswer()) {
                                    if (answer.getOpenAnswer().isEmpty()) {
                                        return;
                                    } else {
                                        count++;
                                    }
                                } else {
                                    count++;
                                }
                            }
                        }

                        if (count == 0) {
                            return;
                        }

                        final SmsAnswerModel smsAnswerModel = new SmsAnswerModel(currentQuestion[0], listView.getAdapter().getCount());

                        //0 - answer_id
                        //1 - duration_time_question
                        //2 - text_open_answer
                        final Map<String, String[]> userAnswers = new HashMap<>();
                        final List<Integer> smsAnswers = new ArrayList<>();

                        for (int i = listView.getAdapter().getCount() - 1; i >= 0; i--) {
                            answer = getModelAnswer(i);
                            if (answer.getCheck()) {
                                final View viewAnswer = listView.getChildAt(i);
                                EditText answerOpen = null;
                                if (answer.getIsOpenAnswer()) {
                                    answerOpen = (EditText) viewAnswer.findViewById(R.id.answer_open);
                                }
                                smsAnswers.add(Integer.valueOf(answer.getNumber()));
                                userAnswers.put(answer.getId(), new String[]{
                                        answer.getId(),
                                        StopWatch.getTime(),
                                        answerOpen != null ? answerOpen.getText().toString() : ""});
                                if (currentQuestion[8].equals("1")) {
                                    for (int j = 0; j < answer.getTableQuestionId().split(",").length; j++) {
                                        if (!tableSequence.contains(answer.getTableQuestionId().split(",")[j])) {
                                            tableSequence.add(answer.getTableQuestionId().split(",")[j]);
                                        }
                                    }
                                }
                            } else if (currentQuestion[8].equals("1")) {
                                for (int j = 0; j < answer.getTableQuestionId().split(",").length; j++) {
                                    if (tableSequence.contains(answer.getTableQuestionId().split(",")[j])) {
                                        tableSequence.remove(answer.getTableQuestionId().split(",")[j]);
                                    }
                                }
                            }
                        }

                        saveAudio();

                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            if (!answer.getCheck()) {
                                mQuestionSequence.removeAll(Arrays.asList(answer.getNextQuestion()));
                            }
                        }

                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            if (answer.getCheck()) {
                                if (!mQuestionSequence.contains(answer.getNextQuestion())) {
                                    if (currentQuestion[8].equals("1")) {
                                        mQuestionSequence.add(answer.getNextQuestion());
                                        break;
                                    } else {
                                        mQuestionSequence.add(answer.getNextQuestion());
                                    }
                                }
                            }
                        }

                        Collections.sort(mQuestionSequence);

                        for (int i = 0; i < mQuestionSequence.size(); i++) {
                            if (mQuestionSequence.get(i).equals(number)) {
                                number = i + 1 == mQuestionSequence.size() ? getModelAnswer(0).getNextQuestion() : mQuestionSequence.get(i + 1);
                                break;
                            }
                        }

                        answerSequenceInsideQuestions.add(userAnswers);

                        final String[] arrayAnswers = new String[listView.getAdapter().getCount()];

                        for (int f = 0; f < arrayAnswers.length; f++) {
                            arrayAnswers[f] = "0";
                        }

                        for (final int number : smsAnswers) {
                            arrayAnswers[number - 1] = "1";
                        }

                        smsAnswerModel.setAnswers(arrayAnswers);
                        mSmsFullAnswerModel.addSmsAnswerModel(smsAnswerModel);

                        goneNumbers.add(currentQuestion[1]);

                        //Если вопрос последний, переходим в следующую активность.
                        addToDB(number);
                    }
                } else {
                    count = 0;
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        selectiveQuestion = getModelSelectiveQuestion(i);
                        for (int j = 0; j < selectiveQuestion.getSelectiveAnswers().size(); j++) {
                            if (selectiveQuestion.getSelectiveAnswers().get(j).getCheck()) {
                                count++;
                                break;
                            }
                        }
                    }

                    if (count != listView.getAdapter().getCount()) {
                        return;
                    }
                    //0 - answer_id
                    //1 - duration_time_question
                    final Map<String, String[]> userAnswers = new HashMap<>();
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        selectiveQuestion = getModelSelectiveQuestion(i);
                        for (int j = 0; j < selectiveQuestion.getSelectiveAnswers().size(); j++) {
                            if (selectiveQuestion.getSelectiveAnswers().get(j).getCheck()) {
                                userAnswers.put(Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId()), new String[]{
                                        Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId()),
                                        StopWatch.getTime()});
                            }
                        }
                    }

                    saveAudio();

                    mQuestionSequence.add(getModelSelectiveQuestion(0).getNextQuestion());

                    Collections.sort(mQuestionSequence);

                    for (int i = 0; i < mQuestionSequence.size(); i++) {
                        if (mQuestionSequence.get(i).equals(number)) {
                            number = i + 1 == mQuestionSequence.size() ? getModelSelectiveQuestion(0).getNextQuestion() : mQuestionSequence.get(i + 1);
                            break;
                        }
                    }

                    answerSequenceInsideQuestions.add(userAnswers);
                    goneNumbers.add(currentQuestion[1]);

                    //Если вопрос последний, переходим в следующую активность.
                    addToDB(number);
                }

                num++;
                //Визуализация
                nextQuestion();
                if (currentQuestion[10].equals("0")) {
                    if (!currentQuestion[9].equals("0")) {
                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                for (int a = 0; a < answerSequenceInsideQuestions.size(); a++) {
                                    if (answerSequenceInsideQuestions.get(a).containsKey(Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId()))
                                            && answerSequenceInsideQuestions.get(a).get(Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId())).length == 3) {
                                        mTableQuestions.get(i).getTableAnswers().get(j).setChecked(true);
                                        final RadioButton radioButton = (RadioButton) findViewById(mTableQuestions.get(i).getTableAnswers().get(j).getId());
                                        radioButton.setChecked(true);
                                    }
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            for (int j = 0; j < answerSequenceInsideQuestions.size(); j++) {
                                if (answerSequenceInsideQuestions.get(j).containsKey(answer.getId())
                                        && answerSequenceInsideQuestions.get(j).get(answer.getId()).length == 3) {
                                    answer.setCheck(true);
                                    answer.setOpenAnswer(answerSequenceInsideQuestions.get(j).get(answer.getId())[2]);
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        selectiveQuestion = getModelSelectiveQuestion(i);
                        for (int j = 0; j < selectiveQuestion.getSelectiveAnswers().size(); j++) {
                            for (int k = 0; k < answerSequenceInsideQuestions.size(); k++) {
                                if (answerSequenceInsideQuestions.get(k).containsKey(Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId())) &&
                                        answerSequenceInsideQuestions.get(k).get(Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId())).length == 2) {
                                    selectiveQuestion.getSelectiveAnswers().get(j).setCheck(true);
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.question_previous_button:
                if (number == 1) {
                    return;
                }

                saveAudio();

                num--;
                number = Integer.parseInt(goneNumbers.get(num));
                //Визуализация
                nextQuestion();

                if (currentQuestion[10].equals("0")) {
                    if (!currentQuestion[9].equals("0")) {
                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                mQuestionSequence.removeAll(Arrays.asList(mTableQuestions.get(i).getTableAnswers().get(j).getNextQuestion()));
                            }
                        }
                    } else {
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            mQuestionSequence.removeAll(Arrays.asList(answer.getNextQuestion()));
                        }
                    }
                } else {
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        mQuestionSequence.removeAll(Arrays.asList(getModelSelectiveQuestion(0).getNextQuestion()));
                    }
                }

                if (currentQuestion[10].equals("0")) {
                    if (!currentQuestion[9].equals("0")) {
                        for (int i = 0; i < mTableQuestions.size(); i++) {
                            for (int j = 0; j < mTableQuestions.get(i).getTableAnswers().size(); j++) {
                                for (int a = 0; a < answerSequenceInsideQuestions.size(); a++) {
                                    if (answerSequenceInsideQuestions.get(a).containsKey(Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId()))
                                            && answerSequenceInsideQuestions.get(a).get(Integer.toString(mTableQuestions.get(i).getTableAnswers().get(j).getId())).length == 3) {
                                        mTableQuestions.get(i).getTableAnswers().get(j).setChecked(true);
                                        final RadioButton radioButton = (RadioButton) findViewById(mTableQuestions.get(i).getTableAnswers().get(j).getId());
                                        radioButton.setChecked(true);
                                    }
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            answer = getModelAnswer(i);
                            for (int j = 0; j < answerSequenceInsideQuestions.size(); j++) {
                                if (answerSequenceInsideQuestions.get(j).containsKey(answer.getId())
                                        && answerSequenceInsideQuestions.get(j).get(answer.getId()).length == 3) {
                                    answer.setCheck(true);
                                    answer.setOpenAnswer(answerSequenceInsideQuestions.get(j).get(answer.getId())[2]);
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                        selectiveQuestion = getModelSelectiveQuestion(i);
                        for (int j = 0; j < selectiveQuestion.getSelectiveAnswers().size(); j++) {
                            for (int k = 0; k < answerSequenceInsideQuestions.size(); k++) {
                                if (answerSequenceInsideQuestions.get(k).containsKey(Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId()))
                                        && answerSequenceInsideQuestions.get(k).get(Integer.toString(selectiveQuestion.getSelectiveAnswers().get(j).getId())).length == 2) {
                                    selectiveQuestion.getSelectiveAnswers().get(j).setCheck(true);
                                }
                            }
                        }
                    }
                }
                break;

                case R.id.question_exit_button:
                    openQuitDialog();
                    break;
        }

        findViewById(R.id.question_next_button).setEnabled(false);
        findViewById(R.id.question_previous_button).setEnabled(false);
        try {
            Thread.sleep(50);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        findViewById(R.id.question_next_button).setEnabled(true);
        findViewById(R.id.question_previous_button).setEnabled(true);

    }

    private void addToDB(final int model) {
        if (model == 0) {
            mSQLiteDatabase = new DBHelper(this,
                    mSharedPreferences.getString("name_file_" + currentUser, ""),
                    new File(getFilesDir() + getString(R.string.separator_path) + mSharedPreferences.getString("name_file_" + currentUser, "").substring(0, mSharedPreferences.getString("name_file_" + currentUser, "").length() - 4)),
                    getString(R.string.sql_file_name),
                    getString(R.string.old_sql_file_name)).getWritableDatabase();

            final String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date());
            final String answerSQL = "answers_" + date; //Имя анкеты
            final String answerSQLSelective = "answers_selective_" + date; //Имя анкеты + selective
            final String commonSQL = "common_" + date; //Общие данные
            final String photoSQL = "photo_" + date; //Фото
            final String audioSQL = "audio_" + date; //Аудио

            final ArrayList<String> AnswerdAnswers = new ArrayList();
            final ArrayList<String> AnswerdSelectiveAnswers = new ArrayList();

            for (int i = 0; i < answerSequenceInsideQuestions.size(); i++) {
                for (final String[] value : answerSequenceInsideQuestions.get(i).values()) {
                    //Записываем в базу ответы
                    if (value.length == 3) {
                        for (int j = 0; j < mTableAnswer.size(); j++) {
                            if (mTableAnswer.get(j)[0].equals(value[0])) {
                                if (!AnswerdAnswers.contains(mTableAnswer.get(j)[6])) {
                                    AnswerdAnswers.add(mTableAnswer.get(j)[6]);
                                }
                            }
                        }
                    } else {
                        for (int j = 0; j < mTableSelectiveAnswer.size(); j++) {
                            if (mTableSelectiveAnswer.get(j)[0].equals(value[0])) {
                                if (!AnswerdSelectiveAnswers.contains(mTableSelectiveAnswer.get(j)[5])) {
                                    AnswerdSelectiveAnswers.add(mTableSelectiveAnswer.get(j)[5]);
                                }
                            }
                        }
                    }
                }
            }

            final int countQuestions = AnswerdAnswers.size() + AnswerdSelectiveAnswers.size();

            if (mAudio != null) {
                if (mAudio.getAudioRecordQuestions().containsKey(0)) {
                    final String s = AudioRecorder.Stop();
                    mSQLiteDatabase.execSQL("create table if not exists " + audioSQL + "(names text);");
                    mSQLiteDatabase.execSQL("insert into " + audioSQL + "(names)" +
                            "values('" + s + "_" + countQuestions + "')");
                    new File(getFilesDir(), "files/" + s + ".amr").renameTo(new File(getFilesDir(), "files/" + s + "_" + countQuestions + ".amr"));

                } else {
                    mSQLiteDatabase.execSQL("create table if not exists " + audioSQL + "(names text);");
                    for (int i = 0; i < mAudio.getAudioRecordQuestions().size(); i++) {
                        if (mAudio.getAudioRecordQuestions().values().toArray()[i] != null) {
                            mSQLiteDatabase.execSQL("insert into " + audioSQL + "(names)" +
                                    "values('" + mAudio.getAudioRecordQuestions().values().toArray()[i] + "')");
                        }
                    }
                }
            }

            if (photoNumber != -1 && photoName != null) {
                mSQLiteDatabase.execSQL("create table if not exists " + photoSQL + "(names text);");
                mSQLiteDatabase.execSQL("insert into " + photoSQL + "(names)" +
                        "values('" + photoName + "')");
            }

            //Запоминаем названия таблиц из БД

            final SharedPreferences.Editor editor = mSharedPreferences.edit()
                    .putString("QuizzesRequest", mSharedPreferences.getString("QuizzesRequest", "") + date + ";")
                    .putString("Quizzes_audio", mSharedPreferences.getString("Quizzes_audio", "") + date + ";")
                    .putString("last_date_interview", mDateInterview);
            editor.apply();

            //Создаем таблицы в БД
            mSQLiteDatabase.execSQL("create table if not exists " + answerSQL + "(answer_id text,duration_time_question text,text_open_answer text);");
            mSQLiteDatabase.execSQL("create table if not exists " + answerSQLSelective + "(answer_id text,duration_time_question text);");
            mSQLiteDatabase.execSQL("create table if not exists " + commonSQL + "(project_id text, questionnaire_id text, user_project_id text,token text, date_interview text, gps text, duration_time_questionnaire text, selected_questions text, login text);");

            //Заполняем таблицыд г
            for (int i = 0; i < answerSequenceInsideQuestions.size(); i++) {
                for (final String[] value : answerSequenceInsideQuestions.get(i).values()) {
                    //Записываем в базу ответы
                    if (value.length == 3) {
                        mSQLiteDatabase.execSQL("insert into " + answerSQL + " (answer_id,duration_time_question,text_open_answer) " +
                                "values('" + value[0] + "','" + value[1] + "','" + value[2] + "')");

                        System.out.println("ID=" + value[0] + " duration=" + value[1] + " text=" + value[2]);
                    } else {
                        mSQLiteDatabase.execSQL("insert into " + answerSQLSelective + " (answer_id,duration_time_question) " +
                                "values('" + value[0] + "','" + value[1] + "')");

                        System.out.println("ID=" + value[0] + " duration=" + value[1]);
                    }
                }
            }

            mSQLiteDatabase.execSQL("insert into " + commonSQL + " (project_id,questionnaire_id,user_project_id,token,date_interview,gps,duration_time_questionnaire,selected_questions,login) " +
                    "values('" +
                    Utils.getProjectId(this) + "','" +
                    Utils.getQuestionnaireId(this) + "','" +
                    mSharedPreferences.getString("user_project_id" + currentUser, "") + "','" +
                    TokenQuiz.TokenQuiz(mSharedPreferences.getString("user_project_id" + currentUser, ""))+"','" +
                    mDateInterview + "','" +
                    mSharedPreferences.getString("gps", "") + "','" +
                    StopWatch.getGlobalTime() + "','" +
                    countQuestions + "','" +
                    mSharedPreferences.getString("login" + currentUser, "") + "')");

            saveSmsAnswers();
            startActivity(new Intent(this, ProjectActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }

        super.onDestroy();
    }

    private void saveSmsAnswers() {
        final long currentTime = mSmsFullAnswerModel.getCurrentTime();
        final List<StagesField> stagesFieldList = Utils.getConfig(this).getConfig().getProject_info().getReserve_channel().getStages();
        final List<SmsAnswerModel> smsAnswerModels = new ArrayList<>();

        for (final SmsAnswerModel smsAnswerModel : mSmsFullAnswerModel.getAnswers()) {
            String smsNumber = Constants.DefaultValues.UNKNOWN;

            for (final StagesField stagesField : stagesFieldList) {
                final long startTime = Long.valueOf(stagesField.getTime_from());
                final long endTime = Long.valueOf(stagesField.getTime_to());

                if (startTime <= currentTime && endTime >= currentTime) {
                    final String questionID = smsAnswerModel.getQestionID();
                    mSmsFullAnswerModel.setStartTime(startTime);
                    mSmsFullAnswerModel.setEndTime(endTime);

                    final List<QuestionsMatchesField> questionsMatchesFieldList = stagesField.getQuestions_matches();

                    for (final QuestionsMatchesField questionsMatchesField : questionsMatchesFieldList) {
                        if (questionsMatchesField.getQuestion_id().equals(questionID)) {
                            smsNumber = questionsMatchesField.getSms_num();
                            break;
                        }
                    }
                    break;
                }
            }

            smsAnswerModel.setSmsNumber(smsNumber);
            smsAnswerModels.add(smsAnswerModel);
        }

        mSmsFullAnswerModel.updateAnswers(smsAnswerModels);

        processSmsAnswers(mSmsFullAnswerModel);
    }

    private void processSmsAnswers(final SmsFullAnswerModel pSmsFullAnswerModel) {
        mSQLiteDatabase.execSQL("create table if not exists " + Constants.SmsDatabase.TABLE_NAME + "(start_time text,end_time text,message text,question_id text,sms_num text, status text,sending_count text);");

        insertOrUpdateToDatabase(pSmsFullAnswerModel);
    }

    private void insertOrUpdateToDatabase(final SmsFullAnswerModel pSmsFullAnswerModel) {
        for (final SmsAnswerModel smsAnswerModel : pSmsFullAnswerModel.getAnswers()) {
            final String smsNumber2 = smsAnswerModel.getSmsNumber();
            final StringBuilder message = new StringBuilder(smsNumber2.startsWith("#") ? smsNumber2 : "#" + smsNumber2);

            for (final String value : smsAnswerModel.getAnswers()) {
                message.append(" ").append(value);
            }

            mCurrentMessage = message.toString();

            boolean isExistField = false;
            String answersInDatabase = null;
            final List<SmsDatabaseModel> smsDatabaseModelList = SmsUtils.getAllSmses(mSQLiteDatabase);
            final long startTime = pSmsFullAnswerModel.getStartTime();
            final long endTime = pSmsFullAnswerModel.getEndTime();
            final String questionId = smsAnswerModel.getQestionID();
            final String smsNumber = smsAnswerModel.getSmsNumber();

            for (final SmsDatabaseModel model : smsDatabaseModelList) {
                if (startTime == Long.parseLong(model.getStartTime()) &&
                        endTime == Long.parseLong(model.getEndTime()) &&
                        questionId.equals(model.getQuestionID()) &&
                        smsNumber.equals(model.getSmsNumber())) {
                    isExistField = true;
                    answersInDatabase = model.getMessage();
                }
            }

            if (isExistField) {
                final String[] answersAdditionalArray = mCurrentMessage.split(" ");
                final String[] answersInDatabaseArray = answersInDatabase.split(" ");
                final String[] resultArray = new String[answersInDatabaseArray.length];

                for (int i = 0; i < answersInDatabaseArray.length; i++) {
                    if (i == 0) {
                        resultArray[i] = answersInDatabaseArray[i];
                    } else {
                        final int first = Integer.parseInt(answersInDatabaseArray[i]);
                        final int second = Integer.parseInt(answersAdditionalArray[i]);
                        final int sum = first + second;

                        resultArray[i] = String.valueOf(sum);
                    }
                }

                final StringBuilder updatedMessage = new StringBuilder();

                for (int i = 0; i < resultArray.length; i++) {
                    if (i == 0) {
                        updatedMessage.append(resultArray[i]);
                    } else {
                        updatedMessage.append(" ").append(resultArray[i]);
                    }
                }

                mSQLiteDatabase.execSQL("update " + Constants.SmsDatabase.TABLE_NAME + " set " + "message = '" + updatedMessage + "' where " +
                        "start_time = '" + startTime + "' AND " +
                        "end_time = '" + endTime + "' AND " +
                        "question_id = '" + questionId + "' AND " +
                        "sms_num = '" + smsNumber + "'");
            } else {
                final String statusNotSent = Constants.SmsStatuses.NOT_SENT;

                mSQLiteDatabase.execSQL("insert into " + Constants.SmsDatabase.TABLE_NAME + " (start_time,end_time,message,question_id,sms_num,status,sending_count) " +
                        "values('" + startTime + "','" + endTime + "','" + mCurrentMessage + "','" + questionId + "','" + smsNumber + "','" + statusNotSent + "','0')");
            }
        }
    }

    private class AnswerAdapter extends ArrayAdapter<Answer> {

        private LayoutInflater mInflater;

        AnswerAdapter(final Context context, final ArrayList<Answer> list) {
            super(context, R.layout.answer_item, list);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, final View convertView,
                            @NonNull final ViewGroup parent) {
            final ViewHolder holder;
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.answer_item, parent, false);
                holder = new ViewHolder();
                holder.answerTitle = (TextView) row.findViewById(R.id.answer_title);
                holder.answerRadio = (RadioButton) row.findViewById(R.id.answer_radio);
                holder.answerCheck = (CheckBox) row.findViewById(R.id.answer_check);
                holder.answerPicture = (ImageView) row.findViewById(R.id.answer_picture);
                holder.answerOpen = (EditText) row.findViewById(R.id.answer_open);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            final Answer answer = getModelAnswer(position);

            final View.OnClickListener onClickListener = new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if (answerSequenceInsideQuestions.size() > 0) {
                        for (int i = answerSequenceInsideQuestions.size() - 1; i >= num; i--) {
                            answerSequenceInsideQuestions.remove(i);
                            goneNumbers.remove(i);
                        }
                    }
                    switch (v.getId()) {
                        case R.id.answer_check:
                            final CheckBox selectedChB = (CheckBox) v;
                            if (answer.getMaxAnswers() == 0) {
                                answer.setCheck(selectedChB.isChecked());
                            } else {
                                int countTrue = 0;

                                View viewAnswer;
                                CheckBox answerCheck;

                                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                    if (getModelAnswer(i).getCheck()) {
                                        countTrue++;
                                    }
                                }

                                if (countTrue == answer.getMaxAnswers()) {
                                    answer.setCheck(false);
                                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                        viewAnswer = listView.getChildAt(i);
                                        answerCheck = (CheckBox) viewAnswer.findViewById(R.id.answer_check);
                                        answerCheck.setEnabled(true);
                                    }

                                } else {
                                    answer.setCheck(selectedChB.isChecked());

                                    if (countTrue == answer.getMaxAnswers() - 1) {
                                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                            viewAnswer = listView.getChildAt(i);
                                            answerCheck = (CheckBox) viewAnswer.findViewById(R.id.answer_check);
                                            if (!answerCheck.isChecked()) {
                                                answerCheck.setEnabled(false);
                                            }
                                        }
                                    }

                                    for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                        if (getModelAnswer(i).getCheck()) {
                                            countTrue++;
                                        }
                                    }

                                    if (countTrue <= answer.getMaxAnswers()) {
                                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                            viewAnswer = listView.getChildAt(i);
                                            answerCheck = (CheckBox) viewAnswer.findViewById(R.id.answer_check);
                                            if (!answerCheck.isChecked()) {
                                                answerCheck.setEnabled(true);
                                            }
                                        }
                                    }
                                }
                            }
                            notifyDataSetChanged();
                            break;
                        case R.id.answer_radio:
                            final RadioButton selectedRB = (RadioButton) v;
                            if (selectedRB.isChecked()) {
                                answer.setCheck(true);
                                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                    if (i != position) {
                                        getModelAnswer(i).setCheck(false);
                                    }
                                }
                                notifyDataSetChanged();
                            }
                            break;
                        case R.id.answer_title:
                            if (holder.answerCheck.isEnabled() && holder.answerRadio.isEnabled()) {
                                if (answer.getPolyAnswer()) {
                                    holder.answerCheck.setChecked(!holder.answerCheck.isChecked());
                                    onClick(holder.answerCheck);
                                } else {
                                    holder.answerRadio.setChecked(true);
                                    onClick(holder.answerRadio);
                                }
                            }
                            break;
                        case R.id.answer_picture:
                            if (holder.answerCheck.isEnabled() && holder.answerRadio.isEnabled()) {
                                if (answer.getPolyAnswer()) {
                                    holder.answerCheck.setChecked(!holder.answerCheck.isChecked());
                                    onClick(holder.answerCheck);
                                } else {
                                    holder.answerRadio.setChecked(true);
                                    onClick(holder.answerRadio);
                                }
                            }
                            break;
                        case R.id.answer_open:
                            if (holder.answerCheck.isEnabled() && holder.answerRadio.isEnabled()) {
                                if (answer.getPolyAnswer()) {
                                    holder.answerCheck.setChecked(true);
                                    onClick(holder.answerCheck);
                                } else {
                                    holder.answerRadio.setChecked(true);
                                    onClick(holder.answerRadio);
                                }

                                final AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);

                                final View view = getLayoutInflater().inflate(R.layout.open_answer_dialog, null);

                                final TextView answerTitle = (TextView) view.findViewById(R.id.answer_title);
                                answerTitle.setText(answer.getTitle());

                                final EditText answerOpen = (EditText) view.findViewById(R.id.answer_open);
                                answerOpen.setText(answer.getOpenAnswer());

                                final AlertDialog dialog = builder.setIcon(R.drawable.edit)
                                        .setTitle(mQuestionTitle.getText())
                                        .setCancelable(false)
                                        .setView(view)

                                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {
                                                answer.setOpenAnswer(answerOpen.getText().toString());
                                                if (!answer.getOpenAnswer().isEmpty()) {
                                                    answer.setOpenAnswerError(null);
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })

                                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(final DialogInterface dialog, final int which) {
                                            }
                                        })
                                        .create();
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                dialog.show();
                            }
                            break;

                    }
                }

            };

            holder.answerTitle.setOnClickListener(onClickListener);
            holder.answerPicture.setOnClickListener(onClickListener);
            holder.answerOpen.setOnClickListener(onClickListener);

            holder.answerTitle.setText(answer.getTitle());

            if (answer.getPolyAnswer()) {
                holder.answerCheck.setVisibility(View.VISIBLE);
                holder.answerCheck.setChecked(answer.getCheck());
                holder.answerCheck.setOnClickListener(onClickListener);

            } else {
                holder.answerRadio.setVisibility(View.VISIBLE);
                holder.answerRadio.setChecked(answer.getCheck());
                holder.answerRadio.setOnClickListener(onClickListener);
            }

            if (answer.getPicture() != null) {
                holder.answerPicture.setVisibility(View.VISIBLE);
                holder.answerPicture.setImageURI(Uri.fromFile(answer.getPicture()));
            }

            if (answer.getIsOpenAnswer()) {
                holder.answerOpen.setVisibility(View.VISIBLE);
                holder.answerOpen.setText(answer.getOpenAnswer());
                holder.answerOpen.setError(answer.getOpenAnswerError());
            }

//            final String quota = mSharedPreferences.getString("quota", "1");
//            final String[] quotasTemp = quota.substring(1, quota.length() - 1).split(";");
//            for (final String re : quotasTemp) {
//                if (re.equals("1"))
//                    return row;
//            }

//            final ArrayList<String[]> quotas = new ArrayList<>();
//            for (final String aQuotasTemp : quotasTemp)
//                if (aQuotasTemp.split("\\|")[2].equals("0") || Integer.parseInt(aQuotasTemp.split("\\|")[1]) >= Integer.parseInt(aQuotasTemp.split("\\|")[2]))
//                    quotas.add(aQuotasTemp.split("\\|")[0].split(","));
//
//            for (int i = 0; i < quotas.size(); i++)
//                if (quotas.get(i)[quotas.get(i).length - 1].equals(answer.getId())) {
//                    for (int j = 0; j < quotas.get(i).length - 1; j++)
//                        for (int k = 0; k < answerSequenceInsideQuestions.size(); k++)
//                            if (!answerSequenceInsideQuestions.get(k).containsKey(quotas.get(i)[j])) {
//                                if (k == answerSequenceInsideQuestions.size() - 1)
//                                    break;
//                            } else {
//                                holder.answerCheck.setEnabled(false);
//                                holder.answerRadio.setEnabled(false);
//                            }
//                }

            return row;
        }

        class ViewHolder {

            TextView answerTitle;
            RadioButton answerRadio;
            CheckBox answerCheck;
            ImageView answerPicture;
            EditText answerOpen;
        }
    }

    private class SelectiveQuestionAdapter extends ArrayAdapter<SelectiveQuestion> {

        private LayoutInflater mInflater;

        SelectiveQuestionAdapter(final Context context, final ArrayList<SelectiveQuestion> list) {
            super(context, R.layout.selective_question_item, list);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, final View convertView,
                            @NonNull final ViewGroup parent) {
            final ViewHolder holder;
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.selective_question_item, parent, false);
                holder = new ViewHolder();
                holder.selectiveQuestionTitle = (TextView) row.findViewById(R.id.selective_question_title);
                holder.selectiveAnswerTitles = (TextView) row.findViewById(R.id.selective_answer_titles);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            final SelectiveQuestion selectiveQuestion = getModelSelectiveQuestion(position);

            holder.selectiveQuestionTitle.setText(selectiveQuestion.getTitle());

            final ArrayList<String> state = new ArrayList<>();

            for (int i = 0; i < selectiveQuestion.getSelectiveAnswers().size(); i++) {
                if (selectiveQuestion.getSelectiveAnswers().get(i).getCheck()) {
                    state.add(selectiveQuestion.getSelectiveAnswers().get(i).getTitle());
                }
            }

            final StringBuilder temp = new StringBuilder();
            for (int i = 0; i < state.size(); i++) {
                if (i != state.size() - 1) {
                    temp.append(state.get(i) + "\n");
                } else {
                    temp.append(state.get(i));
                }
            }

            holder.selectiveAnswerTitles.setText(temp.toString());

            if (holder.selectiveAnswerTitles.getText().equals("")) {
                holder.selectiveAnswerTitles.setVisibility(View.GONE);
            } else {
                holder.selectiveAnswerTitles.setVisibility(View.VISIBLE);
            }

            return row;
        }

        class ViewHolder {

            TextView selectiveQuestionTitle;
            TextView selectiveAnswerTitles;

        }

    }

    private class SelectiveAnswerAdapter extends ArrayAdapter<SelectiveAnswer> {

        private LayoutInflater mInflater;

        SelectiveAnswerAdapter(final Context context, final ArrayList<SelectiveAnswer> list) {
            super(context, R.layout.selective_answer_item, list);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, final View convertView,
                            @NonNull final ViewGroup parent) {
            final ViewHolder holder;
            View row = convertView;
            if (row == null) {
                row = mInflater.inflate(R.layout.selective_answer_item, parent, false);
                holder = new ViewHolder();
                holder.getSelectiveAnswerTitle = (TextView) row.findViewById(R.id.selective_answer_item_title);
                holder.selectiveAnswerCheck = (CheckBox) row.findViewById(R.id.selective_answer_item_check);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            final SelectiveAnswer selectiveAnswer = getModelSelectiveAnswer(position);

            holder.getSelectiveAnswerTitle.setText(selectiveAnswer.getTitle());
            holder.getSelectiveAnswerTitle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if (answerSequenceInsideQuestions.size() > 0) {
                        for (int i = answerSequenceInsideQuestions.size() - 1; i >= num; i--) {
                            answerSequenceInsideQuestions.remove(i);
                            goneNumbers.remove(i);
                        }
                    }
                    selectiveAnswer.setCheck(!selectiveAnswer.getCheck());
                    notifyDataSetChanged();
                }
            });
            holder.selectiveAnswerCheck.setChecked(selectiveAnswer.getCheck());
            holder.selectiveAnswerCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if (answerSequenceInsideQuestions.size() > 0) {
                        for (int i = answerSequenceInsideQuestions.size() - 1; i >= num; i--) {
                            answerSequenceInsideQuestions.remove(i);
                            goneNumbers.remove(i);
                        }
                    }
                    final CheckBox selectedChB = (CheckBox) v;
                    selectiveAnswer.setCheck(selectedChB.isChecked());
                    notifyDataSetChanged();

                }
            });
            return row;
        }

        class ViewHolder {

            TextView getSelectiveAnswerTitle;
            CheckBox selectiveAnswerCheck;
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
                        startActivity(new Intent(QuestionsActivity.this, ProjectActivity.class));
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

    public void onScrollChanged(final ObservableScrollView scrollView, final int x, final int y, final int oldx, final int oldy) {
        if (interceptScroll) {
            interceptScroll = false;
            if (scrollView == scrollView1) {
                scrollView2.onOverScrolled(x, y, true, true);
            } else if (scrollView == scrollView2) {
                scrollView1.onOverScrolled(x, y, true, true);
            }
            interceptScroll = true;
        }
    }

    private void saveAudio() {
        if (mAudio != null) {
            if (mAudio.getAudioRecordQuestions().containsKey(number)) {
                mAudio.getAudioRecordQuestions().put(number, AudioRecorder.Stop());
            }
        }
    }

    private void startAudio() {
        if (mAudio != null) {
            if (mAudio.getAudioRecordQuestions().containsKey(number) && !mAudio.getAudioRecordQuestions().containsKey(0)) {
                if (mAudio.getAudioRecordQuestions().get(number) == null) {
                    AudioRecorder.Start(this, mSharedPreferences.getString(Constants.Shared.LOGIN_ADMIN, "") + "_" +
                            mSharedPreferences.getString("login" + currentUser, "") + "_" +
                            mSharedPreferences.getString("user_project_id" + currentUser, "") + "_" +
                            currentQuestion[0] + "_" +
                            mDateInterview.replace(':', '-').replace(' ', '-') + "_" +
                            1, mAudio.getAudioRecordLimitTime(), mAudio.getAudioSampleRate());
                } else {
                    AudioRecorder.Start(this, mAudio.getAudioRecordQuestions().get(number), mAudio.getAudioRecordLimitTime(), mAudio.getAudioSampleRate());
                }
            }
        }
    }

    private void createPhoto() {
        if (photoNumber != -1) {
            if (photoNumber == number) {
                if (photoName == null) {
                    photoName = "photo-" +
                            mSharedPreferences.getString("login" + currentUser, "") + "-" +
                            mDateInterview.replace(':', '-').replace(' ', '-');
                    System.out.println(photoName);
                    new PhotoCamera(this, frameLayout, photoName).createPhoto();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        showLocation(location);
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        showLocation(locationManager.getLastKnownLocation(provider));
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    private void showLocation(final Location location) {
        if (location == null) {
            return;
        }

        final SharedPreferences.Editor editor = mSharedPreferences.edit()
                .putString("gps", location.getLatitude() + ":" + location.getLongitude());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, this);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
}