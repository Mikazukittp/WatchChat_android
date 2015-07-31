package app.android.ttp.mikazuki.watchchat.domain.entity;

/**
 * Created by haijimakazuki on 15/07/11.
 */
public class User {

    private int id;
    private String name;
    private String token;

    public User() {
    }

    public User(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public User(int id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
