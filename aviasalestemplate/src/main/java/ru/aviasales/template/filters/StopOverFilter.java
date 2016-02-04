package ru.aviasales.template.filters;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import ru.aviasales.core.search_real_time.objects.Flight;

public class StopOverFilter implements Parcelable {

	private boolean oneStopOver = true;
	private boolean withoutStopOver = true;
	private boolean twoPlusStopOver = true;

	private boolean isOneStopOverViewEnabled = true;
	private boolean isWithoutStopOverViewEnabled = true;
	private boolean isTwoPlusStopOverViewEnabled = true;

	public void copyMinMaxValues(StopOverFilter stopOverFilter) {
		if (stopOverFilter.isActive()) {
			withoutStopOver = stopOverFilter.withoutStopOver;
			oneStopOver = stopOverFilter.oneStopOver;
			twoPlusStopOver = stopOverFilter.twoPlusStopOver;
		}
	}

	public StopOverFilter() {
	}

	public StopOverFilter(StopOverFilter stopOverFilter) {
		oneStopOver = stopOverFilter.isOneStopOver();
		withoutStopOver = stopOverFilter.isWithoutStopOver();
		twoPlusStopOver = stopOverFilter.isTwoPlusStopOver();
		isOneStopOverViewEnabled = stopOverFilter.isOneStopOverViewEnabled();
		isWithoutStopOverViewEnabled = stopOverFilter.isWithoutStopOverViewEnabled();
		isTwoPlusStopOverViewEnabled = stopOverFilter.isTwoPlusStopOverViewEnabled();
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

	public boolean isActual(List<Flight> flightDatas) {
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

	public StopOverFilter(Parcel in) {
		oneStopOver = in.readByte() == 1;
		withoutStopOver = in.readByte() == 1;
		twoPlusStopOver = in.readByte() == 1;

		isOneStopOverViewEnabled = in.readByte() == 1;
		isWithoutStopOverViewEnabled = in.readByte() == 1;
		isTwoPlusStopOverViewEnabled = in.readByte() == 1;
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
	}

	public static final Parcelable.Creator<StopOverFilter> CREATOR = new Parcelable.Creator<StopOverFilter>() {
		public StopOverFilter createFromParcel(Parcel in) {
			return new StopOverFilter(in);
		}

		public StopOverFilter[] newArray(int size) {
			return new StopOverFilter[size];
		}
	};
}
