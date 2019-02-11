package pro.quizer.quizerexit.model.view;

import java.io.Serializable;

public class SettingsViewModel implements Serializable {

    private String mConfigId;
    private String mConfigDate;
    private int mAnswerMargin;

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

    public String getConfigId() {
        return mConfigId;
    }

    public int getAnswerMargin() {
        return mAnswerMargin;
    }
}