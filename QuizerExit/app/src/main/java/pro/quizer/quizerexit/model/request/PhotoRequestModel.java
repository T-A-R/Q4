package pro.quizer.quizerexit.model.request;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.utils.MD5Utils;

public class PhotoRequestModel {

    private final String name_form;

    public PhotoRequestModel() {
        name_form = Constants.NameForm.PHOTO_FILE;
    }
}