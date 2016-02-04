package ru.aviasales.template.ui.view.filters.alliances_filter;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.filters.AllianceFilter;
import ru.aviasales.template.filters.OpenJawFiltersSet;
import ru.aviasales.template.ui.adapter.AlliancesAdapter;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;
import ru.aviasales.template.ui.view.filters.SegmentExpandableView;


public class AlliancesPageView extends BaseFiltersScrollView {
	private final List<ExpandedListView> viewListView = new ArrayList<>();
	private boolean hideTitle = true;

	public AlliancesPageView(Context context) {
		super(context);
	}

	public AlliancesPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlliancesPageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setHideTitle(boolean hideTitle) {
		this.hideTitle = hideTitle;
	}

	@Override
	protected void setUpSimplePageView() {
		if (getSimpleGeneralFilters().getAllianceFilter().isValid()) {
			ExpandedListView view = createAlliancesListView(getSimpleGeneralFilters().getAllianceFilter(), hideTitle);
			viewListView.add(view);
			addView(view);
		}
	}

	@Override
	protected void setUpOpenJawPageView() {
		OpenJawFiltersSet filters = getOpenJawGeneralFilters();

		for (Integer segmentNumber : filters.getSegmentFilters().keySet()) {
			if (!filters.getSegmentFilters().get(segmentNumber).getAllianceFilter().isValid()) continue;

			SegmentExpandableView segmentExpandableView = createSegmentExpandableView(segmentList.get(segmentNumber));
			ExpandedListView view = createAlliancesListView(filters.getSegmentFilters().get(segmentNumber).getAllianceFilter(), hideTitle);
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

	private ExpandedListView createAlliancesListView(AllianceFilter alliancesFilter, boolean hideTitle) {
		ExpandedListView alliancesListView = new ExpandedListView(getContext(), null);
		AlliancesAdapter adapter = new AlliancesAdapter(getContext(), alliancesFilter.getAllianceList(), hideTitle);
		alliancesListView.setAdapter(adapter);
		alliancesListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return alliancesListView;
	}
}