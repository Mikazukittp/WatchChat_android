package app.android.ttp.mikazuki.watchchat.ui.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Iterator;

import app.android.ttp.mikazuki.watchchat.util.Constants;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("push_type");

        StringBuilder sb = new StringBuilder();
        if (data != null) {
            Iterator<?> it = data.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Log.v("from GCM", "key: " + key);
                Log.v("from GCM", key + ": " + data.getString(key));
            }
        }

        Intent i;
        String message = data.getString("message");
        switch (type) {
            case "message":
                i = new Intent(Constants.GCM_MESSAGE);
                i.putExtra(Constants.GCM_GET_MESSAGE, message);
                Log.d(TAG, "get message");
                break;
            case "connect":
                i = new Intent(Constants.GCM_CONNECT);
                i.putExtra(Constants.GCM_CONNECT_TYPE, Constants.GCM_CONNECTED);
                i.putExtra(Constants.GCM_GET_MESSAGE, message);
                break;
            case "delete":
                i = new Intent(Constants.GCM_CONNECT);
                i.putExtra(Constants.GCM_CONNECT_TYPE, Constants.GCM_DELETE);
                i.putExtra(Constants.GCM_GET_MESSAGE, message);
                break;
            default:
                i = null;
        }
        if (i != null) {
            Log.d(TAG, "send Broadcast");
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

}
