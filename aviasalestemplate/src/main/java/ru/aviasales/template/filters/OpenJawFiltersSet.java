package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.http.exception.ApiException;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.ResultsSegment;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.template.utils.FiltersUtils;

public class OpenJawFiltersSet implements Serializable, FiltersSet {
	private final AgenciesFilter agenciesFilter;
	private final BaseNumericFilter priceFilter;
	private final PayTypeFilter payTypeFilter;
	private final Map<Integer, SegmentFilter> segmentFilters;

	public OpenJawFiltersSet(Context context) {
		agenciesFilter = new AgenciesFilter();
		priceFilter = new BaseNumericFilter();
		payTypeFilter = new PayTypeFilter(context);
		segmentFilters = new LinkedHashMap<>();
	}

	public OpenJawFiltersSet(Context context, OpenJawFiltersSet openJawFiltersSet) {
		agenciesFilter = new AgenciesFilter(openJawFiltersSet.getAgenciesFilter());
		priceFilter = new BaseNumericFilter(openJawFiltersSet.getPriceFilter());
		payTypeFilter = new PayTypeFilter(context, openJawFiltersSet.getPayTypeFilter());
		segmentFilters = new LinkedHashMap<>();
		for (Integer key : openJawFiltersSet.getSegmentFilters().keySet()) {
			segmentFilters.put(key, new SegmentFilter(context, openJawFiltersSet.getSegmentFilters().get(key)));
		}
	}

	@Override
	public synchronized List<Proposal> applyFilters(SearchData searchData) {
		List<Proposal> proposals = searchData.getProposals();
		Map<String, AirlineData> airlines = searchData.getAirlines();
		Map<String, GateData> gates = searchData.getGatesInfo();
		List<ResultsSegment> resultsSegments = searchData.getSegments();

		if (proposals == null) {
			return new ArrayList<>();
		}

		List<Proposal> filteredProposals = new ArrayList<>();

		if (payTypeFilter.isActive()) {
			payTypeFilter.calculateRestrictedAgencies(new ArrayList<>(gates.values()));
		} else {
			payTypeFilter.clearFilter();
		}

		for (Proposal proposal : proposals) {
			if (!agenciesFilter.isActive()) {
				proposal.setTotalWithFilters(proposal.getBestPrice());
			}
			proposal.setFilteredNativePrices(new HashMap<>(proposal.getNativePrices()));

			if (shouldAddTicketToResults(airlines, resultsSegments, proposal)) {
				filteredProposals.add(proposal);
			}
		}

		return filteredProposals;
	}

	@Override
	public void setContext(Context context) {
		for (SegmentFilter segmentFilter : segmentFilters.values()) {
			segmentFilter.setContext(context);
		}
	}

	private boolean shouldAddTicketToResults(Map<String, AirlineData> airlines, List<ResultsSegment> resultsSegments, Proposal proposal) {
		return
				isSuitedByPrice(proposal) &&
						isSuitedByAgencies(proposal) &&
						isSuitedByPayType(proposal) &&
						isSuitedBySegmentFilters(airlines, proposal);
	}

	public boolean isSuitedByPrice(Proposal proposal) {
		return !priceFilter.isActive() || priceFilter.isActual(proposal.getTotalWithFilters());
	}

