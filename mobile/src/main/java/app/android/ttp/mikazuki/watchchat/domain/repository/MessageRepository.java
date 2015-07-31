package app.android.ttp.mikazuki.watchchat.domain.repository;

import java.util.List;

import app.android.ttp.mikazuki.watchchat.domain.entity.Message;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public interface MessageRepository {

    public void create(int id, String message, BaseCallback<Message> cb);

    public void fetchAll(int id, BaseCallback<List<Message>> cb);
}
