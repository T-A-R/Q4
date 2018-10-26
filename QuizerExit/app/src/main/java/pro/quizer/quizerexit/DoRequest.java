package pro.quizer.quizerexit;

import java.util.Dictionary;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import pro.quizer.quizerexit.Constants;

public class DoRequest {

    public Request post(final Dictionary<String, String> dictionary, final String url) {
        final RequestBody formBody = new FormBody.Builder()
                .add(Constants.ServerFields.JSON_DATA, dictionary.get(Constants.ServerFields.JSON_DATA))
                .build();

        return new Request.Builder()
                .url(url)
                .post(formBody).build();
    }
}