package ru.aviasales.template.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.core.search.object.Proposal;

public class AirportsFilter implements Serializable {
	private final List<FilterCheckedAirport> originAirportList = new ArrayList<>();
	private final List<FilterCheckedAirport> destinationAirportList = new ArrayList<>();
	private final List<FilterCheckedAirport> stopOverAirportList = new ArrayList<>();

	public AirportsFilter() {
	}

	public AirportsFilter(AirportsFilter airportsFilter) {
		fullcopy(originAirportList, airportsFilter.getOriginAirportList());
		fullcopy(destinationAirportList, airportsFilter.getDestinationAirportList());
		fullcopy(stopOverAirportList, airportsFilter.getStopOverAirportList());
	}

	public void mergeFilter(AirportsFilter airportsFilter) {
		if (airportsFilter.isActive()) {
			mergeCheckedState(originAirportList, airportsFilter.originAirportList);
			mergeCheckedState(destinationAirportList, airportsFilter.destinationAirportList);
			mergeCheckedState(stopOverAirportList, airportsFilter.stopOverAirportList);
		}
	}

	public void mergeCheckedState(List<FilterCheckedAirport> checkedAirports, List<FilterCheckedAirport> checkedAirports1) {
		for (FilterCheckedAirport checkedAirport : checkedAirports) {
			for (FilterCheckedAirport checkedAirport1 : checkedAirports1) {
				if (checkedAirport.getIata() != null && checkedAirport1.getIata() != null && checkedAirport.getIata().equals(checkedAirport1.getIata())) {
					checkedAirport.setChecked(checkedAirport1.isChecked());
				}
			}
		}
	}

	private void fullcopy(List<FilterCheckedAirport> list, List<FilterCheckedAirport> sourceList) {
		if (list == null || sourceList == null) return;
		for (int i = 0; i < sourceList.size(); i++) {
			list.add(new FilterCheckedAirport(sourceList.get(i)));
		}
	}

	public List<FilterCheckedAirport> getOriginAirportList() {
		return originAirportList;
	}

	public List<FilterCheckedAirport> getDestinationAirportList() {
		return destinationAirportList;
	}

	public List<FilterCheckedAirport> getStopOverAirportList() {
		return stopOverAirportList;
	}

	public void setSectionedAirportsFromGsonClassSimple(Map<String, AirportData> airportMap, List<Proposal> proposals) {

		for (Proposal proposal : proposals) {
			FilterCheckedAirport originAirport = createAirport(proposal.getSegmentFlights(0).get(0).getDeparture(), airportMap);
			if (originAirport != null && !originAirportList.contains(originAirport)) {
				originAirportList.add(originAirport);
			}
			if (proposal.getSegments().size() == 2) {
				String returnOriginAirport = proposal.getSegmentFlights(1).get(proposal.getSegmentFlights(1).size() - 1).getArrival();
				FilterCheckedAirport returnOriginCAirport = createAirport(returnOriginAirport, airportMap);
				if (returnOriginCAirport != null && !originAirportList.contains(returnOriginCAirport)) {
					originAirportList.add(returnOriginCAirport);
				}
			}
		}

		for (Proposal proposal : proposals) {
			FilterCheckedAirport cAirport = createAirport(proposal.getSegmentFlights(0).get(
					proposal.getSegmentFlights(0).size() - 1).getArrival(), airportMap);
			if (cAirport != null && !destinationAirportList.contains(cAirport)) {
				destinationAirportList.add(cAirport);
			}
			if (proposal.getSegments().size() == 2) {
				FilterCheckedAirport returnDestinationCAirport = createAirport(proposal.getSegmentFlights(1).get(0).getDeparture(), airportMap);
				if (returnDestinationCAirport != null && !destinationAirportList.contains(returnDestinationCAirport)) {
					destinationAirportList.add(returnDestinationCAirport);
				}
			}
		}

		for (String airport : airportMap.keySet()) {
			FilterCheckedAirport cAirport = createAirport(airport, airportMap);
			if (cAirport != null && !originAirportList.contains(cAirport) && !destinationAirportList.contains(cAirport) &&
					!stopOverAirportList.contains(cAirport)) {
				stopOverAirportList.add(cAirport);
			}
		}
	}

	public void setSectionedAirportsFromGsonClass(Map<String, AirportData> airportMap, List<Flight> flights) {

		FilterCheckedAirport originAirport = createAirport(flights.get(0).getDeparture(), airportMap);
		if (originAirport != null && !originAirportList.contains(originAirport)) {
			originAirportList.add(originAirport);
		}


		FilterCheckedAirport destinationAirport = createAirport(flights.get(
				flights.size() - 1).getArrival(), airportMap);
		if (destinationAirport != null && !destinationAirportList.contains(destinationAirport)) {
			destinationAirportList.add(destinationAirport);
		}

		for (int i = 1; i < flights.size(); i++) {
			FilterCheckedAirport stopOverAirport = createAirport(flights.get(i).getDeparture(), airportMap);
			if (stopOverAirport != null && !originAirportList.contains(stopOverAirport) && !destinationAirportList.contains(stopOverAirport) &&
					!stopOverAirportList.contains(stopOverAirport)) {
				stopOverAirportList.add(stopOverAirport);
			}
		}
	}

	public void sortByName() {
		Collections.sort(originAirportList, FilterCheckedAirport.sortByName);
		Collections.sort(destinationAirportList, FilterCheckedAirport.sortByName);
		Collections.sort(stopOverAirportList, FilterCheckedAirport.sortByName);

	}

	public boolean isActual(String airport) {
		List<FilterCheckedAirport> airports = new ArrayList<FilterCheckedAirport>();
		airports.addAll(originAirportList);
		airports.addAll(destinationAirportList);
		airports.addAll(stopOverAirportList);
		for (FilterCheckedAirport checkedAirport : airports) {
			if (checkedAirport.getIata().equals(airport) && !checkedAirport.isChecked()) {
				return false;
			}
		}
		return true;
	}

	public boolean isActive() {
		for (FilterCheckedAirport airport : originAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		for (FilterCheckedAirport airport : destinationAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		for (FilterCheckedAirport airport : stopOverAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (FilterCheckedAirport airport : originAirportList) {
			airport.setChecked(true);
		}
		for (FilterCheckedAirport airport : destinationAirportList) {
			airport.setChecked(true);
		}
		for (FilterCheckedAirport airport : stopOverAirportList) {
			airport.setChecked(true);
		}
	}

	private FilterCheckedAirport createAirport(String airport, Map<String, AirportData> airportMap) {
		FilterCheckedAirport cAirport = new FilterCheckedAirport(airport);
		if (airportMap.get(airport) != null) {
			if (airportMap.get(airport).getCity() != null) {
				cAirport.setCity(airportMap.get(airport).getCity());
			} else {
				cAirport.setCity("");
			}
			if (airportMap.get(airport).getCountry() != null) {
				cAirport.setCountry(airportMap.get(airport).getCountry());
			} else {
				cAirport.setCountry("");
			}
			if (airportMap.get(airport).getName() != null) {
				cAirport.setName(airportMap.get(airport).getName());
			} else {
				cAirport.setName("");
			}
			if (airportMap.get(airport).getAverageRate() != null) {
				cAirport.setRating(airportMap.get(airport).getAverageRate());
			}
		} else {
			return null;
		}
		return cAirport;
	}

	public boolean isValid() {
		return originAirportList.size() > 0 || destinationAirportList.size() > 0 || stopOverAirportList.size() > 0;
	}
}