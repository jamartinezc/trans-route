package com.hitojaguar.transroute.route;

import com.hitojaguar.transroute.entities.Result;

public interface IRouteCalculator {

	public abstract Result FindRoutes(String source, String destination);

}