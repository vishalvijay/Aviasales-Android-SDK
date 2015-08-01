package ru.aviasales.template.utils;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.aviasales.template.R;

public class DateUtils {

	private static final String AM_SYMBOL = "a";
	private static final String PM_SYMBOL = "p";

	public static Calendar getMinCalendarDate(){
		Calendar minDate = new GregorianCalendar(TimeZone.getTimeZone("GMT-11"));
		// Fixes bug of passed dates
		minDate.set(Calendar.DAY_OF_MONTH,minDate.get(Calendar.DAY_OF_MONTH));
		minDate.setTimeZone(TimeZone.getDefault());
		minDate.set(Calendar.HOUR_OF_DAY, 0);
		minDate.set(Calendar.MINUTE, 0);
		minDate.set(Calendar.SECOND, 0);
		minDate.set(Calendar.MILLISECOND, 0);
		return minDate;
	}

	public static Calendar getMaxCalendarDate(){
		Calendar maxDate = new GregorianCalendar(TimeZone.getTimeZone("GMT-11"));
		maxDate.set(Calendar.YEAR, maxDate.get(Calendar.YEAR) + 1);
		return maxDate;
	}

	public static Calendar convertToCalendar(String date){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(Defined.SEARCH_SERVER_DATE_FORMAT);
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			Log.e("aviasales", e.getMessage());
		}
		return calendar;
	}

	public static String convertToString(Calendar calendar){
		SimpleDateFormat sdf = new SimpleDateFormat(Defined.SEARCH_SERVER_DATE_FORMAT, Locale.US);
		return sdf.format(calendar.getTime());
	}

	public static DateFormatSymbols getDateFormatSymbols() {
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
		dateFormatSymbols.setAmPmStrings(new String[]{AM_SYMBOL, PM_SYMBOL});
		return dateFormatSymbols;
	}

	public static DateFormatSymbols getFormatSymbolsShort(Context context) {
		DateFormatSymbols russSymbol = new DateFormatSymbols(Locale.getDefault());
		russSymbol.setShortMonths(context.getResources().getStringArray(R.array.months_short_3));
		russSymbol.setShortWeekdays(context.getResources().getStringArray(R.array.weeks_short_2));
		return russSymbol;
	}

	public static boolean isDateBeforeDateShiftLine(Calendar checkDate) {
		// We don't use -12 because no any airports in that zone
		LocalDate todayInShiftTimezone = new LocalDate(DateTimeZone.forID("-11:00"));
		LocalDate checkLocalDate = LocalDate.fromCalendarFields(checkDate);
		return checkLocalDate.isBefore(todayInShiftTimezone);
	}


	public static Date getAmPmTime(Integer hr, Integer min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.set(Calendar.HOUR_OF_DAY, hr);
		calendar.set(Calendar.MINUTE, min);
		return new Date(calendar.getTimeInMillis());
	}

}
