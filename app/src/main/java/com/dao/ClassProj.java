package com.dao;

public class ClassProj {
	private String projId;
	private String rgdtDate;
	private String endDate;
	private String remark;
	private String photoUri;
	private String vedioCt;

	public String getVedioCt() {
		return vedioCt;
	}

	public void setVedioCt(String vedioCt) {
		this.vedioCt = vedioCt;
	}

	public ClassProj() {
	}

	public ClassProj(String endDate, String photoUri, String projId, String remark, String rgdtDate) {
		this.endDate = endDate;
		this.photoUri = photoUri;
		this.projId = projId;
		this.remark = remark;
		this.rgdtDate = rgdtDate;
	}

	public String getProjId() {
		return projId;
	}
	public void setProjId(String projId) {
		this.projId = projId;
	}
	public String getRgdtDate() {
		return rgdtDate;
	}
	public void setRgdtDate(String rgdtDate) {
		this.rgdtDate = rgdtDate;
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
	public String getPhotoUri() {
		return photoUri;
	}
	public void setPhotoUri(String photoUri) {
		this.photoUri = photoUri;
	}
}
