package ru.aviasales.template.ui.dialog;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.lang.reflect.Field;
import java.util.Calendar;

import ru.aviasales.template.R;

public class DatePickerDialogFragment extends BaseDialogFragment implements DatePickerDialog.OnDateSetListener {

	public final static String TAG = "fragment.DatePickerDialogFragment";

	private final static String EXTRA_CALENDAR_MIN_DATE = "extra_calendar_min_date";
	private final static String EXTRA_CALENDAR_MAX_DATE = "extra_calendar_max_date";
	private final static String EXTRA_CALENDAR_CURRENT_DATE = "extra_calendar_current_date";

	private Calendar minDate;
	private Calendar maxDate;

	private int year;
	private int month;
	private int day;

	private OnDateChangedListener onDateChangedListener;


	public interface OnDateChangedListener {
		void onDateChanged(Calendar calendar);

		void onCancel();
	}

	public static DatePickerDialogFragment newInstance(Calendar minDate, Calendar maxDate, Calendar currentDate) {
		DatePickerDialogFragment fragment = new DatePickerDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXTRA_CALENDAR_MIN_DATE, minDate);
		bundle.putSerializable(EXTRA_CALENDAR_MAX_DATE, maxDate);
		bundle.putSerializable(EXTRA_CALENDAR_CURRENT_DATE, currentDate);

		fragment.setArguments(bundle);
		return fragment;
	}

	public void setOnDateChangedListener(OnDateChangedListener listener) {
		onDateChangedListener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();

		minDate = (Calendar) args.getSerializable(EXTRA_CALENDAR_MIN_DATE);
		maxDate = (Calendar) args.getSerializable(EXTRA_CALENDAR_MAX_DATE);
		Calendar currentDate = (Calendar) args.getSerializable(EXTRA_CALENDAR_CURRENT_DATE);

		year = currentDate.get(Calendar.YEAR);
		month = currentDate.get(Calendar.MONTH);
		day = currentDate.get(Calendar.DAY_OF_MONTH);

		if (minDate.getTimeInMillis() > currentDate.getTimeInMillis()) {
			minDate.setTimeInMillis(currentDate.getTimeInMillis() - 1);
		}

		DatePickerDialog picker = null;
		picker = setupDatePicker();

		return picker;
	}

	private DatePickerDialog setupDatePicker() {

		DatePickerDialog pickerDialog = null;

		if (Build.VERSION.SDK_INT >= 11) {
			pickerDialog = new DatePickerDialog(getActivity(), this,
					year, month, day);

			pickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
			pickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

		} else {
			pickerDialog = new OldAndroidDatePickerDialog(getActivity(), this,
					year, month, day, minDate, maxDate);
			pickerDialog.setTitle(getResources().getString(R.string.search_form_return_date_default));
		}

		return pickerDialog;
	}

	private DatePicker getDatePicker(DatePickerDialog picker) {
		if (Build.VERSION.SDK_INT >= 11) {
			return picker.getDatePicker();
		} else {
			Field mDatePickerField = null;
			try {
				mDatePickerField = picker.getClass().getDeclaredField("mDatePicker");
				mDatePickerField.setAccessible(true);
				return (DatePicker) mDatePickerField.get(picker);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void onStart() {
		super.onStart();
		// добавляем кастомный текст для кнопки
		Button nButton = ((AlertDialog) getDialog())
				.getButton(DialogInterface.BUTTON_POSITIVE);
		nButton.setText(getResources().getString(R.string.ok));

	}

	@Override
	public void onDateSet(DatePicker datePicker, int year,
	                      int month, int day) {
		if (onDateChangedListener != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day);
			if (calendar.compareTo(minDate) < 0) {
				calendar = minDate;
			}
			if (calendar.compareTo(maxDate) > 0) {
				calendar = maxDate;
			}
			onDateChangedListener.onDateChanged(calendar);
		}
	}

	@Override
	public String getFragmentTag() {
		return TAG;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (onDateChangedListener != null) {
			onDateChangedListener.onCancel();
		}
		super.onCancel(dialog);
	}
}
