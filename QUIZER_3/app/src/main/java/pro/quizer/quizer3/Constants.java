package pro.quizer.quizer3;

public final class Constants {

    public interface FTP {
        String server = "new.quizer.pro";
        int port = 21199;
        String user = "ftpchannel";
        String password = "Fj39Qp30!j*41";
    }

    public interface Default {

        String ACTIVATION_URL = "https://qhead.quizerplus.ru/api/v1/get-quizer-service-data";
        String API_PATH = "/api/v1/send-data";
        String API_URL = "http://188.225.11.47/";
        String REG_URL = "/api/v1/registration";
        String TEST_SMS_URL = "/api/v1/sms/receive/directly";
        String ONLINE_QUOTAS_URL = "/api/v1/survey/check-quotas-free";
        String ADDRESS_VER_URL = "/api/v1/address/version";
        String ADDRESS_DOWNLOAD_URL = "/api/v1/address/download";
        String ADDRESS_SEARCH_URL = "/api/v1/address/search";

        String API_key = "a1d64454-4597-4c28-a047-dee22d3c8cfd";

        boolean DEBUG = true;

    }

    public interface SP {

        String SHARED_PREFERENCES_INSTANCE = "SHARED_PREFERENCES_INSTANCE";

        String ANSWER_MARGIN = "ANSWER_MARGIN";
        String FONT_SIZE_POSITION = "FONT_SIZE_POSITION";
        String CURRENT_USED_ID = "CURRENT_USED_ID";
        String SENDED_Q_IN_SESSSION = "SENDED_Q_IN_SESSSION";
        String AUTH_TIME_DIFFERENCE = "auth_time_difference";
        String SEND_TIME_DIFFERENCE = "send_time_difference";
        String QUOTA_TIME_DIFFERENCE = "quota_time_difference";
        String AUTO_ZOOM = "AUTO_ZOOM";
        String SPEED = "SPEED";
        String ABORTED = "ABORTED";

    }

    public interface Strings {

        String SPACE = " ";
        String EMPTY = "";
        String UNKNOWN = "unknown";

    }

    public interface ServerFields {

        String JSON_DATA = "json_data";
    }

    public interface NameForm {

        String QUESTIONNAIRE = "questionnaire";
        String KEY_CLIENT = "key_client";
        String USER_LOGIN = "user_login";
        String GET_QUOTA = "get_quota";
        String STATISTICS = "statistics";
        String ROUTES = "project_routes";
        String PHOTO_FILE = "photo_file";
        String PHOTO_FILE_ANSWER = "photo_file_answer";
        String AUDIO_FILE = "audio_file";
        String DOWNLOAD_UPDATE = "download_update";
        String CRASH = "crash";
        String LOGS = "log";
    }

    public interface QuestionnaireStatuses {
        String ABORTED = "aborted";
        String COMPLETED = "completed";
        String UNFINISHED = "unfinished";
        String COND_COMPLETE = "cond_complete";
    }

    public interface LogUser {
        String ANDROID = "android";
    }

    public interface LogStatus {
        String SENT = "sent";
        String NOT_SENT = "not_sent";
        String READY_FOR_SEND = "ready";
    }

    public interface LogType {
        String SERVER = "server";
        String DATABASE = "database";
        String BUTTON = "button";
        String FILE = "file";
        String DIALOG = "dialog";
        String AUDIO = "dialog";
        String SETTINGS = "settings";
    }

    public interface LogObject {
        String FILE = "file";
        String AUTH = "auth";
        String USER = "user";
        String QUOTA = "quota";
        String KEY = "key";
        String CONFIG = "config";
        String QUESTIONNAIRE = "questionnaire";
        String REGISTRATION = "registration";
        String SMS = "sms";
        String LOG = "log";
        String WARNINGS = "warnings";
        String UI = "ui";
        String AUDIO = "audio";
        String PHOTOS = "photos";
        String TIMINGS = "timings";
    }

    public interface LogResult {
        String SUCCESS = "success";
        String SENT = "sent";
        String PRESSED = "pressed";
        String ERROR = "error";
        String ATTEMPT = "attempt";
    }

    public interface SmsStatus {
        String SENT = "sent";
        String NOT_SENT = "not_sent";
    }

    public interface Sms {
        String SENT = "Отправлена";
        String NOT_SENT = "Не отправлена";
        String WAITING = "Ожидание";
        String ENDED = "Завершена";
        String NOT_ENDED = "Не завершена";
    }

    public interface Warnings {
        String FAKE_GPS = "Подмена координат";
    }

    public interface OptionName {
        String QUIZ_STARTED = "quiz_started";
    }

    public interface Settings {
        String QUIZ_TIME = "quiz_time";
        String SENT_TIME = "sent_time";
        String QUOTA_TIME = "quota_time";
        String LAST_LOGIN_TIME = "last_login_time";
    }

    public interface Registration {
        String SENT = "sent";
        String SENT_NO_SMS = "sent_no_sms";
        String NOT_SENT = "not_sent";
        String SMS = "sms";
        String CODE = "code";
        String HAND = "hand";
    }
}
