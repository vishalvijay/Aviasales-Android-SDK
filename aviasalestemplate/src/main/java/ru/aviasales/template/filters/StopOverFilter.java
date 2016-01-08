package ru.aviasales.template.filters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ru.aviasales.core.legacy.search.object.OldFlightData;


public class StopOverFilter implements Parcelable {

	private boolean oneStopOver = true;
	private boolean withoutStopOver = true;
	private boolean twoPlusStopOver = true;

	private boolean isOneStopOverViewEnabled = true;
	private boolean isWithoutStopOverViewEnabled = true;
	private boolean isTwoPlusStopOverViewEnabled = true;

	private int oneStopOverMinPrice = Integer.MAX_VALUE;
	private int withoutStopOverMinPrice = Integer.MAX_VALUE;
	private int twoStopOverMinPrice = Integer.MAX_VALUE;

	public StopOverFilter() {
	}

	public StopOverFilter(StopOverFilter stopOverFilter) {
		oneStopOver = stopOverFilter.isOneStopOver();
		withoutStopOver = stopOverFilter.isWithoutStopOver();
		twoPlusStopOver = stopOverFilter.isTwoPlusStopOver();
		isOneStopOverViewEnabled = stopOverFilter.isOneStopOverViewEnabled();
		isWithoutStopOverViewEnabled = stopOverFilter.isWithoutStopOverViewEnabled();
		isTwoPlusStopOverViewEnabled = stopOverFilter.isTwoPlusStopOverViewEnabled();
		oneStopOverMinPrice = stopOverFilter.getOneStopOverMinPrice();
		withoutStopOverMinPrice = stopOverFilter.getWithoutStopOverMinPrice();
		twoStopOverMinPrice = stopOverFilter.getWithoutStopOverMinPrice();
	}

	public void clearFilter() {
		oneStopOver = isOneStopOverViewEnabled;
		withoutStopOver = isWithoutStopOverViewEnabled;
		twoPlusStopOver = isTwoPlusStopOverViewEnabled;
	}

	public boolean isActive() {
		return !((oneStopOver || !isOneStopOverViewEnabled) &&
				(withoutStopOver || !isWithoutStopOverViewEnabled) &&
				(twoPlusStopOver || !isTwoPlusStopOverViewEnabled));
	}

	public boolean isActual(List<OldFlightData> flightDatas) {
		int stopOverCount = flightDatas.size();
		return ((oneStopOver && stopOverCount == 2) ||
				(withoutStopOver && stopOverCount == 1) ||
				(twoPlusStopOver && stopOverCount > 2));
	}

	public void setParams(boolean isOneStopOverFlightsAvailable, boolean isWithoutStopOverFlightsAvailable, boolean isTwoPlusStopOverFlightsAvailable) {
		oneStopOver = isOneStopOverFlightsAvailable;
		withoutStopOver = isWithoutStopOverFlightsAvailable;
		twoPlusStopOver = isTwoPlusStopOverFlightsAvailable;
	}

	public boolean isOneStopOver() {
		return oneStopOver;
	}

	public void setOneStopOver(boolean oneStopOver) {
		this.oneStopOver = oneStopOver;
	}

	public boolean isWithoutStopOver() {
		return withoutStopOver;
	}

	public void setWithoutStopOver(boolean withoutStopOver) {
		this.withoutStopOver = withoutStopOver;
	}

	public boolean isTwoPlusStopOver() {
		return twoPlusStopOver;
	}

	public void setTwoPlusStopOver(boolean twoPlusStopOver) {
		this.twoPlusStopOver = twoPlusStopOver;
	}

	public boolean isOneStopOverViewEnabled() {
		return isOneStopOverViewEnabled;
	}

	public void setOneStopOverEnabled(boolean oneStopOverEnabled) {
		isOneStopOverViewEnabled = oneStopOverEnabled;
	}

	public boolean isWithoutStopOverViewEnabled() {
		return isWithoutStopOverViewEnabled;
	}

	public void setWithoutStopOverEnabled(boolean withoutStopOverEnabled) {
		isWithoutStopOverViewEnabled = withoutStopOverEnabled;
	}

	public boolean isTwoPlusStopOverViewEnabled() {
		return isTwoPlusStopOverViewEnabled;
	}

	public void setTwoPlusStopOverEnabled(boolean twoPlusStopOverEnabled) {
		isTwoPlusStopOverViewEnabled = twoPlusStopOverEnabled;
	}

	public int getTwoStopOverMinPrice() {
		return twoStopOverMinPrice;
	}

	public void setTwoStopOverMinPrice(int twoStopOverMinPrice) {
		this.twoStopOverMinPrice = twoStopOverMinPrice;
	}

	public int getWithoutStopOverMinPrice() {
		return withoutStopOverMinPrice;
	}

	public void setWithoutStopOverMinPrice(int withoutStopOverMinPrice) {
		this.withoutStopOverMinPrice = withoutStopOverMinPrice;
	}

	public int getOneStopOverMinPrice() {
		return oneStopOverMinPrice;
	}

	public void setOneStopOverMinPrice(int oneStopOverMinPrice) {
		this.oneStopOverMinPrice = oneStopOverMinPrice;
	}

	public void setMinPrices(int withoutStopOverMinPrice, int oneStopOverMinPrice, int twoPlusStopOverMinPrice) {
		this.withoutStopOverMinPrice = withoutStopOverMinPrice;
		this.oneStopOverMinPrice = oneStopOverMinPrice;
		this.twoStopOverMinPrice = twoPlusStopOverMinPrice;

		withoutStopOver = isWithoutStopOverViewEnabled = withoutStopOverMinPrice != Integer.MAX_VALUE;
		oneStopOver = isOneStopOverViewEnabled = oneStopOverMinPrice != Integer.MAX_VALUE;
		twoPlusStopOver = isTwoPlusStopOverViewEnabled = twoPlusStopOverMinPrice != Integer.MAX_VALUE;
	}


	public StopOverFilter(Parcel in) {
		oneStopOver = in.readByte() == 1;
		withoutStopOver = in.readByte() == 1;
		twoPlusStopOver = in.readByte() == 1;

		isOneStopOverViewEnabled = in.readByte() == 1;
		isWithoutStopOverViewEnabled = in.readByte() == 1;
		isTwoPlusStopOverViewEnabled = in.readByte() == 1;

		oneStopOverMinPrice = in.readInt();
		withoutStopOverMinPrice = in.readInt();
		twoStopOverMinPrice = in.readInt();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (oneStopOver ? 1 : 0));
		dest.writeByte((byte) (withoutStopOver ? 1 : 0));
		dest.writeByte((byte) (twoPlusStopOver ? 1 : 0));

		dest.writeByte((byte) (isOneStopOverViewEnabled ? 1 : 0));
		dest.writeByte((byte) (isWithoutStopOverViewEnabled ? 1 : 0));
		dest.writeByte((byte) (isTwoPlusStopOverViewEnabled ? 1 : 0));

		dest.writeInt(oneStopOverMinPrice);
		dest.writeInt(withoutStopOverMinPrice);
		dest.writeInt(twoStopOverMinPrice);
	}

	public static final Creator<StopOverFilter> CREATOR = new Creator<StopOverFilter>() {
		public StopOverFilter createFromParcel(Parcel in) {
			return new StopOverFilter(in);
		}

		public StopOverFilter[] newArray(int size) {
			return new StopOverFilter[size];
		}
	};
}
