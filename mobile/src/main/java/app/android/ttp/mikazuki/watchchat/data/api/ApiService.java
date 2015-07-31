package app.android.ttp.mikazuki.watchchat.data.api;

import app.android.ttp.mikazuki.watchchat.data.api.model.BaseAPIResponse;
import app.android.ttp.mikazuki.watchchat.domain.entity.User;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by haijimakazuki on 15/07/11.
 */
public interface ApiService {

    final String SAMPLE_PATH = "/message";
    final String REGISTRATION_PATH = "/";
    final String UNLINK_USER = "/";
    final String SEND_MESSAGE = "/";
    final String SEARCH_USER = "/";


    @FormUrlEncoded
    @POST(SAMPLE_PATH)
    public void sendGcmSample(@Field("registration_ids") String token, Callback<BaseAPIResponse> cb);

    @FormUrlEncoded
    @POST(REGISTRATION_PATH)
    public void registerUser(@Field("name") String name, Callback<User> cb);

    @FormUrlEncoded
    @POST(SEND_MESSAGE)
    public void sendMessage(@Field("message") String message, Callback<String> cb);

    @GET(SEARCH_USER)
    public void searchUser(Callback<User> cb);

    @FormUrlEncoded
    @POST(UNLINK_USER)
    public void unlinkUser(@Field("name") String name, Callback<String> cb);


}
