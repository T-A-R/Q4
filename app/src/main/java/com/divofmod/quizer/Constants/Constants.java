package com.divofmod.quizer.Constants;

public class Constants {

    public interface SmsStatuses {

        String NOT_SENT = "не отправлено";
        String SENT = "отправлено";
        String DELIVERED = "доставлено";

    }

    public interface DatabaseValues {

        String DATABASE_NAME = "quizer_database";
    }

    public interface DefaultValues {

        String UNKNOWN = "unknown";
    }

    public interface ServerFields {

        String JSON_DATA = "json_data";
    }

    public interface NameForm {

        String KEY_CLIENT = "key_client";
        String DOWNLOAD_UPDATE = "download_update";
        String QUESTIONNAIRE = "questionnaire";
    }

    public interface Shared {

        String LOGIN_ADMIN = "login_admin";
        String LOGIN = "login";
        String PASSW = "passw";
        String CONFIG = "config_shared";
    }

    public interface SmsDatabase {

        String TABLE_NAME = "sms_table";
    }

}
