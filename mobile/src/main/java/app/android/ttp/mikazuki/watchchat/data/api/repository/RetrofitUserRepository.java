package app.android.ttp.mikazuki.watchchat.data.api.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.android.ttp.mikazuki.watchchat.data.api.ApiUtil;
import app.android.ttp.mikazuki.watchchat.data.api.RetrofitUserService;
import app.android.ttp.mikazuki.watchchat.domain.entity.User;
import app.android.ttp.mikazuki.watchchat.domain.repository.BaseCallback;
import app.android.ttp.mikazuki.watchchat.domain.repository.UserRepository;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public class RetrofitUserRepository implements UserRepository {

    final private String TAG = "RetrofitUserRepository";

    private Context mContext = null;
    RetrofitUserService mAPI;

    public RetrofitUserRepository(Context context) {
        this.mContext = context;
        buildAPI();
    }

    private void buildAPI() {
        Gson GSON = new GsonBuilder().create();

        RestAdapter REST_ADAPTER = new RestAdapter.Builder()
                .setEndpoint(ApiUtil.API_URL)
                .setConverter(new GsonConverter(GSON))
                .build();
        mAPI = REST_ADAPTER.create(RetrofitUserService.class);
    }


    @Override
    public void create(String name, String token, final BaseCallback<User> cb) {
        mAPI.createUser(name, token, "android", new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "create!!");
                cb.onResponse(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });

    }

    @Override
    public void get(int id, final BaseCallback<User> cb) {
        mAPI.get(id, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                cb.onResponse(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }

    @Override
    public void getOpponent(int id, final BaseCallback<User> cb){
        mAPI.getOpponent(id, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                cb.onResponse(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }

    @Override
    public void link(int id, final BaseCallback<User> cb) {
        mAPI.linkUser(id, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "link user.");
                cb.onResponse(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }

    @Override
    public void updateToken(int id, String token, final BaseCallback<User> cb) {
        mAPI.updateToken(id, token, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.d(TAG, "update!!");
                cb.onResponse(user);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getMessage());
                cb.onResponse(null);
            }
        });
    }
}
