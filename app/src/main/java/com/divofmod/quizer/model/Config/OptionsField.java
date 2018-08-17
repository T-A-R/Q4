package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class OptionsField {

    @SerializedName("polyanswer")
    private int polyanswer;

    public OptionsField() {
    }

    public int getPolyanswer() {
        return polyanswer;
    }
}
