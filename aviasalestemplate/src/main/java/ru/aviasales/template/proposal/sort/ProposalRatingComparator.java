package ru.aviasales.template.proposal.sort;

import java.util.Comparator;

import ru.aviasales.core.search.object.Proposal;

public class ProposalRatingComparator implements Comparator<Proposal> {
	private final Proposal cheapestProposal;

	public ProposalRatingComparator(Proposal proposal) {
		this.cheapestProposal = proposal;
	}

	@Override
	public int compare(Proposal lhs, Proposal rhs) {
		double lhsRating = lhs.getRating(cheapestProposal);
		double rhsRating = rhs.getRating(cheapestProposal);
		if (lhsRating - rhsRating < 0) {
			return -1;
		} else if (lhsRating - rhsRating > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
