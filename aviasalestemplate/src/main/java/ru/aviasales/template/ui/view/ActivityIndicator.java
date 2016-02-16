package ru.aviasales.template.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import ru.aviasales.template.R;


public class ActivityIndicator extends RelativeLayout {
	public static final int TYPE_BIG = 0;
	public static final int TYPE_SMALL_WHITE = 2;

	private int type;

	public ActivityIndicator(Context context) {
		this(context, null);
	}

	public ActivityIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		useAttrs(attrs);
		setUpView(context);
	}

	public ActivityIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		useAttrs(attrs);
		setUpView(context);
	}

	private void useAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ActivityIndicator);
		type = a.getInt(R.styleable.ActivityIndicator_indicatorType, TYPE_BIG);
		a.recycle();
	}

	protected void setUpView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.activity_indicator, this, true);

		ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_activity_indicator_animated);

			if (type == TYPE_SMALL_WHITE) {
				progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
			} else {
				progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAviasalesMain), PorterDuff.Mode.SRC_IN);
			}
	}

}
