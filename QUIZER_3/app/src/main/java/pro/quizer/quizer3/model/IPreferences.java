package pro.quizer.quizer3.model;

public interface IPreferences {
    boolean getBoolean(String key, boolean def);
    void putBoolean(String key, boolean value);
    String getString(String key, String def);
    void putString(String key, String value);
}
