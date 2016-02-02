package ru.aviasales.template.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.aviasales.core.search.object.Flight;

public class OvernightFilter implements Serializable {

	private boolean isAirportOvernightAvailable = true;

	private boolean isAirportOvernightViewEnabled = false;

	private List<OvernightTerms> overnightTermsList;

	public OvernightFilter(OvernightFilter overnightFilter) {
		isAirportOvernightAvailable = overnightFilter.isAirportOvernightAvailable();
		isAirportOvernightViewEnabled = overnightFilter.isAirportOvernightViewEnabled();

		if (overnightFilter.getOvernightTermsList() == null) return;
		overnightTermsList = new ArrayList<>();
		for (int i = 0; i < overnightFilter.getOvernightTermsList().size(); i++) {
			overnightTermsList.add(new OvernightTerms(overnightFilter.getOvernightTermsList().get(i)));
		}
	}

	public void mergeFilter(OvernightFilter overnightFilter) {
		if (overnightFilter.isActive()) {
			isAirportOvernightAvailable = overnightFilter.isAirportOvernightAvailable;
		}
	}

	public void clearFilter() {
		isAirportOvernightAvailable = isAirportOvernightViewEnabled;
	}

	private void initOvernightList() {
		overnightTermsList = new ArrayList<>();
		overnightTermsList.add(new OvernightTerms(4, 5, 23, 4));
		overnightTermsList.add(new OvernightTerms(5, 6, 22, 4));
		overnightTermsList.add(new OvernightTerms(6, 7, 21, 5));
		overnightTermsList.add(new OvernightTerms(7, 8, 20, 5));
		overnightTermsList.add(new OvernightTerms(8, 9, 19, 5));
		overnightTermsList.add(new OvernightTerms(9, 10, 18, 5));
		overnightTermsList.add(new OvernightTerms(10, 11, 17, 5));
		overnightTermsList.add(new OvernightTerms(11, 12, 16, 5));
		overnightTermsList.add(new OvernightTerms(12, 13, 15, 5));
		overnightTermsList.add(new OvernightTerms(13, 14, 14, 5));
		overnightTermsList.add(new OvernightTerms(14, 15, 13, 5));
		overnightTermsList.add(new OvernightTerms(15, 16, 12, 5));
		overnightTermsList.add(new OvernightTerms(16, 17, 11, 5));
		overnightTermsList.add(new OvernightTerms(17, 18, 10, 5));
		overnightTermsList.add(new OvernightTerms(18, 19, 9, 5));
		overnightTermsList.add(new OvernightTerms(19, 20, 8, 5));
		overnightTermsList.add(new OvernightTerms(20, 200, 0, 25));
	}

	public OvernightFilter() {
		initOvernightList();
	}

	public boolean isActive() {
		return !(isAirportOvernightAvailable || !isAirportOvernightViewEnabled);
	}

	public boolean isActual(List<Flight> flights) {
		for (int i = 0; i < flights.size(); i++) {
			for (OvernightTerms overnightTerms : overnightTermsList) {
				if ((!isAirportOvernightAvailable && i != 0 && overnightTerms.isOvernight(flights.get(i).getDepartureInMinutes() -
						flights.get(i - 1).getArrivalInMinutes(), flights.get(i - 1).getArrivalInHoursFromDayBeginning()))) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean hasOvernight(List<Flight> flights) {
		for (int i = 0; i < flights.size(); i++) {
			for (OvernightTerms overnightTerms : overnightTermsList) {
				if ((i != 0 && overnightTerms.isOvernight(flights.get(i).getDepartureInMinutes() -
						flights.get(i - 1).getArrivalInMinutes(), flights.get(i - 1).getArrivalInHoursFromDayBeginning()))) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isAirportOvernightAvailable() {
		return isAirportOvernightAvailable;
	}

	public void setAirportOvernightAvailable(boolean airportOvernightAvailable) {
		isAirportOvernightAvailable = airportOvernightAvailable;
	}

	public List<OvernightTerms> getOvernightTermsList() {
		return overnightTermsList;
	}

	public void setOvernightTermsList(List<OvernightTerms> overnightTermsList) {
		this.overnightTermsList = overnightTermsList;
	}

	public boolean isAirportOvernightViewEnabled() {
		return isAirportOvernightViewEnabled;
	}

	public void setAirportOvernightEnabled(boolean airportOvernightEnabled) {
		isAirportOvernightViewEnabled = airportOvernightEnabled;
	}

	public boolean isValid() {
		return isAirportOvernightViewEnabled;
	}
}