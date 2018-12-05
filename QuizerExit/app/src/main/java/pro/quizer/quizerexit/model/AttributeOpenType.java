package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({AttributeOpenType.DATE,
        AttributeOpenType.CHECKBOX,
        AttributeOpenType.TIME,
        AttributeOpenType.NUMBER,
        AttributeOpenType.TEXT})
public @interface AttributeOpenType {

    String CHECKBOX = "checkbox";
    String DATE = "date";
    String TIME = "time";
    String NUMBER = "number";
    String TEXT = "text";
}
