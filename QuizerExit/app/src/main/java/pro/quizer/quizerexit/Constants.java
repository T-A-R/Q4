package pro.quizer.quizerexit;

public final class Constants {

    public interface Default {

        String ACTIVATION_URL = "http://head.quizer.pro/wheretoredirrect_json.php";

    }

    public interface SP {

        String SHARED_PREFERENCES_INSTANCE = "SHARED_PREFERENCES_INSTANCE";

        String CURRENT_USED_ID = "CURRENT_USED_ID";
        String SENDED_Q_IN_SESSSION = "SENDED_Q_IN_SESSSION";

    }

    public interface Strings {

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
        String PHOTO_FILE = "photo_file";
        String AUDIO_FILE = "audio_file";
        String DOWNLOAD_UPDATE = "download_update";
    }
}
