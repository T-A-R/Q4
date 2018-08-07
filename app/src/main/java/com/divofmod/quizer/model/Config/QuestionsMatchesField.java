package com.divofmod.quizer.model.Config;

import com.google.gson.annotations.SerializedName;

public class QuestionsMatchesField {

    @SerializedName("3")
    private String s3;

    @SerializedName("4")
    private String s4;

    public QuestionsMatchesField() {
    }

    public String getS3() {
        return s3;
    }

    public String getS4() {
        return s4;
    }
}
