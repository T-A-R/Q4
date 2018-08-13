package com.divofmod.quizer.model.API;

import com.google.gson.annotations.SerializedName;

public class QuizzesResponse {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    public QuizzesResponse() {
    }

    public Boolean isSeccessful() {
        return result == 1;
    }

    public int getResult() {
        return result;
    }

    public void setResult(final int pResult) {
        result = pResult;
    }

    public String getError() {
        return error;
    }

    public void setError(final String pError) {
        error = pError;
    }
}
