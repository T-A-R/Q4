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

    @SerializedName("small_column")
    private boolean small_column;

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

    @SerializedName("with_card")
    private Boolean with_card;

    @SerializedName("show_in_card")
    private Boolean show_in_card;

    @SerializedName("auto_check")
    private boolean auto_check;

    @SerializedName("prev_condition")
    private String prev_condition;

    @SerializedName("helper")
    private boolean helper;

    @SerializedName("photo_answer")
    private boolean photo_answer;

    @SerializedName("photo_answer_required")
    private boolean photo_answer_required;

    @SerializedName("min_number")
    private Integer min_number;

    @SerializedName("max_number")
    private Integer max_number;

    @SerializedName("show_random_question")
    private Boolean show_random_question;

    @SerializedName("hide_numbers_answers")
    private Boolean hide_numbers_answers;

    @SerializedName("optional_question")
    private Boolean optional_question;

    @SerializedName("is_cancel_survey")
    private Boolean is_cancel_survey;

    @SerializedName("is_use_absentee")
    private Boolean is_use_absentee;

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

    public void setTitle(String title) {
        this.title = title;
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

    public boolean getWithCard() {
        if (with_card == null) {
            return false;
        } else
            return with_card;
    }

    public void setWithCard(boolean with_card) {
        this.with_card = with_card;
    }

    public boolean getShowInCard() {
        if (show_in_card == null) {
            return false;
        } else
            return show_in_card;
    }

    public void setShowInCard(boolean show_in_card) {
        this.show_in_card = show_in_card;
    }

    public boolean isAutoCheck() {
        return auto_check;
    }

    public void setAuto_check(boolean auto_check) {
        this.auto_check = auto_check;
    }

    public boolean isSmallColumns() {
        return small_column;
    }

    public void setSmallColumns(boolean small_column) {
        this.small_column = small_column;
    }

    public String getPrevCondition() {
        return prev_condition;
    }

    public boolean isHelper() {
        return helper;
    }

    public void setHelper(boolean helper) {
        this.helper = helper;
    }

    public boolean isPhotoAnswer() {
        return photo_answer;
    }

    public void setPhotoAnswer(boolean photo_answer) {
        this.photo_answer = photo_answer;
    }

    public boolean isPhotoAnswerRequired() {
        return photo_answer_required;
    }

    public void setPhotoAnswerRequired(boolean photo_answer_required) {
        this.photo_answer_required = photo_answer_required;
    }

    public Integer getMinNumber() {
        return min_number;
    }

    public void setMinNumber(Integer min_number) {
        this.min_number = min_number;
    }

    public Integer getMaxNumber() {
        return max_number;
    }

    public void setMaxNumber(Integer max_number) {
        this.max_number = max_number;
    }

    public Boolean getShowRandomQuestion() {
        return show_random_question;
    }

    public void setShowRandomQuestion(Boolean showRandomQuestion) {
        this.show_random_question = showRandomQuestion;
    }

    public Boolean getHideNumbersAnswers() {
        return hide_numbers_answers;
    }

    public void setHideNumbersAnswers(Boolean hide_numbers_answers) {
        this.hide_numbers_answers = hide_numbers_answers;
    }

    public Boolean isOptionalQuestion() {
        return optional_question;
    }

    public void setOptionalQuestion(Boolean optional_question) {
        this.optional_question = optional_question;
    }

    public Boolean isCancelSurvey() {
        return is_cancel_survey;
    }

    public Boolean isUseAbsentee() {
        return is_use_absentee;
    }
}