package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReserveChannelField {

    @SerializedName("phone")
    private String phone;

    @SerializedName("stages")
    private List<StagesField> stages;

    public ReserveChannelField() {
    }

    public String getPhone() {
        return "+375295809385";
//        return phone;
    }

    public List<StagesField> getStages() {
        return stages;
    }
}
