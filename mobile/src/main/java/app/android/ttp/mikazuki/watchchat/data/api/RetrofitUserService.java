package app.android.ttp.mikazuki.watchchat.data.api;

import app.android.ttp.mikazuki.watchchat.domain.entity.User;
import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by haijimakazuki on 15/07/15.
 */
public interface RetrofitUserService {

    final String USER_PATH = "/users";
    final String USER_PATH_WITH_ID = "/users/{id}";
    final String OPPONENT_PATH = "/users/{id}/opponent";
    final String CONNECT_PATH = "/connections";
    final String DELETE_CONNECT_PATH = "/connections/{id}";

    @FormUrlEncoded
    @POST(USER_PATH)
    public void createUser(@Field("name") String name, @Field("gcm_id") String token, @Field("device_type") String android, Callback<User> cb);

    @GET(USER_PATH_WITH_ID)
    public void get(@Path("id") int id, Callback<User> cb);

    @GET(OPPONENT_PATH)
    public void getOpponent(@Path("id") int id, Callback<User> cb);

    @FormUrlEncoded
    @POST(CONNECT_PATH)
    public void linkUser(@Field("id") int id, Callback<User> cb);

    @PUT(USER_PATH_WITH_ID)
    public void updateToken(@Path("id") int id, @Field("gcm_id") String token, Callback<User> cb);

    @DELETE(DELETE_CONNECT_PATH)
    public void deleteConnection(@Path("id") int id, Callback<User> cb);

}
