package com.divofmod.quizer;

class SelectiveAnswer {
    private int mId;
    private int mNum;
    private String mTitle;
    private int mParentNum;
    private int mSelectiveQuestionId;
    private Boolean mCheck;
    private Boolean mVisibility;

    SelectiveAnswer(int id, int num, String title, int parentNum, int selectiveQuestionId) {
        mId = id;
        mNum = num;
        mTitle = title;
        mParentNum = parentNum;
        mSelectiveQuestionId = selectiveQuestionId;
        mCheck = false;
        mVisibility = parentNum == 0;
    }

    public int getId() {
        return mId;
    }

    public int getNum() {
        return mNum;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getParentNum() {
        return mParentNum;
    }

    public int getSelectiveQuestionId() {
        return mSelectiveQuestionId;
    }

    public Boolean getCheck() {
        return mCheck;
    }

    public void setCheck(Boolean check) {
        mCheck = check;
    }

    public Boolean getVisibility() {
        return mVisibility;
    }

    public void setVisibility(Boolean visibility) {
        mVisibility = visibility;
    }
}
