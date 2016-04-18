package ru.aviasales.template.ads;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import ru.aviasales.adsinterface.AdsInterface;

public class AdsEmptyImpl implements AdsInterface {
	@Override
	public void setStartAdsEnabled(boolean startAdsEnabled) {

	}

	@Override
	public void setWaitingScreenAdsEnabled(boolean waitingScreenAdsEnabled) {

	}

	@Override
	public void setResultsAdsEnabled(boolean resultsAdsEnabled) {

	}

	@Override
	public void showStartAdsIfAvailable(Activity activity) {

	}

	@Nullable
	@Override
	public View getMrecView(Activity activity) {
		return null;
	}

	@Override
	public void showWaitingScreenAdsIfAvailable(Activity activity) {

	}

	@Nullable
	@Override
	public View getNativeAdView(Activity activity) {
		return null;
	}

	@Override
	public boolean isStartAdsEnabled() {
		return false;
	}

	@Override
	public boolean isWaitingScreenAdsEnabled() {
		return false;
	}

	@Override
	public boolean isResultsAdsEnabled() {
		return false;
	}

	@Override
	public boolean areResultsReadyToShow() {
		return false;
	}
}
