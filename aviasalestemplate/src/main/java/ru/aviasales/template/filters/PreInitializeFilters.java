package ru.aviasales.template.filters;

import android.content.Context;

import java.util.List;

import ru.aviasales.core.legacy.search.object.OldSearchData;
import ru.aviasales.core.legacy.search.object.TicketData;

public class PreInitializeFilters {
	private int oneStopOverMinPrice;
	private int withoutStopOverMinPrice;
	private int twoPlusStopOverMinPrice;
	private boolean airportOvernightEnabled;
	private OldSearchData searchData;
	private GeneralFilter testFilter;
	private boolean oneStopOverFilterEnabled;
	private boolean withoutStopOverFilterEnabled;
	private boolean twoPlusStopOverFilterEnabled;

	public PreInitializeFilters(Context context, OldSearchData searchData) {
		this.oneStopOverMinPrice = Integer.MAX_VALUE;
		this.withoutStopOverMinPrice = Integer.MAX_VALUE;
		this.twoPlusStopOverMinPrice = Integer.MAX_VALUE;
		this.airportOvernightEnabled = false;
		this.oneStopOverFilterEnabled = false;
		this.withoutStopOverFilterEnabled = false;
		this.twoPlusStopOverFilterEnabled = false;
		this.searchData = searchData;
		this.testFilter = new GeneralFilter(context);
		testFilter.initMinAndMaxValues(searchData);
		testFilter.clearFilters();
	}

	public void setupFilters() {

		List<TicketData> testList = searchData.getTickets();

		for (TicketData ticketData : testList) {
			applyStopOverFilter(ticketData);
			applyOvernightFilter(ticketData);
		}
		testFilter.clearFilters();
	}

	private void applyOvernightFilter(TicketData ticketData) {
		testFilter.getOvernightFilter().setAirportOvernightAvailable(false);
		if (!testFilter.isSuitedByOvernight(ticketData)) {
			airportOvernightEnabled = true;
		}
	}

	private void applyStopOverFilter(TicketData ticketData) {
		if (setStopOverParamsAndApplyFilter(false, true, false, ticketData)) {
			withoutStopOverMinPrice = Math.min(ticketData.getTotal(), withoutStopOverMinPrice);
		}

		if (setStopOverParamsAndApplyFilter(true, false, false, ticketData)) {
			oneStopOverMinPrice = Math.min(ticketData.getTotal(), oneStopOverMinPrice);
		}

		if (setStopOverParamsAndApplyFilter(false, false, true, ticketData)) {
			twoPlusStopOverMinPrice = Math.min(ticketData.getTotal(), twoPlusStopOverMinPrice);
		}
	}

	private boolean setStopOverParamsAndApplyFilter(boolean isOneStopOverFlightsAvailable,
	                                                boolean isWithoutStopOverFlightsAvailable,
	                                                boolean isTwoPlusStopOverFlightsAvailable,
	                                                TicketData ticketData) {

		testFilter.getStopOverFilter().setParams(isOneStopOverFlightsAvailable, isWithoutStopOverFlightsAvailable,
				isTwoPlusStopOverFlightsAvailable);
		return testFilter.isSuitedByStopOver(ticketData);
	}

	public int getOneStopOverMinPrice() {
		return oneStopOverMinPrice;
	}

	public void setOneStopOverMinPrice(int oneStopOverMinPrice) {
		this.oneStopOverMinPrice = oneStopOverMinPrice;
	}

	public int getWithoutStopOverMinPrice() {
		return withoutStopOverMinPrice;
	}

	public void setWithoutStopOverMinPrice(int withoutStopOverMinPrice) {
		this.withoutStopOverMinPrice = withoutStopOverMinPrice;
	}

	public int getTwoPlusStopOverMinPrice() {
		return twoPlusStopOverMinPrice;
	}

	public void setTwoPlusStopOverMinPrice(int twoPlusStopOverMinPrice) {
		this.twoPlusStopOverMinPrice = twoPlusStopOverMinPrice;
	}

	public boolean isAirportOvernightEnabled() {
		return airportOvernightEnabled;
	}

	public void setAirportOvernightEnabled(boolean airportOvernightEnabled) {
		this.airportOvernightEnabled = airportOvernightEnabled;
	}

	public boolean isOneStopOverFilterEnabled() {
		return oneStopOverFilterEnabled;
	}

	public boolean isWithoutStopOverFilterEnabled() {
		return withoutStopOverFilterEnabled;
	}

	public boolean isTwoPlusStopOverFilterEnabled() {
		return twoPlusStopOverFilterEnabled;
	}
}
