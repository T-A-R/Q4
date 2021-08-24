package pro.quizer.quizer3.API.models.request;

public class RegistrationRequestModel {
    private String admin_key;
    private int user_id;
    private String uik_number;
    private String phone;
    private String gps;
    private String gps_network;
    private Long gps_time;
    private Long gps_time_network;
    private Long reg_time;
    private boolean fake_gps;

    public RegistrationRequestModel(String admin_key, int user_id, String uik_number, String phone, String gps, String gps_network, Long gps_time, Long gps_time_network, Long reg_time, boolean fake_gps) {
        this.admin_key = admin_key;
        this.user_id = user_id;
        this.uik_number = uik_number;
        this.phone = phone;
        this.gps = gps;
        this.gps_network = gps_network;
        this.gps_time = gps_time;
        this.gps_time_network = gps_time_network;
        this.reg_time = reg_time;
        this.fake_gps = fake_gps;
    }

    public String getAdmin_key() {
        return admin_key;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUik_number() {
        return uik_number;
    }

    public String getPhone() {
        return phone;
    }

    public String getGps() {
        return gps;
    }

    public String getGps_network() {
        return gps_network;
    }

    public Long getGps_time() {
        return gps_time;
    }

    public Long getGps_time_network() {
        return gps_time_network;
    }

    public Long getReg_time() {
        return reg_time;
    }

    public boolean isFake_gps() {
        return fake_gps;
    }
}
