package ru.aviasales.template.ui.view.complex_search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import ru.aviasales.template.R;


public class ComplexSegmentView extends FrameLayout implements View.OnClickListener {
	private ComplexInfoBlockView departureBlock;
	private ComplexInfoBlockView arrivalBlock;
	private ComplexInfoBlockView dateBlock;

	private OnBlockClickListener listener;

	public ComplexSegmentView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.complex_search_segment, this, true);
		setupViews();
	}

	public ComplexSegmentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.complex_search_segment, this, true);
		setupViews();
	}

	public ComplexSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.complex_search_segment, this, true);
		setupViews();
	}

	public void setNotClickable() {
		departureBlock.setClickable(false);
		arrivalBlock.setClickable(false);
		dateBlock.setClickable(false);
	}

	@Override
	public void onClick(View view) {
		if (listener != null) {
			listener.onBlockClick(this, ((ComplexInfoBlockView) view).getBlockType());
		}
	}

	private void setupViews() {
		departureBlock = (ComplexInfoBlockView) findViewById(R.id.oj_departure);
		arrivalBlock = (ComplexInfoBlockView) findViewById(R.id.oj_arrival);
		dateBlock = (ComplexInfoBlockView) findViewById(R.id.oj_date);

		departureBlock.setOnClickListener(this);
		arrivalBlock.setOnClickListener(this);
		dateBlock.setOnClickListener(this);
	}

	public void changeDepartureData(String departureIata, String departureName, boolean withAnimation) {
		changeData(departureBlock, departureIata, departureName, withAnimation);
	}

	public void changeArrivalData(String arrivalIata, String arrivalName, boolean withAnimation) {
		changeData(arrivalBlock, arrivalIata, arrivalName, withAnimation);
	}

	public void changeDateData(String day, String year, boolean withAnimation) {
		changeData(dateBlock, day, year, withAnimation);
	}

	private void changeData(ComplexInfoBlockView block, String topText, String bottomText, boolean withAnimation) {
		block.setData(topText, bottomText, withAnimation);
	}

	public void clearAllData(boolean withAnimation) {
		changeDepartureData(null, null, withAnimation);
		changeArrivalData(null, null, withAnimation);
		changeDateData(null, null, withAnimation);
	}

	public void setOnBlockClickListener(OnBlockClickListener listener) {
		this.listener = listener;
	}

	interface OnBlockClickListener {
		void onBlockClick(ComplexSegmentView view, int blockType);
	}
}
