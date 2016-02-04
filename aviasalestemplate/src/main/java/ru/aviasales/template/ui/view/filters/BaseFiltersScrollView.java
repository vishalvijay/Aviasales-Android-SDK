package ru.aviasales.template.ui.view.filters;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;

import ru.aviasales.core.search.object.ResultsSegment;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.FiltersSet;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.filters.SimpleSearchFilters;

public abstract class BaseFiltersScrollView extends ScrollView {
	protected FiltersSet filters;
	protected LinearLayout layout;
	protected List<ResultsSegment> segmentList;

	protected OnSomethingChangeListener listener;

	public interface OnSomethingChangeListener {
		void onChange();
	}

	public BaseFiltersScrollView(Context context) {
		super(context);
		initLayout();
	}

	public BaseFiltersScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout();
	}

	public BaseFiltersScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLayout();
	}

	@Override
	public void addView(View child) {
		layout.addView(child);
	}

	@Override
	public void removeView(View view) {
		layout.removeView(view);
	}

	public void addExtraPaddingBottom(int paddingBottom) {
		layout.setPadding(0, 0, 0, paddingBottom);
	}

	private void initLayout() {
		layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutTransition(new LayoutTransition());

		super.addView(layout);
	}

	public void init(FiltersSet filters, List<ResultsSegment> segmentList) {
		this.filters = filters;
		this.segmentList = segmentList;

		if (filters instanceof SimpleSearchFilters) {
			setUpSimplePageView();
		} else {
			setUpOpenJawPageView();
		}
	}

	public void setListener(OnSomethingChangeListener listener) {
		this.listener = listener;
	}

	protected SimpleSearchFilters getSimpleGeneralFilters() {
		return ((SimpleSearchFilters) filters);
	}

	protected OpenJawFiltersSet getOpenJawGeneralFilters() {
		return ((OpenJawFiltersSet) filters);
	}


	protected String getAirportOriginIata(ResultsSegment segment) {
		return segment.getOrigin();
	}

	protected String getAirportDestinationIata(ResultsSegment segment) {
		return segment.getDestination();
	}

	protected SegmentExpandableView createSegmentExpandableView(ResultsSegment segment) {
		SegmentExpandableView segmentExpandableView = new SegmentExpandableView(getContext());
		segmentExpandableView.setTitleText(getAirportOriginIata(segment) + " " + getResources().getString(R.string.dot) +
				" " + getAirportDestinationIata(segment));
		return segmentExpandableView;
	}


	protected abstract void setUpSimplePageView();

	protected abstract void setUpOpenJawPageView();

	public abstract void clearFilters();
}
