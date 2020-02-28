package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;

public class ElementModelFlat implements Serializable {

    @SerializedName("relative_id")
    private int relative_id;

    @SerializedName("relative_parent_id")
    private int relative_parent_id;

    @ElementType
    @SerializedName("type")
    private String type;

    @ElementSubtype
    @SerializedName("subtype")
    private String subtype;

    @SerializedName("options")
    private OptionsModelNew options;

    @SerializedName("contents")
    private List<Contents> contents;

    // @Ignore start
    private Integer questionnaireId;
    private Integer shown_at_id = -102;
    private Integer done = -102;
    private Integer limit = -102;
    private boolean checked = false;
    private boolean enabled = true;
    private boolean was_shown = false;
    // @Ignore end

    public int getRelativeID() {
        return relative_id;
    }

    public int getRelativeParentID() {
        return relative_parent_id;
    }

    @ElementType
    public String getType() {
        return type;
    }

    @ElementSubtype
    public String getSubtype() {
        return subtype;
    }

    public List<Contents> getContents() {
        return contents;
    }

    public OptionsModelNew getOptions() {
        return options;
    }

    // @Ignore start

    public Integer getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Integer questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public Integer getShown_at_id() {
        return shown_at_id;
    }

    public void setShown_at_id(Integer shown_at_id) {
        this.shown_at_id = shown_at_id;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isWas_shown() {
        return was_shown;
    }

    public void setWas_shown(boolean was_shown) {
        this.was_shown = was_shown;
    }

    // @Ignore end
}