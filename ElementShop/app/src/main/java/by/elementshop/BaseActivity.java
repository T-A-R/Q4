package by.elementshop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    public void signOutUser() {
        mAuth.signOut();
    }

    public boolean isLoggedIn() {
        return getUser() != null;
    }

    public FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void showErrorMessage(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public abstract int getLayoutId();

    public abstract void initViews();

    public void signInUser(final String email, final String password, final IAuthListener authListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            authListener.onSuccess(mAuth.getCurrentUser());
                        } else {
                            authListener.onError(task.getException());
                        }
                    }
                });
    }

    public void registerUser(final String email, final String password, final IAuthListener authListener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            authListener.onSuccess(mAuth.getCurrentUser());
                        } else {
                            authListener.onError(task.getException());
                        }
                    }
                });

    }
}