package pro.quizer.quizerexit.API;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface RetrofitQuizerAPI {

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> sendKey(@Url String apiname, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> authUser(@Url String apiname, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> getConfig(@Url String apiname, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> sendQuestionnaires(@Url String apiname, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> getQuotas(@Url String apiname, @FieldMap Map<String, String> fields);

    @Multipart
    @POST()
    Call<ResponseBody> sendFiles(
            @Url String apiname,
            @Part(Constants.ServerFields.JSON_DATA) RequestBody description,
            @Part List<MultipartBody.Part> files);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> sendCrash(@Url String apiname, @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> sendLogs(@Url String apiname, @FieldMap Map<String, String> fields);

}
