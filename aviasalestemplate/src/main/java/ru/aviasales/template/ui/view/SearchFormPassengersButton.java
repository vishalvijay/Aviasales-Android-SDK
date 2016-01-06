package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.core.search.params.Passengers;
import ru.aviasales.template.R;


public class SearchFormPassengersButton extends RelativeLayout {

	private TextView tvAdults;
	private TextView tvChildren;
	private TextView tvInfants;

	public SearchFormPassengersButton(Context context) {
		super(context);
	}

	public SearchFormPassengersButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setUpViews(context);
	}

	public SearchFormPassengersButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setUpViews(context);
	}

	private void setUpViews(Context context) {
		LayoutInflater.from(context).inflate(R.layout.search_form_passengers_btn, this, true);
		tvAdults = (TextView) findViewById(R.id.tv_adults);
		tvChildren = (TextView) findViewById(R.id.tv_children);
		tvInfants = (TextView) findViewById(R.id.tv_infants);
		setClickable(true);
	}

	public void setData(Passengers passengers) {

		int adults = passengers.getAdults();
		int children = passengers.getChildren();
		int infants = passengers.getInfants();

		tvAdults.setText(Integer.toString(adults));
		tvChildren.setText(Integer.toString(children));
		tvInfants.setText(Integer.toString(infants));

		tvAdults.setEnabled(adults > 0);
		tvChildren.setEnabled(children > 0);
		tvInfants.setEnabled(infants > 0);
	}
}
