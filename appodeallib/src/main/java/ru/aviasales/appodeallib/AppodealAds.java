package ru.aviasales.appodeallib;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;

import java.util.List;

import ru.aviasales.adsinterface.AdsInterface;

public class AppodealAds implements AdsInterface {
	private boolean startAdsEnabled = false;
	private boolean waitingScreenAdsEnabled = false;
	private boolean resultsAdsEnabled = false;

	private NativeAdViewAppWall nativeAdView;

	public void init(Activity activity, String appKey) {
		setUpAppodeal(activity);
		Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
		Appodeal.setTesting(true);
		Appodeal.initialize(activity, appKey, Appodeal.NATIVE | Appodeal.MREC | Appodeal.SKIPPABLE_VIDEO | Appodeal.INTERSTITIAL);
	}

	@Override
	public void setStartAdsEnabled(boolean startAdsEnabled) {
		this.startAdsEnabled = startAdsEnabled;
	}

	@Override
	public void setWaitingScreenAdsEnabled(boolean waitingScreenAdsEnabled) {
		this.waitingScreenAdsEnabled = waitingScreenAdsEnabled;
	}

	@Override
	public void setResultsAdsEnabled(boolean resultsAdsEnabled) {
		this.resultsAdsEnabled = resultsAdsEnabled;
	}

	private void setUpAppodeal(Activity activity) {
		Appodeal.setAutoCacheNativeIcons(true);
		Appodeal.setAutoCacheNativeImages(false);
		Appodeal.setNativeCallbacks(new NativeCallbacksAdapter() {
			@Override
			public void onNativeLoaded(List<NativeAd> list) {
				NativeAdListKeeper.getInstance().setNativeAdList(list);
			}
		});
		Appodeal.cache(activity, Appodeal.NATIVE);
	}

	@Override
	public void showStartAdsIfAvailable(final Activity activity) {
		if (startAdsEnabled) {
			if (Appodeal.isLoaded(Appodeal.SKIPPABLE_VIDEO) || Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
				Appodeal.show(activity, Appodeal.SKIPPABLE_VIDEO | Appodeal.INTERSTITIAL);
			} else {
				Appodeal.setInterstitialCallbacks(new InterstitialCallbacksAdapter() {
					@Override
					public void onInterstitialLoaded(boolean b) {
						Appodeal.show(activity, Appodeal.INTERSTITIAL);
						Appodeal.setInterstitialCallbacks(null);
					}
				});
			}
		}
	}

	@Override
	public View getMrecView(Activity activity) {
		return Appodeal.getMrecView(activity);
	}

	@Override
	public void showWaitingScreenAdsIfAvailable(Activity activity) {
		if (waitingScreenAdsEnabled) {
			Appodeal.show(activity, Appodeal.MREC);
		}
	}

	@Nullable
	@Override
	public View getNativeAdView(Activity activity) {
		List<NativeAd> nativeAdList = NativeAdListKeeper.getInstance().getNativeAdList();
		if (nativeAdList != null && resultsAdsEnabled && areResultsReadyToShow()) {
			nativeAdView = new NativeAdViewAppWall(activity,
					nativeAdList.get(0));
		}
		return nativeAdView;
	}

	@Override
	public boolean isStartAdsEnabled() {
		return startAdsEnabled;
	}

	@Override
	public boolean isWaitingScreenAdsEnabled() {
		return waitingScreenAdsEnabled;
	}

	@Override
	public boolean isResultsAdsEnabled() {
		return resultsAdsEnabled;
	}

	@Override
	public boolean areResultsReadyToShow() {
		List<NativeAd> nativeAdList = NativeAdListKeeper.getInstance().getNativeAdList();
		return nativeAdList != null && !nativeAdList.isEmpty();
	}
}
