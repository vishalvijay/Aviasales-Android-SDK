package ru.aviasales.template.ui.view.filters;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

public class CustomTypefaceSpan extends TypefaceSpan {

	private final Typeface newType;

	public CustomTypefaceSpan(Typeface type) {
		this("", type);
	}

	public CustomTypefaceSpan(String family, Typeface type) {
		super(family);
		newType = type;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		applyCustomTypeFace(ds, newType);
	}

	@Override
	public void updateMeasureState(TextPaint paint) {
		applyCustomTypeFace(paint, newType);
	}

	private static void applyCustomTypeFace(Paint paint, Typeface tf) {
		paint.setTypeface(tf);
	}
}
