package pro.quizer.quizerexit.adapter;

import pro.quizer.quizerexit.SimpleTextWatcher;
import pro.quizer.quizerexit.model.config.ElementModel;


public class ElementTextWatcher extends SimpleTextWatcher {

    private ElementModel mAnswer;

    ElementTextWatcher(final ElementModel pAnswer) {
        mAnswer = pAnswer;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);

        mAnswer.setTextAnswer(s.toString());
    }
}
