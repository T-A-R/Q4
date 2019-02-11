package pro.quizer.quizerexit.model.quota;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.StringUtils;

public class QuotaTimeLineModel implements Serializable {

    private String mAnswer;
    private String mAnswerLoweCase;

    public QuotaTimeLineModel(final String pAnswer) {
        this.mAnswer = pAnswer;
        this.mAnswerLoweCase = StringUtils.isEmpty(pAnswer) ? Constants.Strings.EMPTY : pAnswer.toLowerCase();
    }

    public String getAnswer() {
        return mAnswer;
    }

    public boolean contains(final String pString) {
        if (StringUtils.isEmpty(mAnswer)) {
            return false;
        }

        return mAnswerLoweCase.contains(pString);
    }
}