	public boolean isSuitedByAgencies(Proposal proposal) {
		if (agenciesFilter.isActive()) {
			agenciesFilter.validateProposal(proposal);
			if (agenciesFilter.isActual(proposal)) {
				try {
					proposal.setTotalWithFilters(FiltersUtils.calculateMinimalPriceForProposal(proposal));
				} catch (ApiException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean isSuitedByPayType(Proposal proposal) {
		if (payTypeFilter.isActive()) {
			if (payTypeFilter.isActual(proposal)) {
				try {
					proposal.setTotalWithFilters(FiltersUtils.calculateMinimalPriceForProposal(proposal));
				} catch (ApiException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean isSuitedBySegmentFilters(Map<String, AirlineData> airlines, Proposal proposal) {
		for (int i = 0; i < proposal.getSegments().size(); i++) {
			SegmentFilter segmentFilter = segmentFilters.get(i);
			if (segmentFilter.isActive()) {
				if (!segmentFilter.isSuitedByAirline(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByAirport(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByAlliance(airlines, proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByDuration(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByOvernight(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByStopOver(proposal)
						|| !segmentFilter.isSuitedByStopOverDelay(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByTakeoffTime(proposal.getSegmentFlights(i))
						|| !segmentFilter.isSuitedByLandingTime(proposal.getSegmentFlights(i))) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isActive() {
		for (SegmentFilter segmentFilter : segmentFilters.values()) {
			if (segmentFilter.isActive()) return true;
		}

		return agenciesFilter.isActive() || priceFilter.isActive() || payTypeFilter.isActive();
	}

	@Override
	public BaseNumericFilter getPriceFilter() {
		return priceFilter;
	}

	@Override
	public List<AirlinesFilter> getAirlinesFilters() {
		List<AirlinesFilter> airlinesFilters = new ArrayList<>();
		for (SegmentFilter segmentFilter : segmentFilters.values()) {
			airlinesFilters.add(segmentFilter.getAirlinesFilter());
		}
		return airlinesFilters;
	}

	@Override
	public List<AirportsFilter> getAirportsFilters() {
		List<AirportsFilter> airportsFilters = new ArrayList<>();
		for (SegmentFilter segmentFilter : segmentFilters.values()) {
			airportsFilters.add(segmentFilter.getAirportsFilter());
		}
		return airportsFilters;
	}

	public AgenciesFilter getAgenciesFilter() {
		return agenciesFilter;
	}

	public PayTypeFilter getPayTypeFilter() {
		return payTypeFilter;
	}

	public Map<Integer, SegmentFilter> getSegmentFilters() {
		return segmentFilters;
	}

	@Override
	public synchronized void clearFilters() {
		agenciesFilter.clearFilter();
		priceFilter.clearFilter();
		payTypeFilter.clearFilter();
		for (SegmentFilter segmentFilter : segmentFilters.values()) {
			segmentFilter.clearFilter();
		}
	}

	@Override
	public synchronized void initMinAndMaxValues(Context context, SearchData searchData, List<Proposal> proposals) {
		Map<String, AirportData> airportDataMap = new HashMap<>();
		Map<String, AirlineData> airlineDataMap = new HashMap<>();
		Map<String, GateData> onlyActualGates = new HashMap<>();
		List<String> paymentMethods = new ArrayList<>();
		for (Proposal proposal : proposals) {
			priceFilter.setMaxValue((int) Math.max(priceFilter.getMaxValue(), proposal.getBestPrice()));
			priceFilter.setMinValue((int) Math.min(priceFilter.getMinValue(), proposal.getBestPrice()));
			for (String gateId : proposal.getNativePrices().keySet()) {
				String gateIdWithoutMagicFare = gateId.replace("-", "");
				if (searchData.getGateById(gateIdWithoutMagicFare) != null && !onlyActualGates.containsKey(gateIdWithoutMagicFare)) {
					onlyActualGates.put(gateIdWithoutMagicFare, searchData.getGateById(gateId));
				}
			}

			for (GateData gate : onlyActualGates.values()) {
				if (gate.getPaymentMethods() != null) {
					for (String paymentMethod : gate.getPaymentMethods()) {
						if (!paymentMethods.contains(paymentMethod)) {
							paymentMethods.add(paymentMethod);
						}
					}
				}
			}

			airportDataMap = proposal.addMissingAirportsToHashMap(airportDataMap, searchData.getAirports());
			airlineDataMap = proposal.addMissingAirlinesToHashMap(airlineDataMap, searchData.getAirlines());

			for (int segmentId = 0; segmentId < proposal.getSegments().size(); segmentId++) {
				if (!segmentFilters.containsKey(segmentId)) {
					segmentFilters.put(segmentId, new SegmentFilter(context));
				}

				segmentFilters.get(segmentId).initMinMaxValues(proposal.getSegments().get(segmentId).getFlights(),
						airportDataMap, airlineDataMap);
			}
		}
		getAgenciesFilter().addGates(onlyActualGates);
		getPayTypeFilter().setPayTypesFromGsonClass(paymentMethods);
		for (Integer segmentId : segmentFilters.keySet()) {
			segmentFilters.get(segmentId).getAirlinesFilter().sortByName();
			segmentFilters.get(segmentId).getAllianceFilter().sortByName();
			segmentFilters.get(segmentId).getAirportsFilter().sortByName();
		}
	}

	@Override
	public void mergeFiltersValues(FiltersSet filtersSet) {
		if (!(filtersSet instanceof OpenJawFiltersSet)) return;
		OpenJawFiltersSet filters = (OpenJawFiltersSet) filtersSet;

		priceFilter.mergeFilter(filters.getPriceFilter());
		payTypeFilter.mergeFilter(filters.getPayTypeFilter());
		agenciesFilter.mergeFilter(filters.getAgenciesFilter());
		for (Integer key : segmentFilters.keySet()) {
			segmentFilters.get(key).mergeFilter(filters.getSegmentFilters().get(key));
		}
	}

	@Override
	public FiltersSet getCopy(Context context) {
		return new OpenJawFiltersSet(context, this);
	}

	@Override
	public boolean isValid() {
		return priceFilter.isValid();
	}
}