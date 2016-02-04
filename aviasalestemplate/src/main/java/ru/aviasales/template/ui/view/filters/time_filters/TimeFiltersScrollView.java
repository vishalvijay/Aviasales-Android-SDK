package ru.aviasales.template.ui.view.filters.time_filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.template.R;
import ru.aviasales.template.filters.BaseNumericFilter;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.filters.SegmentFilter;
import ru.aviasales.template.filters.SimpleSearchFilters;
import ru.aviasales.template.ui.listener.OnRangeChangeListener;
import ru.aviasales.template.ui.view.filters.BaseFilterView;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;
import ru.aviasales.template.ui.view.filters.SegmentExpandableView;

public class TimeFiltersScrollView extends BaseFiltersScrollView {
	private final List<TakeoffLandingFilterView> takeoffLandingFilterViews = new ArrayList<>();
	private final List<BaseFilterView> durationFilterViews = new ArrayList<>();

	public TimeFiltersScrollView(Context context) {
		super(context);
	}

	public TimeFiltersScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimeFiltersScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void setUpSimplePageView() {
		SimpleSearchFilters filters = getSimpleGeneralFilters();

		if (filters.getTakeoffTimeFilter().isValid() || filters.getLandingTimeFilter().isValid()) {
			TakeoffLandingFilterView view = createSegmentTakeoffLandingView(filters.getTakeoffTimeFilter(),
					filters.getLandingTimeFilter(), getAirportOriginIata(segmentList.get(0)), getAirportDestinationIata(segmentList.get(0)));
			takeoffLandingFilterViews.add(view);
			addView(view);
			addView(LayoutInflater.from(getContext()).inflate(R.layout.filters_divider, layout, false));
		}

		if (segmentList.size() == 2 && (filters.getTakeoffBackTimeFilter().isValid() || filters.getLandingBackTimeFilter().isValid())) {
			TakeoffLandingFilterView backView = createSegmentTakeoffLandingView(filters.getTakeoffBackTimeFilter(),
					filters.getLandingBackTimeFilter(), getAirportOriginIata(segmentList.get(1)), getAirportDestinationIata(segmentList.get(1)), true);
			takeoffLandingFilterViews.add(backView);

			addView(backView);
			addView(LayoutInflater.from(getContext()).inflate(R.layout.filters_divider, layout, false));
		}

		if (filters.getDurationFilter().isValid()) {
			BaseFilterView durationFilterView = createDurationFilterView(filters.getDurationFilter());
			durationFilterViews.add(durationFilterView);
			addView(durationFilterView);
		}
	}

	@Override
	protected void setUpOpenJawPageView() {
		OpenJawFiltersSet filters = getOpenJawGeneralFilters();

		for (Integer segmentNumber : filters.getSegmentFilters().keySet()) {
			SegmentFilter segmentFilter = filters.getSegmentFilters().get(segmentNumber);
			if (!segmentFilter.getTakeoffTimeFilter().isValid() &&
					!segmentFilter.getLandingTimeFilter().isValid() &&
					!segmentFilter.getDurationFilter().isValid()) continue;

			SegmentExpandableView segmentExpandableView = createSegmentExpandableView(segmentList.get(segmentNumber));

			if (segmentFilter.getTakeoffTimeFilter().isValid() || segmentFilter.getLandingTimeFilter().isValid()) {
				TakeoffLandingFilterView takeoffLandingFilterView = createSegmentTakeoffLandingView(segmentFilter.getTakeoffTimeFilter(),
						segmentFilter.getLandingTimeFilter(), getAirportOriginIata(segmentList.get(segmentNumber)),
						getAirportDestinationIata(segmentList.get(segmentNumber)));
				takeoffLandingFilterViews.add(takeoffLandingFilterView);
				segmentExpandableView.addContentView(takeoffLandingFilterView);

				segmentExpandableView.addContentView(LayoutInflater.from(getContext()).inflate(R.layout.filters_divider,
						segmentExpandableView.getContentLayout(), false));
			}

			if (segmentFilter.getDurationFilter().isValid()) {
				BaseFilterView durationFilterView = createDurationFilterView(segmentFilter.getDurationFilter());
				durationFilterViews.add(durationFilterView);
				segmentExpandableView.addContentView(durationFilterView);

				segmentExpandableView.addContentView(LayoutInflater.from(getContext()).inflate(R.layout.filters_divider,
						segmentExpandableView.getContentLayout(), false));
				addView(segmentExpandableView);
			}
		}
	}

	@Override
	public void clearFilters() {
		for (TakeoffLandingFilterView takeoffLandingFilterView : takeoffLandingFilterViews) {
			takeoffLandingFilterView.clearFilterViews();
		}

		for (BaseFilterView durationFilterView : durationFilterViews) {
			durationFilterView.clear();
		}
	}

	private BaseFilterView createDurationFilterView(final BaseNumericFilter stopOverFilter) {
		BaseFilterView filterView = new BaseFilterView(getContext(), null, BaseFilterView.DURATION_FILTER,
				stopOverFilter.getMinValue(), stopOverFilter.getMaxValue(),
				stopOverFilter.getCurrentMaxValue(),
				new OnRangeChangeListener() {
					@Override
					public void onChange(int max) {
						stopOverFilter.setCurrentMaxValue(max);
						listener.onChange();
					}
				});
		filterView.setEnabled(stopOverFilter.isEnabled());
		return filterView;
	}

	private TakeoffLandingFilterView createSegmentTakeoffLandingView(BaseNumericFilter takeoffTimeFilter,
	                                                                 BaseNumericFilter landingTimeFilter,
	                                                                 String takeoffIata, String landingIata) {
		return createSegmentTakeoffLandingView(takeoffTimeFilter, landingTimeFilter, takeoffIata, landingIata, false);
	}

	private TakeoffLandingFilterView createSegmentTakeoffLandingView(final BaseNumericFilter takeoffTimeFilter,
	                                                                 final BaseNumericFilter landingTimeFilter,
	                                                                 String takeoffIata, String landingIata,
	                                                                 boolean isBackSegment) {
		TakeoffLandingFilterView takeoffLandingFilterView = new TakeoffLandingFilterView(getContext());
		takeoffLandingFilterView.init(takeoffTimeFilter, landingTimeFilter,
				takeoffIata, landingIata, isBackSegment, new TakeoffLandingFilterView.OnTakeoffLandingTimeFilterChanged() {
					@Override
					public void onTakeoffTimeChanged(int min, int max) {
						takeoffTimeFilter.setCurrentMinValue(min);
						takeoffTimeFilter.setCurrentMaxValue(max);
						listener.onChange();
					}

					@Override
					public void onLandingTimeChanged(int min, int max) {
						landingTimeFilter.setCurrentMinValue(min);
						landingTimeFilter.setCurrentMaxValue(max);
						listener.onChange();
					}
				});
		return takeoffLandingFilterView;
	}
}
