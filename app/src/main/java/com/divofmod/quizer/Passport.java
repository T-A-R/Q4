package com.divofmod.quizer;

public class Passport {
    private String mTitle;
    private String mEditText;
    private String mPassportError;
    private String mIsImportant;

    public Passport(String title, String isImportant) {
        mTitle = title;
        mIsImportant = isImportant;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getEditText() {
        return mEditText;
    }

    public void setEditText(String editText) {
        mEditText = editText;
    }

    public String getPassportError() {
        return mPassportError;
    }

    public void setPassportError(String passportError) {
        mPassportError = passportError;
    }

    public String getIsImportant() {
        return mIsImportant;
    }
}
