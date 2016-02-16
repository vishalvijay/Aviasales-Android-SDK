package ru.aviasales.template.utils;

import android.content.Context;
import android.support.annotation.Nullable;
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

	public static final String MIN_AIRPORT_TIME_ZONE = "-11:00";
	public static final String DATE_FORMAT_REG_EXP = "[^M]*M{3}[^M]*";
	private static final String AM_SYMBOL = "a";
	private static final String PM_SYMBOL = "p";

	public static Calendar getMinCalendarDate() {
		Calendar minDate = new GregorianCalendar(TimeZone.getTimeZone("GMT-11"));
		// Fixes bug of passed dates
		minDate.set(Calendar.DAY_OF_MONTH, minDate.get(Calendar.DAY_OF_MONTH));
		minDate.setTimeZone(TimeZone.getDefault());
		minDate.set(Calendar.HOUR_OF_DAY, 0);
		minDate.set(Calendar.MINUTE, 0);
		minDate.set(Calendar.SECOND, 0);
		minDate.set(Calendar.MILLISECOND, 0);
		return minDate;
	}

	public static Date getMinDate() {
		return getMinCalendarDate().getTime();
	}

	public static Calendar getMaxCalendarDate() {
		Calendar maxDate = new GregorianCalendar(TimeZone.getTimeZone("GMT-11"));
		maxDate.set(Calendar.YEAR, maxDate.get(Calendar.YEAR) + 1);
		return maxDate;
	}

	public static Calendar convertToCalendar(String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(Defined.SEARCH_SERVER_DATE_FORMAT);
		try {
			calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
			Log.e("aviasales", e.getMessage());
		}
		return calendar;
	}

	@Nullable
	public static Calendar convertToCalendar(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static String convertToString(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat(Defined.SEARCH_SERVER_DATE_FORMAT, Locale.US);
		return sdf.format(calendar.getTime());
	}

	public static String convertToString(Date date) {
		return convertToString(convertToCalendar(date));
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
		LocalDate todayInShiftTimezone = new LocalDate(DateTimeZone.forID(MIN_AIRPORT_TIME_ZONE));
		LocalDate checkLocalDate = LocalDate.fromCalendarFields(checkDate);
		return checkLocalDate.isBefore(todayInShiftTimezone);
	}

	public static boolean isDateBeforeDateShiftLine(Date date) {
		Calendar checkDate = Calendar.getInstance();
		checkDate.setTime(date);
		return isDateBeforeDateShiftLine(checkDate);
	}

	public static boolean isDateBeforeDateShiftLine(String checkDate) {
		LocalDate todayInShiftTimezone = new LocalDate(DateTimeZone.forID(MIN_AIRPORT_TIME_ZONE));
		LocalDate checkLocalDate = LocalDate.parse(checkDate);
		return checkLocalDate.isBefore(todayInShiftTimezone);
	}

	public static Date getAmPmTime(Integer hr, Integer min) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.set(Calendar.HOUR_OF_DAY, hr);
		calendar.set(Calendar.MINUTE, min);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date getCurrentDateInGMTMinus11Timezone() {
		Date date = new Date();
		date.setTime(date.getTime() - 11 * 1000 * 60 * 60 -
				TimeZone.getDefault().getOffset(date.getTime()));
		return date;
	}

	public static Date getCurrentDayMidnight(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static String convertDateFromTo(String date, String formatFrom, String formatTo) {
		SimpleDateFormat fdfFrom = new SimpleDateFormat(formatFrom);
		SimpleDateFormat fdfTo = new SimpleDateFormat(formatTo);
		TimeZone utc = TimeZone.getTimeZone(Defined.UTC_TIMEZONE);
		fdfFrom.setTimeZone(utc);
		fdfTo.setTimeZone(utc);

		String dateString = convertDateFromTo(date, fdfFrom, fdfTo);
		if (formatTo.matches(DATE_FORMAT_REG_EXP)) {
			dateString = dateString.replace(".", "");
		}

		return dateString;
	}

	public static String convertDateFromTo(String date, SimpleDateFormat formatFrom, SimpleDateFormat formatTo) {
		Date parsedDate = null;
		try {
			parsedDate = formatFrom.parse(date);
		} catch (ParseException e) {
			Log.e("Parse exception", e.getMessage());
		}
		return formatTo.format(parsedDate);
	}


	public static boolean isFirstDateBeforeSecondDateWithDayAccuracy(Date firstDate, Date secondDate) {
		Calendar firstCalendar = Calendar.getInstance();
		firstCalendar.setTime(firstDate);

		Calendar secondCalendar = Calendar.getInstance();
		secondCalendar.setTime(secondDate);

		return firstCalendar.before(secondCalendar) && !areDatesOfOneDay(firstCalendar, secondCalendar);
	}

	public static boolean areDatesOfOneMonth(Calendar firstDate, Calendar secondDate) {
		return firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
				&& firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH);
	}

	public static boolean areDatesOfOneDay(Calendar firstDate, Calendar secondDate) {
		return firstDate.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
				&& firstDate.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)
				&& firstDate.get(Calendar.DAY_OF_MONTH) == secondDate.get(Calendar.DAY_OF_MONTH);
	}

	public static boolean isDateMoreThanOneYearAfterToday(Date checkDate) {
		Calendar calendarWithYearPassed = Calendar.getInstance();
		calendarWithYearPassed.add(Calendar.YEAR, 1);

		Calendar checkCalendar = Calendar.getInstance();
		checkCalendar.setTime(checkDate);

		return calendarWithYearPassed.before(checkCalendar);
	}

}
