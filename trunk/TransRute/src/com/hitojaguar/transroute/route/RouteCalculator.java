package com.hitojaguar.transroute.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.hitojaguar.transroute.dataaccess.dao.RouteDao;
import com.hitojaguar.transroute.dataaccess.dao.StationDao;
import com.hitojaguar.transroute.entities.Result;

public class RouteCalculator implements IRouteCalculator {

	public RouteCalculator(Context ctx) {
		RouteDao rDao = new RouteDao(ctx);
		Cursor routeC =rDao.fetchAllRoutes();
		
		LinkedList<String[]> resList = new LinkedList<String[]>();
		String prevRoute=null;
		LinkedList<String> stationList = new LinkedList<String>();
		while(routeC.moveToNext()){
			int rowRoute = routeC.getColumnIndex(RouteDao.KEY_ROUTE_NAME);
			int rowStation = routeC.getColumnIndex(RouteDao.KEY_STATION_NAME);

			String route = routeC.getString(rowRoute);
			String station = routeC.getString(rowStation);
			
			if(prevRoute != null && prevRoute.equalsIgnoreCase(route)){
				stationList.add(station);
			}else{
				if(prevRoute != null){
					String[] currStationsArray = stationList.toArray(new String[0]);
					resList.add(currStationsArray);
				}
				stationList = new LinkedList<String>();
				stationList.add(route);
				stationList.add(station);
			}
			prevRoute = route;
		}
		String[] currStationsArray = stationList.toArray(new String[0]);
		resList.add(currStationsArray);
		
		routes = resList.toArray(new String[0][0]); 	
	}
	
	private List<int[]> findR(String from, String to) {
		List<int[]> starts = findStart(from);
		List<int[]> solutions = new ArrayList<int[]>();
		for (int i = 0; i < starts.size(); i++) {
			for (int j = starts.get(i)[1]; j < routes[starts.get(i)[0]].length; j++) {
				if (routes[starts.get(i)[0]][j].equalsIgnoreCase(to)) {
					int[] tuple = { starts.get(i)[0], starts.get(i)[1], j };
					solutions.add(tuple);
				}
			}
		}
		return solutions;
	}

	private List<int[]> findStart(String from) {
		List<int[]> solutions = new ArrayList<int[]>();
		for (int i = 0; i < routes.length; i++) {
			for (int j = 1; j < routes[i].length - 1; j++) {
				if (routes[i][j].equalsIgnoreCase(from)) {
					int[] pair = { i, j };
					solutions.add(pair);
				}
			}
		}
		return solutions;
	}

	private class CompositeComparator implements Comparator<Object[]> {

		@Override
		public int compare(Object[] a, Object[] b) {
			return (Integer) a[3] < (Integer) b[3] ? -1 : 1;
		}

	}

	List<Object[]> findComposite(String from, String to) {
		List<int[]> starts = findStart(from);
		List<Object[]> solutions = new ArrayList<Object[]>();
		for (int i = 0; i < starts.size(); i++) {
			boolean direct = false;
			for (int j = starts.get(i)[1] + 1; j < routes[starts.get(i)[0]].length; j++)
				direct |= routes[starts.get(i)[0]][j].equalsIgnoreCase(to);
			if (!direct)
				for (int j = starts.get(i)[1] + 1; j < routes[starts.get(i)[0]].length; j++) {
					List<int[]> secondSol = findR(routes[starts.get(i)[0]][j],
							to);
					for (int k = 0; k < secondSol.size(); k++) {
						direct = false;
						for (int m = secondSol.get(k)[2] - 1; m > 0; m--)
							direct |= routes[secondSol.get(k)[0]][m].equalsIgnoreCase(from);
						if (!direct) {
							Object[] tuple = {
									starts.get(i)[0],
									routes[starts.get(i)[0]][j],
									secondSol.get(k)[0],
									1 + j - starts.get(i)[1]
											+ secondSol.get(k)[2]
											- secondSol.get(k)[1] };
							solutions.add(tuple);
						}
					}
				}
		}
		Collections.sort(solutions, new CompositeComparator());
		return solutions;
	}

