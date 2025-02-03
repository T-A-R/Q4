package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.QToken;
import pro.quizer.quizer3.API.models.response.AddressDatabaseResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.AddressAdapter;
import pro.quizer.quizer3.database.models.AddressR;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.model.config.UserSettings;
import pro.quizer.quizer3.model.mappers.MapperQuizer;
import pro.quizer.quizer3.model.ui.AddressItem;
import pro.quizer.quizer3.utils.GPSModel;
import pro.quizer.quizer3.utils.Internet;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.PhoneFormatter;

public class UikSelectFragment extends ScreenFragment implements AddressAdapter.OnUserClickListener {

    TextView uikInfo;
    TextView addressInfo;
    private Integer startElementId;
    private Button btnApply;
    private Button btnSearch;
    private Button btnFinish;
    private EditText etUik;
    private EditText etAddress;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog infoDialog;
    private AlertDialog uikDialog;
    private AlertDialog downsDialog;

    String street = "";
    String house = "";

    private List<AddressItem> addressList = null;

    public UikSelectFragment() {
        super(R.layout.fragment_uik_select_auto);
    }

    public UikSelectFragment setStartElement(Integer startElementId) {
        this.startElementId = startElementId;
        return this;
    }

    @Override
    protected void onReady() {

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_uik_select_fragment);
        uikInfo = (TextView) findViewById(R.id.uik_info);
        addressInfo = (TextView) findViewById(R.id.address_info);
        btnApply = (Button) findViewById(R.id.btn_apply_uik);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        etUik = (EditText) findViewById(R.id.uik);
        etAddress = (EditText) findViewById(R.id.address);

        UserSettings userSettings = getCurrentUser().getConfigR().getUserSettings();

        MainFragment.disableSideMenu();

        cont.startAnimation(Anim.getAppear(getContext()));
        btnApply.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnSearch.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnFinish.startAnimation(Anim.getAppearSlide(getContext(), 500));

        btnApply.setOnClickListener(view -> {
            String uik = etUik.getText().toString();
            if (uik != null && !uik.isEmpty()) {
                setBtnsActive(false);
                checkUik(uik);
            } else {
                showToast("Введите корректный UIK");
            }
        });

        btnSearch.setOnClickListener(view -> {
            setBtnsActive(false);
            String address = etAddress.getText().toString().trim();
            if (checkAddress(address)) {
                searchAddress(address, 1);
            } else {
                setBtnsActive(true);
                showToast("Введите корректный адрес");
            }
        });

        btnFinish.setOnClickListener(view -> {
            setBtnsActive(false);
//            exitQuestionnaire();
            showDownsDialog();
        });

        initInfo();

