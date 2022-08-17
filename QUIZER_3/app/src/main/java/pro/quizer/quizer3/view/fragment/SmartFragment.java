package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraFragment;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.MultiFileDownloadListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.AuthRequestModel;
import pro.quizer.quizer3.API.models.request.ConfigRequestModel;
import pro.quizer.quizer3.API.models.request.CrashRequestModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireRequestModel;
import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.API.models.response.ConfigResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.CoreApplication;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.CrashLogs;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementDatabaseModelR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementPassedR;
import pro.quizer.quizer3.database.models.OptionsR;
import pro.quizer.quizer3.database.models.QuestionnaireDatabaseModelR;
import pro.quizer.quizer3.database.models.SettingsR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.QuestionnaireRequestModelExecutable;
import pro.quizer.quizer3.model.ElementDatabaseType;
import pro.quizer.quizer3.model.ElementSubtype;
import pro.quizer.quizer3.model.ElementType;
import pro.quizer.quizer3.model.QuestionnaireStatus;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.logs.Crash;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.FontUtils;
import pro.quizer.quizer3.utils.SPUtils;

import static pro.quizer.quizer3.MainActivity.AVIA;
import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.executable.files.UploadingExecutable.UPLOADING_PATH;
import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;

@SuppressWarnings("unused")
public abstract class SmartFragment extends HiddenCameraFragment {

    protected Listener listener;
    private UserModelR mCurrentUser;
    private int layoutSrc;
    private Integer countElements;
    private Integer countScreens;
    private Integer countQuestions;
    private Integer tempUserProjectId;
    private String mUserLogin = Constants.Strings.UNKNOWN;
    private String mLoginAdmin = Constants.Strings.UNKNOWN;
    private String mToken = Constants.Strings.UNKNOWN;
    private int mRelativeId = -1;
    private int mUserId = -1;
    private int mProjectId = -1;
    private int abortedBoxRelativeId = 0;
    private boolean hasAbortedBox = false;
    private CurrentQuestionnaireR currentQuestionnaire = null;
    private List<ElementItemR> elementItemRList = null;
    private long durationTimeQuestionnaire = 0;
    public Events eventsListener = null;
    public boolean canGoBack = true;

