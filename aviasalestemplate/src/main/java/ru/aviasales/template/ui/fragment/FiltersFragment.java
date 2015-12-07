package ru.aviasales.template.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.GeneralFilter;
import ru.aviasales.template.filters.manager.FiltersManager;
import ru.aviasales.template.ui.adapter.AgencyAdapter;
import ru.aviasales.template.ui.adapter.AirlinesAdapter;
import ru.aviasales.template.ui.adapter.AirportsAdapter;
import ru.aviasales.template.ui.adapter.AlliancesAdapter;
import ru.aviasales.template.ui.view.FiltersTimeOfDayView;
import ru.aviasales.template.ui.view.OvernightFilterView;
import ru.aviasales.template.ui.view.SingleSlideFilterView;
import ru.aviasales.template.ui.view.StopOverFilterView;
import ru.aviasales.template.ui.view.TwoSideSeekBarFilterView;

public class FiltersFragment extends BaseFragment {

	private LinearLayout filtersLinearLayout;
	private SingleSlideFilterView priceFilterView;
	private TwoSideSeekBarFilterView takeoffTimeFilterView;
	private TwoSideSeekBarFilterView takeoffBackTimeFilterView = null;
	private StopOverFilterView stopOverFilterView;
	private SingleSlideFilterView durationFilterView;
	private TwoSideSeekBarFilterView stopOverDelay;
	private ExpandedListView airlineListView;
	private ExpandedListView airportListView;
	private ExpandedListView allianceExpandedListView;
	private ExpandedListView agencyExpandedListView;
	private ExpandedListView airlineExpandedListView;
	private ExpandedListView agencyListView;
	private OvernightFilterView overnightFilterView;
	private ViewGroup destinationView;

	private FiltersTimeOfDayView takeoffTimeFilterAdditionalView;
	private FiltersTimeOfDayView takeoffBackTimeFilterAdditionalView;

	private TextView tvFoundTickets;

	private View resetBtn;

	public static FiltersFragment newInstance() {
		return new FiltersFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}


	private GeneralFilter getFilters() {
		return FiltersManager.getInstance().getFilters();
	}

	private void setupActionBar() {
		getActionBar().show();
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
		getActionBar().setTitle(getString(R.string.ab_title_filters));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		destinationView = (ViewGroup) inflater.inflate(R.layout.filters_fragment, container, false);

		if (getFilters() == null) return destinationView;

		setupActionBar();
		setupViews();
		return destinationView;
	}

