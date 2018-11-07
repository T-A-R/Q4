package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class OptionsField {

    @SerializedName("show_if")
    private String  show_if;

    @SerializedName("not_show_if")
    private String  not_show_if;

    @SerializedName("random_order")
    private int random_order;

    @SerializedName("table_id")
    private int table_id;

    @SerializedName("table_header")
    private int table_header;

    @SerializedName("polyanswer")
    private int polyanswer;

    @SerializedName("min_answers")
    private int min_answers;

    @SerializedName("max_answers")
    private int max_answers;

    @SerializedName("rank_answers")
    private int rank_answers;

    @SerializedName("rank_answers_show")
    private int rank_answers_show;

    @SerializedName("is_picture")
    private int is_picture;

    @SerializedName("is_zoomable")
    private int is_zoomable;

    public OptionsField() {
    }

    public int getPolyanswer() {
        return polyanswer;
    }

    public String getShow_if() {
        return show_if;
    }

    public String getNot_show_if() {
        return not_show_if;
    }

    public int getRandom_order() {
        return random_order;
    }

    public int getTable_id() {
        return table_id;
    }

    public int getTable_header() {
        return table_header;
    }

    public int getMin_answers() {
        return min_answers;
    }

    public int getMax_answers() {
        return max_answers;
    }

    public int getRank_answers() {
        return rank_answers;
    }

    public int getRank_answers_show() {
        return rank_answers_show;
    }

    public int getIs_picture() {
        return is_picture;
    }

    public int getIs_zoomable() {
        return is_zoomable;
    }
}
