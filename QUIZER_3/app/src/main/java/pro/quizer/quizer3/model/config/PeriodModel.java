package pro.quizer.quizer3.model.config;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PeriodModel implements Serializable {

    @SerializedName("start")
    private Long start;

    @SerializedName("end")
    private Long end;

    public PeriodModel() {
    }

    public PeriodModel(Long start, Long end) {
        this.start = start;
        this.end = end;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }
}
