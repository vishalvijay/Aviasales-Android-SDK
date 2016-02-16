package ru.aviasales.example;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.identification.IdentificationData;
import ru.aviasales.template.ui.fragment.AviasalesFragment;

public class MainActivity extends AppCompatActivity {

	private AviasalesFragment aviasalesFragment;

	// replace to your travel payout credentiials
	private final static String TRAVEL_PAYOUTS_MARKER = "your_travel_payouts_marker";
	private final static String TRAVEL_PAYOUTS_TOKEN = "your_travel_payouts_token";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AviasalesSDK.getInstance().init(this, new IdentificationData(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN));
		setContentView(R.layout.activity_main);

		init(savedInstanceState);
	}

	private void init(Bundle savedInstanceState) {
		initFragment();
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
