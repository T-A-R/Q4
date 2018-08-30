package com.divofmod.quizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TableQuestion {

    private int mId;
    private int mNumber;
    private String mTitle;
    private int mTableId;
    private ArrayList<TableAnswer> mTableAnswers;
    private int mTextViewId;

    TableQuestion(String id, String number, String title, String tableId, ArrayList<String[]> tableAnswers) {
        mId = Integer.parseInt(id);
        mNumber = Integer.parseInt(number);
        mTitle = title;
        mTableId = Integer.parseInt(tableId);
        Collections.sort(tableAnswers, new Comparator<String[]>() {
            public int compare(String[] o1, String[] o2) {
                return o1[1].compareTo(o2[1]);
            }
        });
        mTableAnswers = new ArrayList<>();
        for (int i = 0; i < tableAnswers.size(); i++) {
            mTableAnswers.add(
                    new TableAnswer(
                            tableAnswers.get(i)[0],
                            tableAnswers.get(i)[1],
                            tableAnswers.get(i)[4]
                    ));
        }
    }

    public int getId() {
        return mId;
    }

    public int getNumber() {
        return mNumber;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getTableId() {
        return mTableId;
    }

    public ArrayList<TableAnswer> getTableAnswers() {
        return mTableAnswers;
    }

    public int getTextViewId() {
        return mTextViewId;
    }

    public void setTextViewId(int textViewId) {
        mTextViewId = textViewId;
    }
}
