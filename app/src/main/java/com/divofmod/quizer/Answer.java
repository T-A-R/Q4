package com.divofmod.quizer;

import java.io.File;

class Answer {

    private final String mId;
    private final String mNumber;
    private final String mTitle;
    private final File mPicture;
    private Boolean mCheck;
    private final Boolean mPolyAnswer;
    private final int mMaxAnswers;
    private final Boolean mIsOpenAnswer;
    private String mOpenAnswerError;
    private String mOpenAnswer;
    private final int mNextQuestion;
    private final String mTableQuestionId;

    Answer(final String id, final String title, final File picture, final String polyAnswer, final String maxAnswers, final String isOpenAnswer, final String nextQuestion, final String tableQuestionId, final String pNumber) {
        mId = id;
        mNumber = pNumber;
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

    public String getNumber() {
        return mNumber;
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

    void setCheck(final Boolean check) {
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

    public void setOpenAnswerError(final String openAnswerError) {
        mOpenAnswerError = openAnswerError;
    }

    String getOpenAnswer() {
        return mOpenAnswer;
    }

    void setOpenAnswer(final String openAnswer) {
        mOpenAnswer = openAnswer;
    }

    int getNextQuestion() {
        return mNextQuestion;
    }

    public String getTableQuestionId() {
        return mTableQuestionId;
    }

}
