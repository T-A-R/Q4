package pro.quizer.quizer3.API.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Session {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public static Session clone(Session session) {
        if (session == null) {
            return null;
        }
        Session newSession = new Session();
        newSession.uuid = session.getUuid();
        newSession.source = session.getSource();
        newSession.createdAt = session.getCreatedAt();
        newSession.updatedAt = session.getUpdatedAt();

        return newSession;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

