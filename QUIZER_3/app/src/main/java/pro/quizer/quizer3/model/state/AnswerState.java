package pro.quizer.quizer3.model.state;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AnswerState implements Serializable {
//public class AnswerState {

    @SerializedName("relative_id")
    private Integer relative_id;

    @SerializedName("checked")
    private boolean checked;

    @SerializedName("open")
    private boolean open;

    @SerializedName("data")
    private String data;

    public AnswerState() {
    }

    public AnswerState(Integer relative_id, boolean checked, String data) {
        this.relative_id = relative_id;
        this.checked = checked;
        this.data = data;
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
}
