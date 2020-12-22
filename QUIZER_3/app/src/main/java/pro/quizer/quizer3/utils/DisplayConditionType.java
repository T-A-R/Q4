package pro.quizer.quizer3.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({DisplayConditionType.SHOW,
        DisplayConditionType.JUMP})
public @interface DisplayConditionType {

    String SHOW = "show";
    String JUMP = "jump:";
    String HIDE = "hide";
}
