package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;

public class QuotaRequestModel implements Serializable, Parcelable {

    private final String name_form;
    private final String login_admin;
    private final String passw;
    private final String login;
    private final long device_time;

    public QuotaRequestModel(final String pLoginAdmin, final String pPassword, final String pLogin) {
        name_form = Constants.NameForm.GET_QUOTA;
        login_admin = pLoginAdmin;
        passw = pPassword;
        login = pLogin;
        device_time = DateUtils.getCurrentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}