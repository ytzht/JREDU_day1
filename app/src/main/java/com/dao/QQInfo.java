package com.dao;

import android.graphics.Bitmap;

import java.io.Serializable;

public class QQInfo implements Serializable {
	public String qqname;
	public Bitmap qqLogo;
	public String getQqname() {
		return qqname;
	}
	public void setQqname(String qqname) {
		this.qqname = qqname;
	}
	public Bitmap getQqLogo() {
		return qqLogo;
	}
	public void setQqLogo(Bitmap qqLogo) {
		this.qqLogo = qqLogo;
	}
	

}
