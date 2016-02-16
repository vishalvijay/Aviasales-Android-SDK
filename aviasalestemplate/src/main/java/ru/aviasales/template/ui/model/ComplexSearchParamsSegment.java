package ru.aviasales.template.ui.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.aviasales.core.http.utils.CoreDateUtils;
import ru.aviasales.core.search.params.Segment;
import ru.aviasales.core.search_airports.object.PlaceData;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Defined;

public class ComplexSearchParamsSegment {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
	private static final SimpleDateFormat dateYearFormat = new SimpleDateFormat("yyyy");

	private PlaceData origin;
	private String date;
	private PlaceData destination;

	public ComplexSearchParamsSegment(PlaceData origin, PlaceData destination, String date) {
		this.origin = origin;
		this.destination = destination;
		this.date = date;
	}

	public ComplexSearchParamsSegment() {

	}

	public PlaceData getOrigin() {
		return origin;
	}

	public void setOrigin(PlaceData origin) {
		this.origin = origin;
	}

	public String getStringDate() {
		return date;
	}

	public Date getDate() {
		if (date == null) return null;
		return CoreDateUtils.parseDateString(date, Defined.SEARCH_SERVER_DATE_FORMAT);
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setCalendarDate(Calendar stringDate) {
		date = DateUtils.convertToString(stringDate);
	}

	public PlaceData getDestination() {
		return destination;
	}

	public void setDestination(PlaceData destination) {
		this.destination = destination;
	}

	public String getDateInMM_ddFormat() {
		Date dateObject = getDate();
		if (dateObject == null) return null;
		return dateFormat.format(dateObject);
	}

	public Segment toSearchSegment() {
		Segment segment = new Segment();
		segment.setDate(date);
		segment.setOrigin(origin.getIata());
		segment.setDestination(destination.getIata());
		return segment;
	}

	public String getYear() {
		Date dateObject = getDate();
		if (dateObject == null) return null;
		return dateYearFormat.format(dateObject);
	}

	public void clearAllData() {
		origin = null;
		destination = null;
		date = null;
	}
}
