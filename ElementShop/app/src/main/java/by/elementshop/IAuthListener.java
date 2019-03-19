package by.elementshop;

import com.google.firebase.auth.FirebaseUser;

public interface IAuthListener {

    void onSuccess(final FirebaseUser user);

    void onError(final Exception exception);

}
