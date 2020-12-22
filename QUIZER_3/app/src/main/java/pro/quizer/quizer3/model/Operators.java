package pro.quizer.quizer3.model;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({Operators.LESS,
        Operators.MORE,
        Operators.LOE,
        Operators.MOE,
        Operators.EQ,
        Operators.NOT})
public @interface Operators {

    String LESS = "<";
    String MORE = ">";
    String LOE = "<=";
    String MOE = ">=";
    String EQ = "==";
    String NOT = "!=";
}
