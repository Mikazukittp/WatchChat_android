package app.android.ttp.mikazuki.watchchat.data.preference.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public class PreferenceBaseRepository {

    private Context mContext;
    protected SharedPreferences mSharedPreferences;

    public PreferenceBaseRepository(Context context) {
        mContext = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected Context getmContext() {
        return mContext;
    }
}
