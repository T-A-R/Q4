package pro.quizer.quizer3.model.state;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SelectItem implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("checked")
    private boolean checked;

    @SerializedName("enabled")
    private boolean enabled;

    public SelectItem(String title, boolean checked, boolean enabled) {
        this.title = title;
        this.checked = checked;
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}
