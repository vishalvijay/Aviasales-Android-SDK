package ru.aviasales.template.ui.view.filters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.listener.OnRangeSeekBarChangeListener;
import ru.aviasales.template.ui.view.RangeBar;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Defined;
import ru.aviasales.template.utils.StringUtils;
import ru.aviasales.template.utils.ViewCompatUtils;

public class BaseRangeSeekBarFilterView extends LinearLayout {


	public static final int TYPE_DURATION_FILTER = 223;
	public static final int TYPE_PRICE_FILTER = 224;
	public static final int TYPE_STOPS_SIZE_FILTER = 226;
	public static final int TYPE_TAKEOFF_TIME_FILTER = 327;
	public static final int TYPE_TAKEOFF_BACK_TIME_FILTER = 328;
	public static final int TYPE_LANDING_TIME_FILTER = 330;
	public static final int TYPE_LANDING_BACK_TIME_FILTER = 331;
	public static final int TYPE_STOP_OVER_DELAY_FILTER = 332;

	private static final String KEY_PARCELABLE = "SUPER";
	private static final String KEY_MIN_VALUE = "MIN";
	private static final String KEY_MAX_VALUE = "MAX";
	private static final String KEY_CURRENT_MIN_VALUE = "CMIN";
	private static final String KEY_CURRENT_MAX_VALUE = "CMAX";

	private final TextView tvValues;
	private final TextView tvTitle;
	private final RangeBar seekBar;

	private final int type;
	private final OnRangeSeekBarChangeListener listener;

	private int currentMin;
	private int currentMax;
	private int min;
	private int max;

	public BaseRangeSeekBarFilterView(Context context, AttributeSet attrs, Integer type, int min, int max, int currentMax,
	                                  OnRangeSeekBarChangeListener listener) {
		this(context, attrs, null, type, min, max, currentMax, min, listener);
	}


	public BaseRangeSeekBarFilterView(Context context, AttributeSet attrs, Integer type, int min, int max, int currentMax,
	                                  int currentMin, OnRangeSeekBarChangeListener listener) {
		this(context, attrs, null, type, min, max, currentMax, currentMin, listener);
	}

