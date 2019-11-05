package pro.quizer.quizer3.utils;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.API.models.request.FileRequestModel;

public class OkHttpUtils {

    public static final String AUDIO_AMR = "audio/amr";
    public static final String IMAGE_JPEG = "image/jpeg";
    private static final String FILES = "files[%1$s]";

    public static Request postFiles(final List<File> files, final String url, final String pNameForm, final String pMediaType) {
        final MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Constants.ServerFields.JSON_DATA,
                        new Gson().toJson(new FileRequestModel(pNameForm)));

        for (int i = 0; i < files.size(); i++) {
            multipartBodyBuilder.addFormDataPart(String.format(FILES, i), files.get(i).getName(),
                    RequestBody.create(MediaType.parse(pMediaType), files.get(i)));
        }

        return new Request.Builder()
                .url(url)
                .post(multipartBodyBuilder.build()).build();
    }
}