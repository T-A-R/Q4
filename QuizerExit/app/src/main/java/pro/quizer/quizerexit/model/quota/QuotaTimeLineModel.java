package pro.quizer.quizerexit.model.quota;

import java.io.Serializable;

public class QuotaTimeLineModel implements Serializable {

    private String mAnswer;

    public QuotaTimeLineModel(final String pAnswer) {
        this.mAnswer = pAnswer;
    }

    public String getAnswer() {
        return mAnswer;
    }
}