package pro.quizer.quizer3.model.quota;

import java.io.Serializable;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.utils.StringUtils;

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