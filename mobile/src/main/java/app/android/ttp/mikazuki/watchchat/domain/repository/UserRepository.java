package app.android.ttp.mikazuki.watchchat.domain.repository;

import app.android.ttp.mikazuki.watchchat.domain.entity.User;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public interface UserRepository {

    public void create(String name, String token, BaseCallback<User> cb);

    public void get(int id, BaseCallback<User> cb);

    void getOpponent(int id, BaseCallback<User> cb);

    public void link(int id, BaseCallback<User> cb);
}
