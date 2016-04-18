package ru.aviasales.template.ads;

import ru.aviasales.adsinterface.AdsInterface;

public class AdsImplKeeper {
	private static volatile AdsImplKeeper instance;
	private AdsInterface adsInterface = new AdsEmptyImpl();

	public static AdsImplKeeper getInstance() {
		if (instance == null) {
			synchronized (AdsImplKeeper.class) {
				if (instance == null) {
					instance = new AdsImplKeeper();
				}
			}
		}
		return instance;
	}

	public void setCustomAdsInterfaceImpl(AdsInterface adsInterfaceImpl) {
		this.adsInterface = adsInterfaceImpl;
	}

	public AdsInterface getAdsInterface() {
		return adsInterface;
	}
}
