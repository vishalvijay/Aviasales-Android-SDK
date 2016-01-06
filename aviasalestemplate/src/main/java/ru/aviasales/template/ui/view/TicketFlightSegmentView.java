package ru.aviasales.template.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.template.R;
import ru.aviasales.template.api.AirlineLogoApi;
import ru.aviasales.template.api.params.AirlineLogoParams;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Defined;
import ru.aviasales.template.utils.StringUtils;

public class TicketFlightSegmentView extends RelativeLayout {

	private ImageView carrierLogo;

	private TextView tvCarrierInfo;
	private TextView tvFlightDuration;
	private TextView tvDepartureTime;
	private TextView tvDepartureDate;
	private TextView tvArrivalTime;
	private TextView tvArrivalDate;
	private TextView tvCarrierName;
	private TextView tvDepartAirportName;
	private TextView tvArrivalAirportName;
	private TextView tvDepartName;
	private TextView tvArrivalName;

	public TicketFlightSegmentView(Context context) {
		super(context);
	}

	public TicketFlightSegmentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TicketFlightSegmentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		tvCarrierInfo = (TextView) findViewById(R.id.tv_carrier_info);
		tvDepartureTime = (TextView) findViewById(R.id.tv_departure_time);
		tvDepartureDate = (TextView) findViewById(R.id.tv_departure_date);
		tvArrivalTime = (TextView) findViewById(R.id.tv_arrival_time);
		tvArrivalDate = (TextView) findViewById(R.id.tv_arrival_date);
		tvFlightDuration = (TextView) findViewById(R.id.tv_flight_duration);
		carrierLogo = (ImageView) findViewById(R.id.iv_carrier_logo);
		tvCarrierName = (TextView) findViewById(R.id.tv_carrier_name);
		tvDepartAirportName = (TextView) findViewById(R.id.tv_depart_airport_name);
		tvArrivalAirportName = (TextView) findViewById(R.id.tv_arrival_airport_name);
		tvDepartName = (TextView) findViewById(R.id.tv_depart_name);
		tvArrivalName = (TextView) findViewById(R.id.tv_arrival_name);
	}

	public void setData(Map<String, AirlineData> airlines, Map<String, AirportData> airports, Flight flight) {


		AirportData departAirport = airports.get(flight.getDeparture());
		String departName = departAirport == null || departAirport.getCity() == null ?
				flight.getDeparture() : departAirport.getCity() + ", " + flight.getDeparture();
		tvDepartName.setText(departName);
		tvDepartAirportName.setText(departAirport == null || departAirport.getName() == null ? "" : departAirport.getName());

		AirportData arrivalAirport = airports.get(flight.getArrival());
		String arrivalName = arrivalAirport == null || arrivalAirport.getCity() == null ?
				flight.getArrival() : arrivalAirport.getCity() + ", " + flight.getArrival();
		tvArrivalName.setText(arrivalName);
		tvArrivalAirportName.setText(arrivalAirport == null || arrivalAirport.getName() == null ? "" : arrivalAirport.getName());

		tvCarrierInfo.setText(getResources().getString(R.string.ticket_flight_text) + " " +
				flight.getOperatingCarrier() + "-" + flight.getNumber());

		loadImage(flight, carrierLogo);

		SimpleDateFormat resultsTimeFormat = new SimpleDateFormat(Defined.RESULTS_TIME_FORMAT);
		SimpleDateFormat serverDateFormat = new SimpleDateFormat(Defined.SEARCH_SERVER_DATE_FORMAT);
		TimeZone utc = TimeZone.getTimeZone(Defined.UTC_TIMEZONE);
		resultsTimeFormat.setTimeZone(utc);
		serverDateFormat.setTimeZone(utc);

		tvDepartureTime.setText(DateUtils.convertDateFromTo(flight.getDepartureTime(), resultsTimeFormat, getTimeFormat()));
		tvArrivalTime.setText(DateUtils.convertDateFromTo(flight.getArrivalTime(), resultsTimeFormat, getTimeFormat()));

		tvDepartureDate.setText(DateUtils.convertDateFromTo(flight.getDepartureDate(), serverDateFormat, getDateFormat()));
		tvArrivalDate.setText(DateUtils.convertDateFromTo(flight.getArrivalDate(), serverDateFormat, getDateFormat()));

		tvFlightDuration.setText(StringUtils.getDurationString(getContext(), flight.getDuration()));

		String carrierName;
		if (flight.getOperatingCarrier() != null && airlines.get(flight.getOperatingCarrier()) != null) {
			carrierName = airlines.get(flight.getOperatingCarrier()).getName();
		} else {
			carrierName = "";
		}

		tvCarrierName.setText(carrierName);

	}

	private void loadImage(Flight flight, ImageView image) {
		AirlineLogoParams params = new AirlineLogoParams();
		params.setContext(getContext());
		params.setIata(flight.getOperatingCarrier());
		params.setImage(image);
		params.setWidth(getResources().getDimensionPixelSize(R.dimen.airline_logo_width));
		params.setHeight(getResources().getDimensionPixelSize(R.dimen.airline_logo_height));
		params.setImageLoadingListener(new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String s, View view) {
			}

			@Override
			public void onLoadingFailed(String s, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
			}

			@Override
			public void onLoadingCancelled(String s, View view) {
			}
		});
		new AirlineLogoApi().getAirlineLogo(params);
	}

	private SimpleDateFormat getDateFormat() {
		SimpleDateFormat dfDate =
				new SimpleDateFormat(Defined.TICKET_SHORT_DATE_FORMAT, DateUtils.getFormatSymbolsShort(getContext()));
		dfDate.setTimeZone(TimeZone.getTimeZone(Defined.UTC_TIMEZONE));
		return dfDate;
	}

	private SimpleDateFormat getTimeFormat() {
		SimpleDateFormat dfTime;
		if (!DateFormat.is24HourFormat(getContext())) {
			dfTime = new SimpleDateFormat(Defined.AM_PM_TICKET_FLIGHT_TIME_FORMAT);
			dfTime.setDateFormatSymbols(DateUtils.getDateFormatSymbols());
		} else {
			dfTime = new SimpleDateFormat(Defined.TICKET_FLIGHT_TIME_FORMAT);
		}
		dfTime.setTimeZone(TimeZone.getTimeZone(Defined.UTC_TIMEZONE));
		return dfTime;
	}

}
