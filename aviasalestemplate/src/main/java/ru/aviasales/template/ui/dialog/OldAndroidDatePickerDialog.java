package ru.aviasales.template.ui.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class OldAndroidDatePickerDialog extends DatePickerDialog {

	private Calendar minDate;
	private Calendar maxDate;
	private java.text.DateFormat titleDateFormat;

	public OldAndroidDatePickerDialog(Context context, OnDateSetListener callBack,
	                                  int year, int monthOfYear, int dayOfMonth, Calendar minDate, Calendar maxDate) {
		super(context,callBack,year,monthOfYear,dayOfMonth);
		this.minDate=minDate;
		this.maxDate=maxDate;
		titleDateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL);
	}

	@Override
	public void onDateChanged (DatePicker view, int year, int month, int day) {
		Calendar newDate=Calendar.getInstance();
		newDate.set(year, month,day);

		if(minDate!=null && minDate.after(newDate))   {
			view.init(minDate.get(Calendar.YEAR),minDate.get(Calendar.MONTH),
					minDate.get(Calendar.DAY_OF_MONTH),this);
			setTitle(titleDateFormat.format(minDate.getTime()));
		}       else if(maxDate!=null && maxDate.before(newDate)) {
			view.init(maxDate.get(Calendar.YEAR),maxDate.get(Calendar.MONTH),
					maxDate.get(Calendar.DAY_OF_MONTH),this);
			setTitle(titleDateFormat.format(maxDate.getTime()));
		}      else {
			view.init(year, month, day,this);
			setTitle(titleDateFormat.format(newDate.getTime()));
		}
	}
}