	@Override
	public Result FindRoutes(String from, String to) {

		List<int[]> solutions = findR(from, to);
		List<Object[]> composite = (!from.equalsIgnoreCase(to)) ? findComposite(from, to)
				: new ArrayList<Object[]>();

		// Rutas directas (sin transbordo):
		List<String> direct = new ArrayList<String>();
		for (int i = 0; i < solutions.size(); i++) {
			direct.add(routes[solutions.get(i)[0]][0] + " ("
					+ (solutions.get(i)[2] - solutions.get(i)[1] + 1) + ")");
		}

		// Rutas con transbordo:
		List<String> noDirect = new ArrayList<String>();
		for (int i = 0; i < composite.size() && i < 15; i++) {
			noDirect.add(composite.get(i)[3] + " paradas: "
					+ routes[(Integer) composite.get(i)[0]][0] + " entre "
					+ from + " y " + composite.get(i)[1] + "; "
					+ routes[(Integer) composite.get(i)[2]][0] + " entre "
					+ composite.get(i)[1] + " y " + to + ".");
		}
		
		Result result = new Result();
		String [] pathType = {"Rutas directas (sin transbordo):","Rutas con transbordo:"}; 
		result.setPathType(pathType);
		String[][] path = new String[pathType.length][];
		path[0]=direct.toArray(new String[0]);
		path[1]=noDirect.toArray(new String[0]);
		result.setPath(path);

		return result;
	}

