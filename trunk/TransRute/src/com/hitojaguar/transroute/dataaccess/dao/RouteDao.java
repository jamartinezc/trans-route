package com.hitojaguar.transroute.dataaccess.dao;

import com.hitojaguar.transroute.entities.RouteStation;

public class RouteDao {

	private TransRouteMainDao mTRdao;

	private static final String ROUTE = "route";
	private static final String KEY_ROWID = "_id";
	private static final String KEY_NAME = "name";
	
	private static final String ROUTE_STATION = "route_station";
	private static final String RS_ROWID = "_id";
	private static final String COST = "time_to_arrive";
	private static final String WAIT_COST = "wait_time";
	private static final String ID_ROUTE = "_id_route";
	private static final String ID_STATION = "_id_station";
	
	public long insertRoute(String routeName,RouteStation[] routes){
		return 0L;
	}
	
}


