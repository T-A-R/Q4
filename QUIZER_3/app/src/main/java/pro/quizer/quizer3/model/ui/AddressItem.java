package pro.quizer.quizer3.model.ui;

public class AddressItem {

    private String address;
    private String uik;

    public AddressItem(String address, String uik) {
        this.address = address;
        this.uik = uik;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUik() {
        return uik;
    }

    public void setUik(String uik) {
        this.uik = uik;
    }
}
