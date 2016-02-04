package ru.aviasales.template.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import ru.aviasales.template.R;

public class OvernightFilterView extends FrameLayout {

	public interface OnOvernightStateChange {
		void onChange(boolean airportOvernight);
	}

	private CheckBox airportOvernightCheckMark;
	private RelativeLayout airportOvernightLayout;
	private OnOvernightStateChange onOvernightStateChange;
	private boolean isAirportOvernight;

	public OvernightFilterView(Context context, boolean airportOvernight, OnOvernightStateChange listener) {
		super(context);
		setUpViews(airportOvernight, listener);
	}

	private void setUpViews(boolean airportOvernight, OnOvernightStateChange listener) {

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