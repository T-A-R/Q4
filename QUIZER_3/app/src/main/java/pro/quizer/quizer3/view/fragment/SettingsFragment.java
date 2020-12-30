package pro.quizer.quizer3.view.fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.RemoveUserExecutable;
import pro.quizer.quizer3.executable.SettingViewModelExecutable;
import pro.quizer.quizer3.model.FontSizeModel;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.PhoneModel;
import pro.quizer.quizer3.model.config.ReserveChannelModel;
import pro.quizer.quizer3.model.view.SettingsViewModel;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;
import pro.quizer.quizer3.view.fragment.ScreenFragment;

import static pro.quizer.quizer3.utils.Fonts.FONT_SIZE_MODELS;

public class SettingsFragment extends ScreenFragment implements View.OnClickListener, ICallback {

    private Toolbar mToolbar;
    private TextView mConfigDateView;
    private View mUpdateConfig;
    private TextView mConfigIdView;
    private TextView mAnswerMarginView;
    private TextView mSpinnerTitle;
    private View mUpdateUserName;
    private View mDeleteUser;
    private String mConfigDateString;
    private String mAnswerMarginString;
    private String mConfigIdString;
    private Spinner mFontSizeSpinner;
    private Spinner mSmsNumberSpinner;
    private AppCompatSeekBar mMarginSeekBar;
    private View mSmsSection;
    private FrameLayout mSpinnerFrame;
    private Switch mAutoZoomSwitch;
    private Switch mSpeedSwitch;
    private Switch mMemorySwitch;
    private Switch mDarkModeSwitch;
    private boolean isCanBackPress = true;
    private Long dateLong = null;

    private MainActivity mBaseActivity;
    private int answerMargin;
    private AlertDialog infoDialog;

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    protected void onReady() {

        initViews();
        initStrings();
        MainFragment.enableSideMenu(true, getMainActivity().isExit());
        setEventsListener(id -> {
            switch (id) {
                case 1:
                    showScreensaver("Идет обновление конфига", true);
                    isCanBackPress = false;
                    break;
                case 2:
                    hideScreensaver();
                    isCanBackPress = true;
                    showToast(getString(R.string.config_updated));
                    break;
            }
        });
        updateData(new SettingViewModelExecutable(getContext()).execute());
    }

    @Override
    public void onClick(View view) {
        if (view == mDeleteUser) {
            showRemoveUserDialog();
        } else if (view == mUpdateConfig) {
            reloadConfig();
        } else if (view == mUpdateUserName) {
            showInputNameDialog();
        }
    }

    private void updateAnswerMarginString(final int pValue) {
//        UiUtils.setTextOrHide(mAnswerMarginView, String.format(mAnswerMarginString, pValue));
    }

