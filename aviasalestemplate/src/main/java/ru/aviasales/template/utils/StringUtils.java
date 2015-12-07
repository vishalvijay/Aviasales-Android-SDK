package ru.aviasales.template.utils;

import android.content.Context;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;

import java.util.List;
import java.util.Map;

import ru.aviasales.core.locale.LocaleUtil;
import ru.aviasales.core.search.object.FlightData;
import ru.aviasales.template.R;

public class StringUtils {

	// returns string in format: 1 234 456 р
	public static String formatPriceInAppCurrency(long priceInDefaultCur, String appCurCode,
	                                              Map<String, Double> currencies) {
		long price;
		if (currencies == null) {
			return getPriceWithDelimiter(priceInDefaultCur);
		}
		price = CurrencyUtils.getPriceInAppCurrency(priceInDefaultCur, appCurCode, currencies);

		return getPriceWithDelimiter(price);
	}

	public static String formatPriceInAppCurrency(int priceInDefaultCur, Context context) {
		return formatPriceInAppCurrency(
				priceInDefaultCur,
				CurrencyUtils.getAppCurrency(context),
				CurrencyUtils.getCurrencyRates());
	}

	public static String getPriceWithDelimiter(long priceInDefaultCur) {
		StringBuilder sb = new StringBuilder();
		String priceStr = String.valueOf(priceInDefaultCur);
		int count = 0;
		for (int i = priceStr.length() - 1; i >= 0; i--) {
			sb.append(priceStr.charAt(i));
			count++;
			if (count == 3 && i > 0) {
				sb.append(LocaleUtil.getPriceDelimiter());
				count = 0;
			}
		}
		return sb.reverse().toString();
	}

	public static String getTransferText(Context context, List<FlightData> flights) {
		StringBuilder builder = new StringBuilder();

		if (flights.size() == 1) {
			builder.append(context.getResources().getString(R.string.results_no_transfers));
		} else {
			for (int i = 0; i < flights.size(); i++) {
				if (i != flights.size() - 1) {
					if (i != 0) {
						builder.append(", ");
					}
					builder.append(flights.get(i).getDestination());
				}
			}
		}

		return builder.toString();
	}

	public static String getDurationString(Context context, Integer durationInMin) {
		return getDefaultDurationString(context, durationInMin);
	}

	// Format: 00h 00m
	private static String getDefaultDurationString(Context context, Integer durationInMin) {
		String durationString = "";
		int hours = durationInMin / 60;
		int minutes = durationInMin % 60;
		String hoursStr;
		if (hours < 10) {
			hoursStr = "0" + hours;
		} else {
			hoursStr = String.valueOf(hours);
		}
		durationString += hoursStr + context.getString(R.string.hour_short) + " ";
		String minutesStr = String.valueOf(minutes);
		if (minutes < 10) {
			minutesStr = "0" + minutes;
		}
		durationString += minutesStr + context.getString(R.string.minute_short);
		return durationString;
	}

	public static String capitalizeFirstLetter(String original) {
		if (original.length() == 0)
			return original;
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static SpannableString getSpannableString(CharSequence string, ParcelableSpan span) {
		SpannableString spannable = new SpannableString(string);
		if (span != null) {
			spannable.setSpan(span, 0, string.length(), 0);
		}
		return spannable;
	}

	public static SpannableStringBuilder getSpannablePriceString(String priceString, String currency) {
		SpannableStringBuilder builder = new SpannableStringBuilder();

		builder.append(priceString);
		builder.append(" ");
		builder.append(getSpannableString(currency, new RelativeSizeSpan(0.4f)));
		return builder;
	}
}
