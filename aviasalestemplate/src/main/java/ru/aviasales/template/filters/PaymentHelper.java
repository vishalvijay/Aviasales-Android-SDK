package ru.aviasales.template.filters;

import android.content.Context;

import ru.aviasales.template.R;

public class PaymentHelper {
	private static final String EXP = "exp";
	private static final String EUROSET = "euroset";
	private static final String CASH = "cash";
	private static final String CARD = "card";
	private static final String YANDEX_MONEY = "yandex_money";
	private static final String WEB_MONEY = "web_money";
	private static final String TERMINAL = "terminal";
	private static final String SVYAZNOY = "svyaznoy";
	private static final String ELEXNET = "elexnet";
	private static final String BANK = "bank";
	private static final String CONTACT = "contact";

	public static String getPaymentStringByCode(Context context, String code) {
		if (code.equalsIgnoreCase(EXP)) {
			return context.getResources().getString(R.string.filter_payment_method_exp);
		} else if (code.equalsIgnoreCase(EUROSET)) {
			return context.getResources().getString(R.string.filter_payment_method_euroset);
		} else if (code.equalsIgnoreCase(CASH)) {
			return context.getResources().getString(R.string.filter_payment_method_cash);
		} else if (code.equalsIgnoreCase(CARD)) {
			return context.getResources().getString(R.string.filter_payment_method_card);
		} else if (code.equalsIgnoreCase(YANDEX_MONEY)) {
			return context.getResources().getString(R.string.filter_payment_method_yandex_money);
		} else if (code.equalsIgnoreCase(WEB_MONEY)) {
			return context.getResources().getString(R.string.filter_payment_method_webmoney);
		} else if (code.equalsIgnoreCase(TERMINAL)) {
			return context.getResources().getString(R.string.filter_payment_method_terminal);
		} else if (code.equalsIgnoreCase(SVYAZNOY)) {
			return context.getResources().getString(R.string.filter_payment_method_svyaznoy);
		} else if (code.equalsIgnoreCase(ELEXNET)) {
			return context.getResources().getString(R.string.filter_payment_method_elexnet);
		} else if (code.equalsIgnoreCase(BANK)) {
			return context.getResources().getString(R.string.filter_payment_method_bank);
		} else if (code.equalsIgnoreCase(CONTACT)) {
			return context.getResources().getString(R.string.filter_payment_method_contact);
		}
		return code;
	}

}
