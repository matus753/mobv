package mobv.fei.stu.sk.mobv.model;

import java.util.Date;

public class Post {

    private String userid;

    private String username;

    private String type;

    private String url;

    private Date date;

    public Post() {
    }

    public Post(String userid, String username, String type, String url, Date date) {
        this.userid = userid;
        this.username = username;
        this.type = type;
        this.url = url;
        this.date = date;
    }

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
}
