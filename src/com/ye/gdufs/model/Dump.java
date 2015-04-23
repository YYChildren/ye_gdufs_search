package com.ye.gdufs.model;

public class Dump implements java.io.Serializable {
	private static final long serialVersionUID = -3931102643835864688L;
	private String keyStr;
	private Object obj;
	public String getKeyStr() {
		return keyStr;
	}
	public void setKeyStr(String keyStr) {
		this.keyStr = keyStr;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
}
