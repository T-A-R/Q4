package by.elementshop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

import by.elementshop.utils.StringUtils;

public class AuthActivity extends BaseActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mSecondPasswordEditText;

    private Button mRegisterButton;
    private Button mSignInButton;

    private IAuthListener mAuthListener = new IAuthListener() {
        @Override
        public void onSuccess(FirebaseUser user) {
            startNextActivity();
        }

        @Override
        public void onError(Exception exception) {
            showErrorMessage(exception.getMessage());
        }
    };

    private View.OnClickListener mSignInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String email = mEmailEditText.getText().toString();
            final String password = mPasswordEditText.getText().toString();

            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
                showErrorMessage(getString(R.string.please_input_email_and_password));
            } else {
                signInUser(email, password, mAuthListener);
            }
        }
    };
    private View.OnClickListener mRegisterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String email = mEmailEditText.getText().toString();
            final String password = mPasswordEditText.getText().toString();
            final String secondPassword = mSecondPasswordEditText.getText().toString();

            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password) || StringUtils.isEmpty(secondPassword)) {
                showErrorMessage(getString(R.string.please_input_email_and_password));
            } else if (password.equals(secondPassword)) {
                registerUser(email, password, mAuthListener);
            } else {
                showErrorMessage(getString(R.string.registration_passwrod_not_equals));
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if (isLoggedIn()) {
            startNextActivity();
        }
    }

    private void startNextActivity() {
        startActivity(MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    public void initViews() {
        mEmailEditText = findViewById(R.id.email);
        mPasswordEditText = findViewById(R.id.password);
        mSecondPasswordEditText = findViewById(R.id.second_password);

        mRegisterButton = findViewById(R.id.register);
        mSignInButton = findViewById(R.id.sign_in);

        mSignInButton.setOnClickListener(mSignInClickListener);
        mRegisterButton.setOnClickListener(mRegisterClickListener);

        final TextView registerScreenButton = findViewById(R.id.register_screen_button);
        final TextView signInScreenButton = findViewById(R.id.sign_in_screen_button);

        registerScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerScreenButton.setVisibility(View.GONE);
                signInScreenButton.setVisibility(View.VISIBLE);

                mRegisterButton.setVisibility(View.VISIBLE);
                mSignInButton.setVisibility(View.GONE);

                mSecondPasswordEditText.setVisibility(View.VISIBLE);
            }
        });

        signInScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInScreenButton.setVisibility(View.GONE);
                registerScreenButton.setVisibility(View.VISIBLE);

                mSignInButton.setVisibility(View.VISIBLE);
                mRegisterButton.setVisibility(View.GONE);

                mSecondPasswordEditText.setVisibility(View.GONE);
            }
        });
    }
}