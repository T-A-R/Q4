package com.divofmod.quizer;

public class TableAnswer {
    private int mId;
    private String mTitle;
    private boolean mChecked;
    private int mTextViewId;
    private int mNextQuestion;

    public TableAnswer(String id, String title, String nextQuestion) {
        mId = Integer.parseInt(id);
        mTitle = title;
        mChecked = false;
        mNextQuestion = Integer.parseInt(nextQuestion);
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public int getTextViewId() {
        return mTextViewId;
    }

    public void setTextViewId(int textViewId) {
        mTextViewId = textViewId;
    }

    public int getNextQuestion() {
        return mNextQuestion;
    }
}
