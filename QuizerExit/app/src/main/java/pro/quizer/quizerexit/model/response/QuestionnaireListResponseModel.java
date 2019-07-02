package pro.quizer.quizerexit.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Deprecated
public class QuestionnaireListResponseModel implements Serializable {

    @SerializedName("result")
    private int result;

    @SerializedName("error")
    private String error;

    @SerializedName("accepted_tokens")
    private List<String> accepted_tokens;

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public List<String> getAcceptedTokens() {
        return accepted_tokens;
    }

}