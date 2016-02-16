package ru.aviasales.template.ui.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.template.R;
import ru.aviasales.template.utils.StringUtils;

public class TicketFlightHeaderView extends RelativeLayout {

	private TextView tvFlightCities;
	private TextView tvFlightDuration;
	private ImageView plane;

	public TicketFlightHeaderView(Context context) {
		super(context);
	}

	public TicketFlightHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TicketFlightHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		tvFlightCities = (TextView) findViewById(R.id.tv_cities);
		tvFlightDuration = (TextView) findViewById(R.id.tv_duration);
		plane = (ImageView) findViewById(R.id.plane);

	}

	public void setData(List<Flight> flights, int routeDurationInMin, SearchData searchData, boolean isReturn) {
		if (isReturn) {
			if (plane != null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					plane.setScaleX(-1);
				}
			}
		}

		String origin = flights.get(0).getDeparture();
		final String destination = flights.get(flights.size() - 1).getArrival();

		Map<String, AirportData> airports = searchData.getAirports();
		tvFlightCities.setText(airports.get(origin).getCity() + " " + getResources().getString(R.string.dash)
				+ " " + airports.get(destination).getCity());

		tvFlightDuration.setText(
				StringUtils.getDurationString(getContext(), routeDurationInMin));

	}
}