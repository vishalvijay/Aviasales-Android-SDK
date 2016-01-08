package ru.aviasales.template.filters;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import ru.aviasales.core.http.exception.ApiException;
import ru.aviasales.core.legacy.search.object.OldFlightData;
import ru.aviasales.core.legacy.search.object.OldSearchData;
import ru.aviasales.core.legacy.search.object.TicketData;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.utils.CoreAviasalesUtils;

public class GeneralFilter implements Parcelable {
	private boolean isActive;
	private AirlinesFilter airlinesFilter;
	private AirportsFilter airportsFilter;
	private AgenciesFilter agenciesFilter;
	private BaseNumericFilter durationFilter;
	private BaseNumericFilter priceFilter;
	private BaseNumericFilter stopOverDelayFilter;
	private BaseNumericFilter takeoffTimeFilter;
	private BaseNumericFilter takeoffBackTimeFilter;
	private AllianceFilter allianceFilter;
	private StopOverFilter stopOverFilter;
	private OvernightFilter overnightFilter;

	List<GateData> gatesWithoutMobileVersion;

	public GeneralFilter(Context context) {
		airlinesFilter = new AirlinesFilter();
		airportsFilter = new AirportsFilter();
		agenciesFilter = new AgenciesFilter();
		durationFilter = new BaseNumericFilter();
		priceFilter = new BaseNumericFilter();
		stopOverDelayFilter = new BaseNumericFilter();
		takeoffTimeFilter = new BaseNumericFilter();
		takeoffBackTimeFilter = new BaseNumericFilter();
		allianceFilter = new AllianceFilter(context);
		stopOverFilter = new StopOverFilter();
		overnightFilter = new OvernightFilter();
	}

	public GeneralFilter(Context context, GeneralFilter generalFilter) {
		isActive = generalFilter.isActive();
		airlinesFilter = new AirlinesFilter(generalFilter.getAirlinesFilter());
		airportsFilter = new AirportsFilter(generalFilter.getAirportsFilter());
		agenciesFilter = new AgenciesFilter(generalFilter.getAgenciesFilter());
		durationFilter = new BaseNumericFilter(generalFilter.getDurationFilter());
		priceFilter = new BaseNumericFilter(generalFilter.getPriceFilter());
		stopOverDelayFilter = new BaseNumericFilter(generalFilter.getStopOverDelayFilter());
		takeoffTimeFilter = new BaseNumericFilter(generalFilter.getTakeoffTimeFilter());
		takeoffBackTimeFilter = new BaseNumericFilter(generalFilter.getTakeoffBackTimeFilter());
		allianceFilter = new AllianceFilter(context, generalFilter.getAllianceFilter());
		stopOverFilter = new StopOverFilter(generalFilter.getStopOverFilter());
		overnightFilter = new OvernightFilter(generalFilter.getOvernightFilter());
	}

	public void init(OldSearchData searchData, PreInitializeFilters preInitializeFilters) {
		initMinAndMaxValues(searchData);
		clearFilters();

		getStopOverFilter().setMinPrices(preInitializeFilters.getWithoutStopOverMinPrice(),
				preInitializeFilters.getOneStopOverMinPrice(),
				preInitializeFilters.getTwoPlusStopOverMinPrice());

		getOvernightFilter().setAirportOvernightEnabled(preInitializeFilters.isAirportOvernightEnabled());

	}

	public synchronized List<TicketData> applyFilters(OldSearchData searchData) {
		if (searchData == null || searchData.getTickets() == null) {
			return new ArrayList<TicketData>();
		}

		List<TicketData> filteredTickets = new ArrayList<TicketData>();

		setGatesWithoutMobileVersion(searchData);

		for (TicketData ticketData : searchData.getTickets()) {
			if (!agenciesFilter.isActive()) {
				ticketData.setTotalWithFilters(ticketData.getTotal() + 0.);
			}
			ticketData.setFilteredNativePrices(ticketData.getNativePrices());

			if (shouldAddTicketToResults(searchData, ticketData)) {
				filteredTickets.add(ticketData);
			}
		}

		return filteredTickets;
	}

