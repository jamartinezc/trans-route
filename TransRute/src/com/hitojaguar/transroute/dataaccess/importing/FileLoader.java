package com.hitojaguar.transroute.dataaccess.importing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {
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
			
			while ((line = fileB.readLine()) != null) {
				String[] row = line.split(";"); 
				routeName = row[0];
				
				
				for (int k = noSchedules+1; k < row.length; k++) {
					String stationName = row[k];
					//stations TODO RouteDao.insertRoute
				}
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
