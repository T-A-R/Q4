package pro.quizer.quizerexit.utils;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.model.request.PhotoRequestModel;

public class OkHttpUtils {

    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String FILES = "files[%1$s]";

    public static Request postPhoto(final List<File> photo, final String url) {
        final MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Constants.ServerFields.JSON_DATA,
                        new Gson().toJson(new PhotoRequestModel()));

        for (int i = 0; i < photo.size(); i++) {
            multipartBodyBuilder.addFormDataPart(String.format(FILES, i), photo.get(i).getName(),
                    RequestBody.create(MediaType.parse(IMAGE_JPEG), photo.get(i)));
        }

        return new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build()).build();
    }
}