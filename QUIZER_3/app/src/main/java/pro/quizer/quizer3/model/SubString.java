package pro.quizer.quizer3.model;

public class SubString {

    private boolean has;
    private Integer start;
    private Integer end;
    private String value;
    private String type;
    private Integer relativeId;

    public SubString(boolean has, Integer start, Integer end, String value, String type, Integer relativeId) {
        this.has = has;
        this.start = start;
        this.end = end;
        this.value = value;
        this.type = type;
        this.relativeId = relativeId;
    }

    public boolean isTrue() {
        return has;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Integer getRelativeId() {
        return relativeId;
    }
}
