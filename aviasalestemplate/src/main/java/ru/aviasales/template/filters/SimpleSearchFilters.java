package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import ru.aviasales.core.http.exception.ApiException;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.ResultsSegment;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.template.utils.FiltersUtils;

public class SimpleSearchFilters implements Serializable, FiltersSet {
	private final AgenciesFilter agenciesFilter;
	private final BaseNumericFilter priceFilter;
	private final PayTypeFilter payTypeFilter;
	private final BaseNumericFilter durationFilter;
	private final BaseNumericFilter stopOverDelayFilter;
	private final BaseNumericFilter takeoffTimeFilter;
	private final BaseNumericFilter takeoffBackTimeFilter;
	private final BaseNumericFilter landingTimeFilter;
	private final BaseNumericFilter landingBackTimeFilter;
	private final BaseNumericFilter stopOverSizeFilter;
	private final OvernightFilter overnightFilter;
	private final AllianceFilter allianceFilter;
	private final AirlinesFilter airlinesFilter;
	private final AirportsFilter airportsFilter;

	public SimpleSearchFilters(Context context) {
		agenciesFilter = new AgenciesFilter();
		priceFilter = new BaseNumericFilter();
		airlinesFilter = new AirlinesFilter();
		airportsFilter = new AirportsFilter();
		allianceFilter = new AllianceFilter(context);
		payTypeFilter = new PayTypeFilter(context);
		stopOverDelayFilter = new BaseNumericFilter();
		takeoffTimeFilter = new BaseNumericFilter();
		takeoffBackTimeFilter = new BaseNumericFilter();
		landingTimeFilter = new BaseNumericFilter();
		landingBackTimeFilter = new BaseNumericFilter();
		stopOverSizeFilter = new BaseNumericFilter();
		overnightFilter = new OvernightFilter();
		durationFilter = new BaseNumericFilter();
	}

	public SimpleSearchFilters(Context context, SimpleSearchFilters generalFilter) {
		airlinesFilter = new AirlinesFilter(generalFilter.getAirlinesFilter());
		airportsFilter = new AirportsFilter(generalFilter.getAirportsFilter());
		agenciesFilter = new AgenciesFilter(generalFilter.getAgenciesFilter());
		priceFilter = new BaseNumericFilter(generalFilter.getPriceFilter());
		allianceFilter = new AllianceFilter(context, generalFilter.getAllianceFilter());
		payTypeFilter = new PayTypeFilter(context, generalFilter.getPayTypeFilter());
		durationFilter = new BaseNumericFilter(generalFilter.getDurationFilter());
		stopOverDelayFilter = new BaseNumericFilter(generalFilter.getStopOverDelayFilter());
		takeoffTimeFilter = new BaseNumericFilter(generalFilter.getTakeoffTimeFilter());
		landingTimeFilter = new BaseNumericFilter(generalFilter.getLandingTimeFilter());
		landingBackTimeFilter = new BaseNumericFilter(generalFilter.getLandingBackTimeFilter());
		takeoffBackTimeFilter = new BaseNumericFilter(generalFilter.getTakeoffBackTimeFilter());
		stopOverSizeFilter = new BaseNumericFilter(generalFilter.getStopOverSizeFilter());
		overnightFilter = new OvernightFilter(generalFilter.getOvernightFilter());
	}

	@Override
	public synchronized List<Proposal> applyFilters(ru.aviasales.core.search.object.SearchData searchData) {
		List<Proposal> proposals = searchData.getProposals();
		Map<String, AirlineData> airlines = searchData.getAirlines();
		Map<String, GateData> gates = searchData.getGatesInfo();
		List<ResultsSegment> resultsSegments = searchData.getSegments();

		if (proposals == null) {
			return new ArrayList<Proposal>();
		}

		List<Proposal> filteredProposals = new ArrayList<Proposal>();

		if (payTypeFilter.isActive()) {
			payTypeFilter.calculateRestrictedAgencies(new ArrayList<GateData>(gates.values()));
		} else {
			payTypeFilter.clearFilter();
		}

		for (Proposal proposal : proposals) {
			if (!agenciesFilter.isActive()) {
				proposal.setTotalWithFilters(proposal.getBestPrice());
			}
			proposal.setFilteredNativePrices(proposal.getNativePrices());

			if (shouldAddTicketToResults(airlines, resultsSegments, proposal)) {
				filteredProposals.add(proposal);
			}
		}

		return filteredProposals;
	}

