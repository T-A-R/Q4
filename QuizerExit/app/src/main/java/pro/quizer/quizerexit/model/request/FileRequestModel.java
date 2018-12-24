package pro.quizer.quizerexit.model.request;

import java.io.Serializable;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.MD5Utils;

public class FileRequestModel implements Serializable {

    private final String name_form;

    public FileRequestModel(final String pNameForm) {
        name_form = pNameForm;
    }
}