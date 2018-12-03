package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizerexit.model.AttributeOpenType;
import pro.quizer.quizerexit.model.AttributeType;
import pro.quizer.quizerexit.model.ElementType;

public class AttributesField implements Serializable {

    @SerializedName("show_condition")
    private String show_condition;

    @SerializedName("title")
    private String title;

    @SerializedName("order")
    private int order;

    @SerializedName("rotation")
    private boolean rotation;

    @SerializedName("static_order")
    private boolean static_order;

    @SerializedName("polyanswer")
    private boolean polyanswer;

    @SerializedName("min_answers")
    private int min_answers;

    @SerializedName("max_answers")
    private int max_answers;

    @SerializedName("record_sound")
    private boolean record_sound;

    @SerializedName("min_value")
    private int min_value;

    @SerializedName("max_value")
    private int max_value;

    @SerializedName("flip_cols_and_rows")
    private boolean flip_cols_and_rows;

    @SerializedName("complicated_cells")
    private boolean complicated_cells;

    @SerializedName("jump")
    private int jump;

    @AttributeType
    @SerializedName("type")
    private String type;

    @SerializedName("link")
    private String link;

    @SerializedName("text")
    private String text;

    @SerializedName("jump_condition")
    private String jump_condition;

    @AttributeOpenType
    @SerializedName("open_type")
    private String open_type;

    @SerializedName("unchecker")
    private boolean unchecker;

}