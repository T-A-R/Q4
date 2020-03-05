package pro.quizer.quizer3.view.fragment;

import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
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
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SettingsFragment extends ScreenFragment implements View.OnClickListener, ICallback {

    public static final List<FontSizeModel> FONT_SIZE_MODELS = new ArrayList<FontSizeModel>() {
        {
            add(new FontSizeModel("Очень маленький", 0.6875F));
            add(new FontSizeModel("Маленький", 0.9375F));
            add(new FontSizeModel("Средний", 1.125F));
            add(new FontSizeModel("Большой", 1.5625F));
            add(new FontSizeModel("Очень большой", 1.6875F));
        }
    };

    private Toolbar mToolbar;
    private TextView mConfigDateView;
    private View mUpdateConfig;
    private TextView mConfigIdView;
    private TextView mAnswerMarginView;
    private TextView mSpinnerTitle;
    private TextView mClosedQuotaTitle;
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

    private MainActivity mBaseActivity;
    private int answerMargin;

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    protected void onReady() {

        initViews();
        initStrings();
        MainFragment.enableSideMenu(true);
        updateData(new SettingViewModelExecutable(getContext()).execute());
    }

    @Override
    public void onClick(View view) {
        if (view == mDeleteUser) {
            showRemoveUserDialog();
        } else if (view == mUpdateConfig) {
            reloadConfig();
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
        mDeleteUser = findViewById(R.id.delete_user);
        mUpdateConfig = findViewById(R.id.update_config);
        mAutoZoomSwitch = findViewById(R.id.auto_zoom_switch);
        mSpeedSwitch = findViewById(R.id.speed_switch);
        mClosedQuotaTitle = findViewById(R.id.closed_quota_title);


//        LinearLayout textSettingsCont = findViewById(R.id.text_settings_cont);
//        textSettingsCont.setVisibility(View.GONE);

        mDeleteUser.setOnClickListener(this);
        mUpdateConfig.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        mDeleteUser.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mUpdateConfig.startAnimation(Anim.getAppearSlide(getContext(), 500));

        mToolbar.setTitle(getString(R.string.settings_screen));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new HomeFragment());
            }
        });

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

        mSpeedSwitch.setChecked(mBaseActivity.isSpeedMode());
        setClosedQuotaTitle();
        mSpeedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mBaseActivity.setSpeedMode(b);
//                replaceFragment(new SettingsFragment());
//                mSpeedSwitch.setChecked(mBaseActivity.isAutoZoom());
                setClosedQuotaTitle();
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
        final ConfigModel configModel = currentUser.getConfigR();
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
        // TODO: 2/5/2019 notify drawer
//        mBaseActivity.getDrawerLayout().notifyAll();

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
                    .setPositiveButton(R.string.view_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            new RemoveUserExecutable(mBaseActivity, new ICallback() {
                                @Override
                                public void onStarting() {

                                }

                                @Override
                                public void onSuccess() {
                                    replaceFragment(new AuthFragment());
                                }

                                @Override
                                public void onError(Exception pException) {
                                    showErrorRemoveUserDialog();
                                }
                            }).execute();
                        }
                    })
                    .setNegativeButton(R.string.view_no, null).show();
        }
    }

    public void showErrorRemoveUserDialog() {
        if (mBaseActivity != null && !mBaseActivity.isFinishing()) {
            new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_error_remove_user_title)
                    .setMessage(R.string.dialog_error_remove_user_body)
                    .setPositiveButton(R.string.dialog_go_to, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            replaceFragment(new SyncFragment());
                        }
                    })
                    .setNegativeButton(R.string.view_cancel, null).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
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

    private void setClosedQuotaTitle() {
        if(mBaseActivity.isSpeedMode()) {
            mClosedQuotaTitle.setText(getString(R.string.view_settings_closed_quota_off));
        } else {
            mClosedQuotaTitle.setText(getString(R.string.view_settings_closed_quota_on));
        }
    }
}