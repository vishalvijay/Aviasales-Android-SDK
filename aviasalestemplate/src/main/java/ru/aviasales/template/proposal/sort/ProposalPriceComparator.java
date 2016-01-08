package ru.aviasales.template.proposal.sort;

import java.util.Comparator;

import ru.aviasales.core.search.object.Proposal;

public class ProposalPriceComparator implements Comparator<Proposal> {
	@Override
	public int compare(Proposal lhs, Proposal rhs) {
		return (int) (lhs.getTotalWithFilters() - rhs.getTotalWithFilters());
	}
}
