package com.ye.gdufs.dao;

import java.io.File;

public abstract class SearchPath {
	protected String path;
	protected File file; 
	protected abstract void setPath();
	public String getPath() {
		return path;
	}
	public File getFile() {
		return file;
	}
}