    private void initViews() {

        mBaseActivity = (MainActivity) getActivity();

        RelativeLayout cont = findViewById(R.id.cont_settings_fragment);
        mToolbar = findViewById(R.id.toolbar);
        mFontSizeSpinner = findViewById(R.id.font_size_spinner);
        mSmsNumberSpinner = findViewById(R.id.sms_number_spinner);
        mAnswerMarginView = findViewById(R.id.settings_space_between_answers);
        mMarginSeekBar = findViewById(R.id.margin_seekbar);
        mSmsSection = findViewById(R.id.sms_section);
        mSpinnerFrame = findViewById(R.id.spinner_frame);
        mConfigDateView = findViewById(R.id.settings_date);
        mConfigIdView = findViewById(R.id.settings_id);
        mSpinnerTitle = findViewById(R.id.spinner_title);
        mUpdateUserName = findViewById(R.id.update_username);
        mDeleteUser = findViewById(R.id.delete_user);
        mUpdateConfig = findViewById(R.id.update_config);
        mAutoZoomSwitch = findViewById(R.id.auto_zoom_switch);
        mSpeedSwitch = findViewById(R.id.speed_switch);
        mMemorySwitch = findViewById(R.id.memory_switch);
        mDarkModeSwitch = findViewById(R.id.dark_switch);

        mUpdateUserName.setOnClickListener(this);
        mDeleteUser.setOnClickListener(this);
        mUpdateConfig.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        mUpdateUserName.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mDeleteUser.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mUpdateConfig.startAnimation(Anim.getAppearSlide(getContext(), 500));

        mToolbar.setTitle(getString(R.string.settings_screen));
        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));

        answerMargin = mBaseActivity.getAnswerMargin();
        mMarginSeekBar.setProgress(answerMargin);
        mMarginSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar pSeekBar, int pProgress, boolean pBoolean) {
                if (answerMargin != pProgress) {
                    answerMargin = pProgress;

                    mBaseActivity.setAnswerMargin(answerMargin);
                    updateAnswerMarginString(answerMargin);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final List<String> fontString = new ArrayList<>();
        final int selectedPosition = mBaseActivity.getFontSizePosition();

        for (final FontSizeModel fontSizeModel : FONT_SIZE_MODELS) {
            fontString.add(fontSizeModel.getName());
        }

        mSpeedSwitch.setChecked(mBaseActivity.isTableSpeedMode());
        mSpeedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mBaseActivity.setTableSpeedMode(b);
            }
        });

        mMemorySwitch.setChecked(mBaseActivity.isMemoryCheckMode());
        mMemorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mBaseActivity.setMemoryCheckMode(b);
            }
        });

        mDarkModeSwitch.setChecked(mBaseActivity.isDarkkMode());
        mDarkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mBaseActivity.setDarkMode(b);
            }
        });

        mAutoZoomSwitch.setChecked(mBaseActivity.isAutoZoom());
        mAutoZoomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mBaseActivity.setAutoZoom(b);
                replaceFragment(new SettingsFragment());
            }
        });

        if (!mBaseActivity.isAutoZoom()) {

            mSpinnerTitle.setVisibility(View.VISIBLE);
            mSpinnerFrame.setVisibility(View.VISIBLE);

            ArrayAdapter<String> fontSizeAdapter = new ArrayAdapter<>(getContext(), mBaseActivity.isAutoZoom() ? R.layout.adapter_spinner_auto : R.layout.adapter_spinner, fontString);
            fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mFontSizeSpinner.setAdapter(fontSizeAdapter);
            mFontSizeSpinner.setSelection(selectedPosition);
            mFontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mBaseActivity.setFontSizePosition(position);
                    if (position != selectedPosition) {
                        refreshFragment();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        } else {
            mSpinnerTitle.setVisibility(View.GONE);
            mSpinnerFrame.setVisibility(View.GONE);
        }

        final UserModelR currentUser = mBaseActivity.getCurrentUser();
        final ConfigModel configModel = mBaseActivity.getConfig();
        final ReserveChannelModel reserveChannelModel = configModel.getProjectInfo().getReserveChannel();

        if (reserveChannelModel != null) {
            showSmsSection();

            final List<String> smsNumbers = new ArrayList<>();
            final List<PhoneModel> phoneModels = reserveChannelModel.getPhones();

            int number = 0;

            for (int i = 0; i < phoneModels.size(); i++) {
                final PhoneModel phoneModel = phoneModels.get(i);

                if (phoneModel.isSelected()) {
                    number = i;
                }

                smsNumbers.add(i, phoneModel.getNumber());
            }

            ArrayAdapter<String> smsNumberAdapter = new ArrayAdapter<>(getContext(), mBaseActivity.isAutoZoom() ? R.layout.adapter_spinner_auto : R.layout.adapter_spinner, smsNumbers);
//            smsNumberAdapter.setDropDownViewResource(R.layout.auto_spinner);
            smsNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSmsNumberSpinner.setAdapter(smsNumberAdapter);
            mSmsNumberSpinner.setSelection(number);
            mSmsNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    reserveChannelModel.selectPhone(position);
                    updateConfig(currentUser, configModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        } else {
            hideSmsSection();
        }


    }

    public void refreshFragment() {
        if (!mBaseActivity.isFinishing()) {
            mFontSizeSpinner.setSelection(mBaseActivity.getFontSizePosition());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
    }

    private void initStrings() {
        mAnswerMarginString = getString(R.string.view_settings_spacing);
        mConfigDateString = getString(R.string.view_date);
        mConfigIdString = getString(R.string.view_id);
    }

    private void showSmsSection() {
        mSmsSection.setVisibility(View.VISIBLE);
    }

    private void hideSmsSection() {
        mSmsSection.setVisibility(View.GONE);
    }

    private void updateData(final SettingsViewModel pSettingsViewModel) {
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mConfigDateView, String.format(mConfigDateString, pSettingsViewModel.getConfigDate()));
                UiUtils.setTextOrHide(mConfigIdView, String.format(mConfigIdString, pSettingsViewModel.getConfigId()));
                updateAnswerMarginString(pSettingsViewModel.getAnswerMargin());
            }
        });
    }

    public void showRemoveUserDialog() {
        if (mBaseActivity != null && !mBaseActivity.isFinishing()) {
            new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_remove_user_title)
                    .setMessage(R.string.dialog_remove_user_body)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> new RemoveUserExecutable(mBaseActivity, new ICallback() {
                        @Override
                        public void onStarting() {

                        }

                        @Override
                        public void onSuccess() {
                            mBaseActivity.restartActivity();
                        }

                        @Override
                        public void onError(Exception pException) {
                            showErrorRemoveUserDialog();
                        }
                    }).execute())
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    public void showErrorRemoveUserDialog() {
        if (mBaseActivity != null && !mBaseActivity.isFinishing()) {
            new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_error_remove_user_title)
                    .setMessage(R.string.dialog_error_remove_user_body)
                    .setPositiveButton(R.string.dialog_go_to, (dialog, which) -> replaceFragment(new SyncFragment()))
                    .setNegativeButton(R.string.view_cancel, null).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isCanBackPress) {
            replaceFragment(new HomeFragment());
        }
        return true;
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.notification_updating));
        }
    }

    @Override
    public void onSuccess() {

        if (isAdded()) {
            updateData(new SettingViewModelExecutable(getContext()).execute());
        }
    }

    @Override
    public void onError(final Exception pException) {

        if (isAdded()) {
            updateData(new SettingViewModelExecutable(getContext()).execute());
        }
    }

    private void showInputNameDialog() {
//        getDao().setUserName(null);
//        getDao().setUserBirthDate(null);
        dateLong = null;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getMainActivity());
        dialogBuilder.setCancelable(false);
        View layoutView = getLayoutInflater().inflate(getMainActivity().isAutoZoom() ? R.layout.dialog_update_name_auto : R.layout.dialog_update_name, null);
        EditText name = layoutView.findViewById(R.id.input_name);
        EditText date = layoutView.findViewById(R.id.input_birthdate);
        Button sendBtn = layoutView.findViewById(R.id.btn_send_name);
        Button cancelBtn = layoutView.findViewById(R.id.btn_cancel);
        date.setOnClickListener(v -> setDate((EditText) v));

        sendBtn.setOnClickListener(v -> {
            String nameString = name.getText().toString();

            if (StringUtils.isEmpty(nameString)) {
                nameString = " ";
            }
            getDao().setUserName(nameString);
            getDao().setUserBirthDate(dateLong);
            infoDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> {
            infoDialog.dismiss();
        });

        dialogBuilder.setView(layoutView);
        infoDialog = dialogBuilder.create();
        infoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideAnimation;
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (getMainActivity() != null && !getMainActivity().isFinishing())
            infoDialog.show();
    }

    private final Calendar mCalendar = Calendar.getInstance();

    public void setDate(final TextView pEditText) {
        if (!getMainActivity().isFinishing()) {
            new DatePickerDialog(getMainActivity(), (view, year, monthOfYear, dayOfMonth) -> {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setInitialDateTime(pEditText);
            },
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime(final TextView mEditText) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        dateFormat.setTimeZone(mCalendar.getTimeZone());
        mEditText.setText(dateFormat.format(mCalendar.getTime()));
//        getDao().setUserBirthDate(mCalendar.getTimeInMillis() / 1000);
        dateLong = (mCalendar.getTimeInMillis() / 1000);
    }
}