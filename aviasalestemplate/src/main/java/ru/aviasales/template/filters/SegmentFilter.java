package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import ru.aviasales.core.legacy.search.object.TicketData;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.core.search.object.Proposal;

public class SegmentFilter implements Serializable {
	public static final String MAX = "max";
	public static final String MIN = "min";

	private final BaseNumericFilter durationFilter;
	private final BaseNumericFilter stopOverDelayFilter;
	private final BaseNumericFilter landingTimeFilter;
	private final BaseNumericFilter takeoffTimeFilter;
	private final BaseNumericFilter stopOverCountFilter;
	private final OvernightFilter overnightFilter;
	private final AllianceFilter allianceFilter;
	private final AirlinesFilter airlinesFilter;
	private final AirportsFilter airportsFilter;

	public SegmentFilter(Context context) {
		airlinesFilter = new AirlinesFilter();
		airportsFilter = new AirportsFilter();
		allianceFilter = new AllianceFilter(context);
		stopOverDelayFilter = new BaseNumericFilter();
		takeoffTimeFilter = new BaseNumericFilter();
		landingTimeFilter = new BaseNumericFilter();
		stopOverCountFilter = new BaseNumericFilter();
		overnightFilter = new OvernightFilter();
		durationFilter = new BaseNumericFilter();
	}

	public SegmentFilter(Context context, SegmentFilter segmentFilter) {
		airlinesFilter = new AirlinesFilter(segmentFilter.getAirlinesFilter());
		airportsFilter = new AirportsFilter(segmentFilter.getAirportsFilter());
		allianceFilter = new AllianceFilter(context, segmentFilter.getAllianceFilter());
		durationFilter = new BaseNumericFilter(segmentFilter.getDurationFilter());
		stopOverDelayFilter = new BaseNumericFilter(segmentFilter.getStopOverDelayFilter());
		takeoffTimeFilter = new BaseNumericFilter(segmentFilter.getTakeoffTimeFilter());
		landingTimeFilter = new BaseNumericFilter(segmentFilter.getLandingTimeFilter());
		overnightFilter = new OvernightFilter(segmentFilter.getOvernightFilter());
		stopOverCountFilter = new BaseNumericFilter(segmentFilter.getStopOverCountFilter());
	}

	public BaseNumericFilter getDurationFilter() {
		return durationFilter;
	}

	public BaseNumericFilter getStopOverDelayFilter() {
		return stopOverDelayFilter;
	}

	public BaseNumericFilter getTakeoffTimeFilter() {
		return takeoffTimeFilter;
	}

	public BaseNumericFilter getStopOverCountFilter() {
		return stopOverCountFilter;
	}

	public OvernightFilter getOvernightFilter() {
		return overnightFilter;
	}

	public AllianceFilter getAllianceFilter() {
		return allianceFilter;
	}

	public AirlinesFilter getAirlinesFilter() {
		return airlinesFilter;
	}

	public AirportsFilter getAirportsFilter() {
		return airportsFilter;
	}

	public BaseNumericFilter getLandingTimeFilter() {
		return landingTimeFilter;
	}

	public void setContext(Context context) {
		allianceFilter.setContext(context);
	}

