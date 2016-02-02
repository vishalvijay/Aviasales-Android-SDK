package ru.aviasales.template.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.expandedlistview.view.BaseCheckedText;

public class AgenciesFilter implements Serializable {

	private List<FilterCheckedAgency> agenciesList;

	public AgenciesFilter() {
		agenciesList = new ArrayList<>();
	}

	public AgenciesFilter(AgenciesFilter agenciesFilter) {
		if (agenciesFilter.getAgenciesList() == null) return;

		agenciesList = new ArrayList<>();
		for (int i = 0; i < agenciesFilter.getAgenciesList().size(); i++) {
			agenciesList.add(new FilterCheckedAgency(agenciesFilter.getAgenciesList().get(i)));
		}
	}

	public void addAgency(String agencyId, String agencyName) {
		agenciesList.add(new FilterCheckedAgency(agencyId, agencyName));
	}

	public List<FilterCheckedAgency> getAgenciesList() {
		return agenciesList;
	}

	public void setAgenciesList(List<FilterCheckedAgency> agenciesList) {
		this.agenciesList = agenciesList;
	}

	public void sortByName() {
		Collections.sort(agenciesList, FilterCheckedAgency.sortByName);
	}

	public void mergeFilter(AgenciesFilter agenciesFilter) {
		if (agenciesFilter.isActive()) {
			for (BaseCheckedText checkedText : agenciesList) {
				for (BaseCheckedText checkedText1 : agenciesFilter.getAgenciesList()) {
					if (checkedText.getName().equals(checkedText1.getName())) {
						checkedText.setChecked(checkedText1.isChecked());
					}
				}
			}
		}
	}

	public void setGatesFromGsonClass(Map<String, GateData> gateDatas) {
		for (GateData gateData : gateDatas.values()) {
			agenciesList.add(new FilterCheckedAgency(gateData.getId(), gateData.getLabel()));
		}
		sortByName();
	}

	public boolean isActual(Proposal proposal) {
		List<String> agenciesToRemove = new ArrayList<>();
		for (FilterCheckedAgency agency : agenciesList) {
			String agencyIdWithoutMagicFare;
			String agencyIdWithMagicFare;
			try {
				agencyIdWithoutMagicFare = Integer.toString(Math.abs(Integer.parseInt(agency.getId())));
				agencyIdWithMagicFare = "-" + agencyIdWithoutMagicFare;
			} catch (Exception e) {
				agencyIdWithoutMagicFare = agency.getId();
				agencyIdWithMagicFare = "-" + agency.getId();
			}

			if (!agency.isChecked() && (!agenciesToRemove.contains(agencyIdWithoutMagicFare) || !agenciesToRemove.contains(agencyIdWithMagicFare))) {
				agenciesToRemove.add(agencyIdWithoutMagicFare);
			 	agenciesToRemove.add(agencyIdWithMagicFare);
			}
		}
		for (String agencyToRemove : agenciesToRemove) {
			if (proposal.getFiltredNativePrices().containsKey(agencyToRemove)) {
				proposal.getFiltredNativePrices().remove(agencyToRemove);
			}
		}
		return proposal.getFiltredNativePrices().size() != 0;
	}

	public boolean isActive() {
		for (FilterCheckedAgency agency : agenciesList) {
			if (!agency.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (FilterCheckedAgency agency : agenciesList) {
			agency.setChecked(true);
		}
	}

	public boolean isValid() {
		return !agenciesList.isEmpty();
	}
}
