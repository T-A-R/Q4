package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementType.BOX,
        ElementType.QUESTION,
        ElementType.TABLE,
        ElementType.ROUTER,
        ElementType.FUNNEL,
        ElementType.CONTENT,
        ElementType.ANSWER})
public @interface ElementType {

    String INFO = "info";
    String QUESTION = "question";
    String BOX = "box";
    String TABLE = "table";
    String ROUTER = "router";
    String FUNNEL = "funnel";
    String CONTENT = "content";
    String ANSWER = "answer";
}
