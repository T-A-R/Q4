package pro.quizer.quizerexit.model.logs;

public class Crash {
    private String message;

    public Crash(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
