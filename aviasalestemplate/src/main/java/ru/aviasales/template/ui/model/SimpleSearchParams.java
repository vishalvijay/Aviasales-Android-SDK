package ru.aviasales.template.ui.model;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.aviasales.core.http.utils.CoreDateUtils;
import ru.aviasales.core.search.params.Segment;
import ru.aviasales.core.search_airports.object.PlaceData;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Defined;

public class SimpleSearchParams {

	private PlaceData origin;
	private PlaceData destination;
	private String departDate;
	private String returnDate;
	private boolean returnEnabled;

	public PlaceData getOrigin() {
		return origin;
	}

	public void setOrigin(PlaceData origin) {
		this.origin = origin;
	}

	public PlaceData getDestination() {
		return destination;
	}

	public void setDestination(PlaceData destination) {
		this.destination = destination;
	}

	public String getDepartDateString() {
		return departDate;
	}

	public Date getDepartDate() {
		return getDate(departDate);
	}

	public void setDepartDate(String departDate) {
		this.departDate = departDate;
	}

	public void setDepartDate(Calendar departDate) {
		this.departDate = DateUtils.convertToString(departDate);
	}

	public String getReturnDateString() {
		return returnDate;
	}

	public Date getReturnDate() {
		return getDate(returnDate);
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public void setReturnDate(Calendar returnDate) {
		this.returnDate = DateUtils.convertToString(returnDate);
	}

	public boolean isReturnEnabled() {
		return returnEnabled;
	}

	public void setReturnEnabled(boolean returnEnabled) {
		this.returnEnabled = returnEnabled;
	}

	public List<Segment> createSegments() {
		List<Segment> segments = new ArrayList<>();
		Segment departSegment = new Segment();
		departSegment.setOrigin(origin.getIata());
		departSegment.setDestination(destination.getIata());
		departSegment.setDate(departDate);

		segments.add(departSegment);

		if (isReturnEnabled()) {
			Segment returnSegment = new Segment();
			returnSegment.setOrigin(destination.getIata());
			returnSegment.setDestination(origin.getIata());
			returnSegment.setDate(returnDate);

			segments.add(returnSegment);
		}

		return segments;
	}

	private Date getDate(String date) {
		if (date == null) return null;
		return CoreDateUtils.parseDateString(date, Defined.SEARCH_SERVER_DATE_FORMAT);
	}

	public boolean areDestinationsSet() {
		return origin == null || destination == null;
	}

	public boolean areDestinationsEqual() {
		return origin.equals(destination) || areCitiesEquals();
	}

	public boolean areCitiesEquals() {
		return origin.getName().equals(destination.getName());
	}

	public void checkReturnDate() {
		if (returnDate == null) {
			return;
		}
		if (DateUtils.convertToCalendar(departDate).compareTo(DateUtils.convertToCalendar(returnDate)) > 0) {
			returnEnabled = false;
			returnDate = null;
		}
	}
}
