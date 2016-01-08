package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.template.R;
import ru.aviasales.template.utils.StringUtils;

public class TicketTransferView extends RelativeLayout {

	private TextView city;
	private TextView duration;

	public TicketTransferView(Context context) {
		super(context);
	}

	public TicketTransferView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TicketTransferView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		city = (TextView) findViewById(R.id.tv_city);
		duration = (TextView) findViewById(R.id.tv_duration);
	}

	public void setData(Flight nextFlight, Map<String, AirportData> airports) {
		city.setText(airports.get(nextFlight.getDeparture()).getCity() + ", " + nextFlight.getDeparture().toUpperCase());

		int transferDuration = nextFlight.getDelay();
		duration.setText(StringUtils.getDurationString(getContext(), transferDuration));
	}
}
