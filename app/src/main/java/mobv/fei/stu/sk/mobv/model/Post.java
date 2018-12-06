package mobv.fei.stu.sk.mobv.model;

import java.util.Date;

public class Post extends Model{

    private String userid;

    private String username;

    private String type;

    private String url;

    private Date date;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", date=" + date +
                '}';
    }
}
