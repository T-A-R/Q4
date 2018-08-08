package com.divofmod.quizer.model;

public class SmsQuestionModel {

    private String mId;
    private final String[] mAnswers;

    public SmsQuestionModel(final int pAnswersCount) {
        mAnswers = new String[5];
    }

}
