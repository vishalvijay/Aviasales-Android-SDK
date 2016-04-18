package ru.aviasales.example;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.aviasales.appodeallib.AppodealAds;
import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.identification.IdentificationData;
import ru.aviasales.template.ads.AdsImplKeeper;
import ru.aviasales.template.ui.fragment.AviasalesFragment;

public class MainActivity extends AppCompatActivity {

	private AviasalesFragment aviasalesFragment;

	// replace to your travel payout credentials
	private final static String TRAVEL_PAYOUTS_MARKER = "48729";
	private final static String TRAVEL_PAYOUTS_TOKEN = "978f851b049dfb0f8a1a24e436be1f82";
	private final static String APPODEAL_APP_KEY = "c162c57e218d2a96d0929b93a123687598ca09e40e2f82d8";

	private final static boolean SHOW_ADS_ON_START = true;
	private final static boolean SHOW_ADS_ON_WAITING_SCREEN = true;
	private final static boolean SHOW_ADS_ON_SEARCH_RESULTS = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AviasalesSDK.getInstance().init(this, new IdentificationData(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN));
		setContentView(R.layout.activity_main);

		init();
	}

	private void init() {
		initAppodeal();
		initFragment();
	}

	private void initAppodeal() {
		AppodealAds ads = new AppodealAds();
		ads.setStartAdsEnabled(SHOW_ADS_ON_START);
		ads.setWaitingScreenAdsEnabled(SHOW_ADS_ON_WAITING_SCREEN);
		ads.setResultsAdsEnabled(SHOW_ADS_ON_SEARCH_RESULTS);
		ads.init(this, APPODEAL_APP_KEY);
		AdsImplKeeper.getInstance().setCustomAdsInterfaceImpl(ads);
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		aviasalesFragment = (AviasalesFragment) fm.findFragmentByTag(AviasalesFragment.TAG);

		if (aviasalesFragment == null) {
			aviasalesFragment = (AviasalesFragment) AviasalesFragment.newInstance();
		}

		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_place, aviasalesFragment, AviasalesFragment.TAG);
		fragmentTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		if (!aviasalesFragment.onBackPressed()) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
