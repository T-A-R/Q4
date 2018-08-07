package com.divofmod.quizer.model.Activation;

public class ActivationRequestModel {

    private final String name_form;
    private final String key;

    public ActivationRequestModel(final String pName_form, final String pKey) {
        name_form = pName_form;
        key = pKey;
    }

    public String getName_form() {
        return name_form;
    }

    public String getKey() {
        return key;
    }
}
