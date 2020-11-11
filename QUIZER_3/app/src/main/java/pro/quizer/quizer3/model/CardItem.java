package pro.quizer.quizer3.model;

public class CardItem {
    int id;
    String title;
    String desc;
    String data;
    boolean isUnChecker;
    boolean checked;

    public CardItem(int id, String title, String desc, boolean isUnChecker, boolean checked) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.isUnChecker = isUnChecker;
        this.checked = checked;
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
}
