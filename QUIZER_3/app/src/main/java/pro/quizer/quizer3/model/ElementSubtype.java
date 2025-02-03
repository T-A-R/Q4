package pro.quizer.quizer3.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementSubtype.HTML,
        ElementSubtype.AUDIO,
        ElementSubtype.END,
        ElementSubtype.LIST,
        ElementSubtype.IMAGE,
        ElementSubtype.RANK,
        ElementSubtype.VIDEO,
        ElementSubtype.CONTAINER,
        ElementSubtype.ABORTED,
        ElementSubtype.SELECT,
        ElementSubtype.SCALE,
        ElementSubtype.QUOTA,
        ElementSubtype.TABLE,
        ElementSubtype.HIDDEN,
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
    String END = "end";
    String AUDIO = "audio";
    String IMAGE = "image";
    String STATUS_IMAGE = "status_image";
    String VIDEO = "video";
    String SLIDER = "slider";
    String QUOTA = "quota";
    String ABORTED = "aborted";
    String HIDDEN = "hidden";
}
