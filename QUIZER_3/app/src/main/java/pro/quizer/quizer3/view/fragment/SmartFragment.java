package pro.quizer.quizer3.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.AuthRequestModel;
import pro.quizer.quizer3.API.models.request.ConfigRequestModel;
import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.API.models.response.ConfigResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.CoreApplication;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.ElementStatusImageR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.database.models.WarningsR;
import pro.quizer.quizer3.model.ElementDatabaseType;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.Contents;
import pro.quizer.quizer3.model.config.ElementModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.OptionsModelNew;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.LogUtils;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.utils.UiUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;

@SuppressWarnings("unused")
public abstract class SmartFragment extends Fragment {

    protected Listener listener;
    private UserModelR mCurrentUser;
    private int layoutSrc;
    private HashMap<Integer, ElementModelNew> mMap;
    private Integer countElements;
    private Integer countScreens;
    private Integer countQuestions;

    private CurrentQuestionnaireR currentQuestionnaire = null;
    List<ElementItemR> elementItemRList = null;

    public SmartFragment(int layoutSrc) {
        this.layoutSrc = layoutSrc;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layoutSrc, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onReady();
    }

    /**
     * Вызывается onActivityCreated
     */
    abstract protected void onReady();

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected void dispatchIntent(final String intent) {
        View view = getView();
        if (listener != null && view != null)
            view.post(() -> listener.fragmentIntent(SmartFragment.this, intent));
    }

    public <T extends View> T findViewById(int id) {
        View view = getView();
        if (view == null) {
            return null;
        }
        return view.findViewById(id);
    }

