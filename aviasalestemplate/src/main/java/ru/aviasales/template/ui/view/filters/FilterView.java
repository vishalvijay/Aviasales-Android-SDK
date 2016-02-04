package ru.aviasales.template.ui.view.filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.core.search.object.ResultsSegment;
import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.AgenciesFilter;
import ru.aviasales.template.filters.AirlinesFilter;
import ru.aviasales.template.filters.AirportsFilter;
import ru.aviasales.template.filters.AllianceFilter;
import ru.aviasales.template.filters.BaseNumericFilter;
import ru.aviasales.template.filters.FiltersSet;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.filters.OvernightFilter;
import ru.aviasales.template.filters.PayTypeFilter;
import ru.aviasales.template.filters.SegmentFilter;
import ru.aviasales.template.filters.SimpleSearchFilters;
import ru.aviasales.template.ui.adapter.AgencyAdapter;
import ru.aviasales.template.ui.adapter.AirlinesAdapter;
import ru.aviasales.template.ui.adapter.AirportsAdapter;
import ru.aviasales.template.ui.adapter.AlliancesAdapter;
import ru.aviasales.template.ui.adapter.PayTypeAdapter;
import ru.aviasales.template.ui.listener.OnRangeChangeListener;
import ru.aviasales.template.ui.view.filters.stop_over_and_price_filters.StopOverAndPriceFiltersPageView;
import ru.aviasales.template.ui.view.filters.stop_over_and_price_filters.StopOverFilterView;
import ru.aviasales.template.ui.view.filters.time_filters.TakeoffLandingFilterView;
import ru.aviasales.template.ui.view.filters.time_filters.TimeFiltersScrollView;

public class FilterView extends LinearLayout {
	private BaseFiltersScrollView.OnSomethingChangeListener listener;
	private BaseFilterView priceFiltersView;
	private final List<TakeoffLandingFilterView> takeoffLandingFilterViews = new ArrayList<>();
	private final List<StopOverFilterView> stopOverFilterViews = new ArrayList<>();
	private final List<ExpandedListView> airlinesFilterViews = new ArrayList<>();
	private final List<ExpandedListView> alliancesFilterViews = new ArrayList<>();
	private final List<ExpandedListView> airportsFilterViews = new ArrayList<>();
	private ExpandedListView agenciesFilterView;
	private ExpandedListView payTypeFilterView;

	private TimeFiltersScrollView timeFiltersPageView;
	private StopOverAndPriceFiltersPageView stopOverAndPriceFiltersPageView;

	public FilterView(Context context) {
		super(context);
		setupViews();
	}