	@Override
	public void setContext(Context context) {
		allianceFilter.setContext(context);
	}

	private boolean shouldAddTicketToResults(Map<String, AirlineData> airlines,
	                                         List<ResultsSegment> segments,
	                                         Proposal proposal) {
		return
				isSuitedByDuration(proposal) &&
						isSuitedByPrice(proposal) &&
						isSuitedByStopOverDelay(proposal) &&
						isSuitedByTakeoffBackTime(proposal) &&
						isSuitedByTakeoffTime(proposal) &&
						isSuitedByLandingTime(proposal) &&
						isSuitedByLandingBackTime(proposal) &&
						isSuitedByStopOver(proposal) &&
						isSuitedByAirline(proposal) &&
						isSuitedByAlliance(airlines, proposal) &&
						isSuitedByAirport(proposal) &&
						isSuitedByOvernight(proposal) &&
						isSuitedByAgencies(proposal) &&
						isSuitedByPayType(proposal);
	}

	public boolean isSuitedByStopOver(Proposal proposal) {
		if (stopOverSizeFilter.isActive()) {
			boolean actualDirect;
			boolean actualReturn;
			actualDirect = stopOverSizeFilter.isActual(proposal.getSegmentFlights(0).size() - 1);
			if (proposal.getSegments().size() == 2) {
				actualReturn = stopOverSizeFilter.isActual(proposal.getSegmentFlights(1).size() - 1);
				return actualDirect && actualReturn;
			}
			return actualDirect;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAlliance(Map<String, AirlineData> airlines, Proposal proposal) {
		if (allianceFilter.isActive()) {
			for (Flight flight : proposal.getAllFlights()) {
				if (!allianceFilter.isActual(airlines.get(flight.getOperatingCarrier()).getAllianceName())) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirport(Proposal proposal) {
		if (airportsFilter.isActive()) {
			for (Flight flight : proposal.getAllFlights()) {
				if (!airportsFilter.isActual(flight.getDeparture()) || !airportsFilter.isActual(flight.getArrival())) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirline(Proposal proposal) {
		return !airlinesFilter.isActive() || airlinesFilter.isActual(proposal.getValidatingCarrier());
	}

	public boolean isSuitedByTakeoffTime(Proposal proposal) {
		if (takeoffTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int takeoffTime;
			calendar.setTimeInMillis(proposal.getSegmentFlights(0).get(0).getLocalDepartureTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return takeoffTimeFilter.isActual(takeoffTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByTakeoffBackTime(Proposal proposal) {
		if (takeoffBackTimeFilter.isActive() && proposal.getSegments().size() >= 2) {
			Calendar calendar = Calendar.getInstance();
			int takeoffBackTime;
			calendar.setTimeInMillis(proposal.getSegmentFlights(1).get(0).getLocalDepartureTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffBackTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return takeoffBackTimeFilter.isActual(takeoffBackTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByLandingTime(Proposal proposal) {
		if (landingTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int landingTime;
			calendar.setTimeInMillis(proposal.getSegmentFlights(0).get(proposal.getSegmentFlights(0).size() - 1).getLocalArrivalTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			landingTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return landingTimeFilter.isActual(landingTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByLandingBackTime(Proposal proposal) {
		if (landingBackTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int landingBackTime;
			calendar.setTimeInMillis(proposal.getSegmentFlights(1).get(proposal.getSegmentFlights(1).size() - 1).getLocalArrivalTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			landingBackTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return landingBackTimeFilter.isActual(landingBackTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByOvernight(Proposal proposal) {
		if (overnightFilter.isActive()) {
			boolean actualDirect = true;
			boolean actualReturn = true;
			actualDirect = overnightFilter.isActual(proposal.getSegmentFlights(0));
			if (proposal.getSegments().size() == 2) {
				actualReturn = overnightFilter.isActual(proposal.getSegmentFlights(1));
			}
			return actualDirect && actualReturn;
		} else {
			return true;
		}
	}

	public boolean isSuitedByStopOverDelay(Proposal proposal) {
		if (stopOverDelayFilter.isActive()) {
			Map<String, Integer> directMinMaxStopOverDelay = proposal.getDirectMinAndMaxStopOverDurationInMinutes();
			if (!stopOverDelayFilter.isActualForMaxValue(directMinMaxStopOverDelay.get(Proposal.MAX))
					|| !stopOverDelayFilter.isActualForMinValue(directMinMaxStopOverDelay.get(Proposal.MIN))) {
				return false;
			}

			if (proposal.getSegments().size() == 2) {
				Map<String, Integer> returnMinMaxStopOverDelay = proposal.getReturnMinAndMaxStopOverDurationInMinutes();
				if (!stopOverDelayFilter.isActualForMaxValue(returnMinMaxStopOverDelay.get(Proposal.MAX))
						|| !stopOverDelayFilter.isActualForMinValue(returnMinMaxStopOverDelay.get(Proposal.MIN))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByDuration(Proposal proposal) {
		if (durationFilter.isActive()) {

			int directDuration = 0;
			int returnDuration = 0;
			for (Flight flight : proposal.getSegmentFlights(0)) {
				directDuration += flight.getDuration() + flight.getDelay();
			}

			if (proposal.getSegments().size() > 1) {
				for (Flight flight : proposal.getSegmentFlights(1)) {
					returnDuration += flight.getDuration() + flight.getDelay();
				}
			}

			return durationFilter.isActual(directDuration) && durationFilter.isActual(returnDuration == 0 ? directDuration : returnDuration);
		} else {
			return true;
		}
	}

	public boolean isSuitedByPrice(Proposal proposal) {
		return !priceFilter.isActive() || priceFilter.isActual(proposal.getTotalWithFilters());
	}

	public boolean isSuitedByAgencies(Proposal proposal) {
		if (agenciesFilter.isActive()) {
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

	@Override
	public boolean isActive() {
		return agenciesFilter.isActive() || priceFilter.isActive() || payTypeFilter.isActive() ||
				airlinesFilter.isActive() || airportsFilter.isActive() || allianceFilter.isActive() ||
				durationFilter.isActive() || takeoffTimeFilter.isActive() || stopOverDelayFilter.isActive() ||
				overnightFilter.isActive() || landingTimeFilter.isActive() || landingBackTimeFilter.isActive() ||
				takeoffBackTimeFilter.isActive() || stopOverSizeFilter.isActive();
	}

	public AirlinesFilter getAirlinesFilter() {
		return airlinesFilter;
	}

	public AirportsFilter getAirportsFilter() {
		return airportsFilter;
	}

	public BaseNumericFilter getDurationFilter() {
		return durationFilter;
	}

	@Override
	public BaseNumericFilter getPriceFilter() {
		return priceFilter;
	}

	@Override
	public List<AirlinesFilter> getAirlinesFilters() {
		return new ArrayList<>(Collections.singletonList(airlinesFilter));
	}

	@Override
	public List<AirportsFilter> getAirportsFilters() {
		return new ArrayList<>(Collections.singletonList(airportsFilter));
	}

	public BaseNumericFilter getStopOverDelayFilter() {
		return stopOverDelayFilter;
	}

	public BaseNumericFilter getTakeoffTimeFilter() {
		return takeoffTimeFilter;
	}

	public BaseNumericFilter getLandingTimeFilter() {
		return landingTimeFilter;
	}

	public BaseNumericFilter getLandingBackTimeFilter() {
		return landingBackTimeFilter;
	}

	public BaseNumericFilter getTakeoffBackTimeFilter() {
		return takeoffBackTimeFilter;
	}

	public AllianceFilter getAllianceFilter() {
		return allianceFilter;
	}

	public AgenciesFilter getAgenciesFilter() {
		return agenciesFilter;
	}

	public OvernightFilter getOvernightFilter() {
		return overnightFilter;
	}

	public PayTypeFilter getPayTypeFilter() {
		return payTypeFilter;
	}

	@Override
	public synchronized void clearFilters() {
		airlinesFilter.clearFilter();
		airportsFilter.clearFilter();
		allianceFilter.clearFilter();
		agenciesFilter.clearFilter();
		priceFilter.clearFilter();
		payTypeFilter.clearFilter();
		durationFilter.clearFilter();
		stopOverDelayFilter.clearFilter();
		takeoffTimeFilter.clearFilter();
		takeoffBackTimeFilter.clearFilter();
		landingTimeFilter.clearFilter();
		landingBackTimeFilter.clearFilter();
		stopOverSizeFilter.clearFilter();
		overnightFilter.clearFilter();
	}

	@Override
	public synchronized void initMinAndMaxValues(Context context, SearchData searchData, List<Proposal> proposals) {
		Map<String, AirportData> airportDataMap = new HashMap<String, AirportData>();
		Map<String, AirlineData> airlineDataMap = new HashMap<String, AirlineData>();
		Map<String, GateData> onlyActualGates = new HashMap<>();
		List<String> paymentMethods = new ArrayList<String>();
		for (Proposal proposal : proposals) {
			priceFilter.setMaxValue((int) Math.max(priceFilter.getMaxValue(), proposal.getBestPrice()));
			priceFilter.setMinValue((int) Math.min(priceFilter.getMinValue(), proposal.getBestPrice()));

			stopOverSizeFilter.setMaxValue(Math.max(stopOverSizeFilter.getMaxValue(), proposal.getMaxStops()));
			stopOverSizeFilter.setMinValue(Math.min(stopOverSizeFilter.getMinValue(), proposal.getMaxStops()));

			takeoffTimeFilter.setMinValue(Math.min(takeoffTimeFilter.getMinValue(),
					proposal.getSegmentFlights(0).get(0).getDepartureInMinutesFromDayBeginning()));
			takeoffTimeFilter.setMaxValue(Math.max(takeoffTimeFilter.getMaxValue(),
					proposal.getSegmentFlights(0).get(0).getDepartureInMinutesFromDayBeginning()));
			landingTimeFilter.setMinValue(Math.min(landingTimeFilter.getMinValue(),
					proposal.getSegmentFlights(0).get(proposal.getSegmentFlights(0).size() - 1).getArrivaInMinutesFromDayBeginning()));
			landingTimeFilter.setMaxValue(Math.max(landingTimeFilter.getMaxValue(),
					proposal.getSegmentFlights(0).get(proposal.getSegmentFlights(0).size() - 1).getArrivaInMinutesFromDayBeginning()));

			if (proposal.getSegments().size() >= 2) {
				takeoffBackTimeFilter.setMinValue(Math.min(takeoffBackTimeFilter.getMinValue(),
						proposal.getSegmentFlights(1).get(0).getDepartureInMinutesFromDayBeginning()));
				takeoffBackTimeFilter.setMaxValue(Math.max(takeoffBackTimeFilter.getMaxValue(),
						proposal.getSegmentFlights(1).get(0).getDepartureInMinutesFromDayBeginning()));
				landingBackTimeFilter.setMinValue(Math.min(landingBackTimeFilter.getMinValue(),
						proposal.getSegmentFlights(1).get(proposal.getSegmentFlights(1).size() - 1).getArrivaInMinutesFromDayBeginning()));
				landingBackTimeFilter.setMaxValue(Math.max(landingBackTimeFilter.getMaxValue(),
						proposal.getSegmentFlights(1).get(proposal.getSegmentFlights(1).size() - 1).getArrivaInMinutesFromDayBeginning()));
			}

			durationFilter.setMaxValue(Math.max(durationFilter.getMaxValue(), proposal.getDirectDurationInMinutes()));
			durationFilter.setMinValue(Math.min(durationFilter.getMinValue(), proposal.getDirectDurationInMinutes()));

			if (proposal.getSegments().size() >= 2) {
				durationFilter.setMaxValue(Math.max(durationFilter.getMaxValue(), proposal.getReturnDurationInMinutes()));
				durationFilter.setMinValue(Math.min(durationFilter.getMinValue(), proposal.getReturnDurationInMinutes()));
			}

			Map<String, Integer> stopOverMinMaxDelay = proposal.getDirectMinAndMaxStopOverDurationInMinutes();
			stopOverDelayFilter.setMaxValue(Math.max(stopOverDelayFilter.getMaxValue(), stopOverMinMaxDelay.get(Proposal.MAX)));
			stopOverDelayFilter.setMinValue(Math.min(stopOverDelayFilter.getMinValue(), stopOverMinMaxDelay.get(Proposal.MIN)));

			if (proposal.getSegments().size() >= 2) {
				Map<String, Integer> stopOverMinMaxReturnDuration = proposal.getReturnMinAndMaxStopOverDurationInMinutes();
				stopOverDelayFilter.setMaxValue(Math.max(stopOverDelayFilter.getMaxValue(), stopOverMinMaxReturnDuration.get(Proposal.MAX)));
				stopOverDelayFilter.setMinValue(Math.min(stopOverDelayFilter.getMinValue(), stopOverMinMaxReturnDuration.get(Proposal.MIN)));
			}

			for (String gateId : proposal.getNativePrices().keySet()) {
				String gateIdWithoutMagicFare = gateId.replace("-", "");
				if (searchData.getGateById(gateId) != null && !onlyActualGates.containsKey(gateIdWithoutMagicFare)) {
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

			if (!overnightFilter.isAirportOvernightViewEnabled() && overnightFilter.hasOvernight(proposal.getSegmentFlights(0))) {
				overnightFilter.setAirportOvernightEnabled(true);
			}

			if (!overnightFilter.isAirportOvernightAvailable() && proposal.getSegments().size() >= 2 &&
					overnightFilter.hasOvernight(proposal.getSegmentFlights(1))) {
				overnightFilter.setAirportOvernightEnabled(true);
			}
		}

		getAirportsFilter().setSectionedAirportsFromGsonClassSimple(airportDataMap, searchData.getProposals());
		getAllianceFilter().setAlliancesFromGsonClass(airlineDataMap);
		getAirlinesFilter().setAirlinesFromGsonClass(airlineDataMap);
		getAgenciesFilter().setGatesFromGsonClass(onlyActualGates);

		getAirlinesFilter().sortByName();
		getAllianceFilter().sortByName();
		getAirportsFilter().sortByName();
		getPayTypeFilter().setPayTypesFromGsonClass(paymentMethods);
	}

	@Override
	public FiltersSet getCopy(Context context) {
		return new SimpleSearchFilters(context, this);
	}

	@Override
	public void mergeFiltersValues(FiltersSet filtersSet) {
		if (!(filtersSet instanceof SimpleSearchFilters)) return;
		SimpleSearchFilters filters = (SimpleSearchFilters) filtersSet;

		priceFilter.mergeFilter(filters.getPriceFilter());
		priceFilter.setCurrentMinValue(priceFilter.getMinValue());
		durationFilter.mergeFilter(filters.getDurationFilter());
		durationFilter.setCurrentMinValue(durationFilter.getMinValue());
		stopOverDelayFilter.mergeFilter(filters.getStopOverDelayFilter());
		takeoffTimeFilter.mergeFilter(filters.getTakeoffTimeFilter());
		takeoffBackTimeFilter.mergeFilter(filters.getTakeoffBackTimeFilter());
		landingTimeFilter.mergeFilter(filters.getLandingTimeFilter());
		landingBackTimeFilter.mergeFilter(filters.getLandingBackTimeFilter());
		stopOverSizeFilter.mergeFilter(filters.getStopOverSizeFilter());
		stopOverSizeFilter.setCurrentMinValue(stopOverSizeFilter.getMinValue());
		overnightFilter.mergeFilter(filters.getOvernightFilter());
		payTypeFilter.mergeFilter(filters.getPayTypeFilter());
		allianceFilter.mergeFilter(filters.getAllianceFilter());
		airlinesFilter.mergeFilter(filters.getAirlinesFilter());
		airportsFilter.mergeFilter(filters.getAirportsFilter());
		agenciesFilter.mergeFilter(filters.getAgenciesFilter());
	}

	@Override
	public boolean isValid() {
		return priceFilter.isValid();
	}

	public BaseNumericFilter getStopOverSizeFilter() {
		return stopOverSizeFilter;
	}
}