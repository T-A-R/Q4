package pro.quizer.quizerexit.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DeletingListResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("accepted")
    private List<String> accepted;


    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public List<String> getAccepted() {
        return accepted;
    }
}