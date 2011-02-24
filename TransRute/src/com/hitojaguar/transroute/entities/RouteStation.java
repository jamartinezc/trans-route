package com.hitojaguar.transroute.entities;

public class RouteStation {
	private int id;
	private float cost;
	private float waitCost;
	private String nameStation;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}
	public float getWaitCost() {
		return waitCost;
	}
	public void setWaitCost(float waitCost) {
		this.waitCost = waitCost;
	}
	public String getNameStation() {
		return nameStation;
	}
	public void setNameStation(String nameStation) {
		this.nameStation = nameStation;
	}
	
	
}
