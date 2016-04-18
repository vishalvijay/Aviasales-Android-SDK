package ru.aviasales.adsinterface;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;


public interface AdsInterface {
	void setStartAdsEnabled(boolean startAdsEnabled);

	void setWaitingScreenAdsEnabled(boolean waitingScreenAdsEnabled);

	void setResultsAdsEnabled(boolean resultsAdsEnabled);

	void showStartAdsIfAvailable(final Activity activity);

	@Nullable
	View getMrecView(Activity activity);

	void showWaitingScreenAdsIfAvailable(Activity activity);

	@Nullable
	View getNativeAdView(Activity activity);

	boolean isStartAdsEnabled();

	boolean isWaitingScreenAdsEnabled();

	boolean isResultsAdsEnabled();

	boolean areResultsReadyToShow();
}
