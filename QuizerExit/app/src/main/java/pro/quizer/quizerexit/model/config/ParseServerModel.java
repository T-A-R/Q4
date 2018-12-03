package pro.quizer.quizerexit.model.config;

public class ParseServerModel {

    private String mServerUrl;
    private String mLoginAdmin;

    public ParseServerModel(String mServerUrl, String mLoginAdmin) {
        this.mServerUrl = mServerUrl;
        this.mLoginAdmin = mLoginAdmin;
    }

    public String getServerUrl() {
        return mServerUrl;
    }

    public String getLoginAdmin() {
        return mLoginAdmin;
    }
}
