package pro.quizer.quizerexit;

public final class Constants {

    public interface FTP {
        String server = "new.quizer.pro";
        int port = 21199;
        String user = "ftpchannel";
        String password = "Fj39Qp30!j*41";
    }

    public interface Default {

        String ACTIVATION_URL = "http://188.225.11.47/wheretoredirrect_json.php";

        String API_URL = "http://188.225.11.47/";

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
        String PHOTO_FILE = "photo_file";
        String AUDIO_FILE = "audio_file";
        String DOWNLOAD_UPDATE = "download_update";
        String CRASH = "crash";
        String LOGS = "log";
    }

    public interface QuestionnaireStatuses {
        String ABORTED = "aborted";
        String COMPLITED = "complited";
    }

    public interface LogUser {
        String ANDROID = "android";
    }

    public interface LogStatus {
        String SENT = "sent";
        String NOT_SENT = "not_sent";
    }

    public interface LogType {
        String SERVER = "server";
        String DATABASE = "database";
        String BUTTON = "button";
        String FILE = "file";
        String DIALOG = "file";
    }

    public interface LogObject {
        String FILE = "file";
        String AUTH = "auth";
        String USER = "user";
        String QUOTA = "quota";
        String KEY = "key";
        String CONFIG = "config";
        String QUESTIONNAIRE = "questionnaire";
        String SMS = "sms";
    }

    public interface LogResult {
        String SUCCESS = "success";
        String SENT = "sent";
        String PRESSED = "pressed";
        String ERROR = "error";

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
}
