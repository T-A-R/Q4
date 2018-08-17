package com.divofmod.quizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class SelectiveQuestion {

    private String mId;
    private String mTitle;
    private ArrayList<SelectiveAnswer> mSelectiveAnswers;
    private int mNextQuestion;

    SelectiveQuestion(String id, String title, ArrayList<String[]> selectiveAnswers, String nextQuestion) {
        mId = id;
        mTitle = title;
        Collections.sort(selectiveAnswers, new Comparator<String[]>() {
            public int compare(String[] o1, String[] o2) {
                return o1[1].compareTo(o2[1]);
            }
        });
        mSelectiveAnswers = new ArrayList<>();
        for (int i = 0; i < selectiveAnswers.size(); i++) {
            mSelectiveAnswers.add(new SelectiveAnswer(
                    Integer.parseInt(selectiveAnswers.get(i)[0]),
                    Integer.parseInt(selectiveAnswers.get(i)[1]),
                    selectiveAnswers.get(i)[2],
                    Integer.parseInt(selectiveAnswers.get(i)[4]),
                    Integer.parseInt(selectiveAnswers.get(i)[5])));
        }
        mNextQuestion = Integer.parseInt(nextQuestion);
    }

    public String getId() {
        return mId;
    }

    String getTitle() {
        return mTitle;
    }

    ArrayList<SelectiveAnswer> getSelectiveAnswers() {
        return mSelectiveAnswers;
    }

    public int getNextQuestion() {
        return mNextQuestion;
    }
}

