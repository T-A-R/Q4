package pro.quizer.quizerexit.database.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import pro.quizer.quizerexit.Constants;

@Entity
public class AppLogsR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "device")
    private String device;

    @ColumnInfo(name = "appversion")
    private String appversion;

    @ColumnInfo(name = "android")
    private String android;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "object")
    private String object;

    @ColumnInfo(name = "action")
    private String action;

    @ColumnInfo(name = "result")
    private String result;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "status")
    private String status;

    public AppLogsR() {
        this.status = Constants.LogStatus.NOT_SENT;
    }

    public AppLogsR(String login, String device, String appversion, String android, String date, String type, String object, String action, String result, String description) {
        this.login = login;
        this.device = device;
        this.appversion = appversion;
        this.android = android;
        this.date = date;
        this.type = type;
        this.object = object;
        this.action = action;
        this.result = result;
        this.description = description;
        this.data = null;
        this.status = Constants.LogStatus.NOT_SENT;
    }

    public AppLogsR(String login, String device, String appversion, String android, String date, String type, String object, String action, String result, String description, String data) {
        this.login = login;
        this.device = device;
        this.appversion = appversion;
        this.android = android;
        this.date = date;
        this.type = type;
        this.object = object;
        this.action = action;
        this.result = result;
        this.description = description;
        this.data = data;
        this.status = Constants.LogStatus.NOT_SENT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
