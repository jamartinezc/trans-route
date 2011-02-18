package com.hitojaguar.transroute.entities;

import java.io.Serializable;

public class Result implements Serializable{
	private static final long serialVersionUID = 7987725111761799573L;
	private String[] pathType = { "Pocos transbordos", "Menor Tiempo", "Sin transbordos" };
    private String[][] path = {
            { "Tomar C60 hasta Suba Tv. 91", "Tomar B50 hasta Portal Norte"},
            { "Tomar D22 hasta Portal de la 80", "Tomar B51 hasta Portal Norte"},
            { "Tomar B54 hasta Portal Norte"}
    };
    
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
