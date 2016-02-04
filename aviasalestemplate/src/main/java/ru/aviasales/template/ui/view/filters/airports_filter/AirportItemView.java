package ru.aviasales.template.ui.view.filters.airports_filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.expandedlistview.view.BaseFiltersListViewItem;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.FilterCheckedAirline;

public class AirportItemView extends BaseFiltersListViewItem {

	private RatingBar ratingBar;
	private TextView iata;
	private TextView airportName;

	public AirportItemView(Context context) {
		this(context, null);
	}

	public AirportItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCheckedAirport(FilterCheckedAirline checkedAirline) {
		super.setCheckedText(checkedAirline);
	}

	public void setRating(float rating) {
		ratingBar.setRating(rating);
	}

	public RatingBar getRatingBar() {
		return ratingBar;
	}

	public TextView getIata() {
		return iata;
	}

	public void setIata(String iata) {
		this.iata.setText(iata);
	}

	public void setAirportName(String airportName) {
		this.airportName.setText(airportName);
	}

	@Override
	protected void setUpView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.airport_filter_list_item,
				this, true);

		layout = (RelativeLayout) findViewById(R.id.rlay_airport_filter_list_item);
		textView = (TextView) findViewById(R.id.tvAirportFilterListItemName);
		checkBox = (CheckBox) findViewById(R.id.cbox_airport_filter_list_item);
		ratingBar = (RatingBar) findViewById(R.id.rbar_airport_filter_list_item);
		iata = (TextView) findViewById(R.id.tvAirportFilterListItemIata);
		airportName = (TextView) findViewById(R.id.tvAirportFilterListItemCity);
		checkBox.setSaveEnabled(false);
	}

	public void setCityText(String text) {
		textView.setText(text);
	}
}