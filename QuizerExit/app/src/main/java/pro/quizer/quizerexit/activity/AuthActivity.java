package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;
import com.reginald.editspinner.EditSpinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.request.ConfigRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.MD5Utils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class AuthActivity extends BaseActivity {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private EditText mPasswordEditText;
    private EditSpinner mLoginSpinner;
    private List<String> mSavedUsers;
    private List<UserModel> mSavedUserModels;
    private TextView mVersionView;
    private int mVersionTapCount = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        final TextView usersCount = findViewById(R.id.users_count);
        UiUtils.setTextOrHide(usersCount, String.format(getString(R.string.count_users_on_this_device), (getUsersCount() + "/" + MAX_USERS)));
        mPasswordEditText = findViewById(R.id.auth_password_edit_text);
        mLoginSpinner = findViewById(R.id.login_spinner);
        mVersionView = findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.app_version), getAppVersionName()));
        mVersionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mVersionTapCount++;

                if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
                    finish();
                    startServiceActivity();

                    mVersionTapCount = 0;
                }
            }
        });

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mSavedUsers);
        mLoginSpinner.setAdapter(adapter);

        if (mSavedUserModels != null && !mSavedUserModels.isEmpty()) {
            final int lastUserId = getCurrentUserId();

            if (lastUserId != -1) {
                for (final UserModel userModel : mSavedUserModels) {
                    if (userModel.user_id == lastUserId) {
                        UiUtils.setTextOrHide(mLoginSpinner, userModel.login);
                    }
                }
            }
        }

        final Button sendAuthButton = findViewById(R.id.send_auth_button);
        sendAuthButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showProgressBar();

                final String login = mLoginSpinner.getText().toString();
                final String password = mPasswordEditText.getText().toString();

                if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
                    showToast(getString(R.string.empty_login_or_password));

                    hideProgressBar();

                    return;
                }

                if (login.length() < 3) {
                    showToast(getString(R.string.short_login));

                    hideProgressBar();

                    return;
                }

                if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
                    showToast(String.format(getString(R.string.error_max_users), String.valueOf(MAX_USERS)));

                    hideProgressBar();

                    return;
                }

                final String passwordMD5 = MD5Utils.formatPassword(login, password);
                final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
                mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new AuthRequestModel(getLoginAdmin(), passwordMD5, login)));

                final Call.Factory client = new OkHttpClient();
                client.newCall(new DoRequest().post(mDictionaryForRequest, getServer()))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                hideProgressBar();

                                final UserModel savedUserModel = getLocalUserModel(login, passwordMD5);

                                if (savedUserModel != null) {
                                    showToast("Удалось войти под сохраненными локальными данными.");
                                    onLoggedInWithoutUpdateLocalData(savedUserModel.user_id);
                                } else {
                                    showToast(getString(R.string.internet_error_please_try_again));
                                }
                            }

                            @Override
                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                hideProgressBar();

                                final ResponseBody responseBody = response.body();

                                if (responseBody == null) {
                                    showToast(getString(R.string.incorrect_server_response));
                                    onFailure(call, null);

                                    return;
                                }

                                final String responseJson = responseBody.string();
                                AuthResponseModel authResponseModel = null;

                                try {
                                    authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
                                } catch (final Exception pE) {
                                    // empty
                                }

                                if (authResponseModel != null) {
                                    if (authResponseModel.getResult() != 0) {
                                        if (isNeedDownloadConfig(authResponseModel)) {
                                            downloadConfig(login, passwordMD5, authResponseModel);
                                        } else {
                                            onLoggedIn(login,
                                                    passwordMD5,
                                                    authResponseModel.getConfigId(),
                                                    authResponseModel.getUserId(),
                                                    authResponseModel.getRoleId(),
                                                    authResponseModel.getUserProjectId());
                                        }
                                    } else {
                                        showToast(authResponseModel.getError());
                                    }
                                } else {
                                    showToast(getString(R.string.server_error));
                                    onFailure(call, null);
                                }
                            }
                        });

            }
        });
    }

    private void onLoggedInWithoutUpdateLocalData(final int pUserId) {
        saveCurrentUserId(pUserId);
        finish();
        startMainActivity();
    }

    private void onLoggedIn(final String pLogin,
                            final String pPassword,
                            final String pConfigId,
                            final int pUserId,
                            final int pRoleId,
                            final int pUserProjectId) {
        SPUtils.resetSendedQInSession(this);
        updateDatabaseUserByUserId(pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);

        onLoggedInWithoutUpdateLocalData(pUserId);
    }

    private boolean isNeedDownloadConfig(final AuthResponseModel pAuthResponseModel) {
        final UserModel userModel = getUserByUserId(pAuthResponseModel.getUserId());

        if (userModel == null) {
            return true;
        } else {
            return !pAuthResponseModel.getConfigId().equals(userModel.config_id);
        }
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (final UserModel model : mSavedUserModels) {
            users.add(model.login);
        }

        return users;
    }

    private List<UserModel> getSavedUserModels() {
        final List<UserModel> userModels = new Select().from(UserModel.class).execute();

        return (userModels == null) ? new ArrayList<UserModel>() : userModels;
    }

    public void downloadConfig(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        showToast("Downloading config...");

        showProgressBar();

        final Dictionary<String, String> mConfigDictionary = new Hashtable();

        final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                getLoginAdmin(),
                pLogin,
                pPassword,
                pModel.getConfigId()
        );

        mConfigDictionary.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(configRequestModel));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest().post(mConfigDictionary, getServer()))
                .enqueue(new Callback() {

                             @Override
                             public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                 hideProgressBar();
                                 showToast(getString(R.string.internet_error_please_try_again));
                             }

                             @Override
                             public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                 hideProgressBar();

                                 final ResponseBody responseBody = response.body();

                                 if (responseBody == null) {
                                     showToast(getString(R.string.incorrect_server_response));

                                     return;
                                 }

                                 final String responseJson = "{\n" +
                                         "  \"result\": 1,\n" +
                                         "  \"config\": {\n" +
                                         "    \"server\": \"ExMTLXjS2Bfs3Hw4Eo0kl/QpiBtC7JeVaFdrUpUw+20KxZ5Hw5UdV7J13c6SztVxhX96W9vVQE8CQE6WHfGlk7j61QR0QsTd7cd2G/baoNhBAru8WVZk8zQBFdbA2B/v/AIFLs2yqeookSpTPaUeajM03UoCwY4t8e0iI5pUUUZwnjVJ65Xxhr3hB2adpjuljCDSBc7L1s72pNYMp5FD3m24yyPbK8pwW+GPcAFpv1KzkMRzf2ioY3gXn96bEFCuLMTpmaD2z4raOyD4heoGsm3vPX4tJhH+EI2kadOHJvLXUtOTnajCJTa+Jm7uMKPwhJt4uVyb3cVbOxEgxZDnyBgb7/Y7ZXCHW8rPawz7rUfIcgR9HcX0i8p7f4h3nSePKsLUBFw0AplQrSUTXMLURrAXvZb84bYBttPZ0Oc0ZugObXB3menJkQqgoA16f/m9NRKUM+mtG7E9XBsOLpksy5bLPOfgmwDLCAlGajfMTEOEY2zFhzJHkhrJOQt\",\n" +
                                         "    \"server_url\": \"http://quizer-dev.quizer.pro/_mobile_core/quizer_json_ios.php\",\n" +
                                         "    \"login_admin\": \"admin350\",\n" +
                                         "    \"photo_questionnaire\": true,\n" +
                                         "    \"autonomous_limit_count_questionnare\": 15,\n" +
                                         "    \"autonomous_limit_time_questionnare\": 300,\n" +
                                         "    \"config_date\": \"2018-16-15 11:33:34\",\n" +
                                         "    \"audio\": true,\n" +
                                         "    \"gps\": true,\n" +
                                         "    \"audio_record_all\":true,\n" +
                                         "    \"audio_record_limit_time\": 2,\n" +
                                         "    \"delete_data_password\": \"12345\",\n" +
                                         "    \"project_info\": {\n" +
                                         "      \"project_id\": 5562,\n" +
                                         "      \"questionnaire_id\": 55162,\n" +
                                         "      \"name\": \"Экзит пул\",\n" +
                                         "      \"agreement\": \"Опрос абсолютно анонимен. user000\",\n" +
                                         "      \"elements\": [\n" +
                                         "        {\n" +
                                         "          \"relative_id\": 1,\n" +
                                         "          \"relative_parent_id\": null,\n" +
                                         "          \"type\": \"question\",\n" +
                                         "          \"options\": {\n" +
                                         "            \"type\": \"list\",\n" +
                                         "            \"show_condition\": \"\",\n" +
                                         "            \"title\": \"Вопрос номер ОДИН\",\n" +
                                         "            \"number\": 1,\n" +
                                         "            \"order\": 1,\n" +
                                         "            \"rotation\": true,\n" +
                                         "            \"polyanswer\": true,\n" +
                                         "            \"min_answers\": 2,\n" +
                                         "            \"max_answers\": 4\n" +
                                         "          },\n" +
                                         "          \"elements\": [\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 2,\n" +
                                         "              \"relative_parent_id\": 1,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"\",\n" +
                                         "                \"title\": \"Ответ вопроса 1 - 1\",\n" +
                                         "                \"order\": 1,\n" +
                                         "                \"jump\": 7,\n" +
                                         "                \"jump_condition\": \"\",\n" +
                                         "                \"open_type\": \"text\"\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 3,\n" +
                                         "              \"relative_parent_id\": 1,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"\",\n" +
                                         "                \"title\": \"Ответ вопроса 1 - 2\",\n" +
                                         "                \"order\": 2,\n" +
                                         "                \"fixed_order\": true,\n" +
                                         "                \"jump\": 7,\n" +
                                         "                \"jump_condition\": \"\",\n" +
                                         "                \"open_type\": \"number\"\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 4,\n" +
                                         "              \"relative_parent_id\": 1,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"\",\n" +
                                         "                \"title\": \"Ответ вопроса 1 - 3\",\n" +
                                         "                \"order\": 3,\n" +
                                         "                \"jump\": 7,\n" +
                                         "                \"jump_condition\": \"\",\n" +
                                         "                \"open_type\": \"date\"\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 5,\n" +
                                         "              \"relative_parent_id\": 1,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"\",\n" +
                                         "                \"title\": \"Ответ вопроса 1 - 4\",\n" +
                                         "                \"order\": 4,\n" +
                                         "                \"fixed_order\": true,\n" +
                                         "                \"jump\": 7,\n" +
                                         "                \"jump_condition\": \"\",\n" +
                                         "                \"open_type\": \"time\"\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 6,\n" +
                                         "              \"relative_parent_id\": 1,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"\",\n" +
                                         "                \"title\": \"Ответ вопроса 1 - 5\",\n" +
                                         "                \"order\": 5,\n" +
                                         "                \"jump\": 7,\n" +
                                         "                \"jump_condition\": \"\",\n" +
                                         "                \"unchecker\": true\n" +
                                         "              }\n" +
                                         "            }\n" +
                                         "          ]\n" +
                                         "        },\n" +
                                         "        {\n" +
                                         "          \"relative_id\": 7,\n" +
                                         "          \"relative_parent_id\": null,\n" +
                                         "          \"type\": \"question\",\n" +
                                         "          \"options\": {\n" +
                                         "            \"type\": \"list\",\n" +
                                         "            \"show_condition\": \"#1|1$\",\n" +
                                         "            \"title\": \"Вопрос номер ДВА\",\n" +
                                         "            \"description\": \"Выберите один вариант ответа из предложенных ниже\",\n" +
                                         "            \"order\": 2,\n" +
                                         "            \"number\": 2,\n" +
                                         "            \"record_sound\": true,\n" +
                                         "            \"take_photo\": true\n" +
                                         "          },\n" +
                                         "          \"elements\": [\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 8,\n" +
                                         "              \"relative_parent_id\": 7,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"title\": \"Ответ вопроса 2 - 1\",\n" +
                                         "                \"order\": 1,\n" +
                                         "                \"jump\": 57,\n" +
                                         "                \"open_type\": \"text\",\n" +
                                         "                \"placeholder\": \"Впишите\"\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 9,\n" +
                                         "              \"relative_parent_id\": 7,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"title\": \"Ответ вопроса 2 - 2\",\n" +
                                         "                \"order\": 2,\n" +
                                         "                \"jump\": 57\n" +
                                         "              }\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 10,\n" +
                                         "              \"relative_parent_id\": 7,\n" +
                                         "              \"type\": \"answer\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"show_condition\": \"#1|2$\",\n" +
                                         "                \"title\": \"Ответ вопроса 2 - 3\",\n" +
                                         "                \"order\": 3,\n" +
                                         "                \"jump\": 57\n" +
                                         "              }\n" +
                                         "            }\n" +
                                         "          ]\n" +
                                         "        },\n" +
                                         "        {\n" +
                                         "          \"relative_id\": 57,\n" +
                                         "          \"relative_parent_id\": null,\n" +
                                         "          \"type\": \"box\",\n" +
                                         "          \"options\": {\n" +
                                         "            \"order\": 3\n" +
                                         "          },\n" +
                                         "          \"elements\": [\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 58,\n" +
                                         "              \"relative_parent_id\": 57,\n" +
                                         "              \"type\": \"box\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"order\": 1\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 59,\n" +
                                         "                  \"relative_parent_id\": 58,\n" +
                                         "                  \"type\": \"box\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"order\": 1\n" +
                                         "                  },\n" +
                                         "                  \"elements\": [\n" +
                                         "                    {\n" +
                                         "                      \"relative_id\": 60,\n" +
                                         "                      \"relative_parent_id\": 59,\n" +
                                         "                      \"type\": \"info\",\n" +
                                         "                      \"jump\":0,\n" +
                                         "                      \"options\": {\n" +
                                         "                        \"order\": 1\n" +
                                         "                      },\n" +
                                         "                      \"elements\": [\n" +
                                         "                        {\n" +
                                         "                          \"relative_id\": 64,\n" +
                                         "                          \"relative_parent_id\": 60,\n" +
                                         "                          \"type\": \"content\",\n" +
                                         "                          \"options\": {\n" +
                                         "                            \"type\":\"text\",\n" +
                                         "                            \"text\":\"В пустыне чахлой и скупой, На почве, зноем раскаленной, Анчар, как грозный часовой, Стоит — один во всей вселенной. Природа жаждущих степей Его в день гнева породила, И зелень мертвую ветвей И корни ядом напоила. Яд каплет сквозь его кору, К полудню растопясь от зною, И застывает ввечеру Густой прозрачною смолою. К нему и птица не летит, В пустыне чахлой и скупой, На почве, зноем раскаленной, Анчар, как грозный часовой, Стоит — один во всей вселенной. Природа жаждущих степей Его в день гнева породила, И зелень мертвую ветвей И корни ядом напоила. Яд каплет сквозь его кору, К полудню растопясь от зною, И застывает ввечеру Густой прозрачною смолою. К нему и птица не летит, В пустыне чахлой и скупой, На почве, зноем раскаленной, Анчар, как грозный часовой, Стоит — один во всей вселенной. Природа жаждущих степей Его в день гнева породила, И зелень мертвую ветвей И корни ядом напоила. Яд каплет сквозь его кору, К полудню растопясь от зною, И застывает ввечеру Густой прозрачною смолою. К нему и птица не летит. В пустыне чахлой и скупой, На почве, зноем раскаленной, Анчар, как грозный часовой, Стоит — один во всей вселенной. Природа жаждущих степей Его в день гнева породила, И зелень мертвую ветвей И корни ядом напоила. Яд каплет сквозь его кору, К полудню растопясь от зною, И застывает ввечеру Густой прозрачною смолою. К нему и птица не летит.\",\n" +
                                         "                            \"order\": 1\n" +
                                         "                          }\n" +
                                         "                        },\n" +
                                         "                        {\n" +
                                         "                          \"relative_id\": 64,\n" +
                                         "                          \"relative_parent_id\": 60,\n" +
                                         "                          \"type\": \"content\",\n" +
                                         "                          \"options\": {\n" +
                                         "                            \"type\":\"audio\",\n" +
                                         "                            \"link\":\"http://quizer-dev.quizer.pro/sound.mp3\",\n" +
                                         "                            \"order\": 2\n" +
                                         "                          }\n" +
                                         "                        },\n" +
                                         "                        {\n" +
                                         "                          \"relative_id\": 65,\n" +
                                         "                          \"relative_parent_id\": 60,\n" +
                                         "                          \"type\": \"content\",\n" +
                                         "                          \"options\": {\n" +
                                         "                            \"type\":\"photo\",\n" +
                                         "                            \"link\":\"http://quizer-dev.quizer.pro/photo.jpg\",\n" +
                                         "                            \"order\": 3\n" +
                                         "                          }\n" +
                                         "                        },\n" +
                                         "                        {\n" +
                                         "                          \"relative_id\": 66,\n" +
                                         "                          \"relative_parent_id\": 60,\n" +
                                         "                          \"type\": \"content\",\n" +
                                         "                          \"options\": {\n" +
                                         "                            \"type\":\"video\",\n" +
                                         "                            \"link\":\"http://quizer-dev.quizer.pro/video.mp4\",\n" +
                                         "                            \"order\": 4\n" +
                                         "                          }\n" +
                                         "                        }\n" +
                                         "                      ]\n" +
                                         "                    }\n" +
                                         "                  ]\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 61,\n" +
                                         "              \"relative_parent_id\": null,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Просто вопрос 3 внутри BOX\",\n" +
                                         "                \"number\": 3\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 62,\n" +
                                         "                  \"relative_parent_id\": 61,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"show_condition\": \"\",\n" +
                                         "                    \"title\": \"Ответ вопроса 3 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 11,\n" +
                                         "                    \"jump_condition\": \"\",\n" +
                                         "                    \"open_type\": \"text\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 63,\n" +
                                         "                  \"relative_parent_id\": 61,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"show_condition\": \"\",\n" +
                                         "                    \"title\": \"Ответ вопроса 3 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 11,\n" +
                                         "                    \"open_type\": \"number\"\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            }\n" +
                                         "          ]\n" +
                                         "        },\n" +
                                         "        {\n" +
                                         "          \"relative_id\": 11,\n" +
                                         "          \"relative_parent_id\": null,\n" +
                                         "          \"type\": \"question\",\n" +
                                         "          \"options\": {\n" +
                                         "            \"type\": \"table\",\n" +
                                         "            \"title\": \"Тут будет заголовок таблицы!\",\n" +
                                         "            \"description\": \"Выберите один вариант ответа в каждом из предложенных ниже вопросов\",\n" +
                                         "            \"order\": 4,\n" +
                                         "            \"record_sound\": true\n" +
                                         "          },\n" +
                                         "          \"elements\": [\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 12,\n" +
                                         "              \"relative_parent_id\": 11,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Это первый вопрос таблицы. Как вы считаете, столько текста в вопросе, который в свою очередь находится в таблица - это нормально? На самом деле вопросы бывают ещё большей длинны...\",\n" +
                                         "                \"description\": \"Тут будет описание вопроса, который в таблице. Оно будет появлятся по нажатию на некий значок-иконку, кот. будет рядом с текстом вопроса или на сам вопрос.\",\n" +
                                         "                \"order\": 1,\n" +
                                         "                \"number\": 4\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 13,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 0,\n" +
                                         "                    \"open_type\": \"text\",\n" +
                                         "                    \"placeholder\": \"Впишите\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 14,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 15,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 3\",\n" +
                                         "                    \"order\": 3,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 16,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 4\",\n" +
                                         "                    \"order\": 4,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 17,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 5\",\n" +
                                         "                    \"order\": 5,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 18,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 6\",\n" +
                                         "                    \"order\": 6,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 19,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 7\",\n" +
                                         "                    \"order\": 7,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 20,\n" +
                                         "                  \"relative_parent_id\": 12,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 4 - 8\",\n" +
                                         "                    \"order\": 8,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 21,\n" +
                                         "              \"relative_parent_id\": 11,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Это ВТОРОЙ вопрос таблицы. Как вы считаете, столько текста в вопросе, который в свою очередь находится в таблица - это нормально?\",\n" +
                                         "                \"description\": \"Тут будет описание вопроса, который в таблице. Оно будет появлятся по нажатию на некий значок-иконку, кот. будет рядом с текстом вопроса или на сам вопрос.\",\n" +
                                         "                \"order\": 2,\n" +
                                         "                \"number\": 5\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 22,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 0,\n" +
                                         "                    \"open_type\": \"text\",\n" +
                                         "                    \"placeholder\": \"Впишите\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 23,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 24,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 3\",\n" +
                                         "                    \"order\": 3,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 25,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 4\",\n" +
                                         "                    \"order\": 4,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 26,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 5\",\n" +
                                         "                    \"order\": 5,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 27,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 6\",\n" +
                                         "                    \"order\": 6,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 28,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 7\",\n" +
                                         "                    \"order\": 7,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 29,\n" +
                                         "                  \"relative_parent_id\": 21,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 5 - 8\",\n" +
                                         "                    \"order\": 8,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 30,\n" +
                                         "              \"relative_parent_id\": 11,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Это ТРЕТИЙ вопрос таблицы. Как вы считаете, столько текста в вопросе, который в свою очередь находится в таблица - это нормально?\",\n" +
                                         "                \"description\": \"Тут будет описание вопроса, который в таблице. Оно будет появлятся по нажатию на некий значок-иконку, кот. будет рядом с текстом вопроса или на сам вопрос.\",\n" +
                                         "                \"order\": 3,\n" +
                                         "                \"number\": 6\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 31,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 0,\n" +
                                         "                    \"open_type\": \"text\",\n" +
                                         "                    \"placeholder\": \"Впишите\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 32,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 33,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 3\",\n" +
                                         "                    \"order\": 3,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 34,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 4\",\n" +
                                         "                    \"order\": 4,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 35,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 5\",\n" +
                                         "                    \"order\": 5,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 36,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 6\",\n" +
                                         "                    \"order\": 6,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 37,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 7\",\n" +
                                         "                    \"order\": 7,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 38,\n" +
                                         "                  \"relative_parent_id\": 30,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 6 - 8\",\n" +
                                         "                    \"order\": 8,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 39,\n" +
                                         "              \"relative_parent_id\": 11,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Это ЧЕТВЁРТЫЙ вопрос таблицы. Как вы считаете, столько текста в вопросе, который в свою очередь находится в таблица - это нормально?\",\n" +
                                         "                \"description\": \"Тут будет описание вопроса, который в таблице. Оно будет появлятся по нажатию на некий значок-иконку, кот. будет рядом с текстом вопроса или на сам вопрос.\",\n" +
                                         "                \"order\": 4,\n" +
                                         "                \"number\": 7\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 40,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 0,\n" +
                                         "                    \"open_type\": \"text\",\n" +
                                         "                    \"placeholder\": \"Впишите\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 41,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 42,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 3\",\n" +
                                         "                    \"order\": 3,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 43,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 4\",\n" +
                                         "                    \"order\": 4,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 44,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 5\",\n" +
                                         "                    \"order\": 5,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 45,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 6\",\n" +
                                         "                    \"order\": 6,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 46,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 7\",\n" +
                                         "                    \"order\": 7,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 47,\n" +
                                         "                  \"relative_parent_id\": 39,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 7 - 8\",\n" +
                                         "                    \"order\": 8,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            },\n" +
                                         "            {\n" +
                                         "              \"relative_id\": 48,\n" +
                                         "              \"relative_parent_id\": 11,\n" +
                                         "              \"type\": \"question\",\n" +
                                         "              \"options\": {\n" +
                                         "                \"type\": \"list\",\n" +
                                         "                \"title\": \"Это ПЯТЫЙ вопрос таблицы. Как вы считаете, столько текста в вопросе, который в свою очередь находится в таблица - это нормально?\",\n" +
                                         "                \"description\": \"Тут будет описание вопроса, который в таблице. Оно будет появлятся по нажатию на некий значок-иконку, кот. будет рядом с текстом вопроса или на сам вопрос.\",\n" +
                                         "                \"order\": 5,\n" +
                                         "                \"number\": 8\n" +
                                         "              },\n" +
                                         "              \"elements\": [\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 49,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 1\",\n" +
                                         "                    \"order\": 1,\n" +
                                         "                    \"jump\": 0,\n" +
                                         "                    \"open_type\": \"text\",\n" +
                                         "                    \"placeholder\": \"Впишите\"\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 50,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 2\",\n" +
                                         "                    \"order\": 2,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 51,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 3\",\n" +
                                         "                    \"order\": 3,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 52,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 4\",\n" +
                                         "                    \"order\": 4,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 53,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 5\",\n" +
                                         "                    \"order\": 5,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 54,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 6\",\n" +
                                         "                    \"order\": 6,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 55,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 7\",\n" +
                                         "                    \"order\": 7,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                },\n" +
                                         "                {\n" +
                                         "                  \"relative_id\": 56,\n" +
                                         "                  \"relative_parent_id\": 48,\n" +
                                         "                  \"type\": \"answer\",\n" +
                                         "                  \"options\": {\n" +
                                         "                    \"title\": \"Ответ вопроса 8 - 8\",\n" +
                                         "                    \"order\": 8,\n" +
                                         "                    \"jump\": 0\n" +
                                         "                  }\n" +
                                         "                }\n" +
                                         "              ]\n" +
                                         "            }\n" +
                                         "          ]\n" +
                                         "        }\n" +
                                         "      ],\n" +
                                         "      \"reserve_channel\": {\n" +
                                         "        \"phone\": [\n" +
                                         "          {\n" +
                                         "            \"number\": \"+79037676883\",\n" +
                                         "            \"preffix\": \"\"\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"number\": \"2420\",\n" +
                                         "            \"preffix\": \"quiz \"\n" +
                                         "          }\n" +
                                         "        ],\n" +
                                         "        \"stages\": [\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536469200,\n" +
                                         "            \"time_to\": 1536472800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#1\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#2\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#3\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536469200,\n" +
                                         "            \"time_to\": 1536472800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#4\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#5\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#6\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536472800,\n" +
                                         "            \"time_to\": 1536476400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#7\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#8\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#9\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536472800,\n" +
                                         "            \"time_to\": 1536476400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#10\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#11\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#12\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536476400,\n" +
                                         "            \"time_to\": 1536480000,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#13\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#14\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#15\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536476400,\n" +
                                         "            \"time_to\": 1536480000,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#16\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#17\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#18\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536480000,\n" +
                                         "            \"time_to\": 1536483600,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#19\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#20\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#21\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536480000,\n" +
                                         "            \"time_to\": 1536483600,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#22\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#23\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#24\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536483600,\n" +
                                         "            \"time_to\": 1536487200,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#25\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#26\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#27\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536483600,\n" +
                                         "            \"time_to\": 1536487200,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#28\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#29\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#30\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536487200,\n" +
                                         "            \"time_to\": 1536490800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#31\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#32\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#33\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536487200,\n" +
                                         "            \"time_to\": 1536490800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#34\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#35\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#36\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536490800,\n" +
                                         "            \"time_to\": 1536494400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#37\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#38\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#39\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536490800,\n" +
                                         "            \"time_to\": 1536494400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#40\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#41\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#42\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536494400,\n" +
                                         "            \"time_to\": 1536498000,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#43\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#44\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#45\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536494400,\n" +
                                         "            \"time_to\": 1536498000,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#46\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#47\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#48\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536498000,\n" +
                                         "            \"time_to\": 1536501600,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#49\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#50\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#51\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536498000,\n" +
                                         "            \"time_to\": 1536501600,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#52\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#53\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#54\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536501600,\n" +
                                         "            \"time_to\": 1536505200,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#55\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#56\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#57\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536501600,\n" +
                                         "            \"time_to\": 1536505200,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#58\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#59\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#60\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536505200,\n" +
                                         "            \"time_to\": 1536508800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#61\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#62\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#63\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536505200,\n" +
                                         "            \"time_to\": 1536508800,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#64\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#65\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#66\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536508800,\n" +
                                         "            \"time_to\": 1536512400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#67\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#68\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#69\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          },\n" +
                                         "          {\n" +
                                         "            \"time_from\": 1536508800,\n" +
                                         "            \"time_to\": 1536512400,\n" +
                                         "            \"questions_matches\": [\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3336,\n" +
                                         "                \"sms_num\": \"#70\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3337,\n" +
                                         "                \"sms_num\": \"#71\"\n" +
                                         "              },\n" +
                                         "              {\n" +
                                         "                \"question_id\": 3338,\n" +
                                         "                \"sms_num\": \"#72\"\n" +
                                         "              }\n" +
                                         "            ]\n" +
                                         "          }\n" +
                                         "        ]\n" +
                                         "      }\n" +
                                         "    }\n" +
                                         "  }\n" +
                                         "}"; // responseBody.string();
                                 final GsonBuilder gsonBuilder = new GsonBuilder();
                                 ConfigResponseModel configResponseModel = null;

                                 try {
                                     configResponseModel = gsonBuilder.create().fromJson(responseJson, ConfigResponseModel.class);
                                 } catch (final Exception pE) {
                                     // empty
                                 }

                                 if (configResponseModel != null) {
                                     if (configResponseModel.getResult() != 0) {
                                         downloadFiles(configResponseModel, pModel, pLogin, pPassword, pModel.getConfigId(), pModel.getUserId(), pModel.getRoleId(), pModel.getUserProjectId());
                                     } else {
                                         showToast(configResponseModel.getError());
                                     }
                                 } else {
                                     showToast(getString(R.string.server_error));
                                 }
                             }
                         }

                );
    }

    private void saveUserAndLogin(final ConfigResponseModel pConfigResponseModel,
                                  final AuthResponseModel pAuthResponseModel,
                                  final String pLogin,
                                  final String pPassword,
                                  final String pConfigId,
                                  final int pUserId,
                                  final int pRoleId,
                                  final int pUserProjectId) {
        try {
            saveUser(pLogin, pPassword, pAuthResponseModel, pConfigResponseModel);
        } catch (final Exception e) {
            showToast(getString(R.string.server_error) + "\n" + e);
        }

        onLoggedIn(pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
    }

    private void downloadFiles(final ConfigResponseModel pConfigResponseModel,
                               final AuthResponseModel pAuthResponseModel,
                               final String pLogin,
                               final String pPassword,
                               final String pConfigId,
                               final int pUserId,
                               final int pRoleId,
                               final int pUserProjectId) {
        final String[] fileUris = pConfigResponseModel.getConfig().getMediaFiles();

        if (fileUris == null || fileUris.length == 0) {
            saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
        } else {
            showProgressBar();

            FileLoader.multiFileDownload(this)
                    .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                    .progressListener(new MultiFileDownloadListener() {
                        @Override
                        public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                            if (progress == totalFiles) {
                                hideProgressBar();

                                saveUserAndLogin(pConfigResponseModel, pAuthResponseModel, pLogin, pPassword, pConfigId, pUserId, pRoleId, pUserProjectId);
                            }

                            showToast(String.format(getString(R.string.downloaded_count_files), String.valueOf(progress)));
                        }

                        @Override
                        public void onError(final Exception e, final int progress) {
                            super.onError(e, progress);
                            showToast(getString(R.string.downloading_files_error));
                            hideProgressBar();
                        }
                    }).loadMultiple(fileUris);
        }
    }
}
