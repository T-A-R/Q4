package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.request.ActivationRequestModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;

public class ActivationActivity extends BaseActivity {

    private EditText mActivationEditText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        mActivationEditText = findViewById(R.id.activation_edit_text);
        final Button sendButton = findViewById(R.id.send_activation_button);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                showProgressBar();
                final String key = mActivationEditText.getText().toString();

                final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
                mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new ActivationRequestModel(key)));

                final Call.Factory client = new OkHttpClient();
                client.newCall(new DoRequest().post(mDictionaryForRequest, Constants.Default.ACTIVATION_URL))
                        .enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                                hideProgressBar();
                                showToastMessage(getString(R.string.internet_error_please_try_again));
                            }

                            @Override
                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                                hideProgressBar();

                                final ResponseBody responseBody = response.body();

                                if (responseBody == null) {
                                    showToastMessage(getString(R.string.incorrect_server_response));

                                    return;
                                }

                                final String responseJson = responseBody.string();
                                final ActivationResponseModel activationResponseModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);

                                if (activationResponseModel != null) {
                                    if (activationResponseModel.getResult() != 0) {
                                        saveActivationBundle(activationResponseModel.getServer(), activationResponseModel.getLoginAdmin());
                                        startAuthActivity();
                                    } else {
                                        showToastMessage(activationResponseModel.getError());
                                    }
                                } else {
                                    showToastMessage(getString(R.string.server_error));
                                }
                            }
                        });

            }
        });
    }
}