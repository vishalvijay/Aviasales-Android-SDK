package ru.aviasales.template.ui.view.filters;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import ru.aviasales.template.R;
import ru.aviasales.template.utils.StringUtils;

public class ClearFiltersView extends FrameLayout {
	private TextView btnClear;
	private TextView tvTicketsCount;
	private View divider;

	public ClearFiltersView(Context context) {
		super(context);
		initViews();
	}

	public ClearFiltersView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	public ClearFiltersView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initViews();
	}

	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.clear_filters_layout, this, true);

		btnClear = (TextView) findViewById(R.id.btn_clear);
		divider = findViewById(R.id.divider);
		tvTicketsCount = (TextView) findViewById(R.id.tv_tickets_count);
	}

	public void setFilteredTicketsCount(int ticketsCount) {
		tvTicketsCount.setText(getResources().getString(R.string.clear_filters_ticket_count) + " " +
				StringUtils.getStringWithDelimeterFromLong(ticketsCount, " ", 3));
	}

	public void setOnClearButtonClickListener(OnClickListener listener) {
		btnClear.setOnClickListener(listener);
	}

	public void hideClearButton() {
		divider.setVisibility(View.GONE);
		btnClear.setVisibility(View.GONE);
	}

	public void showClearButton() {
		divider.setVisibility(View.VISIBLE);
		btnClear.setVisibility(View.VISIBLE);
	}
}
