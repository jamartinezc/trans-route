package com.hitojaguar.transroute.dataaccess.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.hitojaguar.transroute.entities.RouteStation;

public class RouteDao {

	private TransRouteMainDao mTRdao;

	public static final String ROUTE_TABLE = "route";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	
	public static final String ROUTE_STATION_TABLE = "route_station";
	public static final String RS_ROWID = "_id";
	public static final String COST = "time_to_arrive";
	public static final String WAIT_COST = "wait_time";
	public static final String ID_ROUTE = "_id_route";
	public static final String ID_STATION = "_id_station";
	
	public long insertRoute(String routeName,RouteStation[] routes){
		
        ContentValues rowValues = new ContentValues();
        rowValues.put(KEY_NAME, routeName);

        long routeId = mTRdao.mDb.insert(ROUTE_TABLE, null, rowValues);
        
        for (RouteStation routeStation : routes) {

            rowValues = new ContentValues();
            rowValues.put(COST, routeStation.getCost());
            rowValues.put(WAIT_COST, routeStation.getWaitCost());
            rowValues.put(ID_ROUTE, routeId);

            routeStation.getNameStation();
            StationDao sDao = new StationDao(mTRdao);
            Cursor station = sDao.fetchStations(routeStation.getNameStation());
            int stationIdIndex = station.getColumnIndexOrThrow(StationDao.KEY_ROWID);
            long stationId = station.getLong(stationIdIndex);
            
            rowValues.put(ID_STATION, stationId);
            
            //long routeStationId = 
            mTRdao.mDb.insert(ROUTE_STATION_TABLE, null, rowValues);
		}
        
		return routeId;
	}
	
}


