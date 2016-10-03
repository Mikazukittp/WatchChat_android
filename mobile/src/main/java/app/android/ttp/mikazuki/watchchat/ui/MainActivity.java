package app.android.ttp.mikazuki.watchchat.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.List;

import app.android.ttp.mikazuki.watchchat.R;
import app.android.ttp.mikazuki.watchchat.data.api.repository.RetrofitMessageRepository;
import app.android.ttp.mikazuki.watchchat.data.api.repository.RetrofitUserRepository;
import app.android.ttp.mikazuki.watchchat.data.preference.SharedPreferencesUtil;
import app.android.ttp.mikazuki.watchchat.data.preference.repository.PreferenceSettingRepository;
import app.android.ttp.mikazuki.watchchat.domain.entity.Message;
import app.android.ttp.mikazuki.watchchat.domain.entity.User;
import app.android.ttp.mikazuki.watchchat.domain.repository.BaseCallback;
import app.android.ttp.mikazuki.watchchat.domain.repository.MessageRepository;
import app.android.ttp.mikazuki.watchchat.domain.repository.SettingRepository;
import app.android.ttp.mikazuki.watchchat.domain.repository.UserRepository;
import app.android.ttp.mikazuki.watchchat.ui.fragment.ChatFragment;
import app.android.ttp.mikazuki.watchchat.ui.fragment.RegisterFragment;
import app.android.ttp.mikazuki.watchchat.ui.fragment.SearchFragment;
import app.android.ttp.mikazuki.watchchat.ui.gcm.RegistrationIntentService;
import app.android.ttp.mikazuki.watchchat.ui.notification.NotificationSender;
import app.android.ttp.mikazuki.watchchat.util.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Bind(R.id.tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.fragment_container)
    View mFragmentContainer;
    @Bind(R.id.registrationProgressBar)
    ProgressBar mRegistrationProgressBar;

    private UserRepository mUserRepository;
    private MessageRepository mMessageRepository;
    private SettingRepository mSettingRepository;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mMessageBroadcastReceiver;
    private BroadcastReceiver mConnectBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mUserRepository = new RetrofitUserRepository(getApplicationContext());
        mMessageRepository = new RetrofitMessageRepository(getApplicationContext());
        mSettingRepository = new PreferenceSettingRepository(getApplicationContext());
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);

        mMessageBroadcastReceiver = new MessageBroadcastReceiver();
        mConnectBroadcastReceiver = new ConnectBroadcastReceiver();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mMessageBroadcastReceiver, new IntentFilter(Constants.GCM_MESSAGE));
        manager.registerReceiver(mConnectBroadcastReceiver, new IntentFilter(Constants.GCM_CONNECT));


        if (mSettingRepository.isRegistered()) {
            if (mSettingRepository.isLinked()) {
                replaceFragment(ChatFragment.newInstance());
            } else {
                replaceFragment(SearchFragment.newInstance());
            }
        } else {
            replaceFragment(RegisterFragment.newInstance());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
        if (remoteInput != null) {
            onSendMessage(remoteInput.getCharSequence(NotificationSender.EXTRA_VOICE_REPLY).toString());
        }

        setOpponentData();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onRegister(String name) {
        if (checkPlayServices()) {
            Intent i = new Intent(this, RegistrationIntentService.class);
            i.putExtra("NAME", name);
            startService(i);

            mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                    if (mSettingRepository.isRegistered()) {
                        replaceFragment(SearchFragment.newInstance());
                    } else {
                    }
                }
            };
            String intentTag = SharedPreferencesUtil.REGISTRATION_COMPLETE;
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
            manager.registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(intentTag));
        }
    }

    @Override
    public void onSearch() {
        mUserRepository.link(mSettingRepository.getUserId(), new BaseCallback<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    replaceFragment(ChatFragment.newInstance());
                } else {
                    String errorMessage;
                    if (mSettingRepository.isLinked()) {
                        errorMessage = getString(R.string.already_linked);
                    } else {
                        errorMessage = getString(R.string.no_connectable_user);
                    }
                    Snackbar.make(mFragmentContainer, errorMessage, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onSendMessage(String message) {
        mMessageRepository.create(mSettingRepository.getUserId(), message, new BaseCallback<Message>() {
            @Override
            public void onResponse(Message message) {
            }
        });
    }

    @Override
    public void onFetchMessages() {
        mMessageRepository.fetchAll(mSettingRepository.getUserId(), new BaseCallback<List<Message>>() {
            @Override
            public void onResponse(List<Message> messages) {
                if (messages != null && messages.size() > 0) {
                    for (Message message : messages) {
                        System.out.println(message.getCreatedAt());
                    }

                    Message[] messageArr = messages.toArray(new Message[0]);
                    Intent i = new Intent(Constants.FRAGMENT_MESSAGES_FETCHED);
                    i.putExtra(Constants.FETCHED_MESSAGES, messageArr);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    private void replaceFragment(Fragment fragment, boolean stackHistory) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (stackHistory) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void setUserData(User user) {
        if (user != null) {
            mSettingRepository.setUserId(user.getId());
            mSettingRepository.setUserName(user.getName());
        } else {
            mSettingRepository.setUserId(-1);
            mSettingRepository.setUserName("");
        }
    }

    private void setOpponentData() {
        mUserRepository.getOpponent(mSettingRepository.getUserId(), new BaseCallback<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    mSettingRepository.setOpponentId(user.getId());
                    mSettingRepository.setOpponentName(user.getName());
                } else {
                    mSettingRepository.setOpponentId(-1);
                    mSettingRepository.setOpponentName("");
                }
            }
        });
    }

    public void deleteOpponentData() {
        mUserRepository.deleteConnection(mSettingRepository.getUserId(), new BaseCallback<User>() {
            @Override
            public void onResponse(User user) {
                mSettingRepository.setOpponentId(-1);
                mSettingRepository.setOpponentName("");
            }
        });
        replaceFragment(SearchFragment.newInstance());
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

//    // DEBUG用
//    @OnClick(R.id.clear )
//    public void onClearPreference(View v) {
//        mSettingRepository.clear();
//    }

    class RegistrationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
            if (mSettingRepository.isRegistered()) {
                replaceFragment(SearchFragment.newInstance());
            } else {
            }
        }
    }

    class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(Constants.GCM_GET_MESSAGE);
            NotificationSender
                    .build(getApplicationContext())
                    .sendWithAction(mSettingRepository.getOpponentName(), message);

            Intent i = new Intent(Constants.FRAGMENT_MESSAGE_UPDATED);
            i.putExtra(Constants.UPDATED_MESSAGE, message);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        }
    }

    class ConnectBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            setOpponentData();
            String type = intent.getStringExtra(Constants.GCM_CONNECT_TYPE);
            if (Constants.GCM_CONNECTED.equals(type)) {
                replaceFragment(ChatFragment.newInstance());
            } else if (Constants.GCM_DELETE.equals(type)) {
                replaceFragment(SearchFragment.newInstance());
            }

            String message = intent.getStringExtra(Constants.GCM_GET_MESSAGE);
            NotificationSender
                    .build(getApplicationContext())
                    .sendWithAction("友達申請", message);
        }
    }

}
