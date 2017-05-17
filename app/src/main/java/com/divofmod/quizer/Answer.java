package com.divofmod.quizer;

import java.io.File;

class Answer {

    private String mId;
    private String mTitle;
    private File mPicture;
    private Boolean mCheck;
    private Boolean mPolyAnswer;
    private int mMaxAnswers;
    private Boolean mIsOpenAnswer;
    private String mOpenAnswerError;
    private String mOpenAnswer;
    private int mNextQuestion;
    private String mTableQuestionId;

    Answer(String id, String title, File picture, String polyAnswer, String maxAnswers, String isOpenAnswer, String nextQuestion, String tableQuestionId) {
        mId = id;
        mTitle = title;
        mPicture = picture;
        mCheck = false;
        mPolyAnswer = !polyAnswer.equals("0");
        mMaxAnswers = Integer.parseInt(maxAnswers);
        mIsOpenAnswer = !isOpenAnswer.equals("0");
        mOpenAnswer = "";
        mNextQuestion = Integer.parseInt(nextQuestion);
        mTableQuestionId = tableQuestionId;
    }

    public String getId() {
        return mId;
    }

    String getTitle() {
        return mTitle;
    }

    File getPicture() {
        return mPicture;
    }

    Boolean getCheck() {
        return mCheck;
    }

    void setCheck(Boolean check) {
        mCheck = check;
        if (!check)
            mOpenAnswerError = null;
    }

    Boolean getPolyAnswer() {
        return mPolyAnswer;
    }

    int getMaxAnswers() {
        return mMaxAnswers;
    }

    Boolean getIsOpenAnswer() {
        return mIsOpenAnswer;
    }

    public String getOpenAnswerError() {
        return mOpenAnswerError;
    }

    public void setOpenAnswerError(String openAnswerError) {
        mOpenAnswerError = openAnswerError;
    }

    String getOpenAnswer() {
        return mOpenAnswer;
    }

    void setOpenAnswer(String openAnswer) {
        mOpenAnswer = openAnswer;
    }

    int getNextQuestion() {
        return mNextQuestion;
    }

    public String getTableQuestionId() {
        return mTableQuestionId;
    }

}
