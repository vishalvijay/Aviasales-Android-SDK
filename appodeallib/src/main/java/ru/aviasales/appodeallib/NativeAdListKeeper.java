package ru.aviasales.appodeallib;

import android.support.annotation.Nullable;

import com.appodeal.ads.NativeAd;

import java.util.List;

public class NativeAdListKeeper {
	private static volatile NativeAdListKeeper instance;
	private List<NativeAd> nativeAdList;

	public static NativeAdListKeeper getInstance() {
		if (instance == null) {
			synchronized (NativeAdListKeeper.class) {
				if (instance == null) {
					instance = new NativeAdListKeeper();
				}
			}
		}
		return instance;
	}

	public static void setInstance(NativeAdListKeeper instance) {
		NativeAdListKeeper.instance = instance;
	}

	@Nullable
	public List<NativeAd> getNativeAdList() {
		return nativeAdList;
	}

	public void setNativeAdList(List<NativeAd> nativeAdList) {
		this.nativeAdList = nativeAdList;
	}
}
