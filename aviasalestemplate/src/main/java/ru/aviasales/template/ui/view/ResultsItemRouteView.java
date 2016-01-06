package ru.aviasales.template.ui.view;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import ru.aviasales.core.search.object.Flight;
import ru.aviasales.template.R;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Defined;
import ru.aviasales.template.utils.StringUtils;
import ru.aviasales.template.utils.Utils;


public class ResultsItemRouteView extends RelativeLayout {

	private static final String TICKET_SHORT_COMPLEX_FORMAT = "d MMM";

	private TextView tvIatas;
	private TextView tvFlightDate;
	private TextView tvDepartureAndArrivalTime;
	private TextView tvTransfers;
	private TextView tvDuration;

	public ResultsItemRouteView(Context context) {
		super(context);
	}

	public ResultsItemRouteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResultsItemRouteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		tvIatas = (TextView) findViewById(R.id.tv_cities);
		tvDepartureAndArrivalTime = (TextView) findViewById(R.id.tv_arrival_and_departure_time);
		tvTransfers = (TextView) findViewById(R.id.tv_flight_transfers);
		tvDuration = (TextView) findViewById(R.id.tv_flight_duration);
	}

	public void setRouteData(List<Flight> flights, boolean complexSearch) {


		if (complexSearch) {
			tvFlightDate = (TextView) findViewById(R.id.tv_flight_date);
			String flightDate = DateUtils.convertDateFromTo(flights.get(0).getDepartureDate(),
					Defined.SEARCH_SERVER_DATE_FORMAT, TICKET_SHORT_COMPLEX_FORMAT);
			tvFlightDate.setText(flightDate);
		}

		SimpleDateFormat dfFormatFrom = new SimpleDateFormat(Defined.RESULTS_TIME_FORMAT);
		SimpleDateFormat dfTime;
		if (!DateFormat.is24HourFormat(getContext())) {
			dfTime = new SimpleDateFormat(Defined.AM_PM_RESULTS_TIME_FORMAT);
			dfTime.setDateFormatSymbols(DateUtils.getDateFormatSymbols());
		} else {
			dfTime = new SimpleDateFormat(Defined.RESULTS_TIME_FORMAT);
		}
		SimpleDateFormat dfDate = new SimpleDateFormat(
				Defined.RESULTS_SHORT_DATE_FORMAT, DateUtils.getFormatSymbolsShort(getContext()));

		TimeZone utc = TimeZone.getTimeZone(Defined.UTC_TIMEZONE);
		dfTime.setTimeZone(utc);
		dfDate.setTimeZone(utc);

		tvIatas.setText(flights.get(0).getDeparture() + " " + getResources().getText(R.string.dot) +
				" " + flights.get(flights.size() - 1).getArrival());

		String timeDeparture = DateUtils.convertDateFromTo(flights.get(0).getDepartureTime(), dfFormatFrom, dfTime);
		String timeArrival = DateUtils.convertDateFromTo(flights.get(flights.size() - 1).getArrivalTime(), dfFormatFrom, dfTime);

		tvDepartureAndArrivalTime.setText(timeDeparture + " " + getResources().getString(R.string.dash) +
				" " + timeArrival);

		if (complexSearch) {
			tvTransfers.setText(flights.size() > 1 ? String.valueOf(flights.size() - 1) : getResources().getString(R.string.dash));
		} else {
			if (flights.size() > 3) {
				tvTransfers.setText(Integer.toString(flights.size() - 1));
			} else {
				tvTransfers.setText(StringUtils.getTransferText(getContext(), flights));
			}
		}

		tvDuration.setText(StringUtils.getDurationString(
				getContext(), Utils.getRouteDurationInMin(flights)));
	}
}
