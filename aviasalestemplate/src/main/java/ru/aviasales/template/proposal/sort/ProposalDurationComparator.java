package ru.aviasales.template.proposal.sort;

import java.util.Comparator;

import ru.aviasales.core.search.object.Proposal;

public class ProposalDurationComparator implements Comparator<Proposal> {
	@Override
	public int compare(Proposal lhs, Proposal rhs) {
		return lhs.getDurationInMinutes() - rhs.getDurationInMinutes();
	}
}
