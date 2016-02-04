package ru.aviasales.template.ui.view.filters.stop_over_and_price_filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.listener.OnOvernightStateChange;


public class OvernightFilterView extends FrameLayout {

	private CheckBox airportOvernightCheckMark;
	private RelativeLayout airportOvernightLayout;
	private OnOvernightStateChange onOvernightStateChange;
	private boolean isAirportOvernight;

	public OvernightFilterView(Context context, boolean airportOvernight, OnOvernightStateChange listener) {
		super(context);
		setupViews(airportOvernight, listener);
	}

	public OvernightFilterView(Context context, AttributeSet attrs, boolean airportOvernight, OnOvernightStateChange listener) {
		super(context, attrs);
		setupViews(airportOvernight, listener);
	}

	private void setupViews(boolean airportOvernight, OnOvernightStateChange listener) {

		LayoutInflater.from(getContext())
				.inflate(R.layout.overnight_filter_view, this, true);

		isAirportOvernight = airportOvernight;
		airportOvernightLayout = (RelativeLayout) findViewById(R.id.llay_overnight_filter_view_airport);
		airportOvernightCheckMark = (CheckBox) findViewById(R.id.cbox_overnight_filter_view_airport);
		airportOvernightCheckMark.setChecked(isAirportOvernight);
		airportOvernightCheckMark.setSaveEnabled(false);

		airportOvernightLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (airportOvernightLayout.isEnabled()) {
					isAirportOvernight = !isAirportOvernight;

					airportOvernightCheckMark.setChecked(isAirportOvernight);
					onOvernightStateChange.onChange(isAirportOvernight);
				}
			}
		});

		onOvernightStateChange = listener;
	}

	public void setEnabled(boolean airportOvernightEnabled) {
		airportOvernightLayout.setEnabled(airportOvernightEnabled);
		airportOvernightCheckMark.setEnabled(airportOvernightEnabled);
	}

	public void clear(boolean isAirportOvernightViewEnabled) {
		if (isAirportOvernightViewEnabled) {
			isAirportOvernight = true;
			airportOvernightCheckMark.setChecked(true);
		}
	}
}