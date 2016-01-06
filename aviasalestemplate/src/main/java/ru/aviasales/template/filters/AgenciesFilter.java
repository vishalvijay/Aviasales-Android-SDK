package ru.aviasales.template.filters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.aviasales.core.legacy.search.object.TicketData;
import ru.aviasales.core.search.object.GateData;


public class AgenciesFilter implements Parcelable {

	private List<CheckedAgency> agenciesList;

	public AgenciesFilter() {
		agenciesList = new ArrayList<CheckedAgency>();
	}

	public AgenciesFilter(AgenciesFilter agenciesFilter) {
		if (agenciesFilter.getAgenciesList() == null) return;

		agenciesList = new ArrayList<CheckedAgency>();
		for (int i = 0; i < agenciesFilter.getAgenciesList().size(); i++) {
			agenciesList.add(new CheckedAgency(agenciesFilter.getAgenciesList().get(i)));
		}
	}

	public void addAgency(String agencyId, String agencyName) {
		agenciesList.add(new CheckedAgency(agencyId, agencyName));
	}

	public List<CheckedAgency> getAgenciesList() {
		return agenciesList;
	}

	public void setAgenciesList(List<CheckedAgency> agenciesList) {
		this.agenciesList = agenciesList;
	}

	public void sortByName() {
		Collections.sort(agenciesList, CheckedAgency.sortByName);
	}

	public void setGatesFromGsonClass(List<GateData> gateDatas) {
		for (GateData gateData : gateDatas) {
			agenciesList.add(new CheckedAgency(gateData.getId(), gateData.getLabel()));
		}
		sortByName();
	}

	public boolean isActual(TicketData ticket) {
		List<String> agenciesToRemove = new ArrayList<String>();
		for (CheckedAgency agency : agenciesList) {
			if (!agency.isChecked() && !agenciesToRemove.contains(agency.getId())) {
				agenciesToRemove.add(agency.getId());
			}
		}
		for (String agencyToRemove : agenciesToRemove) {
			if (ticket.getFiltredNativePrices().containsKey(agencyToRemove)) {
				ticket.getFiltredNativePrices().remove(agencyToRemove);
			}
		}
		return ticket.getFiltredNativePrices().size() != 0;
	}

	public boolean isActive() {
		for (CheckedAgency agency : agenciesList) {
			if (!agency.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (CheckedAgency agency : agenciesList) {
			agency.setChecked(true);
		}
	}

	public AgenciesFilter(Parcel in) {
		if (agenciesList == null) {
			agenciesList = new ArrayList<CheckedAgency>();
		}
		in.readTypedList(agenciesList, CheckedAgency.CREATOR);
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(agenciesList);
	}

	public static final Creator<AgenciesFilter> CREATOR = new Creator<AgenciesFilter>() {
		public AgenciesFilter createFromParcel(Parcel in) {
			return new AgenciesFilter(in);
		}

		public AgenciesFilter[] newArray(int size) {
			return new AgenciesFilter[size];
		}
	};
}
