package ru.aviasales.template.filters;

import java.io.Serializable;

public class OvernightTerms implements Serializable{
	private int minDurationMinutes;
	private int maxDurationMinutes;

	private int minLandingTime;
	private int maxLandingTime;

	public OvernightTerms() {
	}

	public OvernightTerms(int minDurationHours, int maxDurationHours, int minLandingTime, int maxLandingTime) {
		this.minDurationMinutes = minDurationHours * 60;
		this.maxDurationMinutes = maxDurationHours * 60;
		this.minLandingTime = minLandingTime;
		this.maxLandingTime = maxLandingTime;
	}

	public OvernightTerms(OvernightTerms overnightTerms) {
		minDurationMinutes = overnightTerms.getMinDurationMinutes();
		maxDurationMinutes = overnightTerms.getMaxDurationMinutes();
		minLandingTime = overnightTerms.getMinLandingTime();
		maxLandingTime = overnightTerms.getMaxLandingTime();
	}

	public boolean isOvernight(long durationMinutes, long landingTimeHours) {
		if (isInInterval(durationMinutes)) {
			if (minLandingTime < maxLandingTime) {
				if (landingTimeHours >= minLandingTime && landingTimeHours < maxLandingTime) {
					return true;
				}
			} else {
				if (landingTimeHours >= minLandingTime || landingTimeHours < maxLandingTime) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isInInterval(long duration) {
		return duration >= minDurationMinutes && duration < maxDurationMinutes;
	}

	public int getMinLandingTime() {
		return minLandingTime;
	}

	public void setMinLandingTime(int minLandingTime) {
		this.minLandingTime = minLandingTime;
	}

	public int getMaxLandingTime() {
		return maxLandingTime;
	}

	public void setMaxLandingTime(int maxLandingTime) {
		this.maxLandingTime = maxLandingTime;
	}

	public int getMinDurationMinutes() {
		return minDurationMinutes;
	}

	public void setMinDurationMinutes(int minDurationMinutes) {
		this.minDurationMinutes = minDurationMinutes;
	}

	public int getMaxDurationMinutes() {
		return maxDurationMinutes;
	}

	public void setMaxDurationMinutes(int maxDurationMinutes) {
		this.maxDurationMinutes = maxDurationMinutes;
	}
}