package pro.quizer.quizer3.view.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;

public class Reg4Fragment extends ScreenFragment implements View.OnClickListener {

    public static final int TIMER_VALUE = 299;

    private Button btnFinish;
    private Button btnResend;
    private TextView counterText;
    private EditText codeEditText;

    private boolean canResend = false;
    private boolean inExitDialog = false;

    public Reg4Fragment() {
        super(R.layout.fragment_reg4);
    }

    @Override
    protected void onReady() {
        RelativeLayout cont = findViewById(R.id.cont_reg4_fragment);
        btnFinish = findViewById(R.id.btn_next);
        btnResend = findViewById(R.id.btn_resend_sms);
        counterText = findViewById(R.id.sms_timer);
        codeEditText = findViewById(R.id.code);

        UiUtils.setButtonEnabled(btnFinish, false);

        MainFragment.disableSideMenu();

        btnFinish.setOnClickListener(this);
        btnResend.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnFinish.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnResend.startAnimation(Anim.getAppearSlide(getContext(), 500));

        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int cursorPosition, int before, int count) {
                if (cs.length() > 0) {
                    UiUtils.setButtonEnabled(btnFinish, true);
                } else {
                    UiUtils.setButtonEnabled(btnFinish, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        startResendTimer();
    }

    @Override
    public void onClick(View view) {
        if (view == btnFinish) {
            finishReg();
        } else if (view == btnResend) {
            if (canResend) {
                resendSms();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
//        showExitRegDialog();
        return true;
    }

    private void showExitRegDialog() {
        if (!inExitDialog) {
            inExitDialog = true;
            MainActivity activity = getMainActivity();
            if (activity != null && !activity.isFinishing()) {
                new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.exit_reg_header)
                        .setMessage(R.string.exit_reg_warning)
                        .setPositiveButton(R.string.view_yes, (dialog, which) -> replaceFragment(new HomeFragment()))
                        .setNegativeButton(R.string.view_no, (dialog, which) -> inExitDialog = false).show();
            }
        }
    }

    private void startResendTimer() {
        canResend = false;
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x -> {
                    try {
                        UiUtils.setTextOrHide(counterText, (String.format(getString(R.string.resend_sms_timer), String.valueOf(TIMER_VALUE - x))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .takeUntil(aLong -> aLong == TIMER_VALUE)
                .doOnComplete(() ->
                        {
                            try {
                                UiUtils.setButtonEnabled(btnResend, true);
                                counterText.setVisibility(View.INVISIBLE);
                                canResend = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                ).subscribe();
    }

    private void finishReg() {

    }

    private void resendSms() {
        UiUtils.setButtonEnabled(btnResend, false);
        UiUtils.setTextOrHide(counterText, (String.format(getString(R.string.resend_sms_timer), String.valueOf(TIMER_VALUE + 1))));
        counterText.setVisibility(View.VISIBLE);
        startResendTimer();
    }
}

