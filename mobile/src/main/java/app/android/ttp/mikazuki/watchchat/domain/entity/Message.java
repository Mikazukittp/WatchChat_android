package app.android.ttp.mikazuki.watchchat.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public class Message implements Serializable {

    private String content;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("opponent_id")
    private int opponentId;

    @SerializedName("created_at")
    private Date createdAt;

    public Message() {
    }

    public Message(String content, int senderId, int opponentId, Date createdAt) {
        this.content = content;
        this.senderId = senderId;
        this.opponentId = opponentId;
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(int opponentId) {
        this.opponentId = opponentId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
