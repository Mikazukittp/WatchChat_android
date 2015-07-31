package app.android.ttp.mikazuki.watchchat.domain.repository;

/**
 * Created by haijimakazuki on 15/07/16.
 */
public interface SettingRepository {
    int getUserId();

    void setUserId(int id);

    int getOpponentId();

    void setOpponentId(int id);

    String getUserName();

    void setUserName(String name);

    String getOpponentName();

    void setOpponentName(String name);

    String getToken();

    void setToken(String token);

    boolean isRegistered();

    boolean isLinked();

    void clear();
}