        getDao().setCurrentQuestionnaireIsUseAbsentee(true);
        getMainActivity().getCurrentQuestionnaire().setIs_use_absentee(true);
        getMainActivity().hideProgressBar();
    }

    private void initInfo() {
        String uikInfoText = getMainActivity().getConfig().getAbsenteeChangeUikInfo();
        String addressInfoText = getMainActivity().getConfig().getAbsenteeChangeAddrInfo();
        if (uikInfoText != null && !uikInfoText.isEmpty()) UiUtils.setTextOrHide(uikInfo, uikInfoText);
        if (addressInfoText != null && !addressInfoText.isEmpty()) UiUtils.setTextOrHide(addressInfo, addressInfoText);
    }

    private void doNext(String uik) {
        Log.d("T-A-R.UikSelectFragment", "doNext: " + uik);
        String savedUik = getQuestionnaire().getRegistered_uik();
        if (savedUik != null && savedUik.equals(uik)) {
            getObjectBoxDao().clearElementPassedR();
            getObjectBoxDao().clearPrevElementsR();
        } else {
            getQuestionnaire().setRegistered_uik(uik);
            getDao().setCurrentQuestionnaireUik(uik);
        }
        ElementFragment fragment = new ElementFragment();
        fragment.setStartElement(startElementId, false);
        replaceFragment(fragment);
    }

    private boolean checkAddress(String address) {
        if (address != null && address.contains(",")) {
            char[] charArray = address.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (address.charAt(i) == ',') {
                    street = address.substring(0, i);
                    if ((i + 1) < charArray.length) {
                        if (address.charAt(i + 1) != ' ') house = address.substring(i + 1);
                        else if ((i + 2) < charArray.length) house = address.substring(i + 2);
                    }
                    break;
                }
            }
            return (!street.isEmpty() && !house.isEmpty());
        } else {
            return false;
        }
    }

    private void searchAddress(String address, int page) {

        String loginAdmin = getMainActivity().getConfig().getLoginAdmin();
        String adminKey = getDao().getKey();
        String token = new Gson().toJson(new QToken(loginAdmin));
        String url = getCurrentUser().getConfigR().getExitHost() + Constants.Default.ADDRESS_SEARCH_URL;
        Integer projectId = getCurrentUser().getConfigR().getProjectInfo().getProjectId();
        if (Internet.hasConnection(getMainActivity())) {
            getMainActivity().showProgressBar();
            showScreensaver(true);
            QuizerAPI.searchAddress(url, token, adminKey, projectId, address, page, (data) -> {
                getMainActivity().hideProgressBar();
                hideScreensaver();
                if (data != null) {
                    String responseJson;
                    try {
                        responseJson = data.string();
                        Log.d("T-A-R", "searchAddress: " + responseJson);
                    } catch (IOException e) {
                        setBtnsActive(true);
                        showNoInternetDialog();
                        e.printStackTrace();
                        return;
                    }

                    AddressDatabaseResponseModel responseModel;
                    try {
                        responseModel = new GsonBuilder().create().fromJson(responseJson, AddressDatabaseResponseModel.class);
                    } catch (JsonSyntaxException e) {
                        setBtnsActive(true);
                        showNoInternetDialog();
                        e.printStackTrace();
                        return;
                    }

                    if (responseModel != null && responseModel.getData() != null) {
                        addressList = new MapperQuizer().mapAddress(responseModel.getData());
                        if (!addressList.isEmpty()) showSelectDialog();
                        else {
                            addressList = new ArrayList<>();
                            showSelectDialog();
                        }
                    } else {
                        addressList = new ArrayList<>();
                        showSelectDialog();
                    }

                } else {
                    setBtnsActive(true);
                    showNoInternetDialog();
                    Log.d("T-A-R", "searchAddress: SERVER RESPONSE = NULL");
                }
            });
        } else {
            List<AddressR> localAddressBase1 = getDao().findAddress("%" + street + "%", "%" + house + "%", getMainActivity().getConfig().getProjectInfo().getProjectId());
            List<AddressR> localAddressBase2 = getDao().findAddress4("%" + street + "%", "%" + house + "%", getMainActivity().getConfig().getProjectInfo().getProjectId());

            List<AddressR> localAddressBase = new ArrayList<>(localAddressBase1);
            localAddressBase.addAll(localAddressBase2);

            if (localAddressBase.isEmpty()) {
                List<AddressR> localAddressBase3 = getDao().findAddress1("%" + street + "%", getMainActivity().getConfig().getProjectInfo().getProjectId());
                List<AddressR> localAddressBase4 = getDao().findAddress5("%" + street + "%", getMainActivity().getConfig().getProjectInfo().getProjectId());

                localAddressBase.addAll(localAddressBase3);
                localAddressBase.addAll(localAddressBase4);
            }

            boolean isAddressDatabase = getMainActivity().getSettings().getAddress_database() > 0;
            boolean isAddressChecked = getMainActivity().getSettings().isIs_address_enabled();

            if (!isAddressDatabase || !isAddressChecked) {
                setBtnsActive(true);
                showNoInternetDialog();
            } else if (localAddressBase != null && !localAddressBase.isEmpty()) {
                addressList = new MapperQuizer().mapAddressR(localAddressBase);
                showSelectDialog();
            } else {
                addressList = new ArrayList<>();
                showSelectDialog();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void showSelectDialog() {
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(R.layout.dialog_address_select, null);

        EditText mSearchText = layoutView.findViewById(R.id.etAddress);
        View mSearchBtn = layoutView.findViewById(R.id.view_search);
        View mNoAddressBtn = layoutView.findViewById(R.id.btn_no_address);
        RecyclerView listView = layoutView.findViewById(R.id.address_list);
        AddressAdapter addressAdapter = new AddressAdapter(addressList, this);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(addressAdapter);

        mSearchBtn.setOnClickListener(v -> {
            if (addressList != null && !addressList.isEmpty()) {
                List<AddressItem> filteredAddressList = new ArrayList<>();
                for (AddressItem item : addressList) {
                    if (item.getAddress().contains(mSearchText.getText().toString())) filteredAddressList.add(item);
                }
                addressAdapter.setAddressList(filteredAddressList);
            }
        });

        mNoAddressBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
            showUikDialog();
        });

        dialogBuilder.setView(layoutView, 10, 40, 10, 10);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MainActivity activity = getMainActivity();
        if (activity != null && !activity.isFinishing()) infoDialog.show();

    }

    @Override
    public void onUserClick(String uik) {
        try {
            infoDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkUik(uik);
        setBtnsActive(true);
    }

    private void checkUik(String uik) {
        Log.d("T-A-R.UikSelectFragment", "checkUik: " + uik);

        if (uik != null) {
            List<String> uiksList = new ArrayList<>();
            try {
                uiksList = getCurrentUser().getConfigR().getExitUiks();
                Log.d("T-A-R.UikSelectFragment", "checkUik: " + new Gson().toJson(uiksList));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (uiksList != null && !uiksList.isEmpty() && uiksList.contains(uik))
                doNext(uik);
            else showUikDialog();
        } else showUikDialog();
    }

    private void showUikDialog() {
        Log.d("T-A-R.UikSelectFragment", "showUikDialog: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_uik_info_auto : R.layout.dialog_uik_info_auto, null);

        Button noBtn = layoutView.findViewById(R.id.btn_wrong_name);
        Button yesBtn = layoutView.findViewById(R.id.btn_right_name);

        noBtn.setOnClickListener(v -> {
//            uikDialog.dismiss();
            showDownsDialog();
        });

        yesBtn.setOnClickListener(v -> {
            uikDialog.dismiss();
            setBtnsActive(true);
        });

        dialogBuilder.setView(layoutView);
        uikDialog = dialogBuilder.create();
        uikDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        uikDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            uikDialog.show();

    }

    private void showNoInternetDialog() {
        Log.d("T-A-R.UikSelectFragment", "showUikDialog: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_uik_no_internet_auto : R.layout.dialog_uik_no_internet_auto, null);

        Button okBtn = layoutView.findViewById(R.id.btn_ok);

        okBtn.setOnClickListener(v -> {
            uikDialog.dismiss();
            setBtnsActive(true);
        });

        dialogBuilder.setView(layoutView);
        uikDialog = dialogBuilder.create();
        uikDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        uikDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            uikDialog.show();

    }

    private void showDownsDialog() {
        Log.d("T-A-R.UikSelectFragment", "showDownsDialog: <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_for_downs_auto : R.layout.dialog_for_downs_auto, null);

        Button noBtn = layoutView.findViewById(R.id.btn_no);
        Button yesBtn = layoutView.findViewById(R.id.btn_yes);

        noBtn.setOnClickListener(v -> {
            try {
                downsDialog.dismiss();
                uikDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setBtnsActive(true);
        });

        yesBtn.setOnClickListener(v -> {
            try {
                downsDialog.dismiss();
                uikDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            exitQuestionnaire();
        });

        dialogBuilder.setView(layoutView);
        downsDialog = dialogBuilder.create();
        downsDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        downsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            downsDialog.show();

    }

    public void exitQuestionnaire() {
        Log.d("T-A-R.UikSelectFragment", "exitQuestionnaire: ");
        st("exitQuestionnaire() +++");
        stopAllRecording();
        try {
            getDao().setOption(Constants.OptionName.QUIZ_STARTED, "false");
            getDao().deleteOnlineQuota(getQuestionnaire().getToken());
            Log.d("T-A-R.", "CLEAR Questionnaire: 4");
            getDao().clearCurrentQuestionnaireR();
            getObjectBoxDao().clearElementPassedR();
            getObjectBoxDao().clearPrevElementsR();
            getMainActivity().setCurrentQuestionnaireNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
        st("exitQuestionnaire() ---");
        getMainActivity().restartHome();
    }

    private void stopAllRecording() {
        try {
            getMainActivity().stopRecording();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        exitQuestionnaire();
        return true;
    }

    private void setBtnsActive(boolean active) {
        UiUtils.setButtonEnabled(btnApply, active);
        UiUtils.setButtonEnabled(btnSearch, active);
        UiUtils.setButtonEnabledRed(btnFinish, active);
    }
}

