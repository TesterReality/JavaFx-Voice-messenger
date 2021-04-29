package sample.localDatabase;

public class UserVoiceKey {
    public int id_voice;
    public int id_friend;

    public String key_my;
    public String key_friend;

    public String secret_key1;
    public String secret_key2;

    public UserVoiceKey() {
    }

    public UserVoiceKey(int id_voice, int id_friend, String key_my, String key_friend, String secret_key1, String secret_key2) {
        this.id_voice = id_voice;
        this.id_friend = id_friend;
        this.key_my = key_my;
        this.key_friend = key_friend;
        this.secret_key1 = secret_key1;
        this.secret_key2 = secret_key2;
    }

    @Override
    public String toString() {
        return "UserVoiceKey{" +
                "id_voice=" + id_voice +
                ", id_friend=" + id_friend +
                ", key_my='" + key_my + '\'' +
                ", key_friend='" + key_friend + '\'' +
                ", secret_key1='" + secret_key1 + '\'' +
                ", secret_key2='" + secret_key2 + '\'' +
                '}';
    }
}
