package pro.quizer.quizer3.model.state;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnswerState implements Serializable {

    @SerializedName("relative_id")
    private Integer relative_id;

    @SerializedName("checked")
    private boolean checked;

    @SerializedName("open")
    private boolean open;

    @SerializedName("data")
    private String data;

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("is_photo_answer")
    private boolean is_photo_answer;

    @SerializedName("has_photo")
    private boolean has_photo;

    @SerializedName("checkedInCard")
    private boolean checkedInCard;

    public AnswerState() {
        this.enabled = true;
        this.has_photo = false;
        this.checkedInCard = false;
    }

    public AnswerState(Integer relative_id, boolean checked, String data) {
        this.relative_id = relative_id;
        this.checked = checked;
        this.data = data;
        this.enabled = true;
        this.has_photo = false;
        this.is_photo_answer = false;
        this.checkedInCard = false;
    }

    public AnswerState(Integer relative_id, boolean checked, boolean open, String data, boolean enabled) {
        this.relative_id = relative_id;
        this.checked = checked;
        this.open = open;
        this.data = data;
        this.enabled = enabled;
        this.has_photo = false;
        this.is_photo_answer = false;
        this.checkedInCard = false;
    }

    public AnswerState(Integer relative_id, boolean checked, boolean open, String data, boolean enabled, boolean is_photo_answer, boolean has_photo, boolean checkedInCard) {
        this.relative_id = relative_id;
        this.checked = checked;
        this.open = open;
        this.data = data;
        this.enabled = enabled;
        this.is_photo_answer = is_photo_answer;
        this.has_photo = has_photo;
        this.checkedInCard = checkedInCard;
    }

    public Integer getRelative_id() {
        return relative_id;
    }

    public void setRelative_id(Integer relative_id) {
        this.relative_id = relative_id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean hasPhoto() {
        return has_photo;
    }

    public void setHasPhoto(boolean has_photo) {
        this.has_photo = has_photo;
    }

    public boolean isIsPhotoAnswer() {
        return is_photo_answer;
    }

    public void setIsPhotoAnswer(boolean is_photo_answer) {
        this.is_photo_answer = is_photo_answer;
    }

    public boolean isCheckedInCard() {
        return checkedInCard;
    }

    public void setCheckedInCard(boolean checkedInCard) {
        this.checkedInCard = checkedInCard;
    }
}
