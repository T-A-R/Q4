package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementSubtype.HTML,
        ElementSubtype.AUDIO,
        ElementSubtype.LIST,
        ElementSubtype.IMAGE,
        ElementSubtype.VIDEO,
        ElementSubtype.SLIDER})
public @interface ElementSubtype {

    String SELECT = "select";
    String CONTAINER = "container";
    String FUNNEL = "funnel";
    String PAGE = "page";
    String TABLE = "table";
    String INFO = "info";
    String LIST = "list";
    String SCALE = "scale";
    String RANK = "rank";
    String RANGE = "range";
    String HTML = "html";
    String AUDIO = "audio";
    String IMAGE = "image";
    String STATUS_IMAGE = "status_image";
    String VIDEO = "video";
    String SLIDER = "slider";
}
