package pro.quizer.quizerexit.model.request;

import pro.quizer.quizerexit.Constants;

public class ActivationRequestModel {

    private final String name_form;
    private final String key;

    public ActivationRequestModel(final String pKey) {
        name_form = Constants.NameForm.KEY_CLIENT;
        key = pKey;
    }
}