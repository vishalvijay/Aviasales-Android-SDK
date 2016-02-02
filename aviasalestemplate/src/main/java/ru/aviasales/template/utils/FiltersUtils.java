package ru.aviasales.template.utils;

import java.util.List;

import ru.aviasales.core.http.exception.ApiException;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.ResultsSegment;

public class FiltersUtils {

	public static long calculateMinimalPriceForProposal(Proposal proposal) throws ApiException {
		Long minPrice = Long.MAX_VALUE;
		for (String key : proposal.getFiltredNativePrices().keySet()) {
			minPrice = Math.min(minPrice, proposal.getFiltredNativePrices().get(key).getUnifiedPrice());
		}
		return minPrice;
	}

	public static boolean isProposalContainIatas(Proposal proposal, List<ResultsSegment> iatas) {
		List<List<String>> airports = proposal.getSegmentsAirports();

		List<String> routeAirports;
		ResultsSegment iataSegment;
		for (int i = 0; i < airports.size(); i++) {
			routeAirports = airports.get(i);
			iataSegment = iatas.get(i);
			if (!routeAirportsEquals(routeAirports, iataSegment)) {
				return false;
			}
		}
		return true;
	}

	private static boolean routeAirportsEquals(List<String> routeAirports, ResultsSegment iataSegment) {
		return (routeAirports.get(0).equals(iataSegment.getOriginalOrigin())
				|| iataSegment.getOriginalOrigin().equals(iataSegment.getOrigin()))
				&& (routeAirports.get(1).equals(iataSegment.getOriginalDestination())
				|| iataSegment.getOriginalDestination().equals(iataSegment.getDestination()));
	}
}
