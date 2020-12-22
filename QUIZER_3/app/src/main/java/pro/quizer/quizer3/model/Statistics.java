package pro.quizer.quizer3.model;

public class Statistics {
    private final Integer quoted;
    private final Integer unfinished;
    private final Integer rejected;
    private final Integer tested;
    private final Integer user_quoted;
    private final Integer user_unfinished;
    private final Integer user_rejected;
    private final Integer user_tested;

    public Statistics(Integer quoted, Integer unfinished, Integer rejected, Integer tested, Integer user_quoted, Integer user_unfinished, Integer user_rejected, Integer user_tested) {
        this.quoted = quoted;
        this.unfinished = unfinished;
        this.rejected = rejected;
        this.tested = tested;
        this.user_quoted = user_quoted;
        this.user_unfinished = user_unfinished;
        this.user_rejected = user_rejected;
        this.user_tested = user_tested;
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

    public Integer getUserQuoted() {
        return user_quoted;
    }

    public Integer getUserUnfinished() {
        return user_unfinished;
    }

    public Integer getUserRejected() {
        return user_rejected;
    }

    public Integer getUserTested() {
        return user_tested;
    }
}