	private void setGatesWithoutMobileVersion(OldSearchData searchData) {
		if (gatesWithoutMobileVersion == null) {
			gatesWithoutMobileVersion = new ArrayList<GateData>();
			for (GateData gateData : searchData.getGatesInfo()) {
				if (!gateData.hasMobileVersion()) {
					gatesWithoutMobileVersion.add(gateData);
				}
			}
		}
	}

	private boolean shouldAddTicketToResults(OldSearchData searchData, TicketData ticketData) {
		return isSuitedByDuration(ticketData) &&
				isSuitedByPrice(ticketData) &&
				isSuitedByStopOverDelay(ticketData) &&
				isSuitedByTakeoffBackTime(ticketData) &&
				isSuitedByTakeoffTime(ticketData) &&
				isSuitedByStopOver(ticketData) &&
				isSuitedByAirline(ticketData) &&
				isSuitedByAlliance(searchData, ticketData) &&
				isSuitedByAirport(ticketData) &&
				isSuitedByOvernight(ticketData) &&
				isSuitedByAgencies(searchData, ticketData);
	}

	public boolean isSuitedByStopOver(TicketData ticketData) {
		if (stopOverFilter.isActive()) {
			boolean actualDirect = true;
			boolean actualReturn = true;
			actualDirect = stopOverFilter.isActual(ticketData.getDirectFlights());
			if (ticketData.getReturnFlights() != null) {
				actualReturn = stopOverFilter.isActual(ticketData.getReturnFlights());
				return actualDirect && actualReturn;
			}
			return actualDirect;
		} else {
			return true;
		}
	}

