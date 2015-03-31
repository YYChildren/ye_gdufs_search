package com.ye.gdufs.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public final class Dump {
	private String keyStr;
	private byte[] objByte;
	@Id
	public String getKeyStr() {
		return keyStr;
	}
	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}
	public void setObjByte(byte[] objByte) {
		this.objByte = objByte;
	}
	public byte[] getObjByte() {
		return objByte;
	}
}
