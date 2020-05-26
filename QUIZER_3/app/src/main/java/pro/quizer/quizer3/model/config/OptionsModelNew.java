package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.model.OptionsOpenType;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;

public class OptionsModelNew implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("jump")
    private Integer jump;

    @SerializedName("search")
    private boolean search;

    @SerializedName("pre_condition")
    private String pre_condition;

    @SerializedName("post_condition")
    private String post_condition;

    @SerializedName("data")
    private String data;

    @SerializedName("order")
    private Integer order;

    @SerializedName("number")
    private Integer number;

    @SerializedName("polyanswer")
    private boolean polyanswer;

    @SerializedName("record_sound")
    private boolean record_sound;

    @SerializedName("take_photo")
    private boolean take_photo;

    @SerializedName("description")
    private String description;

    @SerializedName("flip_cols_and_rows")
    private boolean flip_cols_and_rows;

    @SerializedName("rotation")
    private boolean rotation;

    @SerializedName("fixed_order")
    private boolean fixed_order;

    @SerializedName("min_answers")
    private Integer min_answers;

    @SerializedName("max_answers")
    private Integer max_answers;

    @SerializedName("open_type")
    private String open_type;

    @SerializedName("placeholder")
    private String placeholder;

    @SerializedName("unchecker")
    private boolean unchecker;

    @SerializedName("start_value")
    private Integer start_value;

    @SerializedName("end_value")
    private Integer end_value;

    @SerializedName("status_image")
    private StatusImage status_image;

    @SerializedName("is_media")
    private boolean is_media;

    @SerializedName("type_behavior")
    private String type_behavior;

    @SerializedName("show_scale")
    private boolean show_scale;

    @SerializedName("show_images")
    private boolean show_images;

    @SerializedName("unnecessary_fill_open")
    private boolean unnecessary_fill_open;

    @SerializedName("type_end")
    private Integer type_end;

    public String getData() {
        return data;
    }

    public StatusImage getStatusImage() {
        return status_image;
    }

    public String getPreCondition() {
        return pre_condition;
    }

    public boolean isSearch() {
        return search;
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

    public Integer getMinAnswers() {
        return min_answers;
    }

    public Integer getMaxAnswers() {
        return max_answers;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRecordSound() {
        return record_sound;
    }

    public Integer getNumber() {
        return number;
    }

    public boolean isFlipColsAndRows() {
        return flip_cols_and_rows;
    }

    public void setJump(final int pJump) {
        jump = pJump;
    }

    public Integer getJump() {
        return jump;
    }

    @OptionsOpenType
    public String getOpenType() {
        return StringUtils.isEmpty(open_type) ? CHECKBOX : open_type;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public boolean isTakePhoto() {
        return take_photo;
    }

    public boolean isUnchecker() {
        return unchecker;
    }

    public String getTitle(final MainActivity pBaseActivity, final HashMap<Integer, ElementModelNew> pMap) {
        return ConditionUtils.formatTitle(pBaseActivity, title, pMap);
    }

    public String getTitle() {
        return title;
    }

    public String getPre_condition() {
        return pre_condition;
    }

    public String getPost_condition() {
        return post_condition;
    }

    public boolean isRecord_sound() {
        return record_sound;
    }

    public boolean isTake_photo() {
        return take_photo;
    }

    public boolean isFlip_cols_and_rows() {
        return flip_cols_and_rows;
    }

    public boolean isFixed_order() {
        return fixed_order;
    }

    public Integer getMin_answers() {
        return min_answers;
    }

    public Integer getMax_answers() {
        return max_answers;
    }

    public String getOpen_type() {
        return open_type;
    }

    public Integer getStart_value() {
        return start_value;
    }

    public Integer getEnd_value() {
        return end_value;
    }

    public StatusImage getStatus_image() {
        return status_image;
    }

    public boolean isIs_media() {
        return is_media;
    }

    public String getType_behavior() {
        return type_behavior;
    }

    public void setType_behavior(String type_behavior) {
        this.type_behavior = type_behavior;
    }

    public boolean isShow_scale() {
        return show_scale;
    }

    public void setShow_scale(boolean show_scale) {
        this.show_scale = show_scale;
    }

    public boolean isShow_images() {
        return show_images;
    }

    public void setShow_images(boolean show_images) {
        this.show_images = show_images;
    }

    public boolean isUnnecessary_fill_open() {
        return unnecessary_fill_open;
    }

    public void setUnnecessary_fill_open(boolean unnecessary_fill_open) {
        this.unnecessary_fill_open = unnecessary_fill_open;
    }

    public Integer getType_end() {
        return type_end;
    }

    public void setType_end(Integer type_end) {
        this.type_end = type_end;
    }
}