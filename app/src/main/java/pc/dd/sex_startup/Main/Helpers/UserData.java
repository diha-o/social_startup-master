package pc.dd.sex_startup.Main.Helpers;

public class UserData {

    public String lat;
    public String url;
    public String uid;
    public String nickname;

    public UserData(){}
    public UserData(String lat, String url, String uid, String nickname) {
        this.lat = lat;
        this.url = url;
        this.uid = uid;
        this.nickname = nickname;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLat() {
        return lat;
    }

    public String getUrl() {
        return url;
    }

    public String getUid() {
        return uid;
    }
    public String getNickname() {
        return nickname;
    }
}
