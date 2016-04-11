package ru.aviasales.template.ads;

import android.app.Activity;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.MrecView;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;

import java.util.List;

public class AppodealManager {
	private static volatile AppodealManager instance;

	private boolean startAdsShowedOnce = false;
	private boolean startAdsEnabled = false;
	private boolean waitingScreenAdsEnabled = false;
	private boolean resultsAdsEnabled = false;

	private List<NativeAd> nativeAdList;

	public static AppodealManager getInstance() {
		if (instance == null) {
			synchronized (AppodealManager.class) {
				if (instance == null) {
					instance = new AppodealManager();
				}
			}
		}
		return instance;
	}

	public void init(Activity activity, String appKey, boolean enableStartAds, boolean enableWaitingScreenAds, boolean enableResultsAds) {
		this.startAdsEnabled = enableStartAds;
		this.waitingScreenAdsEnabled = enableWaitingScreenAds;
		this.resultsAdsEnabled = enableResultsAds;

		setUpAppodeal(activity);
		Appodeal.initialize(activity, appKey, Appodeal.NATIVE | Appodeal.MREC | Appodeal.NON_SKIPPABLE_VIDEO | Appodeal.INTERSTITIAL);
	}

	private void setUpAppodeal(Activity activity) {
		Appodeal.setAutoCacheNativeIcons(true);
		Appodeal.setAutoCacheNativeImages(false);
		Appodeal.cache(activity, Appodeal.NATIVE);
		Appodeal.setNativeCallbacks(new NativeCallbacksAdapter() {
			@Override
			public void onNativeLoaded(List<NativeAd> list) {
				nativeAdList = list;
			}
		});
	}

	public void showStartAdsIfAvailable(final Activity activity) {
		if (startAdsEnabled && !startAdsShowedOnce) {
			startAdsShowedOnce = true;
			if (Appodeal.isLoaded(Appodeal.NON_SKIPPABLE_VIDEO) || Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
				Appodeal.show(activity, Appodeal.NON_SKIPPABLE_VIDEO | Appodeal.INTERSTITIAL);
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

	public MrecView getMrecView(Activity activity) {
		return Appodeal.getMrecView(activity);
	}

	public void showWaitingScreenAdsIfAvailable(Activity activity) {
		if (waitingScreenAdsEnabled) {
			Appodeal.show(activity, Appodeal.MREC);
		}
	}

	public void showResultsAdsIfAvailable(NativeAdViewNewsFeed nativeAdViewNewsFeed) {
		if (resultsAdsEnabled && areResultsReadyToShow()) {
			nativeAdViewNewsFeed.setNativeAd(nativeAdList.get(0));
		}
	}

	public boolean isStartAdsEnabled() {
		return startAdsEnabled;
	}

	public boolean isWaitingScreenAdsEnabled() {
		return waitingScreenAdsEnabled;
	}

	public boolean isResultsAdsEnabled() {
		return resultsAdsEnabled;
	}

	public boolean areResultsReadyToShow() {
		return nativeAdList != null && !nativeAdList.isEmpty();
	}
}
