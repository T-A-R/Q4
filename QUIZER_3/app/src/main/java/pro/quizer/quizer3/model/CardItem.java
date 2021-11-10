package pro.quizer.quizer3.model;

import java.util.List;

public class CardItem {
    int id;
    String title;
    String desc;
    String data;
    List<String> pic;
    String thumb;
    boolean isUnChecker;
    boolean checked;
    boolean unnecessaryFillOpen;
    boolean isAutoCkecker;
    String open;
    String hint;
    boolean helper;

    public CardItem() {
        this.helper = false;
    }

    public CardItem(int id, String title, String desc, boolean isUnChecker, boolean checked, String open, String hint , boolean unnecessaryFillOpen, boolean isAutoCkecker, boolean helper) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.isUnChecker = isUnChecker;
        this.checked = checked;
        this.open = open;
        this.hint = hint;
        this.unnecessaryFillOpen = unnecessaryFillOpen;
        this.isAutoCkecker = isAutoCkecker;
        this.helper = helper;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isUnChecker() {
        return isUnChecker;
    }

    public void setUnChecker(boolean isUnChecker) {
        this.isUnChecker = isUnChecker;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isUnnecessaryFillOpen() {
        return unnecessaryFillOpen;
    }

    public void setUnnecessaryFillOpen(boolean unnecessaryFillOpen) {
        this.unnecessaryFillOpen = unnecessaryFillOpen;
    }

    public boolean isAutoCkecker() {
        return isAutoCkecker;
    }

    public void setAutoCkecker(boolean autoCkecker) {
        isAutoCkecker = autoCkecker;
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setHelper(boolean helper) {
        this.helper = helper;
    }

    public boolean isHelper() {
        return helper;
    }
}
