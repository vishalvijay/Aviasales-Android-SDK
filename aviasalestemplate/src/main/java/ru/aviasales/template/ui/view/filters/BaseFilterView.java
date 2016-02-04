package ru.aviasales.template.ui.view.filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.listener.OnRangeChangeListener;
import ru.aviasales.template.ui.view.RangeBar;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.StringUtils;

public class BaseFilterView extends LinearLayout {

	public static final int DURATION_FILTER = 324;
	public static final int PRICE_FILTER = 325;
	public static final int STOPS_SIZE_FILTER = 335;

	private final Context context;
	private final TextView values;
	private final TextView title;
	private final int type;
	private final RangeBar seekBar;
	private final int min;
	private final int max;
	private final OnRangeChangeListener listener;

	public BaseFilterView(Context context, AttributeSet attrs, Integer type, long min, long max, long currentMax, OnRangeChangeListener listener) {
		super(context, attrs);
		LayoutInflater.from(context)
				.inflate(R.layout.price_filter_view, this, true);
		this.context = context;
		this.listener = listener;
		this.min = (int) min;
		this.max = (int) max;

		values = (TextView) findViewById(R.id.tv_values);

		seekBar = (RangeBar) findViewById(R.id.sbar_price_filter_view);
		seekBar.setSaveEnabled(false); // switch off autorotate
		seekBar.setSingleThumb(true);
		title = (TextView) findViewById(R.id.tv_title);
		this.type = type;
		switch (type) {
			case DURATION_FILTER:
				title.setText(context.getString(R.string.base_filter_all_flight));
				break;
			case PRICE_FILTER:
				title.setText(context.getString(R.string.base_filter_price));
				break;
			case STOPS_SIZE_FILTER:
				seekBar.setUseStepsWhileDragging(true);
				seekBar.setmShowStepsAsDots(true);
				title.setText(context.getString(R.string.base_filter_stop_over_count));
				break;
			default:
				break;
		}

		setSeekBarMaxProgress((int) (max - min));
		initProgressBar((int) currentMax);

		updateValues((int) (currentMax - min));

		seekBar.setOnRangeSeekBarChangeListener(new RangeBar.OnRangeSeekBarChangeListener() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeBar bar, Double minValue, Double maxValue) {
				onChange(maxValue.intValue());
			}

			@Override
			public void onStopTrackingTouch() {

			}

			@Override
			public void onRangeSeekBarTracking(RangeBar tRangeSeekBar, Double selectedMinValue, Double selectedMaxValue) {
				updateValues(selectedMaxValue.intValue());
			}
		});
	}

	public void clear() {
		initProgressBar(max);
	}

	public void initProgressBar(int current) {
		seekBar.setSelectedMaxValue(current - min);
		updateValues(current - min);
	}

	public void setSeekBarMaxProgress(int maxProgress) {
		seekBar.setRangeValues(0, maxProgress);
	}

	private void onChange(int result) {
		listener.onChange(result + min);
	}

	public void refresh() {
		updateValues((int) seekBar.getSelectedMaxValue());
	}

	private void updateValues(int max) {

		switch (type) {
			case DURATION_FILTER:
				String value = String.format(context.getResources().getString(R.string.filters_hours_and_minutes), (max + min) / 60, (max + min) % 60);

				values.setText(value);
				return;
			case PRICE_FILTER:
				String formatedPrice = String.format(context.getResources().getString(R.string.filters_price), getPrice(max + min)) + " ";
				String currencyAbbreviation = CurrencyUtils.getAppCurrency(getContext()).toLowerCase();

				values.setText(formatedPrice + currencyAbbreviation);
				return;
			case STOPS_SIZE_FILTER:
				values.setText(Integer.toString(max + min));
				return;
			default:
				break;
		}
	}

	private String getPrice(int price) {
		String appCurCode = CurrencyUtils.getAppCurrency(getContext());
		Map<String, Double> currencies = getCurrencyRates();
		return StringUtils.formatPriceInAppCurrency(price, appCurCode, currencies);
	}

	private Map<String, Double> getCurrencyRates() {
		return CurrencyUtils.getCurrencyRates();
	}


	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		values.setEnabled(enabled);
		title.setEnabled(enabled);
		seekBar.setEnabled(enabled);
	}
}