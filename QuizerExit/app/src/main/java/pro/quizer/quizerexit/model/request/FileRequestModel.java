package pro.quizer.quizerexit.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.MD5Utils;

public class FileRequestModel implements Serializable, Parcelable {

    private final String name_form;
    private final long device_time;

    public FileRequestModel(final String pNameForm) {
        name_form = pNameForm;
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