	public boolean isSuitedByStopOverForMinPrice(TicketData ticketData) {
		if (stopOverFilter.isActive()) {
			boolean actualDirect = true;
			boolean actualReturn = true;
			actualDirect = stopOverFilter.isActual(ticketData.getDirectFlights());
			if (ticketData.getReturnFlights() != null) {
				actualReturn = stopOverFilter.isActual(ticketData.getReturnFlights());
				return actualDirect || actualReturn;
			}
			return actualDirect;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAlliance(OldSearchData searchData, TicketData ticketData) {
		if (allianceFilter.isActive()) {
			for (OldFlightData flightData : ticketData.getAllFlights()) {
				if (!allianceFilter.isActual(searchData.getAirlines().get(flightData.getAirline()).getAllianceName())) {
					return false;
				}
			}
			return true;
//			return allianceFilter.isActual(searchData.getAirlines().get(ticketData.getMainAirline()).getAllianceName());
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirport(TicketData ticketData) {
		if (airportsFilter.isActive()) {
			for (OldFlightData flightData : ticketData.getAllFlights()) {
				if (!airportsFilter.isActual(flightData.getOrigin()) || !airportsFilter.isActual(flightData.getDestination())) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByAirline(TicketData ticketData) {
		if (airlinesFilter.isActive()) {
			return airlinesFilter.isActual(ticketData.getMainAirline());
		} else {
			return true;
		}
	}

	public boolean isSuitedByTakeoffTime(TicketData ticketData) {
		if (takeoffTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int takeoffTime;
			calendar.setTimeInMillis(ticketData.getDirectFlights().get(0).getDeparture() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return takeoffTimeFilter.isActual(takeoffTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByTakeoffBackTime(TicketData ticketData) {
		if (takeoffBackTimeFilter.isActive()) {
			Calendar calendar = Calendar.getInstance();
			int takeoffBackTime;
			calendar.setTimeInMillis(ticketData.getReturnFlights().get(0).getDeparture() * 1000);
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			takeoffBackTime = calendar.get(Calendar.MINUTE) + calendar.get(Calendar.HOUR_OF_DAY) * 60;
			return takeoffBackTimeFilter.isActual(takeoffBackTime);
		} else {
			return true;
		}
	}

	public boolean isSuitedByOvernight(TicketData ticketData) {
		if (overnightFilter.isActive()) {
			boolean actualDirect = true;
			boolean actualReturn = true;
			actualDirect = overnightFilter.isActual(ticketData.getDirectFlights());
			if (ticketData.getReturnFlights() != null) {
				actualReturn = overnightFilter.isActual(ticketData.getReturnFlights());
			}
			return actualDirect && actualReturn;
		} else {
			return true;
		}
	}

	public boolean isSuitedByStopOverDelay(TicketData ticketData) {
		if (stopOverDelayFilter.isActive()) {
			Map<String, Integer> directMinMaxStopOverDelay = ticketData.getDirectMinAndMaxStopOverDurationInMinutes();
			if (!stopOverDelayFilter.isActualForMaxValue(directMinMaxStopOverDelay.get(TicketData.MAX))
					|| !stopOverDelayFilter.isActualForMinValue(directMinMaxStopOverDelay.get(TicketData.MIN))) {
				return false;
			}

			if (ticketData.getReturnFlights() != null) {
				Map<String, Integer> returnMinMaxStopOverDelay = ticketData.getReturnMinAndMaxStopOverDurationInMinutes();
				if (!stopOverDelayFilter.isActualForMaxValue(returnMinMaxStopOverDelay.get(TicketData.MAX))
						|| !stopOverDelayFilter.isActualForMinValue(returnMinMaxStopOverDelay.get(TicketData.MIN))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public boolean isSuitedByDuration(TicketData ticketData) {
		if (durationFilter.isActive()) {

			int directDuration = 0;
			int returnDuration = 0;
			for (OldFlightData flightData : ticketData.getDirectFlights()) {
				directDuration += flightData.getDuration() + flightData.getDelay();
			}

			if (ticketData.getReturnFlights() != null) {
				for (OldFlightData flightData : ticketData.getReturnFlights()) {
					returnDuration += flightData.getDuration() + flightData.getDelay();
				}
			}

			return durationFilter.isActual(directDuration) && durationFilter.isActual(returnDuration == 0 ? directDuration : returnDuration);
		} else {
			return true;
		}
	}

	public boolean isSuitedByPrice(TicketData ticketData) {
		if (priceFilter.isActive()) {
			return priceFilter.isActual(ticketData.getTotalWithFilters());
		} else {
			return true;
		}
	}

	public boolean isSuitedByAgencies(OldSearchData searchData, TicketData ticketData) {
		if (agenciesFilter.isActive()) {
			if (agenciesFilter.isActual(ticketData)) {
				try {
					ticketData.setTotalWithFilters(CoreAviasalesUtils.calculateMinimalPriceForTicket(ticketData, searchData.getGatesInfo(),
							searchData.getCurrencyRates()) + 0.);
				} catch (ApiException e) {
					Log.e(e.getClass().getSimpleName(), e.getExceptionMessage());
				}
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean isActive() {
		return airlinesFilter.isActive() || airportsFilter.isActive() || agenciesFilter.isActive() ||
				durationFilter.isActive() || priceFilter.isActive() || stopOverDelayFilter.isActive() ||
				takeoffTimeFilter.isActive() || takeoffBackTimeFilter.isActive() || allianceFilter.isActive() ||
				stopOverFilter.isActive() || overnightFilter.isActive();
	}

	public AirlinesFilter getAirlinesFilter() {
		return airlinesFilter;
	}

	public void setAirlinesFilter(AirlinesFilter airlinesFilter) {
		this.airlinesFilter = airlinesFilter;
	}

	public AirportsFilter getAirportsFilter() {
		return airportsFilter;
	}

	public void setAirportsFilter(AirportsFilter airportsFilter) {
		this.airportsFilter = airportsFilter;
	}

	public BaseNumericFilter getDurationFilter() {
		return durationFilter;
	}

	public void setDurationFilter(BaseNumericFilter durationFilter) {
		this.durationFilter = durationFilter;
	}

	public BaseNumericFilter getPriceFilter() {
		return priceFilter;
	}

	public void setPriceFilter(BaseNumericFilter priceFilter) {
		this.priceFilter = priceFilter;
	}

	public BaseNumericFilter getStopOverDelayFilter() {
		return stopOverDelayFilter;
	}

	public void setStopOverDelayFilter(BaseNumericFilter stopOverDelayFilter) {
		this.stopOverDelayFilter = stopOverDelayFilter;
	}

	public BaseNumericFilter getTakeoffTimeFilter() {
		return takeoffTimeFilter;
	}

	public void setTakeoffTimeFilter(BaseNumericFilter takeoffTimeFilter) {
		this.takeoffTimeFilter = takeoffTimeFilter;
	}

	public BaseNumericFilter getTakeoffBackTimeFilter() {
		return takeoffBackTimeFilter;
	}

	public void setTakeoffBackTimeFilter(BaseNumericFilter takeoffBackTimeFilter) {
		this.takeoffBackTimeFilter = takeoffBackTimeFilter;
	}

	public AllianceFilter getAllianceFilter() {
		return allianceFilter;
	}

	public void setAllianceFilter(AllianceFilter allianceFilter) {
		this.allianceFilter = allianceFilter;
	}

	public AgenciesFilter getAgenciesFilter() {
		return agenciesFilter;
	}

	public void setAgenciesFilter(AgenciesFilter agenciesFilter) {
		this.agenciesFilter = agenciesFilter;
	}

	public StopOverFilter getStopOverFilter() {
		return stopOverFilter;
	}

	public void setStopOverFilter(StopOverFilter stopOverFilter) {
		this.stopOverFilter = stopOverFilter;
	}

	public OvernightFilter getOvernightFilter() {
		return overnightFilter;
	}

	public void setOvernightFilter(OvernightFilter overnightFilter) {
		this.overnightFilter = overnightFilter;
	}

	public synchronized void clearFilters() {
		airlinesFilter.clearFilter();
		airportsFilter.clearFilter();
		agenciesFilter.clearFilter();
		durationFilter.clearFilter();
		priceFilter.clearFilter();
		stopOverDelayFilter.clearFilter();
		takeoffTimeFilter.clearFilter();
		takeoffBackTimeFilter.clearFilter();
		allianceFilter.clearFilter();
		stopOverFilter.clearFilter();
		overnightFilter.clearFilter();
	}


	public GeneralFilter(Parcel in) {
		isActive = in.readByte() == 1;
		airlinesFilter = in.readParcelable(AirlinesFilter.class
				.getClassLoader());
		airportsFilter = in.readParcelable(AirportsFilter.class.getClassLoader());
		agenciesFilter = in.readParcelable(AgenciesFilter.class.getClassLoader());
		durationFilter = in.readParcelable(BaseNumericFilter.class
				.getClassLoader());
		priceFilter = in.readParcelable(BaseNumericFilter.class.getClassLoader());
		stopOverDelayFilter = in.readParcelable(BaseNumericFilter.class
				.getClassLoader());
		takeoffTimeFilter = in.readParcelable(BaseNumericFilter.class
				.getClassLoader());
		takeoffBackTimeFilter = in.readParcelable(BaseNumericFilter.class
				.getClassLoader());
		allianceFilter = in.readParcelable(AllianceFilter.class.getClassLoader());

		stopOverFilter = in.readParcelable(StopOverFilter.class.getClassLoader());
		overnightFilter = in.readParcelable(OvernightFilter.class.getClassLoader());
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (isActive ? 1 : 0));
		dest.writeParcelable(airlinesFilter, flags);
		dest.writeParcelable(airportsFilter, flags);
		dest.writeParcelable(agenciesFilter, flags);
		dest.writeParcelable(durationFilter, flags);
		dest.writeParcelable(priceFilter, flags);
		dest.writeParcelable(stopOverDelayFilter, flags);
		dest.writeParcelable(takeoffTimeFilter, flags);
		dest.writeParcelable(takeoffBackTimeFilter, flags);
		dest.writeParcelable(allianceFilter, flags);
		dest.writeParcelable(stopOverFilter, flags);
		dest.writeParcelable(overnightFilter, flags);
	}

	public static final Creator<GeneralFilter> CREATOR = new Creator<GeneralFilter>() {
		public GeneralFilter createFromParcel(Parcel in) {
			return new GeneralFilter(in);
		}

		public GeneralFilter[] newArray(int size) {
			return new GeneralFilter[size];
		}
	};

	public synchronized void initMinAndMaxValues(OldSearchData searchData) {
		Map<String, AirportData> airportDataMap = new HashMap<String, AirportData>();
		Map<String, AirlineData> airlineDataMap = new HashMap<String, AirlineData>();
		List<GateData> onlyActualGates = new ArrayList<GateData>();
		for (TicketData ticketData : searchData.getTickets()) {
			priceFilter.setMaxValue(Math.max(priceFilter.getMaxValue(), ticketData.getTotal()));
			priceFilter.setMinValue(Math.min(priceFilter.getMinValue(), ticketData.getTotal()));

			takeoffTimeFilter.setMinValue(Math.min(takeoffTimeFilter.getMinValue(),
					ticketData.getDirectFlights().get(0).getDepartureInMinutesFromDayBeginning()));
			takeoffTimeFilter.setMaxValue(Math.max(takeoffTimeFilter.getMaxValue(),
					ticketData.getDirectFlights().get(0).getDepartureInMinutesFromDayBeginning()));

			if (ticketData.getReturnFlights() != null) {
				takeoffBackTimeFilter.setMinValue(Math.min(takeoffBackTimeFilter.getMinValue(), ticketData.getReturnFlights().get(0).getDepartureInMinutesFromDayBeginning()));
				takeoffBackTimeFilter.setMaxValue(Math.max(takeoffBackTimeFilter.getMaxValue(), ticketData.getReturnFlights().get(0).getDepartureInMinutesFromDayBeginning()));
			}

			durationFilter.setMaxValue(Math.max(durationFilter.getMaxValue(), ticketData.getDirectDurationInMinutes()));
			durationFilter.setMinValue(Math.min(durationFilter.getMinValue(), ticketData.getDirectDurationInMinutes()));

			if (ticketData.getReturnFlights() != null) {
				durationFilter.setMaxValue(Math.max(durationFilter.getMaxValue(), ticketData.getReturnDurationInMinutes()));
				durationFilter.setMinValue(Math.min(durationFilter.getMinValue(), ticketData.getReturnDurationInMinutes()));
			}

			Map<String, Integer> stopOverMinMaxDelay = ticketData.getDirectMinAndMaxStopOverDurationInMinutes();
			stopOverDelayFilter.setMaxValue(Math.max(stopOverDelayFilter.getMaxValue(), stopOverMinMaxDelay.get(TicketData.MAX)));
			stopOverDelayFilter.setMinValue(Math.min(stopOverDelayFilter.getMinValue(), stopOverMinMaxDelay.get(TicketData.MIN)));

			if (ticketData.getReturnFlights() != null) {
				Map<String, Integer> stopOverMinMaxReturnDuration = ticketData.getReturnMinAndMaxStopOverDurationInMinutes();
				stopOverDelayFilter.setMaxValue(Math.max(stopOverDelayFilter.getMaxValue(), stopOverMinMaxReturnDuration.get(TicketData.MAX)));
				stopOverDelayFilter.setMinValue(Math.min(stopOverDelayFilter.getMinValue(), stopOverMinMaxReturnDuration.get(TicketData.MIN)));
			}

			for (String gateId : ticketData.getNativePrices().keySet()) {
				if (searchData.getGateById(gateId) != null && !onlyActualGates.contains(searchData.getGateById(gateId))) {
					onlyActualGates.add(searchData.getGateById(gateId));
				}
			}

			airportDataMap = ticketData.addMissingAirportsToHashMap(airportDataMap, searchData.getAirports());
			airlineDataMap = ticketData.addMissingAirlinesToHashMap(airlineDataMap, searchData.getAirlines());
		}

		getAirportsFilter().setSectionedAirportsFromGsonClass(airportDataMap, searchData.getTickets());
		getAllianceFilter().setAlliancesFromGsonClass(airlineDataMap);
		getAirlinesFilter().setAirlinesFromGsonClass(airlineDataMap);
		getAgenciesFilter().setGatesFromGsonClass(onlyActualGates);
	}

	public GeneralFilter getCopy(Context context) {
		return new GeneralFilter(context, this);
	}

}