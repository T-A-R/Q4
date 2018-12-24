package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({AttributeType.TEXT,
        AttributeType.AUDIO,
        AttributeType.LIST,
        AttributeType.PHOTO,
        AttributeType.VIDEO,
        AttributeType.SLIDER})
public @interface AttributeType {

    String LIST = "list";
    String TABLE = "table";
    String TEXT = "text";
    String AUDIO = "audio";
    String PHOTO = "photo";
    String VIDEO = "video";
    String SLIDER = "slider";
}
