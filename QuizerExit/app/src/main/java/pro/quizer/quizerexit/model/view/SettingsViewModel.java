package pro.quizer.quizerexit.model.view;

import java.io.Serializable;

public class SettingsViewModel implements Serializable {

    private String mConfigId;
    private String mConfigDate;

    public void setmConfigId(String mConfigId) {
        this.mConfigId = mConfigId;
    }

    public void setmConfigDate(String mConfigDate) {
        this.mConfigDate = mConfigDate;
    }

    public String getConfigId() {
        return mConfigId;
    }

    public String getConfigDate() {
        return mConfigDate;
    }
}