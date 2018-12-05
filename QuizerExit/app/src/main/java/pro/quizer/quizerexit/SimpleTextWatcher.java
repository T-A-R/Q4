package pro.quizer.quizerexit;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // empty
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // empty
    }

    @Override
    public void afterTextChanged(Editable s) {
        // empty
    }
}
