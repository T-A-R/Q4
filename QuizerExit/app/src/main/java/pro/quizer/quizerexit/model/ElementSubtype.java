package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementSubtype.TEXT,
        ElementSubtype.AUDIO,
        ElementSubtype.LIST,
        ElementSubtype.IMAGE,
        ElementSubtype.VIDEO,
        ElementSubtype.SLIDER})
public @interface ElementSubtype {

    String CONTAINER = "container";
    String FUNNEL = "funnel";
    String TABLE = "table";
    String ONESCREEN = "onescreen";
    String LIST = "list";
    String SCALE = "scale";
    String RANK = "rank";
    String RANGE = "range";
    String TEXT = "text";
    String AUDIO = "audio";
    String IMAGE = "image";
    String VIDEO = "video";
    String SLIDER = "slider";
}