    public Fragment findChildFragmentById(int id) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        return childFragmentManager.findFragmentById(id);
    }

    public Context getContext() {
        View view = getView();
        return view != null ? view.getContext() : null;
    }

    public int getLayoutSrc() {
        return layoutSrc;
    }

    public float getDensity() {
        Context context = getContext();
        if (context == null)
            return 0;

        return getContext().getResources().getDisplayMetrics().density;
    }

    protected void hideKeyboard() {
        Context context = getContext();
        if (context == null)
            return;

        try {
            View view = getView();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (view != null && imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Log.d("IRON", "SmartFragment.hideKeyboard() " + e);
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public interface Listener {
        void fragmentIntent(SmartFragment fragment, String intent);
    }

    public static QuizerDao getDao() {
        return CoreApplication.getQuizerDatabase().getQuizerDao();
    }

    public static void addLog(String login,
                              String type,
                              String object,
                              String action,
                              String result,
                              String desc,
                              String data) {
        AppLogsR appLogsR = new AppLogsR();
        appLogsR.setLogin(login);
        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
        appLogsR.setAppversion(DeviceUtils.getAppVersion());
        appLogsR.setAndroid(DeviceUtils.getAndroidVersion());
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setType(type);
        appLogsR.setObject(object);
        appLogsR.setAction(action);
        appLogsR.setResult(result);
        appLogsR.setDescription(desc);
        if (data != null)
            appLogsR.setData(data.substring(0, Math.min(data.length(), 5000)));

        try {
            getDao().insertAppLogsR(appLogsR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public boolean isActivated() {
        return getActivationModel() != null;
    }

    public ActivationModelR getActivationModel() {

        List<ActivationModelR> list = null;

        try {
            list = getDao().getActivationModelR();
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.db_load_error), Toast.LENGTH_SHORT).show();
        }

        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;

    }

    public int getCurrentUserId() {
        return SPUtils.getCurrentUserId(getContext());
    }

    public UserModelR getUserByUserId(final int pUserId) {

        List<UserModelR> list = null;
        try {
            list = getDao().getUserByUserId(pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.db_load_error));
        }

        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }


    public String getLoginAdmin() {
        return getActivationModel().getLogin_admin();
    }

    public String getServer() {
        return getActivationModel().getServer();
    }

    public UserModelR getLocalUserModel(final String pLogin, final String pPassword) {

        List<UserModelR> list = null;
        try {
            list = getDao().getLocalUserModel(pLogin, pPassword);
        } catch (Exception e) {
            showToast(getString(R.string.db_load_error));
        }

        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;
    }

    public void saveCurrentUserId(final int pUserId) {
        SPUtils.saveCurrentUserId(getContext(), pUserId);
    }

    public void saveUser(final String pLogin, final String pPassword, final AuthResponseModel pModel, final ConfigModel pConfigModel) throws Exception {
        Log.d(TAG, "Saving User To Database............. ");
        try {
            getDao().deleteUserByUserId(pModel.getUserId());
        } catch (Exception e) {
            showToast(getString(R.string.db_clear_error));
        }

        final ReserveChannelModel reserveChannelModel = pConfigModel.getProjectInfo().getReserveChannel();

        if (reserveChannelModel != null) {
            reserveChannelModel.selectPhone(0);
        }

        final UserModelR userModelR = new UserModelR();
        userModelR.setLogin(pLogin);
        userModelR.setPassword(pPassword);
        userModelR.setConfig_id(pModel.getConfigId());
        userModelR.setRole_id(pModel.getRoleId());
        userModelR.setUser_id(pModel.getUserId());
        userModelR.setUser_project_id(pModel.getUserProjectId());
        userModelR.setConfig(new GsonBuilder().create().toJson(pConfigModel));
        try {
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, getString(R.string.save_user), Constants.LogResult.SENT, getString(R.string.save_user_to_db), "login: " + userModelR.getLogin());

            getDao().insertUser(userModelR);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, getString(R.string.save_user), Constants.LogResult.ERROR, getString(R.string.save_user_to_db_error), e.getMessage());
        }


    }

    public void updateDatabaseUserByUserId(final String pLogin,
                                           final String pPassword,
                                           final String pConfigId,
                                           final int pUserId,
                                           final int pRoleId,
                                           final int pUserProjectId) {

        try {
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, getString(R.string.save_user), Constants.LogResult.SENT, getString(R.string.save_user_to_db), "login: " + pLogin);
            getDao().updateUserModelR(pLogin, pPassword, pConfigId, pRoleId, pUserProjectId, pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
            addLog(pLogin, Constants.LogType.DATABASE, Constants.LogObject.USER, getString(R.string.save_user), Constants.LogResult.ERROR, getString(R.string.save_user_to_db_error), e.getMessage());
        }
    }

    public UserModelR getCurrentUser() {
        if (mCurrentUser == null) {
            try {
                mCurrentUser = getUserByUserId(getCurrentUserId());
            } catch (Exception e) {
                showToast(getString(R.string.db_load_error));
            }
        }

        return mCurrentUser;
    }

    private List<ElementModelNew> getElements() {
        if (getCurrentUser() == null) Log.d(TAG, "------------ USER: ");
        if (getCurrentUser().getConfigR() == null) Log.d(TAG, "------------ CONFIG: ");
        return getCurrentUser().getConfigR().getProjectInfo().getElements();
    }

    public HashMap<Integer, ElementModelNew> getMap(boolean rebuild) {
        if (mMap == null) {
            mMap = new HashMap<>();

            generateMap(getElements(), rebuild);

            return mMap;
        } else {
            return mMap;
        }
    }

    private void generateMap(final List<ElementModelNew> elements, boolean rebuild) {
        for (final ElementModelNew element : elements) {
            mMap.put(element.getRelativeID(), element);

            if (rebuild)
                try {
                    ElementItemR elementItemR = new ElementItemR();
                    elementItemR.setConfigId(getCurrentUser().getConfig_id());
                    elementItemR.setUserId(getCurrentUser().getUser_id());
                    elementItemR.setProjectId(getCurrentUser().getConfigR().getProjectInfo().getProjectId());
                    elementItemR.setType(element.getType());
                    elementItemR.setSubtype(element.getSubtype());
                    elementItemR.setRelative_id(element.getRelativeID());
                    elementItemR.setRelative_parent_id(element.getRelativeParentID());

                    final List<Contents> contentsList = element.getContents();
                    List<ElementContentsR> elementContentsRList = new ArrayList<>();
                    if (contentsList != null && !contentsList.isEmpty()) {
                        for (Contents contents : contentsList) {
                            elementContentsRList.add(new ElementContentsR(contents.getType(), contents.getData(), contents.getOrder()));
                        }
                    }
                    if (elementContentsRList.size() > 0) {
                        elementItemR.setElementContentsR(elementContentsRList);
                    }

                    final OptionsModelNew optionsModelNew = element.getOptions();
                    if (optionsModelNew != null) {
                        ElementOptionsR elementOptionsR = new ElementOptionsR();
                        elementOptionsR.setData(optionsModelNew.getData());
                        elementOptionsR.setTitle(optionsModelNew.getTitle());
                        if (optionsModelNew.getJump() != null)
                            elementOptionsR.setJump(optionsModelNew.getJump());
                        elementOptionsR.setSearch(optionsModelNew.isSearch());
                        elementOptionsR.setPre_condition(optionsModelNew.getPre_condition());
                        elementOptionsR.setPost_condition(optionsModelNew.getPost_condition());
                        elementOptionsR.setOrder(optionsModelNew.getOrder());
                        if (optionsModelNew.getNumber() != null)
                            elementOptionsR.setNumber(optionsModelNew.getNumber());
                        elementOptionsR.setPolyanswer(optionsModelNew.isPolyanswer());
                        elementOptionsR.setRecord_sound(optionsModelNew.isRecordSound());
                        elementOptionsR.setTake_photo(optionsModelNew.isTakePhoto());
                        elementOptionsR.setDescription(optionsModelNew.getDescription());
                        elementOptionsR.setFlip_cols_and_rows(optionsModelNew.isFlipColsAndRows());
                        elementOptionsR.setRotation(optionsModelNew.isRotation());
                        elementOptionsR.setFixed_order(optionsModelNew.isFixedOrder());
                        if (optionsModelNew.getMinAnswers() != null)
                            elementOptionsR.setMin_answers(optionsModelNew.getMinAnswers());
                        if (optionsModelNew.getMaxAnswers() != null)
                            elementOptionsR.setMax_answers(optionsModelNew.getMaxAnswers());
                        elementOptionsR.setOpen_type(optionsModelNew.getOpenType());
                        elementOptionsR.setPlaceholder(optionsModelNew.getPlaceholder());
                        elementOptionsR.setUnchecker(optionsModelNew.isUnchecker());
                        elementOptionsR.setStart_value(optionsModelNew.getStart_value());
                        elementOptionsR.setEnd_value(optionsModelNew.getEnd_value());
                        if (optionsModelNew.getStatusImage() != null) {
                            ElementStatusImageR elementStatusImageR = new ElementStatusImageR();
                            elementStatusImageR.setType(optionsModelNew.getStatusImage().getType());
                            elementStatusImageR.setData(optionsModelNew.getStatusImage().getData());
                            elementStatusImageR.setData_on(optionsModelNew.getStatusImage().getData_on());
                            elementStatusImageR.setData_off(optionsModelNew.getStatusImage().getData_off());

                            elementOptionsR.setStatus_image(elementStatusImageR);
                        }

                        elementItemR.setElementOptionsR(elementOptionsR);
                    }

                    getDao().insertElementItemR(elementItemR);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            final List<ElementModelNew> nestedList = element.getElements();
            if (nestedList != null && !nestedList.isEmpty()) {
                generateMap(nestedList, rebuild);
            }
        }
    }

    public void rebuildElementsDatabase() {

        try {
            Log.d(TAG, "Clearing Elements Database............. ");
            getDao().clearElementItemR();
            Log.d(TAG, "Rebuilding Elements Database............. ");
            getMap(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showElementsDB() {
        Log.d(TAG, "========== DATABASE ========== ");
        initCurrentElements();
        Log.d(TAG, "Elements: " + getCurrentElements().size());

        for (int i = 0; i < getCurrentElements().size(); i++) {
            Log.d(TAG, "id: " + getCurrentElements().get(i).getRelative_id());
        }
    }

    public void initCurrentElements() {
        try {
            elementItemRList = getDao().getCurrentElements(getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
            currentQuestionnaire = getDao().getCurrentQuestionnaireR();
        } catch (Exception e) {
            Log.d(TAG, "initCurrentElements: ERROR");
            e.printStackTrace();
        }
    }

    public List<ElementItemR> getCurrentElements() {
        return elementItemRList;
    }

    public ElementItemR getElement(Integer id) {
        ElementItemR elementItemR = null;
        try {
            elementItemR = getDao().getElementById(id, getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementItemR;
    }

    public List<File> getAllPhotos() {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(getContext()));
    }

    public List<File> getPhotosByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(JPEG, FileUtils.getPhotosStoragePath(getContext()) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public List<File> getAllAudio() {
        return FileUtils.getFilesRecursion(AMR, FileUtils.getAudioStoragePath(getContext()));
    }

    public List<File> getAudioByUserId(final int pUserId) {
        return FileUtils.getFilesRecursion(AMR, FileUtils.getAudioStoragePath(getContext()) + FileUtils.FOLDER_DIVIDER + pUserId);
    }

    public CurrentQuestionnaireR getQuestionnaire() {
        return currentQuestionnaire;
    }

    public void showElementsQuery() {
        for (int i = 0; i < getQuestionnaire().getPrev_element_id().size(); i++) {
            Log.d(TAG, i + " element: " + getQuestionnaire().getPrev_element_id().get(i).getPrevId());
        }
    }

    public void reloadConfig() {

        String mLogin = getCurrentUser().getLogin();
        String mPass = getCurrentUser().getPassword();

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), mPass, mLogin);
        Gson gsonAuth = new Gson();
        String jsonAuth = gsonAuth.toJson(post);

        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SENT, getString(R.string.sending_request), jsonAuth);

        QuizerAPI.authUser(getServer(), jsonAuth, responseBody -> {
            if (responseBody == null) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_401));
                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_401_desc), null);

                return;
            }

            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_402_desc), null);
                responseJson = null;
            }

            AuthResponseModel authResponseModel = null;
            try {
                authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
            } catch (final Exception pE) {
                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.log_error_403_desc), responseJson);
            }

            String mConfigId = null;
            if (authResponseModel != null) {
                mConfigId = authResponseModel.getConfigId();
            } else return;

            if (mConfigId != null) {
                final ConfigRequestModel configRequestModel = new ConfigRequestModel(
                        getLoginAdmin(),
                        mLogin,
                        mPass,
                        mConfigId
                );

                Gson gson = new Gson();
                String json = gson.toJson(configRequestModel);

                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.SENT, getString(R.string.try_to_get_config), json);

                QuizerAPI.getConfig(getServer(), json, configResponseBody -> {

                    if (configResponseBody == null) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_601));
                        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_601_desc), null);

                        return;
                    }

                    String configResponseJson = null;
                    try {
                        configResponseJson = configResponseBody.string();
                    } catch (IOException e) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_602));
                        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_602_desc), null);

                    }
                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    ConfigResponseModel configResponseModel = null;

                    try {
                        configResponseModel = gsonBuilder.create().fromJson(configResponseJson, ConfigResponseModel.class);
                    } catch (final Exception pE) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_603));
                        addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, getString(R.string.log_error_603_desc), configResponseJson);
                    }

                    if (configResponseModel != null) {
                        if (configResponseModel.getResult() != 0) {
                            addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.SUCCESS, getString(R.string.get_config_success), configResponseJson);

                            addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.loading_files), Constants.LogResult.SENT, getString(R.string.try_to_load_files), configResponseJson);

                            updateConfig(getCurrentUser(), configResponseModel.getConfig());
                            showToast(getString(R.string.config_updated));

                            final String[] fileUris = getCurrentUser().getConfigR().getProjectInfo().getMediaFiles();

                            if (fileUris == null || fileUris.length == 0) {
                                Log.d(TAG, "reloadConfig: file list empty");
                            } else {

                                FileLoader.multiFileDownload(getContext())
                                        .fromDirectory(Constants.Strings.EMPTY, FileLoader.DIR_EXTERNAL_PRIVATE)
                                        .progressListener(new MultiFileDownloadListener() {
                                            @Override
                                            public void onProgress(final File downloadedFile, final int progress, final int totalFiles) {
                                                FileUtils.renameFile(downloadedFile, FileUtils.getFileName(fileUris[progress - 1]));

                                                if (progress == totalFiles) {
                                                    showToast(String.format(getString(R.string.load_files_complete)));
                                                }
                                                showToast(String.format(getString(R.string.downloaded_count_files), String.valueOf(progress)));
                                            }

                                            @Override
                                            public void onError(final Exception e, final int progress) {
                                                super.onError(e, progress);
                                                showToast(getString(R.string.download_files_error));
                                                addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.FILE, getString(R.string.loading_files), Constants.LogResult.ERROR, getString(R.string.download_files_error), e.toString());
                                            }
                                        }).loadMultiple(fileUris);
                            }
                        } else {
                            showToast(configResponseModel.getError());
                            addLog(mLogin, Constants.LogType.SERVER, Constants.LogObject.CONFIG, getString(R.string.get_config), Constants.LogResult.ERROR, configResponseModel.getError(), configResponseJson);
                        }
                    }

                });
            } else return;

        });


    }

    public void updateConfig(final UserModelR pUserModel, final ConfigModel pConfigModel) {

        try {
            addLog(pUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.CONFIG, getString(R.string.save_config), Constants.LogResult.SENT, getString(R.string.save_config_to_db), null);
            getDao().updateConfig(new GsonBuilder().create().toJson(pConfigModel), pUserModel.getUser_id(), pUserModel.getUser_project_id());

        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
            addLog(pUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.CONFIG, getString(R.string.save_config), Constants.LogResult.ERROR, getString(R.string.save_config_to_db_error), e.getMessage());

        }
    }

    public boolean saveQuestionnaireToDatabase(CurrentQuestionnaireR currentQuiz, boolean aborted) {
        boolean saved = true;
        countElements = 0;
        countScreens = 0;
        countQuestions = 0;

        List<ElementPassedR> elements = null;

        try {
            elements = getDao().getAllElementsPassedR(currentQuiz.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (elements != null && elements.size() > 0) {
            for (ElementPassedR element : elements) {
                if(!saveElement(currentQuiz, element)) {
                    showToast("Ошибка сохранения элемента анкеты");
                    Log.d(TAG, "saveQuestionnaireToDatabase: Elements List is empty");
                    return false;
                }
            }
        }

        final long endTime = DateUtils.getCurrentTimeMillis();
        final long durationTimeQuestionnaire = endTime - currentQuiz.getStart_date();

        final QuestionnaireDatabaseModelR questionnaireDatabaseModel = new QuestionnaireDatabaseModelR();
        questionnaireDatabaseModel.setStatus(QuestionnaireStatus.NOT_SENT);
        questionnaireDatabaseModel.setToken(currentQuiz.getToken());
        questionnaireDatabaseModel.setLogin_admin(getLoginAdmin());
        questionnaireDatabaseModel.setLogin(getCurrentUser().getLogin());
        questionnaireDatabaseModel.setUser_id(getCurrentUserId());
        questionnaireDatabaseModel.setPassw(getCurrentUser().getPassword());
        questionnaireDatabaseModel.setQuestionnaire_id(getCurrentUser().getConfigR().getProjectInfo().getQuestionnaireId());
        questionnaireDatabaseModel.setProject_id(getCurrentUser().getConfigR().getProjectInfo().getProjectId());
//        questionnaireDatabaseModel.setBilling_questions(mBillingQuestions); //TODO Узнать надо или нет!
        questionnaireDatabaseModel.setUser_project_id(getCurrentUser().getUser_project_id());
        questionnaireDatabaseModel.setGps(currentQuiz.getGps());
        questionnaireDatabaseModel.setGps_network(currentQuiz.getGps_network());
        questionnaireDatabaseModel.setGps_time(currentQuiz.getGps_time());
        questionnaireDatabaseModel.setGps_time_network(currentQuiz.getGps_time_network());
        questionnaireDatabaseModel.setDate_interview(currentQuiz.getStart_date());
        questionnaireDatabaseModel.setHas_photo(currentQuiz.getHas_photo());
        questionnaireDatabaseModel.setUsed_fake_gps(currentQuiz.isUsed_fake_gps());
        questionnaireDatabaseModel.setGps_time_fk(currentQuiz.getFake_gps_time());

        if (aborted) {
            if (currentQuiz.isPaused())
                questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.UNFINISHED);
            else
                questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.ABORTED);
        } else {
            questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.COMPLETED);
        }

        questionnaireDatabaseModel.setQuestions_passed(countQuestions);
        questionnaireDatabaseModel.setScreens_passed(countScreens); //TODO сделать подсчет экранов.
        questionnaireDatabaseModel.setSelected_questions(countElements);
        questionnaireDatabaseModel.setDuration_time_questionnaire((int) durationTimeQuestionnaire);
        questionnaireDatabaseModel.setAuth_time_difference(SPUtils.getAuthTimeDifference(getContext()));
        questionnaireDatabaseModel.setQuota_time_difference(SPUtils.getQuotaTimeDifference(getContext()));
        questionnaireDatabaseModel.setSend_time_difference(SPUtils.getSendTimeDifference(getContext()));

        try {
            getDao().insertQuestionnaire(questionnaireDatabaseModel);
            addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.SUCCESS, getString(R.string.save_question_to_db_success), null);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
            addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.ERROR, getString(R.string.save_question_to_db_error), e.toString());
            saved = false;
        }

        if(saved) {
            try {
                getDao().clearCurrentQuestionnaireR();
                getDao().clearElementPassedR();
            } catch (Exception e) {
                showToast(getString(R.string.warning_clear_current_quiz_error));
                addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.ERROR, getString(R.string.warning_clear_current_quiz_error), e.toString());
            }
        }
        return saved;
    }

    private boolean saveElement(CurrentQuestionnaireR currentQuiz, final ElementPassedR element) {
        try {
            final ElementDatabaseModelR elementDatabaseModel = new ElementDatabaseModelR();
            ElementItemR elementItemR = null;
            elementItemR = getDao().getElementById(element.getRelative_id(), getCurrentUserId(), currentQuiz.getProject_id());

            Integer parentId;
            if (elementItemR != null) {
                parentId = elementItemR.getRelative_parent_id();
            } else return false;
            elementDatabaseModel.setToken(element.getToken());

            elementDatabaseModel.setRelative_id(element.getRelative_id());
            elementDatabaseModel.setRelative_parent_id(parentId);
            elementDatabaseModel.setItem_order(elementItemR.getElementOptionsR().getOrder());
            if (ElementType.ANSWER.equals(elementItemR.getType())) {
                elementDatabaseModel.setValue(element.getValue());
                elementDatabaseModel.setType(ElementDatabaseType.ELEMENT);
                countElements++;
            } else {
                if (ElementType.QUESTION.equals(elementItemR.getType())) {
                    countQuestions++;
                }
                elementDatabaseModel.setDuration(element.getDuration());
                elementDatabaseModel.setType(ElementDatabaseType.SCREEN);
                countScreens++;
            }

            LogUtils.logAction("saveElement " + element.getRelative_id());

            try {
                getDao().insertElement(elementDatabaseModel);
            } catch (Exception e) {
                addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.ERROR, getString(R.string.db_save_error), e.toString());
                showToast(getString(R.string.db_save_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.db_load_error));
            return false;
        }
        return true;
    }
}