	public BaseRangeSeekBarFilterView(Context context, AttributeSet attrs, String iata, Integer type, int min,
	                                  int max, int currentMax,
	                                  int currentMin, OnRangeSeekBarChangeListener listener) {
		super(context, attrs);
		LayoutInflater.from(context)
				.inflate(R.layout.price_filter_view, this, true);

		if (currentMax > max) {
			this.currentMax = max;
		} else {
			this.currentMax = currentMax;
		}

		if (currentMin < min) {
			this.currentMin = min;
		} else {
			this.currentMin = currentMin;
		}

		this.min = min;
		this.max = max;

		this.listener = listener;
		seekBar = (RangeBar) findViewById(R.id.sbar_price_filter_view);

		tvValues = (TextView) findViewById(R.id.tv_values);
		tvTitle = (TextView) findViewById(R.id.tv_title);

		this.type = type;
		switch (type) {
			case TYPE_TAKEOFF_TIME_FILTER:
				tvTitle.setText(context.getString(R.string.range_flight_from) + (iata != null ? ", " + iata : ""));
				break;
			case TYPE_TAKEOFF_BACK_TIME_FILTER:
				tvTitle.setText(context.getString(R.string.range_flight_return) + (iata != null ? ", " + iata : ""));
				break;
			case TYPE_STOP_OVER_DELAY_FILTER:
				tvTitle.setText(context.getString(R.string.base_filter_stop_over));
				break;
			case TYPE_LANDING_TIME_FILTER:
				tvTitle.setText(context.getString(R.string.base_filter_landing) + (iata != null ? ", " + iata : ""));
				break;
			case TYPE_LANDING_BACK_TIME_FILTER:
				tvTitle.setText(context.getString(R.string.base_filter_landing_back) + (iata != null ? ", " + iata : ""));
				break;
			case TYPE_DURATION_FILTER:
				tvTitle.setText(context.getString(R.string.base_filter_all_flight));
				break;
			case TYPE_PRICE_FILTER:
				tvTitle.setText(context.getString(R.string.base_filter_price));
				break;
			case TYPE_STOPS_SIZE_FILTER:
				seekBar.setUseStepsWhileDragging(true);
				seekBar.setmShowStepsAsDots(true);
				tvTitle.setText(context.getString(R.string.base_filter_stop_over_count));
				break;
			default:
				break;
		}

		seekBar.setRangeValues(min, max);
		seekBar.setProgressColor(getResources().getColor(R.color.colorAsPrimary));

		seekBar.setSelectedMaxValue(this.currentMax);
		seekBar.setSelectedMinValue(this.currentMin);
		updateValues(this.currentMin, this.currentMax);

		seekBar.setOnRangeSeekBarChangeListener(new RangeBar.OnRangeSeekBarChangeListener() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeBar bar, Double minValue, Double maxValue) {
				getListener().onChange(minValue.intValue(), maxValue.intValue());
			}

			@Override
			public void onStopTrackingTouch() {

			}

			@Override
			public void onRangeSeekBarTracking(RangeBar tRangeSeekBar, Double selectedMinValue, Double selectedMaxValue) {
				updateValues(selectedMinValue.intValue(), selectedMaxValue.intValue());
			}
		});

		tvTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ViewCompatUtils.removeOnGlobalLayoutListener(tvTitle, this);
				seekBar.setSelectedMaxValue(BaseRangeSeekBarFilterView.this.currentMax);
				seekBar.setSelectedMinValue(BaseRangeSeekBarFilterView.this.currentMin);
				updateValues(BaseRangeSeekBarFilterView.this.currentMin, BaseRangeSeekBarFilterView.this.currentMax);
			}
		});
	}

	public void setSingleThumb(boolean isSingleThumb) {
		seekBar.setSingleThumb(isSingleThumb);
	}

	public void clear() {
		seekBar.setSelectedMaxValue(max);
		seekBar.setSelectedMinValue(min);
		updateValues(min, max);
	}

	public void setValuesManually(int min, int max) {
		if (currentMax > max) {
			currentMax = max;
			currentMin = min;
			seekBar.setSelectedMinValue(currentMin);
			seekBar.setSelectedMaxValue(currentMax);
		} else {
			currentMax = max;
			currentMin = min;
			seekBar.setSelectedMaxValue(currentMax);
			seekBar.setSelectedMinValue(currentMin);
		}
		updateValues(min, max);
	}

	private OnRangeSeekBarChangeListener getListener() {
		return listener;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Bundle bundle = new Bundle();
		bundle.putParcelable(KEY_PARCELABLE, super.onSaveInstanceState());
		bundle.putInt(KEY_MIN_VALUE, min);
		bundle.putInt(KEY_MAX_VALUE, max);
		bundle.putInt(KEY_CURRENT_MIN_VALUE, currentMin);
		bundle.putInt(KEY_CURRENT_MAX_VALUE, currentMax);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable parcel) {
		final Bundle bundle = (Bundle) parcel;
		super.onRestoreInstanceState(bundle.getParcelable(KEY_PARCELABLE));
		min = bundle.getInt(KEY_MIN_VALUE);
		max = bundle.getInt(KEY_MAX_VALUE);

		currentMin = bundle.getInt(KEY_CURRENT_MIN_VALUE);
		currentMax = bundle.getInt(KEY_CURRENT_MAX_VALUE);

		seekBar.setSelectedMaxValue(currentMax);
		seekBar.setSelectedMinValue(currentMin);
	}

	private void updateValues(int min, int max) {

		String minValue = null;
		String maxValue = null;
		SimpleDateFormat timeFormat;

		if (!DateFormat.is24HourFormat(getContext())) {
			timeFormat = new SimpleDateFormat(Defined.AM_PM_FILTERS_TIME_FORMAT);
			timeFormat.setDateFormatSymbols(DateUtils.getDateFormatSymbols());
		} else {
			timeFormat = new SimpleDateFormat(Defined.FILTERS_TIME_FORMAT);
		}
		TimeZone utc = TimeZone.getTimeZone(Defined.UTC_TIMEZONE);
		timeFormat.setTimeZone(utc);
		String rangeWith = getResources().getString(R.string.range_with);
		String rangeTo = getResources().getString(R.string.range_to);
		String rangeFrom = getResources().getString(R.string.range_from);

		switch (type) {
			case TYPE_TAKEOFF_TIME_FILTER:
			case TYPE_TAKEOFF_BACK_TIME_FILTER:
			case TYPE_LANDING_TIME_FILTER:
			case TYPE_LANDING_BACK_TIME_FILTER:
				minValue = timeFormat.format(DateUtils.getAmPmTime(min / 60, min % 60));
				maxValue = timeFormat.format(DateUtils.getAmPmTime(max / 60, max % 60));

				tvValues.setText(rangeWith + " " + minValue + " " + rangeTo + " " + maxValue);
				break;
			case TYPE_STOP_OVER_DELAY_FILTER:
				minValue = String.format("%02d", min / 60) + ":" + String.format("%02d", min % 60);
				maxValue = String.format("%02d", max / 60) + ":" + String.format("%02d", max % 60);

				tvValues.setText(rangeFrom + " " + minValue + " " + rangeTo + " " + maxValue);

				break;

			case TYPE_DURATION_FILTER:
				String value = String.format(getResources().getString(R.string.filters_hours_and_minutes), (max) / 60, (max) % 60);

				tvValues.setText(value);
				return;
			case TYPE_PRICE_FILTER:
				String formatedPrice = String.format(getResources().getString(R.string.filters_price), getPrice(max)) + " ";
				String currencyAbbreviation = CurrencyUtils.getAppCurrency(getContext()).toLowerCase();

				tvValues.setText(formatedPrice + currencyAbbreviation);
				return;
			case TYPE_STOPS_SIZE_FILTER:
				tvValues.setText(Integer.toString(max));
				return;
			default:
				break;

		}

	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		tvValues.setEnabled(enabled);
		tvTitle.setEnabled(enabled);
		seekBar.setEnabled(enabled);
	}

	private String getPrice(int price) {
		String appCurCode = CurrencyUtils.getAppCurrency(getContext());
		Map<String, Double> currencies = getCurrencyRates();
		return StringUtils.formatPriceInAppCurrency(price, appCurCode, currencies);
	}

	private Map<String, Double> getCurrencyRates() {
		return CurrencyUtils.getCurrencyRates();
	}


	public void refresh() {
		updateValues((int) seekBar.getSelectedMinValue(), (int) seekBar.getSelectedMaxValue());
	}
}