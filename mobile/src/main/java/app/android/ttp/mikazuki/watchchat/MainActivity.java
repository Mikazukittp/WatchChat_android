package app.android.ttp.mikazuki.watchchat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    @Bind(R.id.registrationProgressBar)
    ProgressBar mRegistrationProgressBar;

    @Bind(R.id.informationTextView)
    TextView mInformationTextView;
    @Bind(R.id.regist)
    Button mRegist;
    @Bind(R.id.send)
    Button mSend;

    private SharedPreferences mSharedPreferences;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ApiService mAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        String token = mSharedPreferences.getString(QuickstartPreferences.REGISTRATION_TOKEN, null);
        if (token != null) {
            mRegist.setEnabled(false);
        } else {
            mSend.setEnabled(false);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                boolean sentToken = mSharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
    }

    @OnClick(R.id.regist)
    public void OnRegisterButtonClicked(View view) {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent i = new Intent(this, RegistrationIntentService.class);
            startService(i);
            mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        }
    }

    @OnClick(R.id.send)
    public void OnSendButtonClicked(View view) {

        Gson GSON = new GsonBuilder().create();
        RestAdapter REST_ADAPTER = new RestAdapter.Builder()
                .setEndpoint(ApiUtil.API_URL)
                .setConverter(new GsonConverter(GSON))
                .build();
        mAPI = REST_ADAPTER.create(ApiService.class);

        String token = mSharedPreferences.getString(QuickstartPreferences.REGISTRATION_TOKEN, null);
        mAPI.sendGcmSample(token, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "sent!!!");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "fail to sent!!!");
                Log.e(TAG, error.getMessage());
                if (error.getResponse() != null) {
                    Log.e(TAG, error.getResponse().getStatus() + "");
                    Log.e(TAG, error.getResponse().getReason() + "");
                }

            }
        });
    }

    @OnClick(R.id.notification)
    public void sendNotification(View view) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 返信アクションのインテントを作成
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */,
                intent, PendingIntent.FLAG_ONE_SHOT);

        // リモート入力の作成
        RemoteInput remoteInput = new RemoteInput.Builder("extra_voice_reply")
                .setLabel("入力内容")
                .build();

        // ウェアラブル通知の作成とリモート入力の追加
        // 返信アクションを生成し、remote inputを追加する
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(android.R.drawable.ic_btn_speak_now, getString(R.string.voice_reply), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        // notificationを作り、WearableExtenderとしてアクションを追加する
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("GCM Message")
                .setContentText("メッセージだよ")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
//                .addAction(R.mipmap.ic_launcher, "返信", pendingIntent)
                .addAction(R.mipmap.ic_launcher, "転送", pendingIntent)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .build();

        // NotificationManagerサービスのインスタンスを取得します
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // 通知マネージャーで通知を作成し発行します
        notificationManager.notify(0 /* ID of notification */, notification);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Returns a string built from the current time
     */
    private String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
