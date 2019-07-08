package pro.quizer.quizerexit.API;

import java.util.Map;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitQuizerAPI {

    @POST("/wheretoredirrect_json.php")
    Call<Void> submitKey(@Body() QuizerAPI.SubmitKeyBody body);

    @FormUrlEncoded
    @POST("/_query/send-data")
    Call<ResponseBody> authUser(@FieldMap Map<String, String> fields);

    @POST("/_query/send-data")
    Call<Void> getConfig(@Body() QuizerAPI.GetConfigBody body);

    @FormUrlEncoded
    @POST("/_query/send-data")
    Call<ResponseBody> sendQuestionnaires(@FieldMap Map<String, String> fields);

    @POST("/_query/send-data")
    Call<Void> sendPhoto(@Body() QuizerAPI.SendPhotoBody body);

    @POST("/_query/send-data")
    Call<Void> sendAudio(@Body() QuizerAPI.SendAudioBody body);
}
