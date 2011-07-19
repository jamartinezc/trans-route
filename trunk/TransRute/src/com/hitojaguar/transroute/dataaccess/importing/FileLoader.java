package com.hitojaguar.transroute.dataaccess.importing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;

import com.hitojaguar.transroute.dataaccess.dao.RouteDao;
import com.hitojaguar.transroute.entities.RouteStation;

public class FileLoader {
	private static Context ctx;
	
	public FileLoader(Context pCtx){
		ctx=pCtx;
	}

	public static void load(File file) {

		try {
			BufferedReader fileB = new BufferedReader(new FileReader(file));
			String line;
			int noSchedules;
			String routeName; 
			
			
			
			// Find noSchedules
			line = fileB.readLine();
			String[] fistRow = line.split(";");
			int i;
			for (i = 0; i < fistRow.length && !fistRow[i].equals("#"); i++) {}
			noSchedules = i-1;
			RouteDao routeD = new RouteDao(ctx);
			
			while ((line = fileB.readLine()) != null) {
				String[] row = line.split(";"); 
				routeName = row[0];

				RouteStation[] routes = new RouteStation[row.length - noSchedules+1];
				for (int k = noSchedules+1; k < row.length; k++) {
					String stationName = row[k];
					//stations TODO RouteDao.insertRoute
				}
				routeD.insertRoute(routeName, routes);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int insertAndGetStation(String stationName) {
		// TODO Auto-generated method stub
		return 0;
	}
}