	/*
	 * function show () {
	 * 
	 * var from = document.getElementById('from').value; var to =
	 * document.getElementById('to').value;
	 * 
	 * var solutions = findR(from, to); var composite = (from != to) ?
	 * findComposite(from, to) : [];
	 * 
	 * var results = document.getElementById('results'); while
	 * (results.hasChildNodes()) results.removeChild(results.firstChild);
	 * 
	 * if (solutions.length == 0 && composite.length == 0) {
	 * results.appendChild(makeElement('p',document.createTextNode('No se
	 * encontraron rutas.'))); return; }
	 * 
	 * if (solutions.length > 0) {
	 * results.appendChild(makeElement('p',document.createTextNode('Rutas
	 * directas (sin transbordo):'))); var tr = makeElement('tr',
	 * makeElement('td')); for (var i = 0; i < solutions.length; i ++) {
	 * tr.appendChild(makeElement('td',
	 * document.createTextNode(routes[solutions[i][0]][0] + " (" +
	 * (solutions[i][2] - solutions[i][1] + 1) + ")"))); } var table =
	 * makeElement('table', makeElement('thead', tr));
	 * table.setAttribute('border', '1'); for (var i = 0; i < stations.length; i
	 * ++) { var include = false; for (var j = 0; j < solutions.length; j ++)
	 * for (var k = 1; k < routes[solutions[j][0]].length; k ++) include |=
	 * routes[solutions[j][0]][k] == stations[i]; if (include) { var tr =
	 * makeElement('tr', makeElement('td',
	 * document.createTextNode(stations[i]))); for (var j = 0; j <
	 * solutions.length; j ++) { var found = false; for (var k = 1; k <
	 * routes[solutions[j][0]].length; k ++) found |= routes[solutions[j][0]][k]
	 * == stations[i]; var td = makeElement('td', document.createTextNode(" "));
	 * if (found) td.setAttribute('bgcolor', '#000000'); tr.appendChild(td); }
	 * table.appendChild(tr); } } results.appendChild(table); }
	 * 
	 * if (composite.length > 0) {
	 * results.appendChild(makeElement('p',document.createTextNode('Rutas con
	 * transbordo:'))); var ul = makeElement('ul'); for (var i = 0; i <
	 * composite.length && i < 15; i ++) { ul.appendChild(makeElement('li',
	 * makeElement('p', document.createTextNode(composite[i][3] + " paradas: " +
	 * routes[composite[i][0]][0] + " entre " + from + " y " + composite[i][1] +
	 * "; " + routes[composite[i][2]][0] + " entre " + composite[i][1] + " y " +
	 * to + ".")))); } results.appendChild(ul); } }
	 */
	/*
	 * function makeElement (type, content) { var elem =
	 * document.createElement(type); if (content) elem.appendChild(content);
	 * return elem; }
	 */
	private String[][] routes = {
			{ "B10", "Portal de la 80", "Av. Cali", "Cr. 77", "Cr. 53",
					"Calle 85", "Virrey", "Calle 100", "Alcal�", "Calle 146",
					"Mazur�n", "Portal Norte" },
			{ "D10", "Portal Norte", "Mazur�n", "Calle 146", "Alcal�",
					"Calle 100", "Virrey", "Calle 85", "Cr. 53", "Cr. 77",
					"Av. Cali", "Portal de la 80" },
			{ "B51", "Portal de la 80", "Cr. 90", "Granja", "Cr. 47", "Virrey",
					"Pepe Sierra", "Toberin", "Portal Norte" },
			{ "B60", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Santa Luc�a", "Marly", "Cl. 57", "Cl. 63", "Cl. 72",
					"Cl. 76", "Portal Norte" },
			{ "H51", "Portal Norte", "Cl. 76", "Cl. 72", "Cl. 63", "Cl. 57",
					"Marly", "Santa Luc�a", "Socorro", "Consuelo", "Molinos",
					"Portal Usme" },
			{ "B70", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Calle 40S", "Restrepo", "Nari�o", "Hort�a", "Av. Jim�nez",
					"Marly", "Cl. 63", "Cl. 72", "Cl. 76", "Calle 127",
					"Prado", "Alcal�", "Calle 146", "Toberin" },
			{ "H70", "Toberin", "Calle 146", "Alcal�", "Prado", "Calle 127",
					"Cl. 76", "Cl. 72", "Cl. 63", "Marly", "Av. Jim�nez",
					"Hort�a", "Nari�o", "Restrepo", "Calle 40S", "Socorro",
					"Consuelo", "Molinos", "Portal Usme" },
			{ "B72", "Portal Usme", "Molinos", "Nari�o", "H�roes", "Calle 85",
					"Calle 100", "Pepe Sierra", "Calle 127", "Alcal�",
					"Portal Norte" },
			{ "H61", "Portal Norte", "Alcal�", "Calle 127", "Pepe Sierra",
					"Calle 100", "Calle 85", "H�roes", "Nari�o", "Molinos",
					"Portal Usme" },
			{ "B73", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Calle 40S", "Restrepo", "Nari�o", "Hort�a", "Av. Jim�nez",
					"Marly", "Cl. 63", "Cl. 72", "Cl. 76", "Calle 127",
					"Prado", "Alcal�", "Calle 146", "Toberin", "Portal Norte" },
			{ "H74", "Portal Norte", "Toberin", "Calle 146", "Alcal�", "Prado",
					"Calle 127", "Cl. 76", "Cl. 72", "Cl. 63", "Marly",
					"Av. Jim�nez", "Hort�a", "Nari�o", "Restrepo", "Calle 40S",
					"Socorro", "Consuelo", "Molinos", "Portal Usme" },
			{ "B13", "Portal Tunal", "Calle 40S", "Olaya", "Restrepo",
					"Hort�a", "Tercer Milenio", "Cl. 22", "Cl. 63", "Flores",
					"Calle 85", "Calle 100", "Calle 142", "Mazur�n",
					"Portal Norte" },
			{ "H13", "Portal Norte", "Mazur�n", "Calle 142", "Calle 100",
					"Calle 85", "Flores", "Cl. 63", "Cl. 22", "Tercer Milenio",
					"Hort�a", "Restrepo", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "B53", "Portal Tunal", "Santa Luc�a", "Hort�a", "Calle 100",
					"Pepe Sierra", "Calle 127", "Alcal�", "Calle 146",
					"Portal Norte" },
			{ "B61", "Portal Tunal", "Restrepo", "Cl. 19", "Cl. 26",
					"Profamilia", "Av. 39", "Cl. 45", "Portal Norte" },
			{ "H52", "Portal Norte", "Cl. 45", "Av. 39", "Profamilia",
					"Cl. 26", "Cl. 19", "Restrepo", "Portal Tunal" },
			{ "B71", "Portal Tunal", "Calle 40S", "Olaya", "Restrepo",
					"H�roes", "Calle 85", "Virrey", "Pepe Sierra", "Prado",
					"Toberin", "Portal Norte" },
			{ "H60", "Portal Norte", "Toberin", "Prado", "Pepe Sierra",
					"Virrey", "Calle 85", "H�roes", "Restrepo", "Olaya",
					"Calle 40S", "Portal Tunal" },
			{ "H73", "Toberin", "Calle 146", "Alcal�", "Calle 127",
					"Pepe Sierra", "Cl. 45", "Av. 39", "Profamilia", "Cl. 26",
					"Cl. 19", "Fucha", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "B50", "Portal Suba", "La Campi�a", "Suba Tv. 91", "H�roes",
					"Calle 85", "Virrey", "Calle 100", "Calle 106",
					"Calle 142", "Portal Norte" },
			{ "C61", "Portal Norte", "Calle 142", "Calle 106", "Calle 100",
					"Virrey", "Calle 85", "H�roes", "Suba Tv. 91",
					"La Campi�a", "Portal Suba" },
			{ "B62", "Las Aguas", "Museo del Oro", "Av. Jim�nez", "Calle 127",
					"Prado", "Alcal�", "Calle 142", "Calle 146", "Toberin",
					"Portal Norte" },
			{ "J70", "Portal Norte", "Toberin", "Calle 146", "Calle 142",
					"Alcal�", "Prado", "Calle 127", "Av. Jim�nez",
					"Museo del Oro", "Las Aguas" },
			{ "B74", "Las Aguas", "Museo del Oro", "Av. Jim�nez", "Cl. 19",
					"Av. 39", "Cl. 57", "Cl. 72", "Cl. 76", "H�roes", "Virrey",
					"Calle 100", "Pepe Sierra", "Calle 142", "Toberin",
					"Portal Norte" },
			{ "J72", "Portal Norte", "Toberin", "Calle 142", "Pepe Sierra",
					"Calle 100", "Virrey", "H�roes", "Cl. 76", "Cl. 72",
					"Cl. 57", "Av. 39", "Cl. 19", "Av. Jim�nez",
					"Museo del Oro", "Las Aguas" },
			{ "B14", "Portal Am�ricas", "Biblioteca Tintal", "Banderas",
					"Pradera", "CDS Cra. 32", "Cl. 26", "Profamilia", "Cl. 45",
					"Marly", "H�roes", "Calle 85", "Calle 100", "Calle 127",
					"Alcal�", "Calle 146", "Portal Norte" },
			{ "F14", "Portal Norte", "Calle 146", "Alcal�", "Calle 127",
					"Calle 100", "Calle 85", "H�roes", "Marly", "Cl. 45",
					"Profamilia", "Cl. 26", "CDS Cra. 32", "Pradera",
					"Banderas", "Biblioteca Tintal", "Portal Am�ricas" },
			{ "B52", "Portal Am�ricas", "Biblioteca Tintal", "Banderas",
					"Marsella", "Cl. 76", "H�roes", "Virrey", "Alcal�",
					"Portal Norte" },
			{ "F61", "Portal Norte", "Alcal�", "Virrey", "H�roes", "Cl. 76",
					"Marsella", "Banderas", "Biblioteca Tintal",
					"Portal Am�ricas" },
			{ "B11", "Portal del Sur", "Venecia", "Alquer�a", "SENA",
					"Ricaurte", "Paloquemao", "CAD", "U. Nacional",
					"El Camp�n", "H�roes", "Calle 85", "Virrey", "Calle 106",
					"Pepe Sierra", "Calle 146", "Mazur�n", "Toberin",
					"Portal Norte" },
			{ "G11", "Portal Norte", "Toberin", "Mazur�n", "Calle 146",
					"Pepe Sierra", "Calle 106", "Virrey", "Calle 85", "H�roes",
					"El Camp�n", "U. Nacional", "CAD", "Paloquemao",
					"Ricaurte", "SENA", "Alquer�a", "Venecia", "Portal del Sur" },
			{ "B54", "Portal del Sur", "General Santander", "H�roes", "Virrey",
					"Calle 106", "Pepe Sierra", "Calle 142", "Calle 146",
					"Mazur�n", "Portal Norte" },
			{ "G61", "Portal Norte", "Mazur�n", "Calle 146", "Calle 142",
					"Pepe Sierra", "Calle 106", "Virrey", "H�roes",
					"General Santander", "Portal del Sur" },
			{ "B12", "Portal del Sur", "Perdomo", "Madelena",
					"General Santander", "Santa Isabel", "Comuneros",
					"Av. El Dorado", "Sim�n Bolivar", "NQS - Cl. 75",
					"La Castellana", "Calle 100", "Calle 127", "Prado",
					"Calle 142", "Toberin", "Portal Norte" },
			{ "G12", "Portal Norte", "Toberin", "Calle 142", "Prado",
					"Calle 127", "Calle 100", "La Castellana", "NQS - Cl. 75",
					"Sim�n Bolivar", "Av. El Dorado", "Comuneros",
					"Santa Isabel", "General Santander", "Madelena", "Perdomo",
					"Portal del Sur" },
			{ "C15", "Portal Tunal", "Calle 40S", "Olaya", "Nari�o",
					"Tercer Milenio", "Av. Jim�nez", "Cl. 19", "Cl. 22",
					"Av. 39", "Cl. 45", "Marly", "Cl. 76", "Puentelargo",
					"Humedal C�rdoba", "Niza Calle 127", "Portal Suba" },
			{ "H15", "Portal Suba", "Niza Calle 127", "Humedal C�rdoba",
					"Puentelargo", "Cl. 76", "Marly", "Cl. 45", "Av. 39",
					"Cl. 22", "Cl. 19", "Av. Jim�nez", "Tercer Milenio",
					"Nari�o", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "C18", "Tercer Milenio", "Av. Jim�nez", "Cl. 19", "Cl. 26",
					"Profamilia", "Cl. 45", "Cl. 63", "Suba Calle 100",
					"21 Angeles", "La Campi�a", "Portal Suba" },
			{ "A18", "Portal Suba", "La Campi�a", "21 Angeles",
					"Suba Calle 100", "Cl. 63", "Cl. 45", "Profamilia",
					"Cl. 26", "Cl. 19", "Av. Jim�nez", "Tercer Milenio" },
			{ "C17", "Portal Usme", "Molinos", "Santa Luc�a", "Calle 40S",
					"Restrepo", "Cl. 57", "Flores", "Cl. 72", "Cl. 76",
					"El Polo", "Shaio", "Suba - Av. Boyac�", "Portal Suba" },
			{ "H17", "Portal Suba", "Suba - Av. Boyac�", "Shaio", "El Polo",
					"Cl. 76", "Cl. 72", "Flores", "Cl. 57", "Restrepo",
					"Calle 40S", "Santa Luc�a", "Molinos", "Portal Usme" },
			{ "C50", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Santa Luc�a", "Hort�a", "Marly", "Cl. 57", "Cl. 63",
					"Cl. 72", "Cl. 76", "Portal Suba" },
			{ "C70", "Calle 40S", "Olaya", "Restrepo", "Nari�o", "Hort�a",
					"Marly", "Cl. 63", "Flores", "Cl. 72", "Cl. 76",
					"Puentelargo", "La Campi�a", "Portal Suba" },
			{ "C19", "Banderas", "Marsella", "Am�ricas Cr. 53A", "Ricaurte",
					"Cl. 19", "Cl. 26", "Profamilia", "Cl. 45", "Marly",
					"Cl. 63", "Cl. 72", "Puentelargo", "Gratamira",
					"Suba Tv. 91", "La Campi�a", "Portal Suba" },
			{ "F19", "Portal Suba", "La Campi�a", "Suba Tv. 91", "Gratamira",
					"Puentelargo", "Cl. 72", "Cl. 63", "Marly", "Cl. 45",
					"Profamilia", "Cl. 26", "Cl. 19", "Ricaurte",
					"Am�ricas Cr. 53A", "Marsella", "Banderas" },
			{ "C16", "Portal del Sur", "Venecia", "General Santander",
					"NQS - Cl. 30S", "U. Nacional", "El Camp�n",
					"Sim�n Bolivar", "Av. Chile", "BORRAME", "Rionegro",
					"Suba Calle 95", "Puentelargo", "Shaio", "Suba Tv. 91",
					"La Campi�a", "Portal Suba" },
			{ "G16", "Portal Suba", "La Campi�a", "Suba Tv. 91", "Shaio",
					"Puentelargo", "Suba Calle 95", "Rionegro", "Av. Chile",
					"BORRAME", "Sim�n Bolivar", "El Camp�n", "U. Nacional",
					"NQS - Cl. 30S", "General Santander", "Venecia",
					"Portal del Sur" },
			{ "C60", "Portal del Sur", "Venecia", "Santa Isabel", "Ricaurte",
					"Paloquemao", "CAD", "Coliseo", "NQS - Cl. 75",
					"Suba - Av. Boyac�", "Suba Tv. 91", "La Campi�a",
					"Portal Suba" },
			{ "G70", "Portal Suba", "La Campi�a", "Suba Tv. 91",
					"Suba - Av. Boyac�", "NQS - Cl. 75", "Coliseo", "CAD",
					"Paloquemao", "Ricaurte", "Santa Isabel", "Venecia",
					"Portal del Sur" },
			{ "D20", "Portal Usme", "Santa Luc�a", "Calle 40S", "Olaya",
					"Fucha", "Av. Jim�nez", "Cl. 19", "Cl. 26", "Profamilia",
					"Cl. 45", "El Polo", "Av. 68", "Cr. 77", "Portal de la 80" },
			{ "H20", "Portal de la 80", "Cr. 77", "Av. 68", "El Polo",
					"Cl. 45", "Profamilia", "Cl. 26", "Cl. 19", "Av. Jim�nez",
					"Fucha", "Olaya", "Calle 40S", "Santa Luc�a", "Portal Usme" },
			{ "D51", "Portal Usme", "Av. Jim�nez", "Cl. 19", "Cl. 26",
					"Profamilia", "Av. 39", "Cl. 45", "Cr. 77",
					"Portal de la 80" },
			{ "D60", "Portal Usme", "Restrepo", "Hort�a", "Av. 39", "Marly",
					"Cl. 57", "Cl. 72", "Cl. 76", "Minuto de Dios", "Granja",
					"Cr. 90", "Portal de la 80" },
			{ "H50", "Portal de la 80", "Cr. 90", "Granja", "Minuto de Dios",
					"Cl. 76", "Cl. 72", "Cl. 57", "Marly", "Av. 39", "Hort�a",
					"Restrepo", "Portal Usme" },
			{ "D50", "Portal Am�ricas", "Cl. 26", "Profamilia", "Cl. 45",
					"Marly", "Cl. 57", "Cl. 63", "Cl. 72", "Cl. 76",
					"Minuto de Dios", "Portal de la 80" },
			{ "F62", "Portal de la 80", "Minuto de Dios", "Cl. 76", "Cl. 72",
					"Cl. 63", "Cl. 57", "Marly", "Cl. 45", "Profamilia",
					"Cl. 26", "Portal Am�ricas" },
			{ "D70", "Las Aguas", "Museo del Oro", "Av. Jim�nez", "Cl. 22",
					"Av. 39", "El Polo", "Escuela Militar", "Av. 68", "Cr. 77",
					"Portal de la 80" },
			{ "J24", "Portal de la 80", "Cr. 77", "Av. 68", "Escuela Militar",
					"El Polo", "Av. 39", "Cl. 22", "Av. Jim�nez",
					"Museo del Oro", "Las Aguas" },
			{ "D21", "Portal Tunal", "Santa Luc�a", "Fucha", "Marly", "Cl. 57",
					"Cl. 63", "Flores", "Cl. 72", "Cl. 76", "Av. Cali",
					"Quirigua", "Portal de la 80" },
			{ "H21", "Portal de la 80", "Quirigua", "Av. Cali", "Cl. 76",
					"Cl. 72", "Flores", "Cl. 63", "Cl. 57", "Marly", "Fucha",
					"Santa Luc�a", "Portal Tunal" },
			{ "D22", "Portal del Sur", "General Santander", "SENA", "Ricaurte",
					"U. Nacional", "Av. Chile", "NQS - Cl. 75", "Av. 68",
					"Minuto de Dios", "Cr. 77", "Portal de la 80" },
			{ "G22", "Portal de la 80", "Cr. 77", "Minuto de Dios", "Av. 68",
					"NQS - Cl. 75", "Av. Chile", "U. Nacional", "Ricaurte",
					"SENA", "General Santander", "Portal del Sur" },
			{ "E50", "Portal del Sur", "Perdomo", "Alquer�a", "NQS - Cl. 38AS",
					"NQS - Cl. 30S", "Santa Isabel", "Ricaurte", "U. Nacional",
					"El Camp�n", "Sim�n Bolivar" },
			{ "G60", "Sim�n Bolivar", "El Camp�n", "U. Nacional", "Ricaurte",
					"Santa Isabel", "NQS - Cl. 30S", "NQS - Cl. 38AS",
					"Alquer�a", "Perdomo", "Portal del Sur" },
			{ "F23", "Las Aguas", "Museo del Oro", "Av. Jim�nez", "Sans Facon",
					"Ricaurte", "Zona Industrial", "Banderas", "Patio Bonito",
					"Portal Am�ricas" },
			{ "J23", "Portal Am�ricas", "Patio Bonito", "Banderas",
					"Zona Industrial", "Ricaurte", "Sans Facon", "Av. Jim�nez",
					"Museo del Oro", "Las Aguas" },
			{ "F60", "Av. Jim�nez", "De la Sabana", "Ricaurte", "Cr. 43",
					"Puente Aranda", "Pradera", "Banderas",
					"Biblioteca Tintal", "Portal Am�ricas" },
			{ "F70", "Portal Am�ricas", "Biblioteca Tintal", "Banderas",
					"Pradera", "Puente Aranda", "Cr. 43", "Ricaurte",
					"De la Sabana", "Av. Jim�nez" } };
	String[] stations = { "Portal del Sur", "Perdomo", "Madelena", "Sevillana",
			"Venecia", "Alquer�a", "General Santander", "NQS - Cl. 38AS",
			"NQS - Cl. 30S", "SENA", "Santa Isabel", "Comuneros", "Ricaurte",
			"Paloquemao", "CAD", "Av. El Dorado", "U. Nacional", "El Camp�n",
			"Coliseo", "Sim�n Bolivar", "Av. Chile", "NQS - Cl. 75",
			"La Castellana", "Portal Am�ricas", "Patio Bonito",
			"Biblioteca Tintal", "Tv. 86", "Banderas", "Mandalay",
			"Mundo Aventura", "Marsella", "Pradera", "Am�ricas Cr. 53A",
			"Puente Aranda", "Cr. 43", "Zona Industrial", "CDS Cra. 32",
			"Ricaurte", "Sans Facon", "De la Sabana", "Las Aguas",
			"Museo del Oro", "Portal Suba", "La Campi�a", "Suba Tv. 91",
			"21 Angeles", "Gratamira", "Suba - Av. Boyac�", "Niza Calle 127",
			"Humedal C�rdoba", "Shaio", "Puentelargo", "Suba Calle 100",
			"Suba Calle 95", "Rionegro", "San Mart�n", "Portal Tunal",
			"Parque", "Biblioteca", "Portal Usme", "Molinos", "Consuelo",
			"Socorro", "Santa Luc�a", "Calle 40S", "Quiroga", "Olaya",
			"Restrepo", "Fucha", "Nari�o", "Hort�a", "Hospital",
			"Tercer Milenio", "Av. Jim�nez", "Cl. 19", "Cl. 22", "Cl. 26",
			"Profamilia", "Av. 39", "Cl. 45", "Marly", "Cl. 57", "Cl. 63",
			"Flores", "Cl. 72", "Cl. 76", "Portal de la 80", "Quirigua",
			"Cr. 90", "Av. Cali", "Granja", "Cr. 77", "Minuto de Dios",
			"Boyac�", "Ferias", "Av. 68", "Cr. 53", "Cr. 47",
			"Escuela Militar", "El Polo", "H�roes", "Calle 85", "Virrey",
			"Calle 100", "Calle 106", "Pepe Sierra", "Calle 127", "Prado",
			"Alcal�", "Calle 142", "Calle 146", "Mazur�n", "Cardio Infantil",
			"Toberin", "Portal Norte" };
	@Override
	public String[] getStations() {
		return stations;
	}
}
