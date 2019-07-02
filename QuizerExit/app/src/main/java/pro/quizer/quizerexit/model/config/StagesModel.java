package pro.quizer.quizerexit.model.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StagesModel implements Serializable {

    @SerializedName("time_from")
    private int time_from;

    @SerializedName("time_to")
    private int time_to;

    @SerializedName("questions_matches")
    private List<QuestionsMatchesModel> questions_matches;

    public StagesModel() {
    }

    public int getTimeFrom() {
        return time_from;
    }

    public int getTimeTo() {
        return time_to;
    }

    public List<QuestionsMatchesModel> getQuestionsMatches() {
        return questions_matches;
    }

}
