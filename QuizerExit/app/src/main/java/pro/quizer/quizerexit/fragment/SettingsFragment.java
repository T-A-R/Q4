package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SettingViewModelExecutable;
import pro.quizer.quizerexit.model.FontSizeModel;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.PhoneModel;
import pro.quizer.quizerexit.model.config.ReserveChannelModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.SettingsViewModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class SettingsFragment extends BaseFragment implements ICallback {

    public static final List<FontSizeModel> FONT_SIZE_MODELS = new ArrayList<FontSizeModel>() {
        {
            add(new FontSizeModel("Очень маленький", 0.6875F));
            add(new FontSizeModel("Маленький", 0.9375F));
            add(new FontSizeModel("Средний", 1.125F));
            add(new FontSizeModel("Большой", 1.5625F));
            add(new FontSizeModel("Очень большой", 1.6875F));
        }
    };

    private TextView mConfigDateView;
    private TextView mConfigIdView;
    private TextView mAnswerMarginView;
    private View mDeleteUser;
    private String mConfigDateString;
    private String mAnswerMarginString;
    private String mConfigIdString;
    private Spinner mFontSizeSpinner;
    private Spinner mSmsNumberSpinner;
    private AppCompatSeekBar mMarginSeekBar;
    private View mSmsSection;
    private int answerMargin;
    private BaseActivity mBaseActivity;

    public static Fragment newInstance() {
        final SettingsFragment fragment = new SettingsFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initStrings();

        updateData(new SettingViewModelExecutable(getContext()).execute());
    }

    private void updateAnswerMarginString(final int pValue) {
        UiUtils.setTextOrHide(mAnswerMarginView, String.format(mAnswerMarginString, pValue));
    }

    private void initViews(final View pView) {
        mBaseActivity = (BaseActivity) getContext();
        mFontSizeSpinner = pView.findViewById(R.id.font_size_spinner);
        mSmsNumberSpinner = pView.findViewById(R.id.sms_number_spinner);
        mAnswerMarginView = pView.findViewById(R.id.settings_space_between_answers);
        mMarginSeekBar = pView.findViewById(R.id.margin_seekbar);
        mSmsSection = pView.findViewById(R.id.sms_section);

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

        ArrayAdapter<String> fontSizeAdapter = new ArrayAdapter<>(getContext(), R.layout.adapter_spinner, fontString);
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

        final UserModel currentUser = mBaseActivity.getCurrentUser();
        final ConfigModel configModel = currentUser.getConfig();
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

            ArrayAdapter<String> smsNumberAdapter = new ArrayAdapter<>(getContext(), R.layout.adapter_spinner, smsNumbers);
            smsNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSmsNumberSpinner.setAdapter(smsNumberAdapter);
            mSmsNumberSpinner.setSelection(number);
            mSmsNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    reserveChannelModel.selectPhone(position);

                    mBaseActivity.updateConfig(currentUser, configModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        } else {
            hideSmsSection();
        }

        mConfigDateView = pView.findViewById(R.id.settings_date);
        mConfigIdView = pView.findViewById(R.id.settings_id);
        mDeleteUser = pView.findViewById(R.id.delete_user);
        mDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaseActivity.showRemoveUserDialog();
            }
        });
    }

    private void refreshFragment() {
        // TODO: 2/5/2019 notify drawer
//        mBaseActivity.getDrawerLayout().notifyAll();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void initStrings() {
        mAnswerMarginString = getString(R.string.VIEW_SETTINGS_SPACING);
        mConfigDateString = getString(R.string.VIEW_DATE);
        mConfigIdString = getString(R.string.VIEW_ID);
    }

    private void showSmsSection() {
        mSmsSection.setVisibility(View.VISIBLE);
    }

    private void hideSmsSection() {
        mSmsSection.setVisibility(View.GONE);
    }

    private void updateData(final SettingsViewModel pSettingsViewModel) {
        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mConfigDateView, String.format(mConfigDateString, pSettingsViewModel.getConfigDate()));
                UiUtils.setTextOrHide(mConfigIdView, String.format(mConfigIdString, pSettingsViewModel.getConfigId()));
                updateAnswerMarginString(pSettingsViewModel.getAnswerMargin());
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.NOTIFICATION_UPDATING));
        }

//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();

        if (isAdded()) {
            updateData(new SettingViewModelExecutable(getContext()).execute());
        }
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();

        if (isAdded()) {
            updateData(new SettingViewModelExecutable(getContext()).execute());
        }
    }
}