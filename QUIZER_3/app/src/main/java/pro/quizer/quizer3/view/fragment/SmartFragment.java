package pro.quizer.quizer3.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pro.quizer.quizer3.API.models.response.AuthResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.CoreApplication;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.QuizerDao;
import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.AppLogsR;
import pro.quizer.quizer3.database.models.CurrentQuestionnaireR;
import pro.quizer.quizer3.database.models.ElementContentsR;
import pro.quizer.quizer3.database.models.ElementItemR;
import pro.quizer.quizer3.database.models.ElementOptionsR;
import pro.quizer.quizer3.database.models.ElementStatusImageR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.Contents;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.config.OptionsModelNew;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.DeviceUtils;
import pro.quizer.quizer3.utils.FileUtils;
import pro.quizer.quizer3.utils.SPUtils;

import static pro.quizer.quizer3.MainActivity.TAG;
import static pro.quizer.quizer3.utils.FileUtils.AMR;
import static pro.quizer.quizer3.utils.FileUtils.JPEG;

@SuppressWarnings("unused")
public abstract class SmartFragment extends Fragment {

    protected Listener listener;
    private UserModelR mCurrentUser;
    private int layoutSrc;
    private HashMap<Integer, ElementModelNew> mMap;

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

        getDao().insertAppLogsR(appLogsR);
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

        for(int i = 0; i < getCurrentElements().size(); i++) {
            Log.d(TAG, "id: " + getCurrentElements().get(i).getRelative_id());
        }
    }

    public void initCurrentElements() {
        try {
            elementItemRList = getDao().getCurrentElements(getCurrentUserId(), getCurrentUser().getConfigR().getProjectInfo().getProjectId());
            currentQuestionnaire = getDao().getCurrentQuestionnaireR();
        } catch (Exception e) {
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
}
