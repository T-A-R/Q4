package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;

public class ActivationRequestModel implements Serializable {

    private final String name_form;
    private final String key;

    public ActivationRequestModel(final String pKey) {
        name_form = Constants.NameForm.KEY_CLIENT;
        key = pKey;
    }
}