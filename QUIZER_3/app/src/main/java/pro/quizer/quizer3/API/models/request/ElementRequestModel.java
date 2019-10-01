package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

public class ElementRequestModel implements Serializable {

    private final Integer relative_id;
    private final Long duration;
    private final Integer click_rank;
    private final Integer rank;
    private final String value;
    private final boolean send_sms;

    public ElementRequestModel(Integer relative_id, Long duration, Integer click_rank, Integer rank, String value, boolean send_sms) {
        this.relative_id = relative_id;
        this.duration = duration;
        this.click_rank = click_rank;
        this.rank = rank;
        this.value = value;
        this.send_sms = send_sms;
    }

}
