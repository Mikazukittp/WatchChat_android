package app.android.ttp.mikazuki.watchchat.data.api.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import app.android.ttp.mikazuki.watchchat.data.api.ApiUtil;
import app.android.ttp.mikazuki.watchchat.data.api.RetrofitMessageService;
import app.android.ttp.mikazuki.watchchat.domain.entity.Message;
import app.android.ttp.mikazuki.watchchat.domain.repository.BaseCallback;
import app.android.ttp.mikazuki.watchchat.domain.repository.MessageRepository;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by haijimakazuki on 15/07/15.
 */
public class RetrofitMessageRepository implements MessageRepository {

    final private String TAG = "RetrofitUserRepository";

    private Context mContext = null;
    RetrofitMessageService mAPI;

    public RetrofitMessageRepository(Context context) {
        this.mContext = context;
        buildAPI();
    }

    private void buildAPI() {
        Gson GSON = new GsonBuilder().create();

        RestAdapter REST_ADAPTER = new RestAdapter.Builder()
                .setEndpoint(ApiUtil.API_URL)
                .setConverter(new GsonConverter(GSON))
//                .setRequestInterceptor(new BaseRequestInterceptor(mContext))
                .build();
        mAPI = REST_ADAPTER.create(RetrofitMessageService.class);
    }


    @Override
    public void create(int id, String message, final BaseCallback<Message> cb) {

        mAPI.sendMessage(id, message, new Callback<Message>() {
            @Override
            public void success(Message message, Response response) {
                Log.d(TAG, "create!!");
                cb.onResponse(message);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }

    @Override
    public void fetchAll(int id, final BaseCallback<List<Message>> cb) {

        mAPI.getMessages(id, new Callback<List<Message>>() {
            @Override
            public void success(List<Message> messages, Response response) {
                Log.d(TAG, "create!!");
                cb.onResponse(messages);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }

}
