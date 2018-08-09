package com.divofmod.quizer.model.Activation;

import com.divofmod.quizer.Constants.Constants;

public class ActivationRequestModel {

    private final String name_form;
    private final String key;

    public ActivationRequestModel(final String pKey) {
        name_form = Constants.NameForm.KEY_CLIENT;
        key = pKey;
    }

    public String getName_form() {
        return name_form;
    }

    public String getKey() {
        return key;
    }
}
