package pro.quizer.quizer3.model;

public class CardItem {
    int id;
    String title;
    String desc;
    String data;
    boolean isUnChecker;
    boolean checked;
    boolean unnecessaryFillOpen;
    String open;
    String hint;

    public CardItem(int id, String title, String desc, boolean isUnChecker, boolean checked, String open, String hint , boolean unnecessaryFillOpen) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.isUnChecker = isUnChecker;
        this.checked = checked;
        this.open = open;
        this.hint = hint;
        this.unnecessaryFillOpen = unnecessaryFillOpen;
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
}
