package ru.aviasales.template.ui.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;

import java.util.List;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.search.object.FlightData;
import ru.aviasales.core.search.object.TicketData;
import ru.aviasales.template.R;
import ru.aviasales.template.ticket.TicketManager;
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

	public void setData(TicketData ticketData, boolean thereTo) {
		List<FlightData> flight;
		if (thereTo) {
			flight = ticketData.getDirectFlights();
		} else {
			if (plane != null) {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
					plane.setScaleX(-1);
				}
			}
			flight = ticketData.getReturnFlights();
		}

		String origin = flight.get(0).getOrigin();
		final String destination = flight.get(flight.size() - 1).getDestination();
		tvFlightCities.setText(AviasalesSDK.getInstance().getSearchData().getCityNameByIata(origin) + " " + getResources().getString(R.string.dash)
				+ " " + AviasalesSDK.getInstance().getSearchData().getCityNameByIata(destination));

		tvFlightDuration.setText(
				StringUtils.getDurationString(getContext(), TicketManager.getInstance().getRouteDurationInMin(flight)));

	}
}