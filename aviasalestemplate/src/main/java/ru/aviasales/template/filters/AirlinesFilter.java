package ru.aviasales.template.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.expandedlistview.view.BaseCheckedText;

public class AirlinesFilter implements Serializable {

	private final List<FilterCheckedAirline> airlineList = new ArrayList<>();

	public AirlinesFilter() {

	}

	public AirlinesFilter(AirlinesFilter airlinesFilter) {
		if (airlinesFilter.getAirlineList() == null) return;
		for (int i = 0; i < airlinesFilter.getAirlineList().size(); i++) {
			airlineList.add(new FilterCheckedAirline(airlinesFilter.getAirlineList().get(i)));
		}
	}

	public void addAirline(String iata) {
		airlineList.add(new FilterCheckedAirline(iata));
	}

	public List<FilterCheckedAirline> getAirlineList() {
		return airlineList;
	}

	public void sortByName() {
		Collections.sort(airlineList, FilterCheckedAirline.sortByName);
	}

	public void mergeFilter(AirlinesFilter airlinesFilter) {
		if (airlinesFilter.isActive()) {
			for (BaseCheckedText checkedText : airlineList) {
				for (BaseCheckedText checkedText1 : airlinesFilter.getAirlineList()) {
					if (checkedText.getName().equals(checkedText1.getName())) {
						checkedText.setChecked(checkedText1.isChecked());
					}
				}
			}
		}
	}

	public void setAirlinesFromGsonClass(Map<String, AirlineData> airlineMap) {
		for (String iata : airlineMap.keySet()) {
			FilterCheckedAirline airline = new FilterCheckedAirline(iata);
			if (airlineMap.get(iata) != null && airlineMap.get(iata).getName() != null) {
				airline.setName(airlineMap.get(iata).getName());
			} else {
				airline.setName(iata);
			}

			if (airlineMap.get(iata) != null && airlineMap.get(iata).getAverageRate() != null) {
				airline.setRating(airlineMap.get(iata).getAverageRate());
			}
			if (!airlineList.contains(airline)) {
				airlineList.add(airline);
			}
		}
		sortByName();
	}

	public void setAirlinesFromGsonClass(Map<String, AirlineData> airlineMap, List<Flight> flights) {
		for (String iata : airlineMap.keySet()) {
			FilterCheckedAirline airline = new FilterCheckedAirline(iata);
			if (airlineMap.get(iata) != null && airlineMap.get(iata).getName() != null) {
				airline.setName(airlineMap.get(iata).getName());
			} else {
				airline.setName(iata);
			}

			if (airlineMap.get(iata) != null && airlineMap.get(iata).getAverageRate() != null) {
				airline.setRating(airlineMap.get(iata).getAverageRate());
			}
			for (Flight flight : flights) {
				if (flight.getOperatingCarrier().equalsIgnoreCase(iata) && !airlineList.contains(airline)) {
					airlineList.add(airline);
				}
			}
		}
	}

	public boolean isActual(String airline) {
		for (FilterCheckedAirline checkedAirline : airlineList) {
			if (checkedAirline.getAirline().equals(airline) && !checkedAirline.isChecked()) {
				return false;
			}
		}
		return true;
	}

	public boolean isActive() {
		for (FilterCheckedAirline airline : airlineList) {
			if (!airline.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (FilterCheckedAirline airline : airlineList) {
			airline.setChecked(true);
		}
	}

	public boolean isValid() {
		return airlineList.size() > 0;
	}
}