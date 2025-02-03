package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionsMatchesModel implements Serializable {

    @SerializedName("qi")
    private int qi;

    @SerializedName("sn")
    private String sn;

    public QuestionsMatchesModel() {
    }

    public int getQuestionId() {
        return qi;
    }

    public String getSmsNum() {
        return sn;
    }

}