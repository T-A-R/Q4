package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class QuestionOptionsModel implements Serializable {

    @SerializedName("polyanswer")
    private int polyanswer;

    @SerializedName("is_zoomable")
    private int is_zoomable;

    @SerializedName("is_picture")
    private int is_picture;

    @SerializedName("rank_answers_show")
    private int rank_answers_show;

    @SerializedName("rank_answers")
    private int rank_answers;

    @SerializedName("random_order")
    private int random_order;

    @SerializedName("max_answers")
    private int max_answers;

    @SerializedName("min_answers")
    private int min_answers;

    @SerializedName("table_id")
    private int table_id;

    @SerializedName("not_show_if")
    private String not_show_if;

    @SerializedName("show_if")
    private String show_if;

    public QuestionOptionsModel() {
    }

    public int getPolyanswer() {
        return polyanswer;
    }

    public int getIsZoomable() {
        return is_zoomable;
    }

    public int getIsPicture() {
        return is_picture;
    }

    public int getRankAnswersShow() {
        return rank_answers_show;
    }

    public int getRankAnswers() {
        return rank_answers;
    }

    public boolean isRandomOrder() {
        return random_order != 0;
    }

    public int getMaxAnswers() {
        return max_answers;
    }

    public int getMinAnswers() {
        return min_answers;
    }

    public int getTableId() {
        return table_id;
    }

    public boolean isTableQuestion() {
        return table_id != 0;
    }

    public String getNotShowIf() {
        return not_show_if;
    }

    public String getShowIf() {
        return show_if;
    }
}