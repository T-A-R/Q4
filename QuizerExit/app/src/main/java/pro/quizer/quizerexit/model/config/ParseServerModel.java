package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ParseServerModel implements Serializable {

    private String mServerUrl;
    private String mLoginAdmin;

    public ParseServerModel(String mServerUrl, String mLoginAdmin) {
        this.mServerUrl = mServerUrl;
        this.mLoginAdmin = mLoginAdmin;
    }

    public String getServerUrl() {
        return mServerUrl;
    }

    public String getLoginAdmin() {
        return mLoginAdmin;
    }

}
