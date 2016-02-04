package ru.aviasales.template.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.expandedlistview.view.BaseCheckedText;

public class AgenciesFilter extends BaseListFilter implements Serializable {

	private final List<FilterCheckedAgency> agenciesList = new ArrayList<>();

	public AgenciesFilter() {
	}

	public AgenciesFilter(AgenciesFilter agenciesFilter) {
		if (agenciesFilter.getAgenciesList() == null) return;

		for (int i = 0; i < agenciesFilter.getAgenciesList().size(); i++) {
			agenciesList.add(new FilterCheckedAgency(agenciesFilter.getAgenciesList().get(i)));
		}
	}

	public List<FilterCheckedAgency> getAgenciesList() {
		return agenciesList;
	}

	public void sortByName() {
		Collections.sort(agenciesList, BaseCheckedText.nameComparator);
	}

	public void addGates(Map<String, GateData> gateDatas) {
		for (GateData gateData : gateDatas.values()) {
			agenciesList.add(new FilterCheckedAgency(gateData.getId(), gateData.getLabel()));
		}
		sortByName();
	}

	public boolean isActual(Proposal proposal) {
		return proposal.getFiltredNativePrices().size() != 0;
	}

	public void validateProposal(Proposal proposal) {
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
	}

	@Override
	public List<? extends BaseCheckedText> getCheckedTextList() {
		return agenciesList;
	}

}
