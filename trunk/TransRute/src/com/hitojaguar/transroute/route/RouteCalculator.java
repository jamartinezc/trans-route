package com.hitojaguar.transroute.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.hitojaguar.transroute.entities.Result;

public class RouteCalculator implements IRouteCalculator {

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
					"Calle 85", "Virrey", "Calle 100", "Alcalá", "Calle 146",
					"Mazurén", "Portal Norte" },
			{ "D10", "Portal Norte", "Mazurén", "Calle 146", "Alcalá",
					"Calle 100", "Virrey", "Calle 85", "Cr. 53", "Cr. 77",
					"Av. Cali", "Portal de la 80" },
			{ "B51", "Portal de la 80", "Cr. 90", "Granja", "Cr. 47", "Virrey",
					"Pepe Sierra", "Toberin", "Portal Norte" },
			{ "B60", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Santa Lucía", "Marly", "Cl. 57", "Cl. 63", "Cl. 72",
					"Cl. 76", "Portal Norte" },
			{ "H51", "Portal Norte", "Cl. 76", "Cl. 72", "Cl. 63", "Cl. 57",
					"Marly", "Santa Lucía", "Socorro", "Consuelo", "Molinos",
					"Portal Usme" },
			{ "B70", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Calle 40S", "Restrepo", "Nariño", "Hortúa", "Av. Jiménez",
					"Marly", "Cl. 63", "Cl. 72", "Cl. 76", "Calle 127",
					"Prado", "Alcalá", "Calle 146", "Toberin" },
			{ "H70", "Toberin", "Calle 146", "Alcalá", "Prado", "Calle 127",
					"Cl. 76", "Cl. 72", "Cl. 63", "Marly", "Av. Jiménez",
					"Hortúa", "Nariño", "Restrepo", "Calle 40S", "Socorro",
					"Consuelo", "Molinos", "Portal Usme" },
			{ "B72", "Portal Usme", "Molinos", "Nariño", "Héroes", "Calle 85",
					"Calle 100", "Pepe Sierra", "Calle 127", "Alcalá",
					"Portal Norte" },
			{ "H61", "Portal Norte", "Alcalá", "Calle 127", "Pepe Sierra",
					"Calle 100", "Calle 85", "Héroes", "Nariño", "Molinos",
					"Portal Usme" },
			{ "B73", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Calle 40S", "Restrepo", "Nariño", "Hortúa", "Av. Jiménez",
					"Marly", "Cl. 63", "Cl. 72", "Cl. 76", "Calle 127",
					"Prado", "Alcalá", "Calle 146", "Toberin", "Portal Norte" },
			{ "H74", "Portal Norte", "Toberin", "Calle 146", "Alcalá", "Prado",
					"Calle 127", "Cl. 76", "Cl. 72", "Cl. 63", "Marly",
					"Av. Jiménez", "Hortúa", "Nariño", "Restrepo", "Calle 40S",
					"Socorro", "Consuelo", "Molinos", "Portal Usme" },
			{ "B13", "Portal Tunal", "Calle 40S", "Olaya", "Restrepo",
					"Hortúa", "Tercer Milenio", "Cl. 22", "Cl. 63", "Flores",
					"Calle 85", "Calle 100", "Calle 142", "Mazurén",
					"Portal Norte" },
			{ "H13", "Portal Norte", "Mazurén", "Calle 142", "Calle 100",
					"Calle 85", "Flores", "Cl. 63", "Cl. 22", "Tercer Milenio",
					"Hortúa", "Restrepo", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "B53", "Portal Tunal", "Santa Lucía", "Hortúa", "Calle 100",
					"Pepe Sierra", "Calle 127", "Alcalá", "Calle 146",
					"Portal Norte" },
			{ "B61", "Portal Tunal", "Restrepo", "Cl. 19", "Cl. 26",
					"Profamilia", "Av. 39", "Cl. 45", "Portal Norte" },
			{ "H52", "Portal Norte", "Cl. 45", "Av. 39", "Profamilia",
					"Cl. 26", "Cl. 19", "Restrepo", "Portal Tunal" },
			{ "B71", "Portal Tunal", "Calle 40S", "Olaya", "Restrepo",
					"Héroes", "Calle 85", "Virrey", "Pepe Sierra", "Prado",
					"Toberin", "Portal Norte" },
			{ "H60", "Portal Norte", "Toberin", "Prado", "Pepe Sierra",
					"Virrey", "Calle 85", "Héroes", "Restrepo", "Olaya",
					"Calle 40S", "Portal Tunal" },
			{ "H73", "Toberin", "Calle 146", "Alcalá", "Calle 127",
					"Pepe Sierra", "Cl. 45", "Av. 39", "Profamilia", "Cl. 26",
					"Cl. 19", "Fucha", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "B50", "Portal Suba", "La Campiña", "Suba Tv. 91", "Héroes",
					"Calle 85", "Virrey", "Calle 100", "Calle 106",
					"Calle 142", "Portal Norte" },
			{ "C61", "Portal Norte", "Calle 142", "Calle 106", "Calle 100",
					"Virrey", "Calle 85", "Héroes", "Suba Tv. 91",
					"La Campiña", "Portal Suba" },
			{ "B62", "Las Aguas", "Museo del Oro", "Av. Jiménez", "Calle 127",
					"Prado", "Alcalá", "Calle 142", "Calle 146", "Toberin",
					"Portal Norte" },
			{ "J70", "Portal Norte", "Toberin", "Calle 146", "Calle 142",
					"Alcalá", "Prado", "Calle 127", "Av. Jiménez",
					"Museo del Oro", "Las Aguas" },
			{ "B74", "Las Aguas", "Museo del Oro", "Av. Jiménez", "Cl. 19",
					"Av. 39", "Cl. 57", "Cl. 72", "Cl. 76", "Héroes", "Virrey",
					"Calle 100", "Pepe Sierra", "Calle 142", "Toberin",
					"Portal Norte" },
			{ "J72", "Portal Norte", "Toberin", "Calle 142", "Pepe Sierra",
					"Calle 100", "Virrey", "Héroes", "Cl. 76", "Cl. 72",
					"Cl. 57", "Av. 39", "Cl. 19", "Av. Jiménez",
					"Museo del Oro", "Las Aguas" },
			{ "B14", "Portal Américas", "Biblioteca Tintal", "Banderas",
					"Pradera", "CDS Cra. 32", "Cl. 26", "Profamilia", "Cl. 45",
					"Marly", "Héroes", "Calle 85", "Calle 100", "Calle 127",
					"Alcalá", "Calle 146", "Portal Norte" },
			{ "F14", "Portal Norte", "Calle 146", "Alcalá", "Calle 127",
					"Calle 100", "Calle 85", "Héroes", "Marly", "Cl. 45",
					"Profamilia", "Cl. 26", "CDS Cra. 32", "Pradera",
					"Banderas", "Biblioteca Tintal", "Portal Américas" },
			{ "B52", "Portal Américas", "Biblioteca Tintal", "Banderas",
					"Marsella", "Cl. 76", "Héroes", "Virrey", "Alcalá",
					"Portal Norte" },
			{ "F61", "Portal Norte", "Alcalá", "Virrey", "Héroes", "Cl. 76",
					"Marsella", "Banderas", "Biblioteca Tintal",
					"Portal Américas" },
			{ "B11", "Portal del Sur", "Venecia", "Alquería", "SENA",
					"Ricaurte", "Paloquemao", "CAD", "U. Nacional",
					"El Campín", "Héroes", "Calle 85", "Virrey", "Calle 106",
					"Pepe Sierra", "Calle 146", "Mazurén", "Toberin",
					"Portal Norte" },
			{ "G11", "Portal Norte", "Toberin", "Mazurén", "Calle 146",
					"Pepe Sierra", "Calle 106", "Virrey", "Calle 85", "Héroes",
					"El Campín", "U. Nacional", "CAD", "Paloquemao",
					"Ricaurte", "SENA", "Alquería", "Venecia", "Portal del Sur" },
			{ "B54", "Portal del Sur", "General Santander", "Héroes", "Virrey",
					"Calle 106", "Pepe Sierra", "Calle 142", "Calle 146",
					"Mazurén", "Portal Norte" },
			{ "G61", "Portal Norte", "Mazurén", "Calle 146", "Calle 142",
					"Pepe Sierra", "Calle 106", "Virrey", "Héroes",
					"General Santander", "Portal del Sur" },
			{ "B12", "Portal del Sur", "Perdomo", "Madelena",
					"General Santander", "Santa Isabel", "Comuneros",
					"Av. El Dorado", "Simón Bolivar", "NQS - Cl. 75",
					"La Castellana", "Calle 100", "Calle 127", "Prado",
					"Calle 142", "Toberin", "Portal Norte" },
			{ "G12", "Portal Norte", "Toberin", "Calle 142", "Prado",
					"Calle 127", "Calle 100", "La Castellana", "NQS - Cl. 75",
					"Simón Bolivar", "Av. El Dorado", "Comuneros",
					"Santa Isabel", "General Santander", "Madelena", "Perdomo",
					"Portal del Sur" },
			{ "C15", "Portal Tunal", "Calle 40S", "Olaya", "Nariño",
					"Tercer Milenio", "Av. Jiménez", "Cl. 19", "Cl. 22",
					"Av. 39", "Cl. 45", "Marly", "Cl. 76", "Puentelargo",
					"Humedal Córdoba", "Niza Calle 127", "Portal Suba" },
			{ "H15", "Portal Suba", "Niza Calle 127", "Humedal Córdoba",
					"Puentelargo", "Cl. 76", "Marly", "Cl. 45", "Av. 39",
					"Cl. 22", "Cl. 19", "Av. Jiménez", "Tercer Milenio",
					"Nariño", "Olaya", "Calle 40S", "Portal Tunal" },
			{ "C18", "Tercer Milenio", "Av. Jiménez", "Cl. 19", "Cl. 26",
					"Profamilia", "Cl. 45", "Cl. 63", "Suba Calle 100",
					"21 Angeles", "La Campiña", "Portal Suba" },
			{ "A18", "Portal Suba", "La Campiña", "21 Angeles",
					"Suba Calle 100", "Cl. 63", "Cl. 45", "Profamilia",
					"Cl. 26", "Cl. 19", "Av. Jiménez", "Tercer Milenio" },
			{ "C17", "Portal Usme", "Molinos", "Santa Lucía", "Calle 40S",
					"Restrepo", "Cl. 57", "Flores", "Cl. 72", "Cl. 76",
					"El Polo", "Shaio", "Suba - Av. Boyacá", "Portal Suba" },
			{ "H17", "Portal Suba", "Suba - Av. Boyacá", "Shaio", "El Polo",
					"Cl. 76", "Cl. 72", "Flores", "Cl. 57", "Restrepo",
					"Calle 40S", "Santa Lucía", "Molinos", "Portal Usme" },
			{ "C50", "Portal Usme", "Molinos", "Consuelo", "Socorro",
					"Santa Lucía", "Hortúa", "Marly", "Cl. 57", "Cl. 63",
					"Cl. 72", "Cl. 76", "Portal Suba" },
			{ "C70", "Calle 40S", "Olaya", "Restrepo", "Nariño", "Hortúa",
					"Marly", "Cl. 63", "Flores", "Cl. 72", "Cl. 76",
					"Puentelargo", "La Campiña", "Portal Suba" },
			{ "C19", "Banderas", "Marsella", "Américas Cr. 53A", "Ricaurte",
					"Cl. 19", "Cl. 26", "Profamilia", "Cl. 45", "Marly",
					"Cl. 63", "Cl. 72", "Puentelargo", "Gratamira",
					"Suba Tv. 91", "La Campiña", "Portal Suba" },
			{ "F19", "Portal Suba", "La Campiña", "Suba Tv. 91", "Gratamira",
					"Puentelargo", "Cl. 72", "Cl. 63", "Marly", "Cl. 45",
					"Profamilia", "Cl. 26", "Cl. 19", "Ricaurte",
					"Américas Cr. 53A", "Marsella", "Banderas" },
			{ "C16", "Portal del Sur", "Venecia", "General Santander",
					"NQS - Cl. 30S", "U. Nacional", "El Campín",
					"Simón Bolivar", "Av. Chile", "BORRAME", "Rionegro",
					"Suba Calle 95", "Puentelargo", "Shaio", "Suba Tv. 91",
					"La Campiña", "Portal Suba" },
			{ "G16", "Portal Suba", "La Campiña", "Suba Tv. 91", "Shaio",
					"Puentelargo", "Suba Calle 95", "Rionegro", "Av. Chile",
					"BORRAME", "Simón Bolivar", "El Campín", "U. Nacional",
					"NQS - Cl. 30S", "General Santander", "Venecia",
					"Portal del Sur" },
			{ "C60", "Portal del Sur", "Venecia", "Santa Isabel", "Ricaurte",
					"Paloquemao", "CAD", "Coliseo", "NQS - Cl. 75",
					"Suba - Av. Boyacá", "Suba Tv. 91", "La Campiña",
					"Portal Suba" },
			{ "G70", "Portal Suba", "La Campiña", "Suba Tv. 91",
					"Suba - Av. Boyacá", "NQS - Cl. 75", "Coliseo", "CAD",
					"Paloquemao", "Ricaurte", "Santa Isabel", "Venecia",
					"Portal del Sur" },
			{ "D20", "Portal Usme", "Santa Lucía", "Calle 40S", "Olaya",
					"Fucha", "Av. Jiménez", "Cl. 19", "Cl. 26", "Profamilia",
					"Cl. 45", "El Polo", "Av. 68", "Cr. 77", "Portal de la 80" },
			{ "H20", "Portal de la 80", "Cr. 77", "Av. 68", "El Polo",
					"Cl. 45", "Profamilia", "Cl. 26", "Cl. 19", "Av. Jiménez",
					"Fucha", "Olaya", "Calle 40S", "Santa Lucía", "Portal Usme" },
			{ "D51", "Portal Usme", "Av. Jiménez", "Cl. 19", "Cl. 26",
					"Profamilia", "Av. 39", "Cl. 45", "Cr. 77",
					"Portal de la 80" },
			{ "D60", "Portal Usme", "Restrepo", "Hortúa", "Av. 39", "Marly",
					"Cl. 57", "Cl. 72", "Cl. 76", "Minuto de Dios", "Granja",
					"Cr. 90", "Portal de la 80" },
			{ "H50", "Portal de la 80", "Cr. 90", "Granja", "Minuto de Dios",
					"Cl. 76", "Cl. 72", "Cl. 57", "Marly", "Av. 39", "Hortúa",
					"Restrepo", "Portal Usme" },
			{ "D50", "Portal Américas", "Cl. 26", "Profamilia", "Cl. 45",
					"Marly", "Cl. 57", "Cl. 63", "Cl. 72", "Cl. 76",
					"Minuto de Dios", "Portal de la 80" },
			{ "F62", "Portal de la 80", "Minuto de Dios", "Cl. 76", "Cl. 72",
					"Cl. 63", "Cl. 57", "Marly", "Cl. 45", "Profamilia",
					"Cl. 26", "Portal Américas" },
			{ "D70", "Las Aguas", "Museo del Oro", "Av. Jiménez", "Cl. 22",
					"Av. 39", "El Polo", "Escuela Militar", "Av. 68", "Cr. 77",
					"Portal de la 80" },
			{ "J24", "Portal de la 80", "Cr. 77", "Av. 68", "Escuela Militar",
					"El Polo", "Av. 39", "Cl. 22", "Av. Jiménez",
					"Museo del Oro", "Las Aguas" },
			{ "D21", "Portal Tunal", "Santa Lucía", "Fucha", "Marly", "Cl. 57",
					"Cl. 63", "Flores", "Cl. 72", "Cl. 76", "Av. Cali",
					"Quirigua", "Portal de la 80" },
			{ "H21", "Portal de la 80", "Quirigua", "Av. Cali", "Cl. 76",
					"Cl. 72", "Flores", "Cl. 63", "Cl. 57", "Marly", "Fucha",
					"Santa Lucía", "Portal Tunal" },
			{ "D22", "Portal del Sur", "General Santander", "SENA", "Ricaurte",
					"U. Nacional", "Av. Chile", "NQS - Cl. 75", "Av. 68",
					"Minuto de Dios", "Cr. 77", "Portal de la 80" },
			{ "G22", "Portal de la 80", "Cr. 77", "Minuto de Dios", "Av. 68",
					"NQS - Cl. 75", "Av. Chile", "U. Nacional", "Ricaurte",
					"SENA", "General Santander", "Portal del Sur" },
			{ "E50", "Portal del Sur", "Perdomo", "Alquería", "NQS - Cl. 38AS",
					"NQS - Cl. 30S", "Santa Isabel", "Ricaurte", "U. Nacional",
					"El Campín", "Simón Bolivar" },
			{ "G60", "Simón Bolivar", "El Campín", "U. Nacional", "Ricaurte",
					"Santa Isabel", "NQS - Cl. 30S", "NQS - Cl. 38AS",
					"Alquería", "Perdomo", "Portal del Sur" },
			{ "F23", "Las Aguas", "Museo del Oro", "Av. Jiménez", "Sans Facon",
					"Ricaurte", "Zona Industrial", "Banderas", "Patio Bonito",
					"Portal Américas" },
			{ "J23", "Portal Américas", "Patio Bonito", "Banderas",
					"Zona Industrial", "Ricaurte", "Sans Facon", "Av. Jiménez",
					"Museo del Oro", "Las Aguas" },
			{ "F60", "Av. Jiménez", "De la Sabana", "Ricaurte", "Cr. 43",
					"Puente Aranda", "Pradera", "Banderas",
					"Biblioteca Tintal", "Portal Américas" },
			{ "F70", "Portal Américas", "Biblioteca Tintal", "Banderas",
					"Pradera", "Puente Aranda", "Cr. 43", "Ricaurte",
					"De la Sabana", "Av. Jiménez" } };
	String[] stations = { "Portal del Sur", "Perdomo", "Madelena", "Sevillana",
			"Venecia", "Alquería", "General Santander", "NQS - Cl. 38AS",
			"NQS - Cl. 30S", "SENA", "Santa Isabel", "Comuneros", "Ricaurte",
			"Paloquemao", "CAD", "Av. El Dorado", "U. Nacional", "El Campín",
			"Coliseo", "Simón Bolivar", "Av. Chile", "NQS - Cl. 75",
			"La Castellana", "Portal Américas", "Patio Bonito",
			"Biblioteca Tintal", "Tv. 86", "Banderas", "Mandalay",
			"Mundo Aventura", "Marsella", "Pradera", "Américas Cr. 53A",
			"Puente Aranda", "Cr. 43", "Zona Industrial", "CDS Cra. 32",
			"Ricaurte", "Sans Facon", "De la Sabana", "Las Aguas",
			"Museo del Oro", "Portal Suba", "La Campiña", "Suba Tv. 91",
			"21 Angeles", "Gratamira", "Suba - Av. Boyacá", "Niza Calle 127",
			"Humedal Córdoba", "Shaio", "Puentelargo", "Suba Calle 100",
			"Suba Calle 95", "Rionegro", "San Martín", "Portal Tunal",
			"Parque", "Biblioteca", "Portal Usme", "Molinos", "Consuelo",
			"Socorro", "Santa Lucía", "Calle 40S", "Quiroga", "Olaya",
			"Restrepo", "Fucha", "Nariño", "Hortúa", "Hospital",
			"Tercer Milenio", "Av. Jiménez", "Cl. 19", "Cl. 22", "Cl. 26",
			"Profamilia", "Av. 39", "Cl. 45", "Marly", "Cl. 57", "Cl. 63",
			"Flores", "Cl. 72", "Cl. 76", "Portal de la 80", "Quirigua",
			"Cr. 90", "Av. Cali", "Granja", "Cr. 77", "Minuto de Dios",
			"Boyacá", "Ferias", "Av. 68", "Cr. 53", "Cr. 47",
			"Escuela Militar", "El Polo", "Héroes", "Calle 85", "Virrey",
			"Calle 100", "Calle 106", "Pepe Sierra", "Calle 127", "Prado",
			"Alcalá", "Calle 142", "Calle 146", "Mazurén", "Cardio Infantil",
			"Toberin", "Portal Norte" };
}
