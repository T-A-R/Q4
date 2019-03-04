package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionsMatchesModel implements Serializable, Parcelable {

    @SerializedName("question_id")
    private String question_id;

    @SerializedName("sms_num")
    private String sms_num;

    public QuestionsMatchesModel() {
    }

    public String getQuestionId() {
        return question_id;
    }

    public String getSmsNum() {
        return sms_num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}