	public boolean isSuitedByStopOver(Proposal proposal) {
		if (stopOverCountFilter.isActive()) {
			boolean actual = true;
			actual = stopOverCountFilter.isActual(proposal.getMaxStops());
			return actual;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAlliance(Map<String, AirlineData> airlines, List<Flight> flights) {
		if (allianceFilter.isActive()) {
			for (Flight flight : flights) {
				if (!allianceFilter.isActual(airlines.get(flight.getOperatingCarrier()).getAllianceName())) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirport(List<Flight> flights) {
		if (airportsFilter.isActive()) {
			for (Flight flight : flights) {
				if (!airportsFilter.isActual(flight.getDeparture()) || !airportsFilter.isActual(flight.getArrival())) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirline(List<Flight> flights) {
		if (airlinesFilter.isActive()) {
			for (Flight flight : flights) {
				if (!airlinesFilter.isActual(flight.getOperatingCarrier())) return false;
			}
		} else {
			return true;
		}
		return true;
	}

	public boolean isSuitedByTakeoffTime(List<Flight> flights) {
		if (takeoffTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int takeoffTime;
			calendar.setTimeInMillis(flights.get(0).getLocalDepartureTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return takeoffTimeFilter.isActual(takeoffTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByLandingTime(List<Flight> flights) {
		if (landingTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int takeoffTime;
			calendar.setTimeInMillis(flights.get(0).getLocalArrivalTimestamp() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return landingTimeFilter.isActual(takeoffTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByOvernight(List<Flight> flights) {
		return !overnightFilter.isActive() || overnightFilter.isActual(flights);
	}

	public boolean isSuitedByStopOverDelay(List<Flight> flights) {
		if (stopOverDelayFilter.isActive()) {
			Map<String, Integer> directMinMaxStopOverDelay = getDirectMinAndMaxStopOverDurationInMinutes(flights);
			return !(!stopOverDelayFilter.isActualForMaxValue(directMinMaxStopOverDelay.get(TicketData.MAX))
					|| !stopOverDelayFilter.isActualForMinValue(directMinMaxStopOverDelay.get(TicketData.MIN)));

		} else {
			return true;
		}
	}

	public boolean isSuitedByDuration(List<Flight> flights) {
		if (durationFilter.isActive()) {

			int directDuration = 0;
			for (Flight flight : flights) {
				directDuration += flight.getDuration() + flight.getDelay();
			}

			return durationFilter.isActual(directDuration);
		} else {
			return true;
		}
	}

	public boolean isActive() {
		return airlinesFilter.isActive() || airportsFilter.isActive() ||
				durationFilter.isActive() || stopOverDelayFilter.isActive() ||
				takeoffTimeFilter.isActive() || allianceFilter.isActive() ||
				stopOverCountFilter.isActive() || overnightFilter.isActive() ||
				landingTimeFilter.isActive();
	}

	public void clearFilter() {
		durationFilter.clearFilter();
		stopOverDelayFilter.clearFilter();
		takeoffTimeFilter.clearFilter();
		landingTimeFilter.clearFilter();
		stopOverCountFilter.clearFilter();
		overnightFilter.clearFilter();
		allianceFilter.clearFilter();
		airlinesFilter.clearFilter();
		airportsFilter.clearFilter();
	}

	public void initMinMaxValues(List<Flight> flights, Map<String, AirportData> airportDataMap,
	                             Map<String, AirlineData> airlineDataMap) {
		takeoffTimeFilter.setMinValue(Math.min(takeoffTimeFilter.getMinValue(),
				flights.get(0).getDepartureInMinutesFromDayBeginning()));
		takeoffTimeFilter.setMaxValue(Math.max(takeoffTimeFilter.getMaxValue(),
				flights.get(0).getDepartureInMinutesFromDayBeginning()));

		landingTimeFilter.setMinValue(Math.min(landingTimeFilter.getMinValue(),
				flights.get(0).getArrivaInMinutesFromDayBeginning()));

		landingTimeFilter.setMaxValue(Math.max(landingTimeFilter.getMaxValue(),
				flights.get(0).getArrivaInMinutesFromDayBeginning()));

		if (!overnightFilter.isAirportOvernightViewEnabled() && overnightFilter.hasOvernight(flights)) {
			overnightFilter.setAirportOvernightEnabled(true);
		}

		int segmentDurationInMinutes = calculateDurationInMinutes(flights);

		stopOverCountFilter.setMaxValue(Math.max(stopOverCountFilter.getMaxValue(), flights.size() - 1));
		stopOverCountFilter.setMinValue(Math.min(stopOverCountFilter.getMinValue(), flights.size() - 1));

		durationFilter.setMaxValue(Math.max(durationFilter.getMaxValue(), segmentDurationInMinutes));
		durationFilter.setMinValue(Math.min(durationFilter.getMinValue(), segmentDurationInMinutes));

		Map<String, Integer> stopOverMinMaxDelay = getMinMaxStopOverDuration(flights);
		stopOverDelayFilter.setMaxValue(Math.max(stopOverDelayFilter.getMaxValue(), stopOverMinMaxDelay.get(TicketData.MAX)));
		stopOverDelayFilter.setMinValue(Math.min(stopOverDelayFilter.getMinValue(), stopOverMinMaxDelay.get(TicketData.MIN)));

		airportsFilter.setSectionedAirportsFromGsonClass(airportDataMap, flights);
		allianceFilter.setAlliancesFromGsonClass(airlineDataMap, flights);
		airlinesFilter.setAirlinesFromGsonClass(airlineDataMap, flights);
	}

	public Map<String, Integer> getDirectMinAndMaxStopOverDurationInMinutes(List<Flight> flights) {
		Map<String, Integer> minMaxStopOverDurationHashMap = new HashMap<String, Integer>();

		minMaxStopOverDurationHashMap.put(MIN, Integer.MAX_VALUE);
		minMaxStopOverDurationHashMap.put(MAX, Integer.MIN_VALUE);

		for (int i = 1; i < flights.size(); i++) {
			minMaxStopOverDurationHashMap.put(MIN, Math.min(minMaxStopOverDurationHashMap.get(MIN),
					flights.get(i).getDepartureInMinutes().intValue() - flights.get(i - 1).getArrivalInMinutes().intValue()));
			minMaxStopOverDurationHashMap.put(MAX, Math.max(minMaxStopOverDurationHashMap.get(MAX),
					flights.get(i).getDepartureInMinutes().intValue() - flights.get(i - 1).getArrivalInMinutes().intValue()));
		}

		return minMaxStopOverDurationHashMap;
	}

	public Map<String, Integer> getMinMaxStopOverDuration(List<Flight> flights) {
		Map<String, Integer> minMaxStopOverDurationHashMap = new HashMap<String, Integer>();

		minMaxStopOverDurationHashMap.put(MIN, Integer.MAX_VALUE);
		minMaxStopOverDurationHashMap.put(MAX, Integer.MIN_VALUE);

		for (int i = 1; i < flights.size(); i++) {
			minMaxStopOverDurationHashMap.put(MIN, Math.min(minMaxStopOverDurationHashMap.get(MIN),
					flights.get(i).getDepartureInMinutes().intValue() - flights.get(i - 1).getArrivalInMinutes().intValue()));
			minMaxStopOverDurationHashMap.put(MAX, Math.max(minMaxStopOverDurationHashMap.get(MAX),
					flights.get(i).getDepartureInMinutes().intValue() - flights.get(i - 1).getArrivalInMinutes().intValue()));
		}

		return minMaxStopOverDurationHashMap;
	}

	public void mergeFilter(SegmentFilter segmentFilter) {
		allianceFilter.mergeFilter(segmentFilter.getAllianceFilter());
		airlinesFilter.mergeFilter(segmentFilter.getAirlinesFilter());
		airportsFilter.mergeFilter(segmentFilter.getAirportsFilter());
		durationFilter.mergeFilter(segmentFilter.getDurationFilter());
		stopOverDelayFilter.mergeFilter(segmentFilter.getStopOverDelayFilter());
		takeoffTimeFilter.mergeFilter(segmentFilter.getTakeoffTimeFilter());
		landingTimeFilter.mergeFilter(segmentFilter.getLandingTimeFilter());
		stopOverCountFilter.mergeFilter(segmentFilter.getStopOverCountFilter());
		overnightFilter.mergeFilter(segmentFilter.getOvernightFilter());
	}


	private int calculateDurationInMinutes(List<Flight> flights) {
		int duration = 0;
		for (int i = 0; i < flights.size(); i++) {
			duration += flights.get(i).getDuration();
			if (i != 0) {
				duration += flights.get(i).getDepartureInMinutes() - flights.get(i - 1).getArrivalInMinutes();
			}
		}
		return duration;
	}
}
