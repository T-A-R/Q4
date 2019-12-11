package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

//import pro.quizer.quizer3.activity.BaseActivity;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.model.OptionsOpenType;
import pro.quizer.quizer3.model.quota.QuotaModel;
//import pro.quizer.quizer3.utils.ConditionUtils;
//import pro.quizer.quizer3.utils.QuotasUtils;
import pro.quizer.quizer3.utils.ConditionUtils;
import pro.quizer.quizer3.utils.StringUtils;

import static pro.quizer.quizer3.model.OptionsOpenType.CHECKBOX;

public class OptionsModel implements Serializable {

    @SerializedName("pre_condition")
    private String pre_condition;

    @SerializedName("title")
    private String title;

    @SerializedName("search")
    private boolean search;

    @SerializedName("description")
    private String description;

    @SerializedName("status_image")
        private ElementModel status_image;

    @SerializedName("data")
    private String data;

    @SerializedName("order")
    private int order;

    @SerializedName("number")
    private int number;

    @SerializedName("rotation")
    private boolean rotation;

    @SerializedName("rotationAnswers")
    private boolean rotationAnswers;

    @SerializedName("fixed_order")
    private boolean fixed_order;

    @SerializedName("polyanswer")
    private boolean polyanswer;

    @SerializedName("min_answers")
    private int min_answers;

    @SerializedName("max_answers")
    private int max_answers;

    @SerializedName("take_photo")
    private boolean take_photo;

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

    @SerializedName("text")
    private String text;

    @SerializedName("jump_condition")
    private String jump_condition;

    @OptionsOpenType
    @SerializedName("open_type")
    private String open_type;

    @SerializedName("placeholder")
    private String placeholder;

    @SerializedName("unchecker")
    private boolean unchecker;

    @SerializedName("is_media")
    private boolean is_media;

    public String getData() {
        return data;
    }

    public ElementModel getStatusImage() {
        return status_image;
    }

    public String getPreCondition() {
        return pre_condition;
    }

    public String getTitle(final MainActivity pBaseActivity, final HashMap<Integer, ElementModelNew> pMap) {
        return ConditionUtils.formatTitle(pBaseActivity, title, pMap);
    }


    public String getTitle() {
        return title;
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
    public boolean isRotationAnswers() {
        return rotationAnswers;
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

    public void setJump(final int pJump) {
        jump = pJump;
    }

    public boolean isComplicatedCells() {
        return complicated_cells;
    }

    public int getJump() {
        return jump;
    }

    public String getText() {
        return text;
    }

//    public boolean isEnabled(final List<QuotaModel> quotas, final BaseActivity pBaseActivity, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel) {
//        return QuotasUtils.isCanDisplayed(quotas, mMap, pElementModel, pBaseActivity);
//    }
//
//    public boolean isCanShow(final BaseActivity pBaseActivity, final HashMap<Integer, ElementModel> mMap, final ElementModel pElementModel) {
//        return ConditionUtils.CAN_SHOW == ConditionUtils.evaluateCondition(getPreCondition(), mMap, pBaseActivity);
//    }

    public String getJumpCondition() {
        return jump_condition;
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

    public boolean isMedia() {
        return is_media;
    }
}