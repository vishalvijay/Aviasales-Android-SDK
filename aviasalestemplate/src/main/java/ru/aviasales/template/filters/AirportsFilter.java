package ru.aviasales.template.filters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.legacy.search.object.TicketData;
import ru.aviasales.core.search.object.AirportData;


public class AirportsFilter implements Parcelable {

	@Deprecated
	private List<CheckedAirport> airportList;

	private List<CheckedAirport> originAirportList;
	private List<CheckedAirport> destinationAirportList;
	private List<CheckedAirport> stopOverAirportList;

	public AirportsFilter() {
		initLists();
	}

	private void initLists() {
		airportList = new ArrayList<CheckedAirport>();
		originAirportList = new ArrayList<CheckedAirport>();
		destinationAirportList = new ArrayList<CheckedAirport>();
		stopOverAirportList = new ArrayList<CheckedAirport>();
	}

	public AirportsFilter(AirportsFilter airportsFilter) {
		initLists();
		fullcopy(airportList, airportsFilter.getAirportList());
		fullcopy(originAirportList, airportsFilter.getOriginAirportList());
		fullcopy(destinationAirportList, airportsFilter.getDestinationAirportList());
		fullcopy(stopOverAirportList, airportsFilter.getStopOverAirportList());
	}

	private void fullcopy(List<CheckedAirport> list, List<CheckedAirport> sourceList) {
		if (list == null || sourceList == null) return;
		for (int i = 0; i < sourceList.size(); i++) {
			list.add(new CheckedAirport(sourceList.get(i)));
		}
	}

	public List<CheckedAirport> getOriginAirportList() {
		return originAirportList;
	}

	public void setOriginAirportList(List<CheckedAirport> originAirportList) {
		this.originAirportList = originAirportList;
	}

	public List<CheckedAirport> getDestinationAirportList() {
		return destinationAirportList;
	}

	public void setDestinationAirportList(List<CheckedAirport> destinationAirportList) {
		this.destinationAirportList = destinationAirportList;
	}

	public List<CheckedAirport> getStopOverAirportList() {
		return stopOverAirportList;
	}

	public void setStopOverAirportList(List<CheckedAirport> stopOverAirportList) {
		this.stopOverAirportList = stopOverAirportList;
	}

	public void addAirport(String airport) {
		airportList.add(new CheckedAirport(airport));
	}

	public List<CheckedAirport> getAirportList() {
		return airportList;
	}

	public void setAirportList(List<CheckedAirport> airportList) {
		this.airportList = airportList;
	}

	public void sortByName() {

	}

	@Deprecated
	public void setAirportsFromGsonClass(Map<String, AirportData> airportMap) {
		for (String airport : airportMap.keySet()) {
			CheckedAirport cAirport = new CheckedAirport(airport);
			cAirport.setCity(airportMap.get(airport).getCity());
			cAirport.setCountry(airportMap.get(airport).getCountry());
			cAirport.setName(airportMap.get(airport).getName());
			airportList.add(cAirport);
		}
	}

	public void setSectionedAirportsFromGsonClass(Map<String, AirportData> airportMap, List<TicketData> ticketDatas) {

		for (TicketData ticketData : ticketDatas) {
			CheckedAirport originAirport = createAirport(ticketData.getDirectFlights().get(0).getOrigin(), airportMap);
			if (originAirport != null && !originAirportList.contains(originAirport)) {
				originAirportList.add(originAirport);
			}
			if (ticketData.getReturnFlights() != null) {
				String returnOriginAirport = ticketData.getReturnFlights().get(ticketData.getReturnFlights().size() - 1).getDestination();
				CheckedAirport returnOriginCAirport = createAirport(returnOriginAirport, airportMap);
				if (returnOriginCAirport != null && !originAirportList.contains(returnOriginCAirport)) {
					originAirportList.add(returnOriginCAirport);
				}
			}
		}
		Collections.sort(originAirportList, CheckedAirport.sortByName);

		for (TicketData ticketData : ticketDatas) {
			CheckedAirport cAirport = createAirport(ticketData.getDirectFlights().get(ticketData.getDirectFlights().size() - 1).getDestination(), airportMap);
			if (cAirport != null && !destinationAirportList.contains(cAirport)) {
				destinationAirportList.add(cAirport);
			}
			if (ticketData.getReturnFlights() != null) {
				CheckedAirport returnDestinationCAirport = createAirport(ticketData.getReturnFlights().get(0).getOrigin(), airportMap);
				if (returnDestinationCAirport != null && !destinationAirportList.contains(returnDestinationCAirport)) {
					destinationAirportList.add(returnDestinationCAirport);
				}
			}
		}
		Collections.sort(destinationAirportList, CheckedAirport.sortByName);

		for (String airport : airportMap.keySet()) {
			CheckedAirport cAirport = createAirport(airport, airportMap);
			if (cAirport != null && !originAirportList.contains(cAirport) && !destinationAirportList.contains(cAirport)) {
				stopOverAirportList.add(cAirport);
			}
		}
		Collections.sort(stopOverAirportList, CheckedAirport.sortByName);
	}

	public boolean isActual(String airport) {
		List<CheckedAirport> airports = new ArrayList<CheckedAirport>();
		airports.addAll(originAirportList);
		airports.addAll(destinationAirportList);
		airports.addAll(stopOverAirportList);
		for (CheckedAirport checkedAirport : airports) {
			if (checkedAirport.getIata().equals(airport) && !checkedAirport.isChecked()) {
				return false;
			}
		}
		return true;
	}

	public boolean isActive() {
		for (CheckedAirport airport : airportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		for (CheckedAirport airport : originAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		for (CheckedAirport airport : destinationAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		for (CheckedAirport airport : stopOverAirportList) {
			if (!airport.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (CheckedAirport airport : airportList) {
			airport.setChecked(true);
		}
		for (CheckedAirport airport : originAirportList) {
			airport.setChecked(true);
		}
		for (CheckedAirport airport : destinationAirportList) {
			airport.setChecked(true);
		}
		for (CheckedAirport airport : stopOverAirportList) {
			airport.setChecked(true);
		}
	}

	private CheckedAirport createAirport(String airport, Map<String, AirportData> airportMap) {
		CheckedAirport cAirport = new CheckedAirport(airport);
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

	/**
	 * ************ Передача данных в интент ***************
	 */
	public AirportsFilter(Parcel in) {

		if (airportList == null) {
			airportList = new ArrayList<CheckedAirport>();
		}
		if (originAirportList == null) {
			originAirportList = new ArrayList<CheckedAirport>();
		}
		if (destinationAirportList == null) {
			destinationAirportList = new ArrayList<CheckedAirport>();
		}
		if (stopOverAirportList == null) {
			stopOverAirportList = new ArrayList<CheckedAirport>();
		}

		in.readTypedList(airportList, CheckedAirport.CREATOR);
		in.readTypedList(originAirportList, CheckedAirport.CREATOR);
		in.readTypedList(destinationAirportList, CheckedAirport.CREATOR);
		in.readTypedList(stopOverAirportList, CheckedAirport.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(airportList);
		dest.writeTypedList(originAirportList);
		dest.writeTypedList(destinationAirportList);
		dest.writeTypedList(stopOverAirportList);
	}

	public static final Creator<AirportsFilter> CREATOR = new Creator<AirportsFilter>() {
		public AirportsFilter createFromParcel(Parcel in) {
			return new AirportsFilter(in);
		}

		public AirportsFilter[] newArray(int size) {
			return new AirportsFilter[size];
		}
	};
}