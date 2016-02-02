package ru.aviasales.template.ui.view.filters.airports_filter;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.filters.AirportsFilter;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.ui.adapter.AirportsAdapter;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;
import ru.aviasales.template.ui.view.filters.SegmentExpandableView;


public class AirportPageView extends BaseFiltersScrollView {
	private final List<ExpandedListView> viewListView = new ArrayList<>();
	private boolean hideTitle = true;

	public AirportPageView(Context context) {
		super(context);
	}

	public AirportPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AirportPageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setHideTitle(boolean hideTitle) {
		this.hideTitle = hideTitle;
	}

	@Override
	protected void setupSimplePageView() {
		if (getSimpleGeneralFilters().getAirportsFilter().isValid()) {
			ExpandedListView view = createAirportsListView(getSimpleGeneralFilters().getAirportsFilter(), hideTitle);
			viewListView.add(view);
			addView(view);
		}
	}

	@Override
	protected void setupOpenJawPageView() {
		OpenJawFiltersSet filters = getOpenJawGeneralFilters();

		for (Integer segmentNumber : filters.getSegmentFilters().keySet()) {
			if (!filters.getSegmentFilters().get(segmentNumber).getAirportsFilter().isValid()) continue;

			SegmentExpandableView segmentExpandableView = createSegmentExpandableView(segmentList.get(segmentNumber));
			ExpandedListView view = createAirportsListView(filters.getSegmentFilters().get(segmentNumber).getAirportsFilter(), hideTitle);
			viewListView.add(view);
			segmentExpandableView.addContentView(view);
			addView(segmentExpandableView);
		}
	}

	@Override
	public void clearFilters() {
		for (ExpandedListView view : viewListView) {
			view.notifyDataChanged();
		}
	}

	private ExpandedListView createAirportsListView(AirportsFilter airportsFilter, boolean hideTitle) {
		ExpandedListView airportListView = new ExpandedListView(getContext(), null);
		AirportsAdapter adapter = new AirportsAdapter(getContext(),
				airportsFilter.getOriginAirportList(), airportsFilter.getDestinationAirportList(),
				airportsFilter.getStopOverAirportList(), hideTitle);
		airportListView.setAdapter(adapter);
		airportListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return airportListView;
	}
}
