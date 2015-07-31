package app.android.ttp.mikazuki.watchchat.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;

import app.android.ttp.mikazuki.watchchat.ui.MainActivity;
import app.android.ttp.mikazuki.watchchat.R;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public class NotificationSender {

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private final long[] VIBRATE_PATTERN = {0, 600, 200, 100, 100, 100};

    private static NotificationSender self;
    private static Context mContext;

    private NotificationSender() {
    }

    public static NotificationSender build(Context context) {
        mContext = context;
        return self = (self == null) ? new NotificationSender() : self;
    }

    public void send(String title, String message) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 返信アクションのインテントを作成
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        // notificationを作り、WearableExtenderとしてアクションを追加する
        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_mic_white_48dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(VIBRATE_PATTERN)
                .setContentIntent(pendingIntent)
                .build();

        // NotificationManagerサービスのインスタンスを取得します
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // 通知マネージャーで通知を作成し発行します
        notificationManager.notify(0 /* ID of notification */, notification);
    }

    public void sendWithAction(String title, String message) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 返信アクションのインテントを作成
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        // リモート入力の作成
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(mContext.getString(R.string.input_content))
                .build();

        // ウェアラブル通知の作成とリモート入力の追加
        // 返信アクションを生成し、remote inputを追加する
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_mic_white_48dp, mContext.getString(R.string.voice_reply), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        // notificationを作り、WearableExtenderとしてアクションを追加する
        Notification notification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_mic_white_48dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(VIBRATE_PATTERN)
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.ic_launcher, mContext.getString(R.string.reply), pendingIntent)
                .addAction(R.mipmap.ic_launcher, mContext.getString(R.string.transfer), pendingIntent)
                .extend(new NotificationCompat.WearableExtender().addAction(action))
                .build();

        // NotificationManagerサービスのインスタンスを取得します
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // 通知マネージャーで通知を作成し発行します
        notificationManager.notify(0 /* ID of notification */, notification);
    }
}
