package pro.quizer.quizerexit.utils;

import android.util.Log;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    private static final String PNG = "png";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String FILE_ARRAY = "file[]";

    public static void sendPhotos(final String pServerUrl, final List<String> pPhotos) {
        final OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE;

        MultipartBody.Builder builderNew = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (String photo : pPhotos) {
            MEDIA_TYPE = photo.endsWith(PNG) ? MediaType.parse(IMAGE_PNG) : MediaType.parse(IMAGE_JPEG);
            RequestBody imageBody = RequestBody.create(MEDIA_TYPE, photo);
            builderNew.addFormDataPart(FILE_ARRAY, photo, imageBody);
        }

        MultipartBody requestBody = builderNew.build();

        final Request request = new Request.Builder()
                .url(pServerUrl)
                .post(requestBody)
                .build();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();

                    Log.e("Sending photos", response.body().string());
                } catch (Exception e) {
                    Log.e("Sending photos ERROR", e.getMessage());
                }
            }
        });

        thread.start();
    }
}