    public SmartFragment(int layoutSrc) {
        this.layoutSrc = layoutSrc;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean isAutoZoom = getMainActivity().isAutoZoom();
        switch (layoutSrc) {
            case R.layout.fragment_settings:
                layoutSrc = isAutoZoom ? R.layout.fragment_settings_auto : layoutSrc;
                break;
            case R.layout.fragment_auth:
                layoutSrc = isAvia() ? R.layout.fragment_auth_avia : isAutoZoom ? R.layout.fragment_auth_auto : layoutSrc;
                break;
            case R.layout.fragment_home:
                layoutSrc = isAvia() ? R.layout.fragment_home_avia : isAutoZoom ? R.layout.fragment_home_auto : layoutSrc;
                break;
            case R.layout.fragment_sync:
                layoutSrc = isAutoZoom ? R.layout.fragment_sync_auto : layoutSrc;
                break;
            case R.layout.fragment_service:
                layoutSrc = isAutoZoom ? R.layout.fragment_service_auto : layoutSrc;
                break;
            case R.layout.fragment_quotas:
                layoutSrc = isAutoZoom ? R.layout.fragment_quotas_auto : layoutSrc;
                break;
            case R.layout.fragment_logs:
                layoutSrc = isAutoZoom ? R.layout.fragment_logs_auto : layoutSrc;
                break;
            case R.layout.fragment_key:
                layoutSrc = isAvia() ? R.layout.fragment_key_avia : isAutoZoom ? R.layout.fragment_key_auto : layoutSrc;
                break;
            case R.layout.fragment_about:
                layoutSrc = isAutoZoom ? R.layout.fragment_about_auto : layoutSrc;
                break;
            case R.layout.fragment_element:
                layoutSrc = isAutoZoom ? R.layout.fragment_element_auto : layoutSrc;
                break;
            case R.layout.fragment_reg1:
                layoutSrc = isAutoZoom ? R.layout.fragment_reg1_auto : layoutSrc;
                break;
            case R.layout.fragment_reg2:
                layoutSrc = isAutoZoom ? R.layout.fragment_reg2_auto : layoutSrc;
                break;
            case R.layout.fragment_reg3:
                layoutSrc = isAutoZoom ? R.layout.fragment_reg3_auto : layoutSrc;
                break;
            case R.layout.fragment_reg4:
                layoutSrc = isAutoZoom ? R.layout.fragment_reg4_auto : layoutSrc;
                break;

            default:
        }

        return inflater.inflate(layoutSrc, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        startBackCounter();
        onReady();
    }

    public void refreshFragment() {
        Log.d(TAG, "refreshFragment: " + getVisibleFragment().getClass());
        if (getVisibleFragment() instanceof SettingsFragment) {
            Log.d(TAG, "refreshFragment: SETTINGS");
        } else {
            if (!getMainActivity().isFinishing()) {
                Log.d(TAG, "refreshFragment: RANDOM");
                showToast(getString(R.string.setted) + " " + FontUtils.getCurrentFontName(getMainActivity().getFontSizePosition()));
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getMainActivity().getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    public boolean isAvia() {
        return AVIA;
    }

    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
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

    //TODO Вернуть логи! Временно выключены из за тестов на учечку памяти.

//    public static void addLog(String login,
//                              String type,
//                              String object,
//                              String action,
//                              String result,
//                              String desc,
//                              String data) {
//        AppLogsR appLogsR = new AppLogsR();
//        appLogsR.setLogin(login);
//        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
//        appLogsR.setAppversion(DeviceUtils.getAppVersion());
//        appLogsR.setPlatform(DeviceUtils.getAndroidVersion());
//        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
//        appLogsR.setType(type);
//        appLogsR.setObject(object);
//        appLogsR.setAction(action);
//        appLogsR.setResult(result);
//        appLogsR.setDescription(desc);
//        if (data != null)
//            appLogsR.setInfo(data.substring(0, Math.min(data.length(), 5000)));
//
//        try {
//            getDao().insertAppLogsR(appLogsR);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void showToast(String text) {
        final MainActivity activity = getMainActivity();
        if (activity != null) {
            try {
                activity.runOnUiThread(() -> Toast.makeText(activity, text, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isActivated() {
        return getActivationModel() != null;
    }

    public ActivationModelR getActivationModel() {

        List<ActivationModelR> list = null;

        try {
            list = getDao().getActivationModelR();
        } catch (Exception e) {
            showToast(getString(R.string.db_load_error));
        }

        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;

    }

    public int getCurrentUserId() {
        MainActivity activity = getMainActivity();
        if (activity != null)
            return SPUtils.getCurrentUserId(activity);
        else return 0;
    }

    public UserModelR getUserByUserId(final int pUserId) {

        UserModelR user = null;
        try {
            user = getDao().getUserByUserId(pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.db_load_error));
        }

        return user;
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
        int oldUserId = SPUtils.getCurrentUserId(getContext());
        if (oldUserId != pUserId) {
            SPUtils.saveCurrentUserId(getContext(), pUserId);
            try {
                MainActivity activity = getMainActivity();
                activity.getMainDao().clearCurrentQuestionnaireR();
                activity.getMainDao().clearPrevElementsR();
                activity.setCurrentQuestionnaireNull();
                activity.getMainDao().clearElementPassedR();
                activity.getMainDao().clearElementItemR();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveUser(final String pLogin, final String pPassword, final AuthResponseModel pModel, final ConfigModel pConfigModel) throws Exception {
        Log.d(TAG, "Saving User To Database............. ");

        String oldConfig = null;
        UserModelR oldUser = null;

        try {
            oldUser = getMainActivity().getUserByUserId(pModel.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (oldUser != null && oldUser.getUser_id() == pModel.getUserId()) {
            String configId = oldUser.getConfigR().getConfigId();
            if (configId == null)
                configId = oldUser.getConfig_id();
            if (getMainActivity().getCurrentQuestionnaireByConfigId(configId) != null) {
                oldConfig = oldUser.getConfig();
            }
        } else {
            try {
                getDao().clearCurrentQuestionnaireR();
//                getDao().clearQuotaR(configId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
        userModelR.setRole_id(pModel.getRoleId());
        userModelR.setUser_id(pModel.getUserId());
        userModelR.setUser_project_id(pModel.getUserProjectId());

        pConfigModel.setUserProjectId(pModel.getUserProjectId());
        if (oldConfig == null) {
            userModelR.setConfig(new GsonBuilder().create().toJson(pConfigModel));
            userModelR.setConfig_id(pModel.getConfigId());
            getDao().setConfigTime(DateUtils.getCurrentTimeMillis());
        } else {
            userModelR.setConfig(oldConfig);
            userModelR.setConfig_new(new GsonBuilder().create().toJson(pConfigModel));
            userModelR.setConfig_id(pModel.getConfigId());
            getDao().setConfigTime(DateUtils.getCurrentTimeMillis());
        }

        try {
            getDao().insertUser(userModelR);
            getDao().insertOption(new OptionsR(Constants.OptionName.QUIZ_STARTED, "false"));
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.db_save_error));
        }


    }

    public void updateDatabaseUserByUserId(final String pLogin,
                                           final String pPassword,
                                           final String pConfigId,
                                           final int pUserId,
                                           final int pRoleId,
                                           final int pUserProjectId) {

        try {
            getDao().updateUserModelR(pLogin, pPassword, pConfigId, pRoleId, pUserProjectId, pUserId);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
        }
    }

    public UserModelR getCurrentUser() {
        if (mCurrentUser == null) {
            try {
                mCurrentUser = getUserByUserId(getCurrentUserId());
            } catch (Exception e) {
                e.printStackTrace();
                showToast(getString(R.string.db_load_error));
            }
        }

        return mCurrentUser;
    }

    private List<ElementModelNew> getElements() {
        if (getCurrentUser() == null) Log.d(TAG, "------------ USER: ");
        if (getMainActivity().getConfig() == null) Log.d(TAG, "------------ CONFIG: ");
        return getMainActivity().getConfig().getProjectInfo().getElements();
    }


    public void rebuildElementsDatabase() {

        try {
            Log.d(TAG, "Rebuilding Elements Database............. ");
            getMainActivity().setTree(null);
            getMainActivity().getMap(true);
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
            elementItemRList = getMainActivity().getElementItemRList();
            currentQuestionnaire = getMainActivity().getCurrentQuestionnaire();
        } catch (Exception e) {
            Log.d(TAG, "initCurrentElements: ERROR");
            e.printStackTrace();
        }
    }

    public List<ElementItemR> getCurrentElements() {
        return elementItemRList;
    }

    public void checkAbortedBox() {
        if (elementItemRList != null) {
            for (ElementItemR element : elementItemRList) {
                if (element.getSubtype() != null && element.getSubtype().equals(ElementSubtype.ABORTED)) {
                    hasAbortedBox = true;
                    abortedBoxRelativeId = element.getRelative_id();
                    return;
                }
            }
        }
    }

    public int getAbortedBoxRelativeId() {
        return abortedBoxRelativeId;
    }

    public boolean hasAbortedBox() {
        return hasAbortedBox;
    }

    public ElementItemR getElement(Integer id) {
        ElementItemR elementItemR = null;
        try {
//            elementItemR = getDao().getElementById(id, getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
            elementItemR = getDao().getElementById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elementItemR;
    }

    public List<Integer> getPassedQuotasBlock(int max) {
        List<Integer> passedQuotasBlock = new ArrayList<>();
        List<ElementPassedR> passedElements = getDao().getAllElementsPassedR(getMainActivity().getCurrentQuestionnaire().getToken());
        if (passedElements == null || passedElements.size() == 0) {
            return null;
        }

        for (int k = 0; k < passedElements.size(); k++) {
            if (passedElements.get(k).isFrom_quotas_block()) {
                passedQuotasBlock.add(passedElements.get(k).getRelative_id());
            }
        }

        List<Integer> passedQuotasBlockNew = new ArrayList<>();

        if (passedQuotasBlock.size() > 0) {
            for (int i = 0; i < max - 1; i++) {
                passedQuotasBlockNew.add(passedQuotasBlock.get(i));
            }
        }
        return passedQuotasBlockNew;
    }

    public List<List<Integer>> getMultiPassedQuotasBlock(int max) {
        List<List<Integer>> passedQuotasBlock = new ArrayList<>();
        List<ElementPassedR> passedElements = getDao().getQuotaPassedElements(getMainActivity().getCurrentQuestionnaire().getToken(), true);
//        List<ElementPassedR> passedElements2 = getDao().getAllElementsPassedRNoToken();
        if (passedElements == null || passedElements.size() == 0) {
            return null;
        }

        for (ElementPassedR item : passedElements) {
            Log.d("T-A-R.SmartFragment", "PASSED ID >>>>>: " + item.getRelative_id());
        }

        Integer savedParent = null;
        for (int k = 0; k < passedElements.size(); k++) {
//            Log.d("T-A-R.SmartFragment", "Passed ID: " + passedElements.get(k).getRelative_id());
            Integer parentId = passedElements.get(k).getParent_id();
//            Log.d("T-A-R.SmartFragment", "getMultiPassedQuotasBlock PARENT: " + parentId);
            if (!parentId.equals(savedParent)) {
                savedParent = parentId;
                List<Integer> passedAnswersList = getDao().getQuotaPassedAnswers(getMainActivity().getCurrentQuestionnaire().getToken(), true, parentId);
//                try {
//                    Log.d("T-A-R.SmartFragment", "getMultiPassedQuotasBlock LIST: " + passedAnswersList.size());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                passedQuotasBlock.add(passedAnswersList);
            }
        }

//        for (List<Integer> item : passedQuotasBlock) {
//            Log.d("T-A-R.SmartFragment", "PASSED ID >>>>>: " + item.get(0));
//        }

        List<List<Integer>> passedQuotasBlockNew = new ArrayList<>();
//
//        for(List<Integer> block : passedQuotasBlock) {
//            Log.d("T-A-R.SmartFragment", "????: " + block.get(0));
//        }

        if (passedQuotasBlock.size() > 0) {
            for (int i = 0; i < max - 1; i++) {
                passedQuotasBlockNew.add(passedQuotasBlock.get(i));
            }
        }
        return passedQuotasBlockNew;
    }

    public Map<Integer, List<Integer>> getPassedQuotasMap(int max) {
        Map<Integer, List<Integer>> mapBlock = new HashMap<>();
        Map<Integer, List<Integer>> limitedMapBlock = new HashMap<>();
        List<ElementPassedR> passedElements = getDao().getAllElementsPassedR(getMainActivity().getCurrentQuestionnaire().getToken());
        if (passedElements == null || passedElements.size() == 0) {
            return null;
        }

        int key = -1;
        int currentOrder = -1;
        for (int k = 0; k < passedElements.size(); k++) {
            if (passedElements.get(k).isFrom_quotas_block()) {
                int id = passedElements.get(k).getRelative_id();
                int parentId = getElement(id).getRelative_parent_id();
                int parentOrder = getElement(parentId).getElementOptionsR().getOrder();
                if (parentOrder != currentOrder) {
                    currentOrder = parentOrder;
                    key++;
                }
                List<Integer> step = mapBlock.get(key);
                if (step == null) step = new ArrayList<>();
                step.add(id);
                mapBlock.put(key, step);

            }
        }

        if (mapBlock.size() > 0) {
            for (int i = 0; i < max - 1; i++) {
                limitedMapBlock.put(i, mapBlock.get(i));
            }
        }

        return limitedMapBlock;
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
        currentQuestionnaire = getMainActivity().getCurrentQuestionnaire();
        return currentQuestionnaire;

    }

    public void setCurrentQuestionnaire(CurrentQuestionnaireR currentQuestionnaire) {
        this.currentQuestionnaire = currentQuestionnaire;
    }

    public CurrentQuestionnaireR getQuestionnaireFromDB() {
        CurrentQuestionnaireR quiz = getDao().getCurrentQuestionnaireR();
        return quiz;
    }

    public void reloadConfig() {

        String mLogin = getCurrentUser().getLogin();
        String mPass = getCurrentUser().getPassword();

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), mPass, mLogin);
        Gson gsonAuth = new Gson();
        String jsonAuth = gsonAuth.toJson(post);

        QuizerAPI.authUser(getServer(), jsonAuth, responseBody -> {
            if (responseBody == null) {
                showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_401));
                return;
            }

            String responseJson;
            try {
                responseJson = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
                responseJson = null;
            }

            AuthResponseModel authResponseModel = null;
            try {
                authResponseModel = new GsonBuilder().create().fromJson(responseJson, AuthResponseModel.class);
            } catch (final Exception pE) {
                pE.printStackTrace();
            }

            String mConfigId = null;
            if (authResponseModel != null) {
                mConfigId = authResponseModel.getConfigId();
                tempUserProjectId = authResponseModel.getUserProjectId();
                if (authResponseModel.isProjectActive() != null) {
                    try {
                        getDao().setProjectActive(authResponseModel.isProjectActive());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

                QuizerAPI.getConfig(getServer(), json, configResponseBody -> {

                    if (configResponseBody == null) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_601));
                        return;
                    }

                    String configResponseJson = null;
                    try {
                        configResponseJson = configResponseBody.string();
                    } catch (IOException e) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_602));
                        e.printStackTrace();
                    }
                    final GsonBuilder gsonBuilder = new GsonBuilder();
                    ConfigResponseModel configResponseModel = null;

                    try {
                        configResponseModel = gsonBuilder.create().fromJson(configResponseJson, ConfigResponseModel.class);
                    } catch (final Exception pE) {
                        showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_603));
                        pE.printStackTrace();
                    }

                    if (configResponseModel != null) {
                        if (configResponseModel.isProjectActive() != null) {
                            try {
                                getDao().setProjectActive(configResponseModel.isProjectActive());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (configResponseModel.getResult() != 0) {
                            updateConfig(getCurrentUser(), configResponseModel.getConfig());

                            ConfigModel configModel = getMainActivity().getConfigForce();
                            final String[] fileUris = configResponseModel.getConfig().getProjectInfo().getMediaFiles();

                            if (fileUris == null || fileUris.length == 0) {
                                Log.d("TARLOGS.SmartFragment", "reloadConfig: file list empty");
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
                                            }
                                        }).loadMultiple(fileUris);
                            }
                        } else {
                            showToast(configResponseModel.getError());
                        }
                    }
                });
            } else return;

        });


    }

    public boolean updateConfig(final UserModelR pUserModel, final ConfigModel pConfigModel) {

        try {
            String oldConfig = null;
            UserModelR oldUser = null;

            try {
                Log.d(TAG, "==== pUserModel.getUser_id() = " + pUserModel.getUser_id());

                oldUser = getMainActivity().getUserByUserId(pUserModel.getUser_id());
            } catch (Exception e) {
                Log.d(TAG, "updateConfig: ERROR!");
                e.printStackTrace();
            }

            if (oldUser != null) {
                String configId = pUserModel.getConfigR().getConfigId();
                if (configId == null)
                    configId = pUserModel.getConfig_id();
                if (getMainActivity().getCurrentQuestionnaireByConfigId(configId) != null) {
                    oldConfig = oldUser.getConfig();
                } else {
                    Log.d(TAG, "==== CURRENT QUIZ IS NULL ==== ");
                }
            } else {
                Log.d(TAG, "==== OLD USER IS NULL ====");
            }

            if (tempUserProjectId == null) {
                tempUserProjectId = pUserModel.getUser_project_id();
            }
            pConfigModel.setUserProjectId(pUserModel.getUser_project_id());
            if (oldConfig != null) {
                Log.d(TAG, "==== HAVE QUIZ. SAVE TO NEW CONFIG ====");
                getDao().updateNewConfig(new GsonBuilder().create().toJson(pConfigModel), pUserModel.getUser_id(), pUserModel.getUser_project_id());
                showToast(getString(R.string.update_config_delay));
                return false;
            } else {
                Log.d(TAG, "==== SAVE TO CONFIG COZ NO HAVE QUIZ ====");
                getDao().setConfigTime(DateUtils.getCurrentTimeMillis());
                getDao().updateConfig(new GsonBuilder().create().toJson(pConfigModel), pUserModel.getUser_id(), pUserModel.getUser_project_id());
                getMainActivity().getConfigForce();

                SmartFragment.UpdateQuiz updateQuiz = new SmartFragment.UpdateQuiz();
                updateQuiz.execute();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.db_save_error));
            return false;
        }
    }

    public boolean saveQuestionnaireToDatabase(CurrentQuestionnaireR currentQuiz, boolean aborted) {

        try {
            getMainActivity().addLog(Constants.LogObject.QUESTIONNAIRE, "SAVE_TO_DB", Constants.LogResult.ATTEMPT, currentQuiz.getToken(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean saved = true;
        countElements = 0;
        countScreens = 0;
        countQuestions = 0;

        Integer user_project_id = null;
        user_project_id = getCurrentUser().getConfigR().getUserProjectId();
        if (user_project_id == null)
            user_project_id = getCurrentUser().getUser_project_id();

        List<ElementPassedR> elements = null;
        try {
            elements = getDao().getAllElementsPassedR(currentQuiz.getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (elements != null && elements.size() > 0) {
            for (ElementPassedR element : elements) {

                if (!saveElement(currentQuiz, element)) {
                    showToast("Ошибка сохранения элемента анкеты");
                    Log.d(TAG, "saveQuestionnaireToDatabase: Elements List is empty: " + element.getRelative_id());
                    return false;
                }
            }
        }
        final long endTime = DateUtils.getCurrentTimeMillis();
        final QuestionnaireDatabaseModelR questionnaireDatabaseModel = new QuestionnaireDatabaseModelR();
        final SettingsR settings = getMainActivity().getSettings();
        questionnaireDatabaseModel.setStatus(QuestionnaireStatus.NOT_SENT);
        questionnaireDatabaseModel.setConfig_id(getMainActivity().getConfigId());
        questionnaireDatabaseModel.setQuota_time(settings.getLast_quota_time());
        questionnaireDatabaseModel.setLast_login_time(settings.getLast_login_time());
        questionnaireDatabaseModel.setToken(currentQuiz.getToken());
        questionnaireDatabaseModel.setLogin_admin(getLoginAdmin());
        questionnaireDatabaseModel.setLogin(getCurrentUser().getLogin());
        questionnaireDatabaseModel.setUser_id(getCurrentUserId());
        questionnaireDatabaseModel.setPassw(getCurrentUser().getPassword());
        questionnaireDatabaseModel.setQuestionnaire_id(getMainActivity().getConfig().getProjectInfo().getQuestionnaireId());
        questionnaireDatabaseModel.setProject_id(getMainActivity().getConfig().getProjectInfo().getProjectId());
        questionnaireDatabaseModel.setUser_project_id(user_project_id);
        questionnaireDatabaseModel.setGps(currentQuiz.getGps());
        questionnaireDatabaseModel.setGps_network(currentQuiz.getGps_network());
        questionnaireDatabaseModel.setGps_time(currentQuiz.getGps_time());
        questionnaireDatabaseModel.setGps_time_network(currentQuiz.getGps_time_network());
        questionnaireDatabaseModel.setDate_interview(currentQuiz.getStart_date());
        questionnaireDatabaseModel.setHas_photo(currentQuiz.getHas_photo());
        questionnaireDatabaseModel.setUsed_fake_gps(currentQuiz.isUsed_fake_gps());
        questionnaireDatabaseModel.setGps_time_fk(currentQuiz.getFake_gps_time());
        questionnaireDatabaseModel.setUser_name(settings.getUser_name());
        questionnaireDatabaseModel.setUser_date(settings.getUser_date());
        questionnaireDatabaseModel.setIs_google_gps(currentQuiz.isIs_google_gps());
        questionnaireDatabaseModel.setRegistered_uik(currentQuiz.getRegistered_uik());
        questionnaireDatabaseModel.setHas_sim(currentQuiz.getHas_sim());
        questionnaireDatabaseModel.setPermissions(currentQuiz.getPermissions());
        questionnaireDatabaseModel.setAirplane_mode(currentQuiz.isAirplane_mode());
        questionnaireDatabaseModel.setGps_on(currentQuiz.isGps_on());

        if (aborted || getQuestionnaire().isIn_aborted_box() || getQuestionnaireFromDB().isIn_aborted_box()) {
            questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.ABORTED);
            questionnaireDatabaseModel.setCount_interrupted(currentQuiz.getCount_interrupted() + 1);

        } else {
            questionnaireDatabaseModel.setSurvey_status(Constants.QuestionnaireStatuses.COMPLETED);
            questionnaireDatabaseModel.setCount_interrupted(currentQuiz.getCount_interrupted());
        }
        questionnaireDatabaseModel.setQuestions_passed(countQuestions);
        questionnaireDatabaseModel.setScreens_passed(countScreens);
        questionnaireDatabaseModel.setSelected_questions(countElements);
        questionnaireDatabaseModel.setDate_end_interview(endTime);
        questionnaireDatabaseModel.setDuration_time_questionnaire((int) (endTime - currentQuiz.getStart_date()));
        questionnaireDatabaseModel.setAuth_time_difference(SPUtils.getAuthTimeDifference(getContext()));
        questionnaireDatabaseModel.setQuota_time_difference(SPUtils.getQuotaTimeDifference(getContext()));
        questionnaireDatabaseModel.setSend_time_difference(SPUtils.getSendTimeDifference(getContext()));

        try {
            getDao().insertQuestionnaire(questionnaireDatabaseModel);
            getMainActivity().setSettings(Constants.Settings.QUIZ_TIME, String.valueOf(DateUtils.getCurrentTimeMillis()));
            Log.d("T-A-R.SmartFragment", "saveQuestionnaireToDatabase: " + questionnaireDatabaseModel.toString());
            saveQuizToFile(questionnaireDatabaseModel.getToken());
            getMainActivity().addLog(Constants.LogObject.QUESTIONNAIRE, "SAVE_TO_DB", Constants.LogResult.SUCCESS, questionnaireDatabaseModel.getToken(), null);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
            getMainActivity().addLog(Constants.LogObject.QUESTIONNAIRE, "SAVE_TO_DB", Constants.LogResult.ERROR, currentQuiz.getToken(), null);
            saved = false;
        }

        if (saved) {
            try {
                getDao().clearCurrentQuestionnaireR();
                getDao().clearElementPassedR();
                getDao().clearPrevElementsR();
                getMainActivity().setCurrentQuestionnaireNull();
            } catch (Exception e) {
                showToast(getString(R.string.warning_clear_current_quiz_error));
//                addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.ERROR, getString(R.string.warning_clear_current_quiz_error), e.toString());
            }
        }

        try {
            getDao().updateQuestionnaireStart(false, getCurrentUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "saveQuestionnaireToDatabase finished: " + saved);
        return saved;

    }

    private void saveQuizToFile(String token) {
        UserModelR user = getCurrentUser();
        final QuestionnaireRequestModel requestModel = new QuestionnaireRequestModelExecutable(getMainActivity(), token).execute();
        if (requestModel != null) {
            Gson gson = new Gson();
            String json = gson.toJson(requestModel);
            try {
                FileUtils.createTxtFile(UPLOADING_PATH, String.format("data_%1$s_%2$s" + FileUtils.JSON, user.getLogin(), DateUtils.getCurrentTimeMillis()), json);
                getMainActivity().addLog(Constants.LogObject.QUESTIONNAIRE, "SAVE_TO_FILE", Constants.LogResult.ATTEMPT, token, json);

            } catch (final IOException pE) {

            }
        }
    }

    private boolean saveElement(CurrentQuestionnaireR currentQuiz, final ElementPassedR element) {
        try {
//            Log.d("T-A-R.SmartFragment", "saveElement ID: " + element.getRelative_id());
            final ElementDatabaseModelR elementDatabaseModel = new ElementDatabaseModelR();
            ElementItemR elementItemR = null;
            elementItemR = getDao().getElementById(element.getRelative_id());
            Integer parentId;
            if (elementItemR != null) {
                parentId = elementItemR.getRelative_parent_id();
            } else {

                return false;
            }
            elementDatabaseModel.setToken(element.getToken());
            elementDatabaseModel.setRelative_id(element.getRelative_id());
            elementDatabaseModel.setRelative_parent_id(parentId);
            elementDatabaseModel.setItem_order(elementItemR.getElementOptionsR().getOrder());
            if (ElementType.ANSWER.equals(elementItemR.getType())) {
                if (element.isHelper() != null && element.isHelper()) {
                    elementDatabaseModel.setHelper(true);
                }
                elementDatabaseModel.setValue(element.getValue());
                elementDatabaseModel.setRank(element.getRank());
                elementDatabaseModel.setType(ElementDatabaseType.ELEMENT);
                countElements++;
            } else {
                if (ElementType.QUESTION.equals(elementItemR.getType())) {
                    countQuestions++;
                }
                if(element.getDuration() != null) {
                    elementDatabaseModel.setDuration(element.getDuration());
                    durationTimeQuestionnaire += element.getDuration();
                }
                elementDatabaseModel.setType(ElementDatabaseType.SCREEN);
                countScreens++;
            }
            try {
                getDao().insertElement(elementDatabaseModel);
            } catch (Exception e) {
//                addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, getString(R.string.save_question_to_db), Constants.LogResult.ERROR, getString(R.string.db_save_error), e.toString());
                showToast(getString(R.string.db_save_error));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.db_load_error));
            return false;
        }
        return true;
    }

    @Override
    public void onImageCapture(@NonNull File pImageFile) {
        if (FileUtils.renameFile(getContext(),
                pImageFile,
                getCurrentUserId(),
                FileUtils.generatePhotoFileName(mLoginAdmin, mProjectId, mUserLogin, mToken, mRelativeId))) {
        }
    }

    @Override
    public void onCameraError(int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
//                showToast("Не удается сохранить фото");
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                showToast(getString(R.string.no_camera_access));

                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.

                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                showToast(getString(R.string.no_front_camera));

                break;
        }
    }


    @SuppressLint("MissingPermission")
    public void shotPicture(final String pLoginAdmin,
                            final String pToken,
                            final int pRelativeId,
                            final int pUserId,
                            final int pProjectId,
                            final String pUserLogin) {
        mProjectId = pProjectId;
        mUserLogin = pUserLogin;
        mLoginAdmin = pLoginAdmin;
        mToken = pToken;
        mRelativeId = pRelativeId;
        mUserId = pUserId;

        TakePicture take = new TakePicture();
        take.execute();
    }

    public void sendCrashLogs() {
        Log.d(TAG, "Crash logs: " + getDao().getCrashLogs().size() + " quiz started: " + getCurrentUser().isQuestionnaire_opened());
        List<Crash> crashList = new ArrayList<>();
        boolean wasStarted = false;
        List<CrashLogs> crashLogsList = null;
        try {
            wasStarted = getCurrentUser().isQuestionnaire_opened();
        } catch (Exception e) {
            e.printStackTrace();
//            addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.LOG, getString(R.string.load_crash_log_from_db), Constants.LogResult.ERROR, getString(R.string.db_load_error), e.getMessage());
        }

//        if (wasStarted) {
//            getDao().insertCrashLog(new CrashLogs(DateUtils.getCurrentTimeMillis(),
//                    "Date: " + DateUtils.getCurrentFormattedDate(DateUtils.PATTERN_FULL) +
//                            "\nVersion: " + DeviceUtils.getAppVersion() +
//                            "\nDevice: " + DeviceUtils.getDeviceInfo() +
//                            "\nПриложение зависло или было закрыто во время анкеты. Лога нет", true));
//        }

        try {
            crashLogsList = getDao().getCrashLogs();
        } catch (Exception e) {
            e.printStackTrace();
//            addLog(getCurrentUser().getLogin(), Constants.LogType.DATABASE, Constants.LogObject.LOG, getString(R.string.load_crash_log_from_db), Constants.LogResult.ERROR, getString(R.string.db_load_error), e.getMessage());
        }


        if (crashLogsList != null && crashLogsList.size() > 0) {
            Log.d(TAG, "sendCrashLogs: " + crashLogsList.size());
            for (CrashLogs crash : crashLogsList) {
                crashList.add(new Crash(getCurrentUser().getLogin(), crash.getLog(), crash.isFrom_questionnaire()));
            }

            CrashRequestModel crashRequestModel = new CrashRequestModel(getLoginAdmin(), crashList);
            Gson gson = new Gson();
            String json = gson.toJson(crashRequestModel);
            QuizerAPI.sendCrash(getServer(), json, (ok, message) -> {
                if (ok) {
                    try {
                        getDao().clearCrashLogs();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Crash Logs Clear Error: " + e);
                    }
                } else {
                    Log.d(TAG, "Crash Logs Not Sent: " + message);
                }
            });
        }
    }

    public boolean hasUnfinishedOrSend(int mQUnsendedCount, boolean hasUnfinishedQuiz) {
        if (mQUnsendedCount > 0 || hasUnfinishedQuiz) {
            if (mQUnsendedCount > 0 && hasUnfinishedQuiz) {
                showToast(getString(R.string.notification_please_send_and_clear_quiz));
            } else if (hasUnfinishedQuiz) {
                showToast(getString(R.string.notification_please_clear_quiz));
            } else {
                showToast(getString(R.string.notification_please_send_quiz));
            }
            return true;
        } else return false;
    }

    class UpdateQuiz extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (eventsListener != null) {
                eventsListener.runEvent(1);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rebuildElementsDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (eventsListener != null) {
                eventsListener.runEvent(2);
            }
        }
    }

    public void setEventsListener(Events listener) {
        eventsListener = listener;
    }

    public interface Events {
        void runEvent(int id);
        // 01 - isCanBackPress = false
        // 02 - run HomeFragment
    }

    class TakePicture extends AsyncTask<Void, Void, Void> {

        @SuppressLint("MissingPermission")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                startCamera(new CameraConfig()
                        .getBuilder(getContext())
                        .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.LOW_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .setImageRotation(CameraRotation.ROTATION_270)
                        .build());
            } catch (final Exception pException) {
                showToast("Не удается стартануть камеру");
                pException.printStackTrace();

            }
        }

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, ">>> Taking Hidden Picture <<<");

            try {
                takePicture();
                try {
                    getDao().setCurrentQuestionnairePhoto(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
                try {
                    getDao().setCurrentQuestionnairePhoto(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        takePicture();
                        try {
                            getDao().setCurrentQuestionnairePhoto(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
                        try {
                            getDao().setCurrentQuestionnairePhoto(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }, 10);
        }
    }

    public void showErrorDialog(String header, String message) {
        MainActivity activity = getMainActivity();
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (!activity.isFinishing()) {
                    new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setTitle(header)
                            .setMessage(message)
                            .setPositiveButton(R.string.view_OK, (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            }
        });
    }

    public String getUserName() {
        SettingsR settings = getMainActivity().getSettings();
        String userName = settings.getUser_name() + " ";
        try {
            if (settings.getUser_date() != null)
                userName += settings.getUser_date();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userName;
    }

    private void startBackCounter() {
        canGoBack = false;
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> {
                })
                .takeUntil(aLong -> aLong == 2)
                .doOnComplete(() -> canGoBack = true)
                .subscribe();
    }

    public String encode(String message) {
        StringBuilder encoded = new StringBuilder();
        for (Character ch : message.toCharArray()) {
            encoded.append(getEncrypted(ch));
        }
        return encoded.toString();
    }

    public Character getDecrypted(char encrypted) {
        return getDao().getSymbolsForDecrypt(encrypted);
    }

    public Character getEncrypted(char decrypted) {
        return getDao().getSymbolsForEncrypt(decrypted);
    }

}
