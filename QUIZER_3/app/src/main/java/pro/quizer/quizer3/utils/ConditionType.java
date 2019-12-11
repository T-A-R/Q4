package pro.quizer.quizer3.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ConditionType.TITLE,
        ConditionType.CHECKED,
        ConditionType.VALUE})
public @interface ConditionType {

    String TITLE = "title";
    String VALUE = "value";
    String CHECKED = "checked";
}
