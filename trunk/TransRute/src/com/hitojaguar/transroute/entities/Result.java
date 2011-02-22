package com.hitojaguar.transroute.entities;

import java.io.Serializable;

public class Result implements Serializable{
	private static final long serialVersionUID = 7987725111761799573L;
	private String[] pathType = {};
    private String[][] path = {};
    
	public String[] getPathType() {
		return pathType;
	}
	public void setPathType(String[] pathType) {
		this.pathType = pathType;
	}
	public String[][] getPath() {
		return path;
	}
	public void setPath(String[][] path) {
		this.path = path;
	}
    
}
