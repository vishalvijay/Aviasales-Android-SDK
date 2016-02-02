package ru.aviasales.template.ui.view.filters.time_filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import ru.aviasales.template.R;
import ru.aviasales.template.filters.BaseNumericFilter;

public class FiltersTimeOfDayView extends FrameLayout implements View.OnClickListener {
	private static final int morningMinTime = 0; // in minutes
	private static final int morningMaxTime = 12 * 60;
	private static final int dayMinTime = 12 * 60;
	private static final int dayMaxTime = 18 * 60;
	private static final int eveningMinTime = 18 * 60;
	private static final int eveningMaxTime = 24 * 60;

	private static final String MORNING_TAG = "morning";
	private static final String DAY_TAG = "day";
	private static final String EVENING_TAG = "evening";

	private Button morningButton;
	private Button dayButton;
	private Button eveningButton;

	private OnButtonsStateChangeListener listener;

	@Override
	public void onClick(View view) {
		if (view.getTag() == null) return;
		if (view.getTag().equals(MORNING_TAG)) {
			setSelectedButton(true, false, false);
			sendCallback(morningMinTime, morningMaxTime);
		} else if (view.getTag().equals(DAY_TAG)) {
			setSelectedButton(false, true, false);
			sendCallback(dayMinTime, dayMaxTime);
		} else if (view.getTag().equals(EVENING_TAG)) {
			setSelectedButton(false, false, true);
			sendCallback(eveningMinTime, eveningMaxTime);
		}
	}

	private void sendCallback(int min, int max) {
		if (listener != null) {
			listener.onChanged(min, max);
		}
	}

	public interface OnButtonsStateChangeListener {
		void onChanged(int newMin, int newMax);
	}

	public FiltersTimeOfDayView(Context context) {
		super(context);
		setupViews(context);
	}

	public FiltersTimeOfDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews(context);
	}

	public FiltersTimeOfDayView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupViews(context);
	}

	private void setupViews(Context context) {
		LayoutInflater.from(context).inflate(R.layout.filters_day_time_layout, this, true);

		morningButton = (Button) findViewById(R.id.btn_range_seekbar_morning);
		morningButton.setTag(MORNING_TAG);
		dayButton = (Button) findViewById(R.id.btn_range_seekbar_day);
		dayButton.setTag(DAY_TAG);
		eveningButton = (Button) findViewById(R.id.btn_range_seekbar_evening);
		eveningButton.setTag(EVENING_TAG);

		morningButton.setOnClickListener(this);
		dayButton.setOnClickListener(this);
		eveningButton.setOnClickListener(this);
	}

	public void setupButtonsState(BaseNumericFilter filter) {
		setDisabledButtons(filter.getMinValue(), filter.getMaxValue());
		if (!filter.isActive()) {
			setDefaultState();
			return;
		}

		setSelectedButton(filter.getCurrentMinValue(), filter.getCurrentMaxValue());
	}

	public void setOnButtonsStateChanged(OnButtonsStateChangeListener listener) {
		this.listener = listener;
	}

	private void setSelectedButton(int selectedMinTime, int selectedMaxTime) {
		if (selectedMinTime >= morningMinTime && selectedMaxTime <= morningMaxTime) {
			setSelectedButton(true, false, false);
		} else if (selectedMinTime >= dayMinTime && selectedMaxTime <= dayMaxTime) {
			setSelectedButton(false, true, false);
		} else if (selectedMinTime >= eveningMinTime && selectedMaxTime <= eveningMaxTime) {
			setSelectedButton(false, false, true);
		} else {
			setSelectedButton(false, false, false);
		}
	}

	private void setDisabledButtons(int minValue, int maxValue) {
		morningButton.setEnabled(!(minValue > morningMaxTime || maxValue < morningMinTime));
		dayButton.setEnabled(!(minValue > dayMaxTime || maxValue < dayMinTime));
		eveningButton.setEnabled(!(minValue > eveningMaxTime || maxValue < eveningMinTime));

		if (!dayButton.isEnabled() && !eveningButton.isEnabled()) {
			morningButton.setEnabled(false);
		}

		if (!morningButton.isEnabled() && !eveningButton.isEnabled()) {
			dayButton.setEnabled(false);
		}

		if (!dayButton.isEnabled() && !morningButton.isEnabled()) {
			eveningButton.setEnabled(false);
		}
	}

	private void setSelectedButton(boolean morningButtonSelected, boolean dayButtonSelected, boolean eveningButtonSelected) {
		morningButton.setSelected(morningButtonSelected);
		dayButton.setSelected(dayButtonSelected);
		eveningButton.setSelected(eveningButtonSelected);
	}

	public void setDefaultState() {
		morningButton.setSelected(false);
		dayButton.setSelected(false);
		eveningButton.setSelected(false);
	}
}
