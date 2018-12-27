package pro.quizer.quizerexit.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ElementType.BOX,
        ElementType.QUESTION,
        ElementType.LIST,
        ElementType.INFO,
        ElementType.SCALE,
        ElementType.RANK,
        ElementType.RANGE,
        ElementType.TABLE,
        ElementType.ROUTER,
        ElementType.FUNNEL,
        ElementType.CONTENT,
        ElementType.ANSWER})
public @interface ElementType {

    String QUESTION = "question";
    String INFO = "info";
    String BOX = "box";
    String LIST = "question:list";
    String SCALE = "question:scale";
    String RANK = "question:rank";
    String RANGE = "question:range";
    String TABLE = "question:table";
    String ROUTER = "router";
    String FUNNEL = "funnel";
    String CONTENT = "content";
    String ANSWER = "answer";
}
