package ru.aviasales.template.ui.view.filters.stop_over_and_price_filters;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import ru.aviasales.template.filters.BaseNumericFilter;
import ru.aviasales.template.filters.OvernightFilter;
import ru.aviasales.template.ui.listener.OnOvernightStateChange;
import ru.aviasales.template.ui.listener.OnRangeChangeListener;
import ru.aviasales.template.ui.listener.OnRangeSeekBarChangeListener;
import ru.aviasales.template.ui.view.filters.BaseFilterView;
import ru.aviasales.template.ui.view.filters.BaseRangeSeekBarFilterView;


public class StopOverFilterView extends LinearLayout {
	private BaseFilterView stopOverCountFilterView;
	private BaseRangeSeekBarFilterView stopOverDurationFilterView;
	private OvernightFilterView overnightFilterView;
	private OnStopOverFilterChangedListener listener;
	private OvernightFilter overnightFilter;

	public StopOverFilterView(Context context) {
		super(context);
		setupLayout();
	}

	public StopOverFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout();
	}

	public StopOverFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupLayout();
	}

	public interface OnStopOverFilterChangedListener {
		void onStopOverCountChanged(int max);

		void onStopOverDurationChanged(int min, int max);

		void onOvernightChanged(boolean overnight);
	}

	private void setupLayout() {
		setOrientation(VERTICAL);
	}

	public void init(final BaseNumericFilter stopOverCountFilter, BaseNumericFilter stopOverDurationFilter,
	                 OvernightFilter overnightFilter, OnStopOverFilterChangedListener listener) {
		this.listener = listener;
		this.overnightFilter = overnightFilter;

		if (stopOverCountFilter.isValid()) {
			stopOverCountFilterView = initStopOverSeekBarFilter(stopOverCountFilter, BaseFilterView.STOPS_SIZE_FILTER, new OnRangeChangeListener() {
				@Override
				public void onChange(int max) {
					if (getContext() == null) return;

					if (StopOverFilterView.this.listener != null) {
						StopOverFilterView.this.listener.onStopOverCountChanged(max);
					}
				}
			});

			addView(stopOverCountFilterView);
		}

		if (stopOverDurationFilter.isValid()) {
			stopOverDurationFilterView = initStopOverDelayFilterView(stopOverDurationFilter, BaseRangeSeekBarFilterView.TYPE_STOP_OVER_DELAY_FILTER,
					new OnRangeSeekBarChangeListener() {
						@Override
						public void onChange(int min, int max) {
							if (getContext() == null) return;

							if (StopOverFilterView.this.listener != null) {
								StopOverFilterView.this.listener.onStopOverDurationChanged(min, max);
							}
						}
					});
			addView(stopOverDurationFilterView);
		}

		if (overnightFilter.isValid()) {
			overnightFilterView = initOvernightFilterView(overnightFilter, new OnOvernightStateChange() {
				@Override
				public void onChange(boolean airportOvernight) {
					if (getContext() == null) return;

					if (StopOverFilterView.this.listener != null) {
						StopOverFilterView.this.listener.onOvernightChanged(airportOvernight);
					}
				}
			});
			addView(overnightFilterView);
		}
	}

	private BaseRangeSeekBarFilterView initStopOverDelayFilterView(BaseNumericFilter filter, int type, OnRangeSeekBarChangeListener listener) {
		BaseRangeSeekBarFilterView filterView = new BaseRangeSeekBarFilterView(getContext(), null, type, filter.getMinValue(), filter.getMaxValue(),
				filter.getCurrentMaxValue(), filter.getCurrentMinValue(), listener);
		filterView.setEnabled(filter.isEnabled());
		return filterView;
	}

	private BaseFilterView initStopOverSeekBarFilter(BaseNumericFilter filter, int type, OnRangeChangeListener listener) {
		BaseFilterView filterView = new BaseFilterView(getContext(), null, type, filter.getMinValue(), filter.getMaxValue(),
				filter.getCurrentMaxValue(), listener);
		filterView.setEnabled(filter.isEnabled());
		return filterView;
	}

	private OvernightFilterView initOvernightFilterView(OvernightFilter filter, OnOvernightStateChange listener) {
		return new OvernightFilterView(getContext(), filter.isAirportOvernightAvailable(), listener);
	}

	public void clearFilterViews() {
		if (stopOverCountFilterView != null) {
			stopOverCountFilterView.clear();
		}
		if (stopOverDurationFilterView != null) {
			stopOverDurationFilterView.clear();
		}
		if (overnightFilterView != null) {
			overnightFilterView.clear(overnightFilter.isAirportOvernightViewEnabled());
		}
	}
}
