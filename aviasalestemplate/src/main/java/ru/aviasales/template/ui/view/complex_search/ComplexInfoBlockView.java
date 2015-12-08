package ru.aviasales.template.ui.view.complex_search;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.template.R;


public class ComplexInfoBlockView extends RelativeLayout {
	public static final int TYPE_LOCATION_DEPARTURE = 0;
	public static final int TYPE_LOCATION_ARRIVAL = 1;
	public static final int TYPE_DATE = 2;
	private static final int CHANGE_BLOCK_ANIMATION_DURATION = 200;
	private LinearLayout textInfoLayout;
	private TextView topTextView;
	private TextView bottomTextView;
	private ImageView image;

	private int blockType = TYPE_LOCATION_DEPARTURE;

	public ComplexInfoBlockView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.complex_search_info_block_view, this, true);
	}

	public ComplexInfoBlockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.complex_search_info_block_view, this, true);

		readAttrs(context, attrs);
	}

	public ComplexInfoBlockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.complex_search_info_block_view, this, true);

		readAttrs(context, attrs);
	}

	public int getBlockType() {
		return blockType;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		textInfoLayout = (LinearLayout) findViewById(R.id.text_info_layout);
		topTextView = (TextView) findViewById(R.id.tv_top);
		bottomTextView = (TextView) findViewById(R.id.tv_bottom);
		image = (ImageView) findViewById(R.id.iv_icon);

		setupIcon();
		setData(null, null);
	}

	public void setData(String topText, String bottomText) {
		setData(topText, bottomText, false);
	}

	public void setData(String topText, String bottomText, boolean withAnimation) {
		if (topText != null) {
			topTextView.setText(topText);
		} else {
			topTextView.setText("");
		}

		if (bottomText != null) {
			bottomTextView.setText(bottomText);
		} else {
			bottomTextView.setText("");
		}

		if (topText != null || bottomText != null) {
			changeVisibility(true);
		} else {
			changeVisibility(false);
		}
	}

	public void changeVisibility(boolean showData) {
		ChangeVisibilityWithoutAnimation(showData);
	}

	private void ChangeVisibilityWithoutAnimation(boolean showData) {
		if (showData) {
			image.setVisibility(View.INVISIBLE);
			textInfoLayout.setVisibility(View.VISIBLE);
		} else {
			image.setVisibility(View.VISIBLE);
			textInfoLayout.setVisibility(View.INVISIBLE);
		}
	}

	private void readAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ComplexInfoBlockView, 0, 0);
		blockType = ta.getInt(R.styleable.ComplexInfoBlockView_type, TYPE_LOCATION_DEPARTURE);
		ta.recycle();
	}

	private void setupIcon() {
		image.setImageResource(R.drawable.ic_add_complex_search);
	}
}
