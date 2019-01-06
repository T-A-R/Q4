package pro.quizer.quizerexit.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DisplayConditionType.SHOW,
        DisplayConditionType.NOT_SHOW})
public @interface DisplayConditionType {

    String SHOW = " {show}";
    String NOT_SHOW = " {not_show}";
}
