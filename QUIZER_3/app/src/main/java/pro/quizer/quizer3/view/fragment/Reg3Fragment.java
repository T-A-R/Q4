package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.RegistrationRequestModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.GPSModel;
import pro.quizer.quizer3.utils.GpsUtils;
import pro.quizer.quizer3.utils.Internet;
import pro.quizer.quizer3.utils.SmsUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.PhoneFormatter;

import static pro.quizer.quizer3.MainActivity.TAG;

public class Reg3Fragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.SendRegCallback, ICallback {
    private Button btnNext;
    private EditText uik;
    private EditText inputPhone;
    private TextView phoneLabel;
    private TextView detectedPhoneLabel;
    private ImageView clearPhone;
    private LinearLayout detectedPhoneCont;

    private GPSModel gps;
    private RegistrationR registration;
    private PhoneFormatter phoneFormatter = new PhoneFormatter();
    private String mPhoneNumber = "";
    private boolean isUikValid = false;
    private List<String> uikList;
    private Long mTimeToken;

    public Reg3Fragment() {
        super(R.layout.fragment_reg3_auto);
    }

    @Override
    protected void onReady() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTimeToken = bundle.getLong("time");
//            showToast(DateUtils.getFormattedDate(DateUtils.PATTERN_FULL_SMS, mTimeToken * 1000));
        } else showTimeErrorDialog();

        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_reg3_fragment);
        btnNext = (Button) findViewById(R.id.btn_next);
        uik = (EditText) findViewById(R.id.uik);
        inputPhone = (EditText) findViewById(R.id.phone);
        phoneLabel = (TextView) findViewById(R.id.phone_info);
        detectedPhoneLabel = (TextView) findViewById(R.id.detected_phone_info);
        detectedPhoneCont = (LinearLayout) findViewById(R.id.detected_phone_cont);
        clearPhone = (ImageView) findViewById(R.id.btn_clear_phone);

        uikList = getCurrentUser().getConfigR().getAllowedUiks();
        //TODO FOR TEST !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (uikList == null) {
            uikList = new ArrayList<>();
            uikList.add("123");
            uikList.add("321");
        }

        MainFragment.disableSideMenu();
        if (!checkPhoneNumber()) {
            showEditPhoneView();
        } else {
            showDetectedPhoneView();
        }

        btnNext.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));

        uik.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (uikList != null) {
                    if (!uikList.contains(s.toString())) {
                        uik.setError("Неверный UIK");
                        isUikValid = false;
                    } else {
                        isUikValid = true;
                    }
                } else {
                    isUikValid = false;
                    uik.setError("Нет списка UIK");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            String mUik = uik.getText().toString();
            String mPhone;
            if (mPhoneNumber == null || mPhoneNumber.equals("") || mPhoneNumber.equals("7")) {
//                Log.d("T-L.Reg3Fragment", "============ onClick: " + inputPhone + " / " + inputPhone.getText().toString());
                mPhone = "7" + phoneFormatter.cleaned(inputPhone.getText().toString());
                mPhoneNumber = mPhone;
            } else mPhone = mPhoneNumber;

            if (mPhone.length() == 11) {
                if (isUikValid) {
                    getGps();
                    if (gps == null) {
                        showNoGpsDialog();
                    } else if (gps.isFakeGPS()) {
                        showFakeGpsDialog();
                    } else {
//                        showToast(mUik + " / " + mPhone);
                        UiUtils.setButtonEnabled(btnNext, false);
                        if (addRegistrationToDB(mUik, mPhone)) {
                            Log.d("T-L.Reg3Fragment", "====== SAVE TO DB OK ");
//                            if (getCurrentUser().getConfigR().getExitHost() != null) {
                            String url; url = getCurrentUser().getConfigR().getExitHost() != null ? getCurrentUser().getConfigR().getExitHost() + Constants.Default.REG_URL : null;

                            List<File> photos = getMainActivity().getRegPhotosByUserId(registration.getUser_id());

                                if (photos == null || photos.isEmpty()) {
                                    showToast(getString(R.string.no_reg_photo));
                                    return;
                                }

                                try {
                                    if (Internet.isConnected() && url != null) {
                                        QuizerAPI.sendReg(url, photos, new RegistrationRequestModel(
                                                getDao().getKey(),
                                                registration.getUser_id(),
                                                registration.getUik_number(),
                                                registration.getPhone(),
                                                registration.getGps(),
                                                registration.getGps_network(),
                                                registration.getGps_time(),
                                                registration.getGps_time_network(),
                                                registration.getReg_time()
                                        ), registration.getId(), "jpeg", this);
                                    } else {
                                        showToast("Нет доступа в интернет");
                                        showNoInternetDialog();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    showToast("Нет доступа в интернет");
                                    showNoInternetDialog();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showToast("Нет доступа в интернет");
                                    showNoInternetDialog();
                                }
//                            }
//                            else {
//                                UiUtils.setButtonEnabled(btnNext, true);
//                            }
                        } else {
                            Log.d("T-L.Reg3Fragment", "====== SAVE TO DB FAIL ");
                            UiUtils.setButtonEnabled(btnNext, true);
                        }
                    }
                } else {
                    Toast.makeText(getMainActivity(), "Неверный Uik", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getMainActivity(), "Неверный номер телефона: " + mPhone, Toast.LENGTH_SHORT).show();
                inputPhone.setError("Неверный номер телефона");
            }
        } else if (view == clearPhone) {
            mPhoneNumber = "";
            showEditPhoneView();
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new Reg2Fragment());
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showEditPhoneView() {

        detectedPhoneCont.setVisibility(View.GONE);
        inputPhone.setVisibility(View.VISIBLE);
        phoneLabel.setText(R.string.enter_phone);

        inputPhone.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                inputPhone.setText("+7(");
            } else {
                hideKeyboard();
            }
        });

        inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                phoneFormatter.beforeTextChanged(start, count);
            }

            @Override
            public void onTextChanged(CharSequence cs, int cursorPosition, int before, int count) {
                if (!cs.toString().equals(phoneFormatter.getPhone())) {
                    phoneFormatter.onTextChanged(cs.toString(), cursorPosition, before, count);
                    inputPhone.setText(phoneFormatter.getPhone());
                    inputPhone.setSelection(phoneFormatter.getSelection());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputPhone.setOnTouchListener((v, event) -> {
            if (inputPhone.getText().length() > 2) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    inputPhone.post(new Runnable() {
                        public void run() {
                            Editable sb = inputPhone.getText();
                            int currentPos = inputPhone.getSelectionStart();

                            inputPhone.setSelection(inputPhone.getText().length());
                        }
                    });
                }
                return false;
            }
            return false;
        });
    }

    private void showDetectedPhoneView() {
        detectedPhoneCont.setVisibility(View.VISIBLE);
        String text = "Ваш номер:";
        String detected = "+" + mPhoneNumber;
        phoneLabel.setText(text);
        detectedPhoneLabel.setText(detected);
        inputPhone.setVisibility(View.GONE);
        clearPhone.setOnClickListener(this);
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getMyPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getMainActivity().getSystemService(Context.TELEPHONY_SERVICE);

        return mTelephonyMgr.getLine1Number();
    }

    private boolean checkPhoneNumber() {
        mPhoneNumber = getMyPhoneNumber();
        if (mPhoneNumber != null && mPhoneNumber.length() > 1)
            mPhoneNumber = mPhoneNumber.substring(1);

        return mPhoneNumber != null && mPhoneNumber.length() == 11;
        //TODO FOR TEST !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        mPhoneNumber = null;
//        return false;
    }

    private boolean checkUik(String text) {
        if (text != null && text.length() > 0) {

            if (uikList == null || uikList.size() == 0) return false;
            if (uikList.contains(text)) return true;
            else {
                showToast(getString(R.string.wrong_uik));
                return false;
            }
        } else {
            showToast(getString(R.string.enter_uik_message));
            return false;
        }
    }

    private boolean addRegistrationToDB(String uik, String phone) {
        try {
            UserModelR user = getCurrentUser();
            registration = null;
            registration = new RegistrationR(
                    user.getUser_id(),
                    uik,
                    phone,
                    gps.getGPS(),
                    gps.getGPSNetwork(),
                    gps.getTime(),
                    gps.getTimeNetwork(),
                    mTimeToken
            );
            getDao().insertRegistrationR(registration);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void getGps() {
        gps = null;
        try {
            gps = GpsUtils.getCurrentGps(getActivity(), false);
        } catch (final Exception e) {
            e.printStackTrace();
            Log.d(TAG, "startGps: " + e.getMessage());
        }
    }

    private void showFakeGpsDialog() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_fake_gps_title)
                    .setMessage(R.string.dialog_fake_gps_body)
                    .setPositiveButton(R.string.dialog_apply, (dialog, which) -> {
                        dialog.dismiss();
                        UiUtils.setButtonEnabled(btnNext, true);
                    })
                    .show();
        }
    }

    private void showNoGpsDialog() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_no_gps)
                    .setMessage("Попробуйте снова.")
                    .setPositiveButton(R.string.dialog_apply, (dialog, which) -> {
                        dialog.dismiss();
                        UiUtils.setButtonEnabled(btnNext, true);
                    })
                    .show();
        }
    }

    private void showNoInternetDialog() {
        MainActivity activity = getMainActivity();
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing()) {
                new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.no_internet)
                        .setMessage(R.string.continue_reg_by_sms)
                        .setPositiveButton(R.string.button_continue, (dialog, which) -> {
                            dialog.dismiss();
                            sendRegSms();
                        })
                        .setNegativeButton(R.string.back, (dialog, which) -> {
                            dialog.dismiss();
                            UiUtils.setButtonEnabled(btnNext, true);
                        })
                        .show();
            }
        });
    }

    private void showTimeErrorDialog() {
        MainActivity activity = getMainActivity();
        activity.runOnUiThread(() -> {
            if (!activity.isFinishing()) {
                new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.reg_error)
                        .setMessage(R.string.reg_error_message)
                        .setPositiveButton(R.string.button_continue, (dialog, which) -> {
                            dialog.dismiss();
                            replaceFragment(new HomeFragment());
                        })
                        .show();
            }
        });
    }

    private void sendRegSms() {

        // r {admin_key}:[{user_id} {uik_number} {gps} {gps_network} {reg_time} {phone}] - в квадратных скобках шифрованное

        int number1 = registration.getUser_id(); // user_id
        String number2 = registration.getUik_number(); // uik
        String decodedMessage = "r " + getDao().getKey() + ":";
        String message = number1
                + " " + number2
                + " " + registration.getGps()
                + " " + registration.getGps_network()
                + " " + registration.getReg_time()
                + " " + registration.getPhone();

        SmsUtils.sendRegSms(getMainActivity(), this, decodedMessage + encode(message));
    }

    private String encode(String message) {
        //TODO Зашифровать message.
        return message;
    }

    @Override
    public void onSendRegCallback(ResponseBody response, Integer id) {
        if (response == null) {
            showToast("Нет ответа от сервера");
            showNoInternetDialog();
            return;
        }

        try {
            if (id != null)
                getDao().clearRegistrationRById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStarting() {
    }

    @Override
    public void onSuccess() {
        replaceFragment(new Reg4Fragment());
    }

    @Override
    public void onError(Exception pException) {
        UiUtils.setButtonEnabled(btnNext, true);
        replaceFragment(new Reg4Fragment()); //TODO FOR TEST
    }
}

