package ru.aviasales.template.ui.view.filters.time_filters;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ru.aviasales.template.filters.BaseNumericFilter;
import ru.aviasales.template.ui.listener.OnRangeSeekBarChangeListener;
import ru.aviasales.template.ui.view.filters.BaseRangeSeekBarFilterView;


public class TakeoffLandingFilterView extends LinearLayout {
	private BaseRangeSeekBarFilterView takeoffTimeFilterView;
	private FiltersTimeOfDayView takeoffTimeFilterAdditionalView;
	private BaseRangeSeekBarFilterView landingTimeFilterView;
	private OnTakeoffLandingTimeFilterChanged listener;

	public TakeoffLandingFilterView(Context context) {
		super(context);
		setupLayout();
	}

	public TakeoffLandingFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout();
	}

	public TakeoffLandingFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupLayout();
	}

	private void setupLayout() {
		setOrientation(VERTICAL);
	}

	public interface OnTakeoffLandingTimeFilterChanged {
		void onTakeoffTimeChanged(int min, int max);

		void onLandingTimeChanged(int min, int max);
	}

	public void init(final BaseNumericFilter takeoffFilter, BaseNumericFilter landingFilter,
	                 String takeoffIata, String landingIata, boolean isBackSegment,
	                 final OnTakeoffLandingTimeFilterChanged listener) {
		this.listener = listener;

		if (takeoffFilter.isValid()) {
			takeoffTimeFilterView = initTimeFilterView(takeoffFilter, takeoffIata,
					isBackSegment ? BaseRangeSeekBarFilterView.TYPE_TAKEOFF_BACK_TIME_FILTER : BaseRangeSeekBarFilterView.TYPE_TAKEOFF_TIME_FILTER,
					new OnRangeSeekBarChangeListener() {
						@Override
						public void onChange(int min, int max) {
							if (getContext() == null) return;

							if (TakeoffLandingFilterView.this.listener != null) {
								TakeoffLandingFilterView.this.listener.onTakeoffTimeChanged(min, max);
							}

							if (takeoffTimeFilterAdditionalView != null) {
								takeoffTimeFilterAdditionalView.setupButtonsState(takeoffFilter);
							}
						}
					});

			takeoffTimeFilterAdditionalView = initTakeOffTimeFilterAdditionalView(takeoffFilter);

			addView(takeoffTimeFilterView);
			addView(takeoffTimeFilterAdditionalView);
		}

		if (landingFilter.isValid()) {
			landingTimeFilterView = initTimeFilterView(landingFilter, landingIata,
					isBackSegment ? BaseRangeSeekBarFilterView.TYPE_LANDING_BACK_TIME_FILTER : BaseRangeSeekBarFilterView.TYPE_LANDING_TIME_FILTER,
					new OnRangeSeekBarChangeListener() {
						@Override
						public void onChange(int min, int max) {
							if (getContext() == null) return;

							if (TakeoffLandingFilterView.this.listener != null) {
								TakeoffLandingFilterView.this.listener.onLandingTimeChanged(min, max);
							}
						}
					});
			addView(landingTimeFilterView);
		}
	}

	public void init(BaseNumericFilter takeoffFilter, BaseNumericFilter landingFilter,
	                 String takeoffIata, String landingIata,
	                 OnTakeoffLandingTimeFilterChanged listener) {
		init(takeoffFilter, landingFilter, takeoffIata, landingIata, false, listener);
	}


	private BaseRangeSeekBarFilterView initTimeFilterView(final BaseNumericFilter takeoffTimeFilter, String iata, int type,
	                                                      OnRangeSeekBarChangeListener onRangeSeekBarChangeListener) {
		BaseRangeSeekBarFilterView filterView = new BaseRangeSeekBarFilterView(getContext(), null, iata, type,
				takeoffTimeFilter.getMinValue(), takeoffTimeFilter.getMaxValue(),
				takeoffTimeFilter.getCurrentMaxValue(), takeoffTimeFilter.getCurrentMinValue(),
				onRangeSeekBarChangeListener
		);
		filterView.setEnabled(takeoffTimeFilter.isEnabled());
		return filterView;
	}

	private FiltersTimeOfDayView initTakeOffTimeFilterAdditionalView(final BaseNumericFilter takeoffFilter) {
		FiltersTimeOfDayView view = new FiltersTimeOfDayView(getContext());
		view.setupButtonsState(takeoffFilter);
		view.setOnButtonsStateChanged(new FiltersTimeOfDayView.OnButtonsStateChangeListener() {
			@Override
			public void onChanged(int min, int max) {
				if (getContext() == null) return;

				if (min < takeoffFilter.getMinValue()) {
					takeoffFilter.setCurrentMinValue(takeoffFilter.getMinValue());
				} else {
					takeoffFilter.setCurrentMinValue(min);
				}

				if (max > takeoffFilter.getMaxValue()) {
					takeoffFilter.setCurrentMaxValue(takeoffFilter.getMaxValue());
				} else {
					takeoffFilter.setCurrentMaxValue(max);
				}
				if (takeoffTimeFilterView != null) {
					takeoffTimeFilterView.setValuesManually(takeoffFilter.getCurrentMinValue(),
							takeoffFilter.getCurrentMaxValue());
				}

				listener.onTakeoffTimeChanged(min, max);
			}
		});
		return view;
	}

	public void clearFilterViews() {
		if (takeoffTimeFilterView != null) {
			takeoffTimeFilterView.clear();
			takeoffTimeFilterAdditionalView.setDefaultState();
		}
		if (landingTimeFilterView != null) {
			landingTimeFilterView.clear();
		}
	}
}
