package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QuestionModel implements Serializable, Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("number")
    private int number;

    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private int type;

    @SerializedName("options")
    private QuestionOptionsModel options;

    @SerializedName("answers")
    private List<AnswersModel> answers;

    public QuestionModel() {
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public QuestionOptionsModel getOptions() {
        return options;
    }

    public List<AnswersModel> getAnswers() {
        return answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
