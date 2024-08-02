package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReserveChannelModel implements Serializable {

    @SerializedName("phone")
    private List<PhoneModel> phones;

//    @SerializedName("stages")
//    private List<StagesModel> stages;

    @SerializedName("notification_offset")
    private Integer notification_offset;

    public ReserveChannelModel() {
    }

    public void selectPhone(final int index) {
        for (int i = 0; i < phones.size(); i++) {
            phones.get(i).setSelected(i == index);
        }
    }

    public PhoneModel getSelectedPhone() {
        for (final PhoneModel phoneModel : phones) {
            if (phoneModel.isSelected()) {
                return phoneModel;
            }
        }

        if(phones != null && phones.size() > 0) return phones.get(0);

        return null;
    }

    public List<PhoneModel> getPhones() {
        return phones;
    }

//    public List<StagesModel> getStages() {
//        return stages;
//    }

    public Integer getNotificationOffset() {
        return notification_offset;
    }
}