	public FilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}

	public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupViews();
	}

	private void setupViews() {
		setOrientation(VERTICAL);
	}

	public void init(FiltersSet filtersSet, List<ResultsSegment> segmentList, BaseFiltersScrollView.OnSomethingChangeListener listener) {
		this.listener = listener;
		if (isComplexSearchFilters(filtersSet)) {
			OpenJawFiltersSet openJawFiltersSet = (OpenJawFiltersSet) filtersSet;
			addPriceFilter(openJawFiltersSet.getPriceFilter());
			addSegmentFilters(openJawFiltersSet, segmentList);
			addAgenciesFilter(openJawFiltersSet.getAgenciesFilter());
			addPayTypeFilter(openJawFiltersSet.getPayTypeFilter());
		} else {
			SimpleSearchFilters filters = (SimpleSearchFilters) filtersSet;
			if (filters.getTakeoffTimeFilter().isValid() || filters.getLandingTimeFilter().isValid() ||
					(filters.getTakeoffBackTimeFilter() != null && filters.getTakeoffBackTimeFilter().isValid()) ||
					(filters.getLandingBackTimeFilter() != null && filters.getLandingBackTimeFilter().isValid())) {
				timeFiltersPageView = new TimeFiltersScrollView(getContext());
				timeFiltersPageView.init(filtersSet, segmentList);
				timeFiltersPageView.setListener(listener);

				addView(timeFiltersPageView);
				addView(createDivider(this));
			}

			if (filters.getStopOverSizeFilter().isValid() || filters.getStopOverDelayFilter().isValid() || filters.getPriceFilter().isValid()) {
				stopOverAndPriceFiltersPageView = new StopOverAndPriceFiltersPageView(getContext());
				stopOverAndPriceFiltersPageView.init(filters, segmentList);
				stopOverAndPriceFiltersPageView.setListener(listener);
				addView(stopOverAndPriceFiltersPageView);
				addView(createDivider(this));
			}

			if (filters.getAirportsFilter().isValid()) {
				ExpandedListView airportFilterView = createAirportFilterView(filters.getAirportsFilter());
				airportsFilterViews.add(airportFilterView);
				addView(airportFilterView);
				addView(createDivider(this));
			}

			if (filters.getAirlinesFilter().isValid()) {
				ExpandedListView airlinesFilterView = createAirlinesFilterView(filters.getAirlinesFilter());
				airlinesFilterViews.add(airlinesFilterView);
				addView(airlinesFilterView);
				addView(createDivider(this));
			}

			if (filters.getAllianceFilter().isValid()) {
				ExpandedListView alliancesFilterView = createAlliancesFilterView(filters.getAllianceFilter());
				alliancesFilterViews.add(alliancesFilterView);
				addView(alliancesFilterView);
				addView(createDivider(this));
			}

			if (filters.getAgenciesFilter().isValid()) {
				addAgenciesFilter(filters.getAgenciesFilter());
			}

			if (filters.getPayTypeFilter().isValid()) {
				addPayTypeFilter(filters.getPayTypeFilter());
			}
		}
	}

	private void addPayTypeFilter(PayTypeFilter payTypeFilter) {
		payTypeFilterView = new ExpandedListView(getContext(), null);
		PayTypeAdapter payTypeAdapter = new PayTypeAdapter(getContext(), payTypeFilter.getPayTypeList(), false);
		payTypeFilterView.setAdapter(payTypeAdapter);
		payTypeFilterView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				FilterView.this.listener.onChange();
			}
		});

		addView(payTypeFilterView);
		addView(createDivider(this));
	}

	private void addAgenciesFilter(AgenciesFilter agenciesFilter) {
		agenciesFilterView = new ExpandedListView(getContext(), null);
		AgencyAdapter adapter = new AgencyAdapter(getContext(), agenciesFilter.getAgenciesList(), false);
		agenciesFilterView.setAdapter(adapter);
		agenciesFilterView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				FilterView.this.listener.onChange();
			}
		});
		addView(agenciesFilterView);
		addView(createDivider(this));
	}

	private boolean isComplexSearchFilters(FiltersSet filtersSet) {
		return filtersSet instanceof OpenJawFiltersSet;
	}

	private void addPriceFilter(BaseNumericFilter priceFilter) {
		if (!priceFilter.isValid()) return;

		priceFiltersView = createPriceFilterView(priceFilter);
		addView(priceFiltersView);
		addView(createDivider(this));
	}

	private void addSegmentFilters(OpenJawFiltersSet filter, List<ResultsSegment> segmentList) {
		for (Integer segmentIndex : filter.getSegmentFilters().keySet()) {
			SegmentFilter segmentFilter = filter.getSegmentFilters().get(segmentIndex);
			if (!segmentFilter.getAirlinesFilter().isValid() && !segmentFilter.getAirportsFilter().isValid() &&
					!segmentFilter.getAllianceFilter().isValid() && !segmentFilter.getTakeoffTimeFilter().isValid() &&
					!segmentFilter.getLandingTimeFilter().isValid()) continue;

			View segmentFiltersGroup = createSegmentFiltersGroup(segmentList.get(segmentIndex),
					filter.getSegmentFilters().get(segmentIndex));
			addView(segmentFiltersGroup);
		}
	}

	private View createSegmentFiltersGroup(final ResultsSegment segment, final SegmentFilter segmentFilter) {
		SegmentExpandableView segmentExpandableView = new SegmentExpandableView(getContext());
		segmentExpandableView.setTitleText(getAirportOriginIata(segment) + " " + getResources().getString(R.string.dot) + " " +
				getAirportDestinationIata(segment));

		if (segmentFilter.getTakeoffTimeFilter().isValid() || segmentFilter.getLandingTimeFilter().isValid()) {
			TakeoffLandingFilterView takeoffLandingFilterView = createTakeoffLandingFilter(segmentFilter.getTakeoffTimeFilter(),
					segmentFilter.getLandingTimeFilter(), getAirportOriginIata(segment),
					getAirportDestinationIata(segment), false);
			takeoffLandingFilterViews.add(takeoffLandingFilterView);
			segmentExpandableView.addContentView(takeoffLandingFilterView);
			segmentExpandableView.addContentView(createDivider(segmentExpandableView.getContentLayout()));
		}

		if (segmentFilter.getStopOverCountFilter().isValid() || segmentFilter.getStopOverDelayFilter().isValid()) {
			StopOverFilterView stopOverFilterView = createStopOverFilterView(segmentFilter.getStopOverCountFilter(),
					segmentFilter.getStopOverDelayFilter(), segmentFilter.getOvernightFilter());
			stopOverFilterViews.add(stopOverFilterView);
			segmentExpandableView.addContentView(stopOverFilterView);
			segmentExpandableView.addContentView(createDivider(segmentExpandableView.getContentLayout()));
		}

		if (segmentFilter.getAllianceFilter().isValid()) {
			ExpandedListView alliancesListView = createAlliancesFilterView(segmentFilter.getAllianceFilter());
			alliancesFilterViews.add(alliancesListView);
			segmentExpandableView.addContentView(alliancesListView);
			segmentExpandableView.addContentView(createDivider(segmentExpandableView.getContentLayout()));
		}

		if (segmentFilter.getAirportsFilter().isValid()) {
			ExpandedListView airportListView = createAirportFilterView(segmentFilter.getAirportsFilter());
			airportsFilterViews.add(airportListView);
			segmentExpandableView.addContentView(airportListView);
			segmentExpandableView.addContentView(createDivider(segmentExpandableView.getContentLayout()));
		}

		if (segmentFilter.getAirlinesFilter().isValid()) {
			ExpandedListView airlinesListView = createAirlinesFilterView(segmentFilter.getAirlinesFilter());
			segmentExpandableView.addContentView(airlinesListView);
			segmentExpandableView.addContentView(createDivider(segmentExpandableView.getContentLayout()));
			airlinesFilterViews.add(airlinesListView);
		}

		return segmentExpandableView;
	}

	private String getAirportOriginIata(ResultsSegment segment) {
		return segment.getOrigin();
	}

	private String getAirportDestinationIata(ResultsSegment segment) {
		return segment.getDestination();
	}


	private ExpandedListView createAirlinesFilterView(AirlinesFilter airlinesFilter) {
		ExpandedListView airlinesListView = new ExpandedListView(getContext(), null);
		AirlinesAdapter airlinesAdapter = new AirlinesAdapter(getContext(), airlinesFilter.getAirlineList(), false);
		airlinesListView.setAdapter(airlinesAdapter);
		airlinesListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return airlinesListView;
	}

	private ExpandedListView createAirportFilterView(AirportsFilter airportsFilter) {
		ExpandedListView airportListView = new ExpandedListView(getContext(), null);
		AirportsAdapter airportsAdapter = new AirportsAdapter(getContext(),
				airportsFilter.getOriginAirportList(),
				airportsFilter.getDestinationAirportList(),
				airportsFilter.getStopOverAirportList(), false);
		airportListView.setAdapter(airportsAdapter);
		airportListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return airportListView;
	}

	private ExpandedListView createAlliancesFilterView(AllianceFilter allianceFilter) {
		ExpandedListView alliancesListView = new ExpandedListView(getContext(), null);
		AlliancesAdapter adapter = new AlliancesAdapter(getContext(), allianceFilter.getAllianceList(), false);
		alliancesListView.setAdapter(adapter);
		alliancesListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return alliancesListView;
	}

	private StopOverFilterView createStopOverFilterView(final BaseNumericFilter stopOverFilter,
	                                                    final BaseNumericFilter stopOverDelayFilter,
	                                                    final OvernightFilter overnightFilter) {
		StopOverFilterView stopOverFilterView = new StopOverFilterView(getContext());
		stopOverFilterView.init(stopOverFilter, stopOverDelayFilter,
				overnightFilter, new StopOverFilterView.OnStopOverFilterChangedListener() {
					@Override
					public void onStopOverCountChanged(int max) {
						stopOverFilter.setCurrentMaxValue(max);
						listener.onChange();
					}

					@Override
					public void onStopOverDurationChanged(int min, int max) {
						stopOverDelayFilter.setCurrentMaxValue(max);
						stopOverDelayFilter.setCurrentMinValue(min);
						listener.onChange();
					}

					@Override
					public void onOvernightChanged(boolean overnight) {
						overnightFilter.setAirportOvernightAvailable(overnight);
						listener.onChange();
					}
				});
		return stopOverFilterView;
	}

	private TakeoffLandingFilterView createTakeoffLandingFilter(final BaseNumericFilter takeoffFilter, final BaseNumericFilter landingFilter,
	                                                            String origin, String destination, boolean isBackSegment) {
		TakeoffLandingFilterView takeoffLandingFilterView = new TakeoffLandingFilterView(getContext());
		takeoffLandingFilterView.init(takeoffFilter, landingFilter,
				origin, destination, isBackSegment,
				new TakeoffLandingFilterView.OnTakeoffLandingTimeFilterChanged() {
					@Override
					public void onTakeoffTimeChanged(int min, int max) {
						takeoffFilter.setCurrentMinValue(min);
						takeoffFilter.setCurrentMaxValue(max);
						listener.onChange();
					}

					@Override
					public void onLandingTimeChanged(int min, int max) {
						landingFilter.setCurrentMinValue(min);
						landingFilter.setCurrentMaxValue(max);
						listener.onChange();
					}
				});
		return takeoffLandingFilterView;
	}

	private View createDivider(ViewGroup rootView) {
		return LayoutInflater.from(getContext()).inflate(R.layout.filters_divider,
				rootView, false);
	}

	private BaseFilterView createPriceFilterView(final BaseNumericFilter priceFilter) {
		BaseFilterView filterView = new BaseFilterView(getContext(), null, BaseFilterView.PRICE_FILTER,
				priceFilter.getMinValue(), priceFilter.getMaxValue(),
				priceFilter.getCurrentMaxValue(),
				new OnRangeChangeListener() {
					@Override
					public void onChange(int max) {
						priceFilter.setCurrentMaxValue(max);
						listener.onChange();
					}
				});
		filterView.setEnabled(priceFilter.isEnabled());
		return filterView;
	}

	public void clearViews() {
		if (priceFiltersView != null) priceFiltersView.clear();
		for (TakeoffLandingFilterView takeoffLandingFilterView : takeoffLandingFilterViews) {
			takeoffLandingFilterView.clearFilterViews();
		}
		for (StopOverFilterView stopOverFilterView : stopOverFilterViews) {
			stopOverFilterView.clearFilterViews();
		}
		for (ExpandedListView alliancesFilterView : alliancesFilterViews) {
			alliancesFilterView.notifyDataChanged();
		}
		for (ExpandedListView airlinesFilterView : airlinesFilterViews) {
			airlinesFilterView.notifyDataChanged();
		}
		for (ExpandedListView airportsFilterView : airportsFilterViews) {
			airportsFilterView.notifyDataChanged();
		}
		if (agenciesFilterView != null) agenciesFilterView.notifyDataChanged();
		if (payTypeFilterView != null) payTypeFilterView.notifyDataChanged();
		if (timeFiltersPageView != null) timeFiltersPageView.clearFilters();
		if (stopOverAndPriceFiltersPageView != null) stopOverAndPriceFiltersPageView.clearFilters();
	}
}