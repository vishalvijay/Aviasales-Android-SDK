package ru.aviasales.template.ui.view.filters.airports_filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.aviasales.template.R;


public class AirportSectionHeader extends LinearLayout {

	private final TextView headerText;

	public AirportSectionHeader(Context context) {
		this(context, null);
	}

	public AirportSectionHeader(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.airport_filter_section_header,
				this, true);

		headerText = (TextView) findViewById(R.id.tv_airport_filter_section_header);
	}

	public void setHeaderText(String text) {
		headerText.setText(text);
	}
}
