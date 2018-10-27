package pro.quizer.quizerexit.model.config;

import com.google.gson.annotations.SerializedName;

public class AnswerOptionsField {

    @SerializedName("is_open")
    private int is_open;

    @SerializedName("persistent_order")
    private int persistent_order;

    @SerializedName("unchecker")
    private int unchecker;

    @SerializedName("picture")
    private String picture;

    @SerializedName("show_if")
    private String show_if;

    @SerializedName("not_show_if")
    private String not_show_if;

    @SerializedName("goto_if")
    private String goto_if;

    public AnswerOptionsField() {
    }

    public int getIsOpen() {
        return is_open;
    }

    public int getPersistentOrder() {
        return persistent_order;
    }

    public int getUnchecker() {
        return unchecker;
    }

    public String getPicture() {
        return picture;
    }

    public String getShowIf() {
        return show_if;
    }

    public String getNotShowIf() {
        return not_show_if;
    }

    public String getGotoIf() {
        return goto_if;
    }
}