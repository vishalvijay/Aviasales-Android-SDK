package ru.aviasales.template.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.core.search_airports.object.PlaceData;
import ru.aviasales.template.R;
import ru.aviasales.template.utils.Utils;

public class SearchFormPlaceButton extends RelativeLayout {

	public static final int TYPE_DEPART = 0;
	public static final int TYPE_DESTINATION = 1;

	private TextView tvCity;
	private TextView tvAirport;
	private TextView tvIata;
	private int type;

	public SearchFormPlaceButton(Context context) {
		super(context);
	}

	public SearchFormPlaceButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttributes(context, attrs);
		setUpViews(context);
	}

	public SearchFormPlaceButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		parseAttributes(context, attrs);
		setUpViews(context);
	}

	private void setUpViews(Context context) {
		LayoutInflater.from(context).inflate(R.layout.search_form_place_btn, this, true);
		tvCity = (TextView) findViewById(R.id.tv_city);
		tvAirport = (TextView) findViewById(R.id.tv_airport);
		tvIata = (TextView) findViewById(R.id.tv_iata);

		setUpDefaultValues();
	}

	private void setUpDefaultValues() {
		switch (type) {
			case TYPE_DEPART:
				tvCity.setText(R.string.search_form_depart_city_default);
				break;
			case TYPE_DESTINATION:
				tvCity.setText(R.string.search_form_destination_city_default);
				break;
		}

		tvAirport.setText(R.string.search_form_airport_default);
		tvIata.setText("");

	}

	private void parseAttributes(Context context, AttributeSet attrs) {
		TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.SearchFormPlaceButton);
		type = values.getInt(R.styleable.SearchFormPlaceButton_placeType, TYPE_DEPART);
		values.recycle();
	}

	public void setData(PlaceData placeData) {
		if (placeData == null) {
			setUpDefaultValues();
			return;
		}

		tvCity.setText(Utils.capitalizeFirstLetter(placeData.getCityName()));
		String airport;
		if (placeData.getAirportName() == null) {
			airport = getResources().getString(R.string.destination_any_airport);
		} else {
			airport = placeData.getAirportName().toUpperCase();
		}
		airport += ", " + placeData.getCountry();
		tvAirport.setText(airport.toUpperCase());

		tvIata.setText(placeData.getIata());
	}

	public int getType() {
		return type;
	}
}
