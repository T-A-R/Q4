package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnswersField implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("number")
    private int number;

    @SerializedName("next_question")
    private int next_question;

    @SerializedName("options")
    private AnswerOptionsField options;

    @SerializedName("title")
    private String title;

    private boolean mIsSelected;
    private boolean mIsEnabled = true;

    public AnswersField() {
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setEnabled(final boolean pIsEnabled) {
        mIsEnabled = pIsEnabled;
    }

    public void setSelected(final boolean selected) {
        mIsSelected = selected;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getNextQuestion() {
        return next_question;
    }

    public String getTitle() {
        return title;
    }

    public void setId(final int pId) {
        id = pId;
    }

    public void setTitle(final String pTitle) {
        title = pTitle;
    }

    public AnswerOptionsField getOptions() {
        return options;
    }
}