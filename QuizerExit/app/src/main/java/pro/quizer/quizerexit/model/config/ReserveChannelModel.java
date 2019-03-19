package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

public class ReserveChannelModel implements Serializable, Parcelable {

    @SerializedName("phone")
    private List<PhoneModel> phones;

    @SerializedName("stages")
    private List<StagesModel> stages;

    public ReserveChannelModel() {
    }

    public void selectPhone(final int index) {
        for (int i = 0; i < phones.size(); i++) {
            phones.get(i).setSelected(i == index);
        }
    }

    @Nullable
    public PhoneModel getSelectedPhone() {
        for (final PhoneModel phoneModel : phones) {
            if (phoneModel.isSelected()) {
                return phoneModel;
            }
        }

        return null;
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
