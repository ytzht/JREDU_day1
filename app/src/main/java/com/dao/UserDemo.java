package com.dao;

import java.io.Serializable;

/**
 * Created by 道欣 on 2015/11/17.
 */
public class UserDemo implements Serializable {
    private String  uname;
    private String upwd;
    private String nickName;
    private String sexId;
    private String rgdtDate;
    private String level;
    private String photoUri;
    public  UserDemo(){
        this.uname = "";
    }

    public UserDemo(String uname, String upwd, String nickName, String sexId, String rgdtDate, String level, String photoUri) {
        this.uname = uname;
        this.upwd = upwd;
        this.nickName = nickName;
        this.sexId = sexId;
        this.rgdtDate = rgdtDate;
        this.level = level;
        this.photoUri = photoUri;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpwd() {
        return upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSexId() {
        return sexId;
    }

    public void setSexId(String sexId) {
        this.sexId = sexId;
    }

    public String getRgdtDate() {
        return rgdtDate;
    }

    public void setRgdtDate(String rgdtDate) {
        this.rgdtDate = rgdtDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
