package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({OptionsOpenType.DATE,
        OptionsOpenType.CHECKBOX,
        OptionsOpenType.TIME,
        OptionsOpenType.NUMBER,
        OptionsOpenType.TEXT})
public @interface OptionsOpenType {

    String CHECKBOX = "checkbox";
    String DATE = "date";
    String TIME = "time";
    String NUMBER = "number";
    String TEXT = "text";
}
