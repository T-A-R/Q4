package pro.quizer.quizerexit.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class SettingsViewModel implements Serializable {

    private String mConfigId;
    private String mConfigDate;
    private int mAnswerMargin;
    private boolean mSmsSection;

    public void setmConfigId(String mConfigId) {
        this.mConfigId = mConfigId;
    }

    public void setmConfigDate(String mConfigDate) {
        this.mConfigDate = mConfigDate;
    }

    public void setmAnswerMargin(int mAnswerMargin) {
        this.mAnswerMargin = mAnswerMargin;
    }

    public String getConfigDate() {
        return mConfigDate;
    }

    public void setSmsSection(boolean mSmsSection) {
        this.mSmsSection = mSmsSection;
    }

    public boolean hasSmsSection() {
        return mSmsSection;
    }

    public String getConfigId() {
        return mConfigId;
    }

    public int getAnswerMargin() {
        return mAnswerMargin;
    }

}