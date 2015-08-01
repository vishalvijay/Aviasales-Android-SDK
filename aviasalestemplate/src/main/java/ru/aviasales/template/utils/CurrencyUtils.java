package ru.aviasales.template.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.template.currencies.Currency;

public class CurrencyUtils {

	private static final String PREF_CURRENCY_CODE = "currency_code";

	public static Map<String, Double> getCurrencyRates() {
		return AviasalesSDK.getInstance().getSearchData().getCurrencyRates();
	}

	public static long getPriceInAppCurrency(int priceInDefaultCur, String appCurCode, Map<String, Double> currencies) {
		if (currencies == null) return 0;
		long price = priceInDefaultCur;
		if (!appCurCode.equalsIgnoreCase(Defined.RESPONSE_DEFAULT_CURRENCY)) {
			if (currencies.get(appCurCode.toLowerCase()) != null) {
				if (currencies.get(appCurCode.toLowerCase()) == 0.0) {
					price = 0;
				} else {
					price = Math.round(priceInDefaultCur / currencies.get(appCurCode.toLowerCase()));
				}
			}
		}
		return price;
	}


	public static String getAppCurrency(Context context) {
		return Utils.getPreferences(context).getString(PREF_CURRENCY_CODE, Defined.getDefaultCurrency());
	}


	public static List<Currency> getCurrenciesList() {
		Map<String,String> currencyCodes = Defined.getCurrenciesArray();

		List<Currency> currencies = new ArrayList<Currency>();

		for (Map.Entry<String,String> currencyEntry : currencyCodes.entrySet()) {

			Currency currency = new Currency();
			currency.setCode(currencyEntry.getKey());
			currency.setName(currencyEntry.getValue());
			currencies.add(currency);
		}

		return currencies;
	}

	public static void setAppCurrency(String code, Context context) {
		Utils.getPreferences(context).edit()
				.putString(PREF_CURRENCY_CODE, code)
				.apply();
	}
}
