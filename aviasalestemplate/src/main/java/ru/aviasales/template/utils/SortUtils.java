package ru.aviasales.template.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.aviasales.core.search.object.Proposal;

public class SortUtils {

	public static final int SORTING_BY_PRICE = 0;
	public static final int SORTING_BY_DEPARTURE = 1;
	public static final int SORTING_BY_ARRIVAL = 2;
	public static final int SORTING_BY_DEPARTURE_ON_RETURN = 3;
	public static final int SORTING_BY_ARRIVAL_ON_RETURN = 4;
	public static final int SORTING_BY_DURATION = 5;
	public static final int SORTING_BY_RATING = 6;

	private static int savedSortingType = SORTING_BY_PRICE;

	public static void sortProposals(List<Proposal> proposals, int sortingType, boolean isComplexSearch) {

		savedSortingType = sortingType;

		switch (sortingType) {
			case SORTING_BY_PRICE:
				sortByPrice(proposals);
				break;
			case SORTING_BY_DEPARTURE:
				sortByDeparture(proposals);
				break;
			case SORTING_BY_ARRIVAL:
				sortingByArrival(proposals, isComplexSearch);
				break;
			case SORTING_BY_DEPARTURE_ON_RETURN:
				soringByDepartureOnReturn(proposals);
				break;
			case SORTING_BY_ARRIVAL_ON_RETURN:
				sortingByArrivalOnReturn(proposals);
				break;
			case SORTING_BY_DURATION:
				sortingByDuration(proposals);
				break;
			case SORTING_BY_RATING:
				sortingByRating(proposals);
				break;
		}
	}

	private static void sortingByRating(final List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				double lhsRating = lhs.getRating(proposals.get(0));
				double rhsRating = rhs.getRating(proposals.get(0));
				if (lhsRating - rhsRating < 0) {
					return -1;
				} else if (lhsRating - rhsRating > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		});
	}

	private static void sortingByDuration(List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				return lhs.getDurationInMinutes() - rhs.getDurationInMinutes();
			}
		});
	}

	private static void sortingByArrivalOnReturn(List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				return (int) (lhs.getReturnArrival() - rhs.getReturnArrival());
			}
		});
	}

	private static void soringByDepartureOnReturn(List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				return (int) (lhs.getReturnDeparture() - rhs.getReturnDeparture());
			}
		});
	}

	private static void sortingByArrival(List<Proposal> proposals, final boolean isComplexSearch) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				if (isComplexSearch) {
					return (int) (lhs.getReturnArrival() - rhs.getReturnArrival());
				} else {
					return (int) (lhs.getArrival() - rhs.getArrival());
				}
			}
		});
	}

	private static void sortByDeparture(List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				return (int) (lhs.getDeparture() - rhs.getDeparture());
			}
		});
	}

	private static void sortByPrice(List<Proposal> proposals) {
		Collections.sort(proposals, new Comparator<Proposal>() {
			@Override
			public int compare(Proposal lhs, Proposal rhs) {
				return (int) (lhs.getTotalWithFilters() - rhs.getTotalWithFilters());
			}
		});
	}

	public static int getSavedSortingType() {
		return savedSortingType;
	}

	public static void resetSavedSortingType() {
		savedSortingType = SORTING_BY_PRICE;
	}
}
