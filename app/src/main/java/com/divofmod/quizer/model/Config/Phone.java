package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class Phone {

    @SerializedName("number")
    private String number;

    @SerializedName("preffix")
    private String preffix;

    public Phone() {
    }

    public String getNumber() {
        return number;
    }

    public String getPreffix() {
        return preffix;
    }
}
