package app.android.ttp.mikazuki.watchchat.ui.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import app.android.ttp.mikazuki.watchchat.data.api.repository.RetrofitUserRepository;
import app.android.ttp.mikazuki.watchchat.data.preference.SharedPreferencesUtil;
import app.android.ttp.mikazuki.watchchat.data.preference.repository.PreferenceSettingRepository;
import app.android.ttp.mikazuki.watchchat.domain.entity.User;
import app.android.ttp.mikazuki.watchchat.domain.repository.BaseCallback;
import app.android.ttp.mikazuki.watchchat.domain.repository.SettingRepository;
import app.android.ttp.mikazuki.watchchat.domain.repository.UserRepository;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private UserRepository mUserRepository;
    private SettingRepository mSettingRepository;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mSettingRepository = new PreferenceSettingRepository(getApplicationContext());

        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken("231934616945", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                sendRegistrationToServer(intent.getStringExtra("NAME"), token);
                subscribeTopics(token);
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
            finishService();
        }
    }

    private void sendRegistrationToServer(String name, final String token) {
        Log.i(TAG, "GCM Registration Token: " + token);
        mUserRepository = new RetrofitUserRepository(getApplicationContext());
        mUserRepository.create(name, token, new BaseCallback<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    mSettingRepository.setUserId(user.getId());
                    mSettingRepository.setUserName(user.getName());
                    mSettingRepository.setToken(token);
                    finishService();
                }
            }
        });
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    private void finishService() {
        Intent registrationComplete = new Intent(SharedPreferencesUtil.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


}