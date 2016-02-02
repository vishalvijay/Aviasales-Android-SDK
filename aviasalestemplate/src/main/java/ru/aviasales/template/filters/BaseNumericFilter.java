package ru.aviasales.template.filters;

import java.io.Serializable;

public class BaseNumericFilter implements Serializable {

	protected int maxValue = Integer.MIN_VALUE;
	protected int minValue = Integer.MAX_VALUE;
	protected int currentMaxValue;
	protected int currentMinValue;

	public BaseNumericFilter() {

	}

	public BaseNumericFilter(BaseNumericFilter numericFilter) {
		maxValue = numericFilter.getMaxValue();
		minValue = numericFilter.getMinValue();
		currentMaxValue = numericFilter.getCurrentMaxValue();
		currentMinValue = numericFilter.getCurrentMinValue();
	}

	public void mergeFilter(BaseNumericFilter filter) {
		if (filter.isActive()) {
			currentMinValue = Math.min(Math.max(filter.getCurrentMinValue(), minValue), maxValue);
			currentMaxValue = Math.max(Math.min(filter.getCurrentMaxValue(), maxValue), minValue);
		}
	}

	public boolean isActive() {
		return !(maxValue == currentMaxValue && minValue == currentMinValue);
	}

	public boolean isValid() {
		return !(maxValue == Integer.MIN_VALUE ||
				minValue == Integer.MAX_VALUE);
	}

	public boolean isEnabled() {
		return maxValue != minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getCurrentMinValue() {
		return currentMinValue;
	}

	public void setCurrentMinValue(int currentMinValue) {
		this.currentMinValue = currentMinValue;
	}

	public int getCurrentMaxValue() {
		return currentMaxValue;
	}

	public void setCurrentMaxValue(int currentMaxValue) {
		this.currentMaxValue = currentMaxValue;
	}

	public void clearFilter() {
		currentMaxValue = maxValue;
		currentMinValue = minValue;
	}

	protected boolean isActual(long value) {
		return value >= currentMinValue && value <= currentMaxValue;
	}

	protected boolean isActualForMaxValue(int value) {
		return value <= currentMaxValue;
	}

	protected boolean isActualForMinValue(int value) {
		return value >= currentMinValue;
	}
}
