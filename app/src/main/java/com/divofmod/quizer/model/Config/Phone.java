package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class Phone {

    @SerializedName("number")
    private String number;

    @SerializedName("prefix")
    private String prefix;

    public Phone() {
    }

    public String getNumber() {
        return number;
    }

    public String getPrefix() {
        return prefix;
    }
}
