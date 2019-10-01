package pro.quizer.quizer3.model;

import pro.quizer.quizer3.API.models.UserModel;

public class User {
    static private final String KEY_FIRST_START = "firstStart";
    static private final String KEY_FIRST_START_MAP = "firstStartMap";
    static private final String KEY_TOKEN = "token";
    static private final String KEY_NOTIFY = "notify";
    static private final String KEY_CITY = "cityName";
    static private final String KEY_DELEGATE_MODE = "delegateMode";

    static public final int REQUEST_CODE_GPS = 123;

    static private final String DEF_CITY = "Москва";

    static private User inst;
    private IPreferences preferences;

    private ModeChangeListener modeChangeListener;
    private CityDetectListener cityDetectListener;
    private PlaceUpdateListener placeUpdateListener;
    private UserUpdateListener userUpdateListener;

    private UserModel currentUser;

    static public User getUser() {
        if (inst == null)
            inst = new User();

        return inst;
    }

    private User() {

    }

    public void setPreferences(IPreferences preferences) {
        this.preferences = preferences;
    }

    public void updateUser(UpdateUserCallback callback) {
//        new Thread(() -> ServerAPI.getCurrentUser(data -> {
//            if (data == null) {
//                if (callback != null) {
//                    callback.onUserUpdated(false);
//                }
//                return;
//            }
//            currentUser = data;
//
//            if (userUpdateListener != null)
//                userUpdateListener.onUserUpdated();
//
//            if (callback != null)
//                callback.onUserUpdated(true);
//
//        })).start();
    }

    public interface UpdateUserCallback {
        void onUserUpdated(boolean ok);
    }

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public String getUserUuid() {
        return currentUser == null ? null : currentUser.getUuid();
    }

    public void logout() {
        setAuthToken(null);
        setDelegateMode(false);
    }

    public boolean isAuthorized() {
        return getAuthToken() != null;
    }

    public boolean isDelegateMode() {
        return preferences.getBoolean(KEY_DELEGATE_MODE, false);
    }

    public void setDelegateMode(boolean delegate) {
        if (isDelegateMode() == delegate)
            return;

        preferences.putBoolean(KEY_DELEGATE_MODE, delegate);
        modeChangeListener.onModeChanged();
    }

    public void setModeChangeListener(ModeChangeListener listener) {
        modeChangeListener = listener;
    }

    public void setCityDetectListener(CityDetectListener callback) {
        this.cityDetectListener = callback;
    }

    public boolean isFirstStart() {
        return preferences.getBoolean(KEY_FIRST_START, true);
    }

    public void setFirstStart(boolean notify) {
        preferences.putBoolean(KEY_FIRST_START, notify);
    }

    public boolean isFirstStartMap() {
        return preferences.getBoolean(KEY_FIRST_START_MAP, true);
    }

    public void setFirstStartMap(boolean notify) {
        preferences.putBoolean(KEY_FIRST_START_MAP, notify);
    }

    public String getAuthToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void setAuthToken(String token) {
        preferences.putString(KEY_TOKEN, token);
    }

    public boolean isNotify() {
        return preferences.getBoolean(KEY_NOTIFY, true);
    }

    public void setNotify(boolean notify) {
        preferences.putBoolean(KEY_NOTIFY, notify);
    }

    public String getCityName() {
        return preferences.getString(KEY_CITY, null);
    }

    public void setCityName(String cityName) {
        preferences.putString(KEY_CITY, cityName);
    }

    public void setPlaceUpdateListener(PlaceUpdateListener placeUpdateListener) {
        this.placeUpdateListener = placeUpdateListener;
    }

    public void setUserUpdateListener(UserUpdateListener userUpdateListener) {
        this.userUpdateListener = userUpdateListener;
    }

    public interface ModeChangeListener {
        void onModeChanged();
    }

    public interface CityDetectListener {
        void onCityDetected();
    }

    public interface PlaceUpdateListener {
        void onPlacesUpdated();
    }

    public interface UserUpdateListener {
        void onUserUpdated();
    }
}