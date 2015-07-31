package app.android.ttp.mikazuki.watchchat.data.api;

import java.util.List;

import app.android.ttp.mikazuki.watchchat.domain.entity.Message;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by haijimakazuki on 15/07/15.
 */
public interface RetrofitMessageService {

    final String MESSAGE_PATH = "/messages";
    final String MESSAGE_PATH_WITH_ID = "/messages/{id}";

    @FormUrlEncoded
    @POST(MESSAGE_PATH)
    public void sendMessage(@Field("sender_id") int id, @Field("content") String content, Callback<Message> cb);

    @GET(MESSAGE_PATH)
    public void getMessages(@Query("sender_id") int id, Callback<List<Message>> cb);

}
