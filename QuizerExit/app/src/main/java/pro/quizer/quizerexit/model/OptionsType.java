package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({OptionsType.TEXT,
        OptionsType.AUDIO,
        OptionsType.LIST,
        OptionsType.PHOTO,
        OptionsType.VIDEO,
        OptionsType.SLIDER})
public @interface OptionsType {

    String LIST = "list";
    String TABLE = "table";
    String TEXT = "text";
    String AUDIO = "audio";
    String PHOTO = "photo";
    String VIDEO = "video";
    String SLIDER = "slider";
}
