package sample.localDatabase;

public class UserVoiceKey {
    public int id;
    public String friend_name;

    public String key_my;
    public String key_friend;

    public String secret_key1;
    public String secret_key2;

    public UserVoiceKey() {
    }

    public UserVoiceKey(int id, String friend_name, String key_my, String key_friend, String secret_key1, String secret_key2) {
        this.id = id;
        this.friend_name = friend_name;
        this.key_my = key_my;
        this.key_friend = key_friend;
        this.secret_key1 = secret_key1;
        this.secret_key2 = secret_key2;
    }


    @Override
    public String toString() {
        return "UserVoiceKey{" +
                "id=" + id +
                ", friend_name='" + friend_name + '\'' +
                ", key_my='" + key_my + '\'' +
                ", key_friend='" + key_friend + '\'' +
                ", secret_key1='" + secret_key1 + '\'' +
                ", secret_key2='" + secret_key2 + '\'' +
                '}';
    }
}
