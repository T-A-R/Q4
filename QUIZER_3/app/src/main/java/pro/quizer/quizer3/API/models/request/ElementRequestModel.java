package pro.quizer.quizer3.API.models.request;

import java.io.Serializable;

public class ElementRequestModel implements Serializable {

    private final Integer relative_id;
    private final Long duration;
    private Integer click_rank;
    private Integer rank;
    private String value;
    private Boolean send_sms;
    private Boolean showed_in_card;
    private Boolean checked_in_card;

    public ElementRequestModel(Integer relative_id, Long duration) {
        this.relative_id = relative_id;
        this.duration = duration;
    }

    public ElementRequestModel(
            Integer relative_id,
            Long duration,
            Integer click_rank,
            Integer rank,
            String value,
            Boolean send_sms,
            Boolean card_showed,
            Boolean checked_in_card) {
        this.relative_id = relative_id;
        this.duration = duration;
        this.click_rank = click_rank;
        this.rank = rank;
        this.value = value;
        this.send_sms = send_sms;
        this.showed_in_card = card_showed;
        this.checked_in_card = checked_in_card;
    }

    public void setClick_rank(Integer click_rank) {
        this.click_rank = click_rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSend_sms(Boolean send_sms) {
        this.send_sms = send_sms;
    }

    public void setCard_showed(Boolean card_showed) {
        this.showed_in_card = card_showed;
    }

    public void setChecked_in_card(Boolean checked_in_card) {
        this.checked_in_card = checked_in_card;
    }
}
