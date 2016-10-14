package com.dao;

import java.io.Serializable;

/**
 * Created by heng on 2015/11/26.
 */
public class UserCollect implements Serializable{


    private String uname;
    private String endDate;
    private String remark;
    private String vedioid;
    private String projid;

    private String vedioima;
    private String vedioUrl;
    private String date;

    public UserCollect() {

    }

    public UserCollect(String uname, String endDate, String remark, String vedioid, String projid,
                       String vedioima, String vedioUrl, String date) {
        this.uname = uname;
        this.endDate = endDate;
        this.remark = remark;
        this.vedioid = vedioid;
        this.projid = projid;
        this.vedioima = vedioima;
        this.vedioUrl = vedioUrl;
        this.date = date;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVedioid() {
        return vedioid;
    }

    public void setVedioid(String vedioid) {
        this.vedioid = vedioid;
    }

    public String getProjid() {
        return projid;
    }

    public void setProjid(String projid) {
        this.projid = projid;
    }

    public String getVedioima() {
        return vedioima;
    }

    public void setVedioima(String vedioima) {
        this.vedioima = vedioima;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
