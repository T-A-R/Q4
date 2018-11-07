package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class AnswerFieldOptions {

    @SerializedName("is_open")
    private int is_open;

    @SerializedName("show_if")
    private String  is_opshow_ifen;

    @SerializedName("not_show_if")
    private String  not_show_if;

    @SerializedName("goto_if")
    private String  goto_if;

    @SerializedName("persistent_order")
    private int persistent_order;

    @SerializedName("unchecker")
    private int unchecker;

    @SerializedName("picture")
    private String  picture;

    public int getIs_open() {
        return is_open;
    }

    public String getIs_opshow_ifen() {
        return is_opshow_ifen;
    }

    public String getNot_show_if() {
        return not_show_if;
    }

    public String getGoto_if() {
        return goto_if;
    }

    public int getPersistent_order() {
        return persistent_order;
    }

    public int getUnchecker() {
        return unchecker;
    }

    public String getPicture() {
        return picture;
    }
}
