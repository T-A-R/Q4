package pro.quizer.quizer3.model;

public class Statistics {
    private final Integer quoted;
    private final Integer unfinished;
    private final Integer rejected;
    private final Integer tested;

    public Statistics(Integer quoted, Integer unfinished, Integer rejected, Integer tested) {
        this.quoted = quoted;
        this.unfinished = unfinished;
        this.rejected = rejected;
        this.tested = tested;
    }

    public Integer getQuotas() {
        return quoted;
    }

    public Integer getAborted() {
        return unfinished;
    }

    public Integer getDefective() {
        return rejected;
    }

    public Integer getTests() {
        return tested;
    }
}
