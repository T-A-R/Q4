package pro.quizer.quizer3.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import pro.quizer.quizer3.Constants;

@Entity(indices = {@Index("user_id")})
public class RegistrationR {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int user_id;

    @ColumnInfo(name = "uik_number")
    private String uik_number;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "gps")
    private String gps;

    @ColumnInfo(name = "gps_network")
    private String gps_network;

    @ColumnInfo(name = "gps_time")
    private Long gps_time;

    @ColumnInfo(name = "gps_time_network")
    private Long gps_time_network;

    @ColumnInfo(name = "reg_time")
    private Long reg_time;

    @ColumnInfo(name = "status")
    private String status;

    public RegistrationR() {
        this.status = Constants.Registration.NOT_SENT;
    }

    public RegistrationR(int user_id, String uik_number, String phone, String gps, String gps_network, Long gps_time, Long gps_time_network, Long reg_time) {
        this.user_id = user_id;
        this.uik_number = uik_number;
        this.phone = phone;
        this.gps = gps;
        this.gps_network = gps_network;
        this.gps_time = gps_time;
        this.gps_time_network = gps_time_network;
        this.reg_time = reg_time;
        this.status = Constants.Registration.NOT_SENT;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUik_number() {
        return uik_number;
    }

    public void setUik_number(String uik_number) {
        this.uik_number = uik_number;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getGps_network() {
        return gps_network;
    }

    public void setGps_network(String gps_network) {
        this.gps_network = gps_network;
    }

    public Long getGps_time() {
        return gps_time;
    }

    public void setGps_time(Long gps_time) {
        this.gps_time = gps_time;
    }

    public Long getGps_time_network() {
        return gps_time_network;
    }

    public void setGps_time_network(Long gps_time_network) {
        this.gps_time_network = gps_time_network;
    }

    public Long getReg_time() {
        return reg_time;
    }

    public void setReg_time(Long reg_time) {
        this.reg_time = reg_time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAccepted() {
        return status.equals(Constants.Registration.SENT) || status.equals(Constants.Registration.SMS) || status.equals(Constants.Registration.SENT_NO_SMS);
    }

    public boolean isCode() {
        return status.equals(Constants.Registration.CODE);
    }

    public boolean smsClosed() {
        return status.equals(Constants.Registration.SENT_NO_SMS);
    }

    public boolean notSent() {
        return status.equals(Constants.Registration.NOT_SENT) || status.equals(Constants.Registration.SMS);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
