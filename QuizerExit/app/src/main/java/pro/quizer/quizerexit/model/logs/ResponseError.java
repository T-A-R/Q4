package pro.quizer.quizerexit.model.logs;

import com.google.gson.annotations.SerializedName;

public class ResponseError {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    public ResponseError() {
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
