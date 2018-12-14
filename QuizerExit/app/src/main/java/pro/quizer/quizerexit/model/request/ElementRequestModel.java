package pro.quizer.quizerexit.model.request;

public class ElementRequestModel {

    private final Integer relative_id;
    private final Long duration;
    private final Integer click_rank;
    private final Integer rank;
    private final String value;

    public ElementRequestModel(Integer relative_id, Long duration, Integer click_rank, Integer rank, String value) {
        this.relative_id = relative_id;
        this.duration = duration;
        this.click_rank = click_rank;
        this.rank = rank;
        this.value = value;
    }
}
