package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionsMatchesModel implements Serializable {

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
}