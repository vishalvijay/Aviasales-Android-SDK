package ru.aviasales.template.proposal.sort;

import java.util.Comparator;

import ru.aviasales.core.search.object.Proposal;

public class ProposalArrivalComparator implements Comparator<Proposal> {
	private final boolean isComplexSearch;

	public ProposalArrivalComparator(boolean isComplexSearch) {
		this.isComplexSearch = isComplexSearch;
	}

	@Override
	public int compare(Proposal lhs, Proposal rhs) {
		if (isComplexSearch) {
			return (int) (lhs.getReturnArrival() - rhs.getReturnArrival());
		} else {
			return (int) (lhs.getArrival() - rhs.getArrival());
		}
	}
}
