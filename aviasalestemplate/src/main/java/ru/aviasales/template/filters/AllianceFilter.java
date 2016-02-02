package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.expandedlistview.view.BaseCheckedText;
import ru.aviasales.template.R;

public class AllianceFilter implements Serializable {

	private transient Context context;
	private List<BaseCheckedText> allianceList;

	public AllianceFilter(Context context) {
		this.context = context;
		allianceList = new ArrayList<>();
	}

	public AllianceFilter(Context context, AllianceFilter allianceFilter) {
		if (allianceFilter.getAllianceList() == null) return;
		this.context = context;

		allianceList = new ArrayList<>();
		for (int i = 0; i < allianceFilter.getAllianceList().size(); i++) {
			allianceList.add(new BaseCheckedText(allianceFilter.getAllianceList().get(i)));
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}
	public void addAlliance(String alliance) {
		allianceList.add(new BaseCheckedText(alliance));
	}

	public List<BaseCheckedText> getAllianceList() {
		return allianceList;
	}

	public void setAllianceList(List<BaseCheckedText> allianceList) {
		this.allianceList = allianceList;
	}

	public void mergeFilter(AllianceFilter allianceFilter) {
		if (allianceFilter.isActive()) {
			for (BaseCheckedText checkedText : allianceList) {
				for (BaseCheckedText checkedText1 : allianceFilter.getAllianceList()) {
					if (checkedText.getName().equals(checkedText1.getName())) {
						checkedText.setChecked(checkedText1.isChecked());
					}
				}
			}
		}
	}

	public boolean isActual(String alliance) {
		for (BaseCheckedText checkedAlliance : allianceList) {
			if (checkedAlliance.getName().equals(alliance) && !checkedAlliance.isChecked()) {
				return false;
			}
			if (context.getString(R.string.filters_another_alliances).equals(checkedAlliance.getName()) &&
					alliance == null &&
					!checkedAlliance.isChecked()) {
				return false;
			}
		}
		return true;
	}

	public void setAlliancesFromGsonClass(Map<String, AirlineData> airlineMap) {
		for (String airline : airlineMap.keySet()) {
			if (airlineMap.get(airline) != null && airlineMap.get(airline).getAllianceName() != null) {
				BaseCheckedText cAlliance = new BaseCheckedText(airlineMap.get(airline).getAllianceName());

				if (!allianceList.contains(cAlliance) && cAlliance.getName() != null) {
					allianceList.add(cAlliance);
				}
			}
		}
		BaseCheckedText anotherAlliances = new BaseCheckedText();
		anotherAlliances.setChecked(true);
		anotherAlliances.setName(context.getString(R.string.filters_another_alliances));
		allianceList.add(anotherAlliances);
	}

	public void setAlliancesFromGsonClass(Map<String, AirlineData> airlineMap, List<Flight> flights) {
		for (String airline : airlineMap.keySet()) {
			if (airlineMap.get(airline) != null && airlineMap.get(airline).getAllianceName() != null) {
				BaseCheckedText cAlliance = new BaseCheckedText(airlineMap.get(airline).getAllianceName());

				for (Flight flight : flights) {
					if (!allianceList.contains(cAlliance) && cAlliance.getName() != null &&
							flight.getOperatingCarrier().equalsIgnoreCase(airline)) {
						allianceList.add(cAlliance);
					}
				}
			}
		}
		BaseCheckedText anotherAlliances = new BaseCheckedText();
		anotherAlliances.setChecked(true);
		anotherAlliances.setName(context.getString(R.string.filters_another_alliances));
		if (!allianceList.contains(anotherAlliances)) {
			allianceList.add(anotherAlliances);
		}
	}

	public void sortByName() {
		Collections.sort(allianceList, BaseCheckedText.sortByName);
	}

	public boolean isActive() {
		for (BaseCheckedText alliance : allianceList) {
			if (!alliance.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (BaseCheckedText alliance : allianceList) {
			alliance.setChecked(true);
		}
	}

	public boolean isValid() {
		return allianceList.size() > 0;
	}
}