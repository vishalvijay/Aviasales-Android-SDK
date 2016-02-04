package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;

import ru.aviasales.expandedlistview.view.BaseFiltersListViewItem;
import ru.aviasales.template.R;

public class AirlineItemView extends BaseFiltersListViewItem {

	private RatingBar ratingBar;

	public AirlineItemView(Context context) {
		this(context, null);
	}

	public AirlineItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		airlineViewStub.inflate();
		ratingBar = (RatingBar) findViewById(R.id.rbar_base_filter_list_item);
	}

	public RatingBar getRatingBar() {
		return ratingBar;
	}
}