package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import pro.quizer.quizerexit.model.AttributeOpenType;
import pro.quizer.quizerexit.model.AttributeType;
import pro.quizer.quizerexit.utils.StringUtils;

import static pro.quizer.quizerexit.model.AttributeOpenType.CHECKBOX;

public class AttributesModel implements Serializable {

    @SerializedName("show_condition")
    private String show_condition;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("order")
    private int order;

    @SerializedName("number")
    private int number;

    @SerializedName("rotation")
    private boolean rotation;

    @SerializedName("fixed_order")
    private boolean fixed_order;

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

    @SerializedName("placeholder")
    private String placeholder;

    @SerializedName("unchecker")
    private boolean unchecker;

    public String getShowCondition() {
        return show_condition;
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public boolean isRotation() {
        return rotation;
    }

    public boolean isFixedOrder() {
        return fixed_order;
    }

    public boolean isPolyanswer() {
        return polyanswer;
    }

    public int getMinAnswers() {
        return min_answers;
    }

    public int getMaxAnswers() {
        return max_answers;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRecordSound() {
        return record_sound;
    }

    public int getNumber() {
        return number;
    }

    public int getMinValue() {
        return min_value;
    }

    public int getMaxValue() {
        return max_value;
    }

    public boolean isFlipColsAndRows() {
        return flip_cols_and_rows;
    }

    public boolean isComplicatedCells() {
        return complicated_cells;
    }

    public int getJump() {
        return jump;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getText() {
        return text;
    }

    public String getJumpCondition() {
        return jump_condition;
    }

    @AttributeOpenType
    public String getOpenType() {
        return StringUtils.isEmpty(open_type) ? CHECKBOX : open_type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public boolean isUnchecker() {
        return unchecker;
    }
}