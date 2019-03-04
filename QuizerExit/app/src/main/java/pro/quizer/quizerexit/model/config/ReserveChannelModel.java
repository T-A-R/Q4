package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReserveChannelModel implements Serializable, Parcelable {

    @SerializedName("phone")
    private List<PhoneModel> phones;

    @SerializedName("stages")
    private List<StagesModel> stages;

    public ReserveChannelModel() {
    }

    public List<PhoneModel> getPhones() {
        return phones;
    }

    public List<StagesModel> getStages() {
        return stages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