	private void setupViews() {
		tvFoundTickets = (TextView) destinationView.findViewById(R.id.tv_filters_fragment_found_tickets);
		filtersLinearLayout = (LinearLayout) destinationView.findViewById(R.id.llay_filters_fragment_components);


		initAndAddFiltersToView();

		resetBtn = destinationView.findViewById(R.id.btn_filters_fragment_clear);
		resetBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetFilters();
			}
		});

		applyFilters();
	}

	private void applyFilters() {
// TODO: 12/3/15 починить
//		FiltersManager.getInstance().filterSearchData(AviasalesSDK.getInstance().getSearchData(), new FiltersManager.OnFilterResultListener() {
//			@Override
//			public void onFilteringFinished(List<TicketData> filteredTicketsData) {
//
//				if (resetBtn != null) {
//					if (!getFilters().isActive()) {
//						resetBtn.setEnabled(false);
//					} else {
//						resetBtn.setEnabled(true);
//					}
//				}
//
//				setFoundTicketsText(filteredTicketsData.size());
//			}
//		});
		setFiltersVisibility();
	}

	private void initAndAddFiltersToView() {
		if (getParentContext() == null) return;

		stopOverFilterView = initStopOverFilterView();
		filtersLinearLayout.addView(stopOverFilterView);

		overnightFilterView = initOvernightFilterView();
		filtersLinearLayout.addView(overnightFilterView);

		if (getFilters().getPriceFilter().isValid()) {
			priceFilterView = initPriceFilterView();
			filtersLinearLayout.addView(priceFilterView);
		}

		if (getFilters().getDurationFilter().isValid()) {
			durationFilterView = initDurationFilterView();
			filtersLinearLayout.addView(durationFilterView);
		}

		if (getFilters().getStopOverDelayFilter().isValid()) {
			stopOverDelay = initStopOverDelayFilterView();
			filtersLinearLayout.addView(stopOverDelay);
		}
		if (getFilters().getTakeoffTimeFilter().isValid()) {
			takeoffTimeFilterView = initTakeoffTimeFilterView();
			takeoffTimeFilterAdditionalView = initTakeOffTimeFilterAdditionalView();
			filtersLinearLayout.addView(takeoffTimeFilterView);
			filtersLinearLayout.addView(takeoffTimeFilterAdditionalView);
		}

		if (getSearchData().getTickets().get(0).getReturnFlights() != null &&
				(getFilters().getTakeoffBackTimeFilter().isValid())) {
			takeoffBackTimeFilterView = initTakeoffBackTimeFilterView();
			takeoffBackTimeFilterAdditionalView = initTakeOffBackTimeFilterAdditionalView();
			filtersLinearLayout.addView(takeoffBackTimeFilterView);
			filtersLinearLayout.addView(takeoffBackTimeFilterAdditionalView);
		}

		if (getFilters().getAllianceFilter().getAllianceList().size() > 0) {
			allianceExpandedListView = initAllianceListView();
			filtersLinearLayout.addView(allianceExpandedListView);
		}

		airlineListView = initAirlineListView();
		filtersLinearLayout.addView(airlineListView);

		airportListView = initAirportListView();
		filtersLinearLayout.addView(airportListView);

		if (getFilters().getAgenciesFilter().getAgenciesList().size() > 1) {
			agencyListView = initAgencyListView();
			filtersLinearLayout.addView(agencyListView);
		}

	}

	private FiltersTimeOfDayView initTakeOffBackTimeFilterAdditionalView() {
		FiltersTimeOfDayView view = new FiltersTimeOfDayView(getParentContext());
		view.setupButtonsState(getFilters().getTakeoffBackTimeFilter());
		view.setOnButtonsStateChanged(new FiltersTimeOfDayView.OnButtonsStateChangeListener() {
			@Override
			public void onChanged(int min, int max) {
				if (getActivity() == null) return;

				getFilters().getTakeoffBackTimeFilter().setCurrentMinValue(min);

				getFilters().getTakeoffBackTimeFilter().setCurrentMaxValue(max);

				if (takeoffBackTimeFilterView != null) {
					takeoffBackTimeFilterView.setValuesManually(getFilters().getTakeoffBackTimeFilter().getCurrentMinValue(),
							getFilters().getTakeoffBackTimeFilter().getCurrentMaxValue());
				}
				applyFilters();
			}
		});
		return view;
	}

	private FiltersTimeOfDayView initTakeOffTimeFilterAdditionalView() {
		FiltersTimeOfDayView view = new FiltersTimeOfDayView(getParentContext());
		view.setupButtonsState(getFilters().getTakeoffTimeFilter());
		view.setOnButtonsStateChanged(new FiltersTimeOfDayView.OnButtonsStateChangeListener() {
			@Override
			public void onChanged(int min, int max) {
				if (getActivity() == null) return;


				getFilters().getTakeoffTimeFilter().setCurrentMinValue(min);
				getFilters().getTakeoffTimeFilter().setCurrentMaxValue(max);

				if (takeoffTimeFilterView != null) {
					takeoffTimeFilterView.setValuesManually(getFilters().getTakeoffTimeFilter().getCurrentMinValue(),
							getFilters().getTakeoffTimeFilter().getCurrentMaxValue());
				}
				applyFilters();
			}
		});
		return view;
	}

	private OvernightFilterView initOvernightFilterView() {
		return new OvernightFilterView(getParentContext(),
				getFilters().getOvernightFilter().isAirportOvernightAvailable(), new OvernightFilterView.OnOvernightStateChange() {
			@Override
			public void onChange(boolean airportOvernight) {
				if (getActivity() == null) return;
				getFilters().getOvernightFilter().setAirportOvernightAvailable(airportOvernight);
				applyFilters();
			}
		});
	}

	private ExpandedListView initAirlineListView() {
		airlineExpandedListView = new ExpandedListView(getParentContext(), null);
		AirlinesAdapter adapter = new AirlinesAdapter(getParentContext(), getFilters().getAirlinesFilter().getAirlineList());
		airlineExpandedListView.setAdapter(adapter);
		airlineExpandedListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				if (getActivity() == null) return;
				applyFilters();
			}
		});
		return airlineExpandedListView;
	}

	private ExpandedListView initAllianceListView() {
		allianceExpandedListView = new ExpandedListView(getParentContext(), null);
		AlliancesAdapter adapter = new AlliancesAdapter(getParentContext(), getFilters().getAllianceFilter().getAllianceList());
		allianceExpandedListView.setAdapter(adapter);
		allianceExpandedListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				if (getActivity() == null) return;
				applyFilters();
			}
		});
		return allianceExpandedListView;
	}

	private ExpandedListView initAirportListView() {
		airportListView = new ExpandedListView(getParentContext(), null);
		AirportsAdapter adapter = new AirportsAdapter(getParentContext(),
				getFilters().getAirportsFilter().getOriginAirportList(),
				getFilters().getAirportsFilter().getDestinationAirportList(),
				getFilters().getAirportsFilter().getStopOverAirportList());
		airportListView.setAdapter(adapter);
		airportListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				if (getActivity() == null) return;
				applyFilters();
			}
		});
		return airportListView;
	}

	private ExpandedListView initAgencyListView() {
		agencyExpandedListView = new ExpandedListView(getParentContext(), null);
		AgencyAdapter adapter = new AgencyAdapter(getParentContext(), getFilters().getAgenciesFilter().getAgenciesList());
		agencyExpandedListView.setAdapter(adapter);
		agencyExpandedListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				if (getActivity() == null) return;
				applyFilters();
			}
		});
		return agencyExpandedListView;
	}

	private TwoSideSeekBarFilterView initStopOverDelayFilterView() {

		return TwoSideSeekBarFilterView.newStopOverTimeFilter(getActivity(),
				getActivity().getString(R.string.base_filter_stop_over),
				getFilters().getStopOverDelayFilter())
				.setListener(new TwoSideSeekBarFilterView.OnRangeSeekBarChangeListener() {
					@Override
					public void onChange(int min, int max) {
						if (getActivity() == null) return;
						getFilters().getStopOverDelayFilter().setCurrentMaxValue(max);
						getFilters().getStopOverDelayFilter().setCurrentMinValue(min);
						applyFilters();
					}
				});
	}

	private SingleSlideFilterView initDurationFilterView() {
		return SingleSlideFilterView.newHoursMinutesFilterView(getActivity(),
				getActivity().getString(R.string.base_filter_all_flight),
				getFilters().getDurationFilter())
				.setListener(new SingleSlideFilterView.OnRangeChangeListener() {
					@Override
					public void onChange(int max) {
						if (getActivity() == null) return;
						getFilters().getDurationFilter().setCurrentMaxValue(max);
						applyFilters();
					}
				});
	}

	private TwoSideSeekBarFilterView initTakeoffBackTimeFilterView() {

		return TwoSideSeekBarFilterView.newTakeOfTimeFilter(getActivity(),
				getActivity().getString(R.string.range_flight_return),
				getFilters().getTakeoffBackTimeFilter())
				.setListener(new TwoSideSeekBarFilterView.OnRangeSeekBarChangeListener() {
					@Override
					public void onChange(int min, int max) {
						if (getActivity() == null) return;
						getFilters().getTakeoffBackTimeFilter().setCurrentMaxValue(max);
						getFilters().getTakeoffBackTimeFilter().setCurrentMinValue(min);
						if (takeoffBackTimeFilterAdditionalView != null) {
							takeoffBackTimeFilterAdditionalView.setupButtonsState(getFilters().getTakeoffBackTimeFilter());
						}
						applyFilters();
					}
				});
	}

	private TwoSideSeekBarFilterView initTakeoffTimeFilterView() {

		return TwoSideSeekBarFilterView.newTakeOfTimeFilter(getActivity(),
				getActivity().getString(R.string.range_flight_from),
				getFilters().getTakeoffTimeFilter())
				.setListener(new TwoSideSeekBarFilterView.OnRangeSeekBarChangeListener() {
					@Override
					public void onChange(int min, int max) {
						if (getActivity() == null) return;
						getFilters().getTakeoffTimeFilter().setCurrentMaxValue(max);
						getFilters().getTakeoffTimeFilter().setCurrentMinValue(min);
						if (takeoffTimeFilterAdditionalView != null) {
							takeoffTimeFilterAdditionalView.setupButtonsState(getFilters().getTakeoffTimeFilter());
						}
						applyFilters();
					}
				});
	}

	private SingleSlideFilterView initPriceFilterView() {
		return SingleSlideFilterView.newPriceFilterView(getActivity(),
				getActivity().getString(R.string.base_filter_price),
				getFilters().getPriceFilter())
				.setListener(new SingleSlideFilterView.OnRangeChangeListener() {
					@Override
					public void onChange(int max) {
						if (getActivity() == null) return;
						getFilters().getPriceFilter().setCurrentMaxValue(max);
						applyFilters();
					}
				});
	}

	private StopOverFilterView initStopOverFilterView() {
		return StopOverFilterView.newStopOverFilterView(getActivity(),
				getFilters().getStopOverFilter()).
				setListener(new StopOverFilterView.OnStopOverChangeState() {
					@Override
					public void onChange(boolean oneStopOver, boolean withoutStopOver, boolean twoStopOver) {
						if (getActivity() == null) return;
						getFilters().getStopOverFilter().setOneStopOver(oneStopOver);
						getFilters().getStopOverFilter().setWithoutStopOver(withoutStopOver);
						getFilters().getStopOverFilter().setTwoPlusStopOver(twoStopOver);
						applyFilters();
					}
				});
	}

	private void setFiltersVisibility() {
		if (stopOverFilterView != null) {
			stopOverFilterView.setMinPrices(getFilters().getStopOverFilter().getWithoutStopOverMinPrice(),
					getFilters().getStopOverFilter().getOneStopOverMinPrice(),
					getFilters().getStopOverFilter().getTwoStopOverMinPrice());
			stopOverFilterView.setViewsEnabled(getFilters().getStopOverFilter().isWithoutStopOverViewEnabled(),
					getFilters().getStopOverFilter().isOneStopOverViewEnabled(),
					getFilters().getStopOverFilter().isTwoPlusStopOverViewEnabled());
		}
		if (stopOverDelay != null) {
			if ((!getFilters().getStopOverFilter().isOneStopOver() || !stopOverFilterView.isOneStopOverEnabled()) &&
					(!getFilters().getStopOverFilter().isTwoPlusStopOver() ||
							!stopOverFilterView.isTwoPlusStopOverEnabled())) {

				stopOverDelay.setVisibility(View.GONE);
			} else {
				stopOverDelay.setVisibility(View.VISIBLE);
			}
		}
		if (overnightFilterView != null) {
			overnightFilterView.setEnabled((getFilters().getOvernightFilter().isAirportOvernightViewEnabled()) &&
					(getFilters().getStopOverFilter().isOneStopOver() || getFilters().getStopOverFilter().isTwoPlusStopOver()));
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void setFoundTicketsText(int count) {

		String text = Integer.toString(count) + " " +
				getResources().getQuantityString(R.plurals.found_tickets, getSearchData().getTickets().size(), getSearchData().getTickets().size());

		tvFoundTickets.setText(text);
	}

	private SearchData getSearchData() {
		return AviasalesSDK.getInstance().getSearchData();
	}

	public void clearFilterViews() {
		if (stopOverFilterView != null) {
			stopOverFilterView.clear();
		}
		if (priceFilterView != null) {
			priceFilterView.clear();
		}
		if (durationFilterView != null) {
			durationFilterView.clear();
		}
		if (stopOverDelay != null) {
			stopOverDelay.clear();
		}
		if (takeoffTimeFilterView != null) {
			takeoffTimeFilterView.clear();
		}
		if (takeoffTimeFilterAdditionalView != null) {
			takeoffTimeFilterAdditionalView.setDefaultState();
		}
		if (takeoffBackTimeFilterView != null) {
			takeoffBackTimeFilterView.clear();
		}
		if (takeoffBackTimeFilterAdditionalView != null) {
			takeoffBackTimeFilterAdditionalView.setDefaultState();
		}
		if (airportListView != null) {
			airportListView.notifyDataChanged();
		}
		if (allianceExpandedListView != null) {
			allianceExpandedListView.notifyDataChanged();
		}
		if (agencyListView != null) {
			agencyListView.notifyDataChanged();
		}
		if (airlineListView != null) {
			airlineListView.notifyDataChanged();
		}
		if (overnightFilterView != null) {
			overnightFilterView.clear(getFilters().getOvernightFilter().isAirportOvernightViewEnabled());
		}

		setFiltersVisibility();
	}

	public void resetFilters() {
		getFilters().clearFilters();
		clearFilterViews();
		applyFilters();
		if (resetBtn != null) {
			resetBtn.setEnabled(false);
		}
	}

	private Context getParentContext() {
		if (destinationView != null) {
			return destinationView.getContext();
		} else {
			return getActivity();
		}
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {

	}
}
