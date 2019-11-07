package pro.quizer.quizer3.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementDatabaseType.SCREEN,
        ElementDatabaseType.ELEMENT})
public @interface ElementDatabaseType {

    String SCREEN = "screen";
    String ELEMENT = "element";
}
