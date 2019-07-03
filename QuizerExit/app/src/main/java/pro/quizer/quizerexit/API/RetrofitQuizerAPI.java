package pro.quizer.quizerexit.API;

import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.AuthRequestModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitQuizerAPI {

    @POST("/wheretoredirrect_json.php")
    Call<Void> submitKey(@Body() QuizerAPI.SubmitKeyBody body);

    @POST("/_query/send-data")
    Call<AuthResponseModel> authUser(@Body() AuthRequestModel body);

    @POST("/_query/send-data")
    Call<Void> getConfig(@Body() QuizerAPI.GetConfigBody body);

    @POST("/_query/send-data")
    Call<Void> sendData(@Body() QuizerAPI.SendDataBody body);

    @POST("/_query/send-data")
    Call<Void> sendPhoto(@Body() QuizerAPI.SendPhotoBody body);

    @POST("/_query/send-data")
    Call<Void> sendAudio(@Body() QuizerAPI.SendAudioBody body);
}
