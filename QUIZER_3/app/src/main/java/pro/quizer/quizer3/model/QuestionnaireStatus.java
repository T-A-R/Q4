package pro.quizer.quizer3.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({QuestionnaireStatus.SENT,
        QuestionnaireStatus.NOT_SENT})
public @interface QuestionnaireStatus {

    String SENT = "sent";
    String NOT_SENT = "not_sent";
}
