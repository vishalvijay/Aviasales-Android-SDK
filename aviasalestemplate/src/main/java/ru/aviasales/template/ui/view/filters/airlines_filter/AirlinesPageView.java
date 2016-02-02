package ru.aviasales.template.ui.view.filters.airlines_filter;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.filters.AirlinesFilter;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.ui.adapter.AirlinesAdapter;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;
import ru.aviasales.template.ui.view.filters.SegmentExpandableView;

public class AirlinesPageView extends BaseFiltersScrollView {
	private final List<ExpandedListView> viewListView = new ArrayList<>();
	private boolean hideTitle = true;

	public AirlinesPageView(Context context) {
		super(context);
	}

	public AirlinesPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AirlinesPageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setHideTitle(boolean hideTitle) {
		this.hideTitle = hideTitle;
	}

	@Override
	protected void setupSimplePageView() {
		if (getSimpleGeneralFilters().getAirlinesFilter().isValid()) {
			ExpandedListView view = createAirlinesFilterView(getSimpleGeneralFilters().getAirlinesFilter(), hideTitle);
			viewListView.add(view);
			addView(view);
		}
	}

	@Override
	protected void setupOpenJawPageView() {
		OpenJawFiltersSet filters = getOpenJawGeneralFilters();

		for (Integer segmentNumber : filters.getSegmentFilters().keySet()) {
			if (!filters.getSegmentFilters().get(segmentNumber).getAirlinesFilter().isValid()) continue;


			SegmentExpandableView segmentExpandableView = createSegmentExpandableView(segmentList.get(segmentNumber));
			ExpandedListView view = createAirlinesFilterView(filters.getSegmentFilters().get(segmentNumber).getAirlinesFilter(), hideTitle);
			viewListView.add(view);
			segmentExpandableView.addContentView(view);
			addView(segmentExpandableView);
		}
	}

	@Override
	public void clearFilters() {
		for (ExpandedListView view : viewListView) {
			view.notifyDataChanged();
		}
	}

	private ExpandedListView createAirlinesFilterView(AirlinesFilter airlinesFilter, boolean hideTitle) {
		ExpandedListView airlinesListView = new ExpandedListView(getContext(), null);
		AirlinesAdapter adapter = new AirlinesAdapter(getContext(), airlinesFilter.getAirlineList(), hideTitle);
		airlinesListView.setAdapter(adapter);
		airlinesListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return airlinesListView;
	}
}