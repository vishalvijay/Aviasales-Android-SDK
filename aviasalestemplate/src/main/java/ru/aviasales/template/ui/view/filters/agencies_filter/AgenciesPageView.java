package ru.aviasales.template.ui.view.filters.agencies_filter;

import android.content.Context;
import android.util.AttributeSet;

import ru.aviasales.expandedlistview.view.ExpandedListView;
import ru.aviasales.template.filters.AgenciesFilter;
import ru.aviasales.template.ui.adapter.AgencyAdapter;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;

public class AgenciesPageView extends BaseFiltersScrollView {
	private ExpandedListView agenciesExpandedListView;
	private boolean hideTitle = true;

	public AgenciesPageView(Context context) {
		super(context);
	}

	public AgenciesPageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AgenciesPageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setHideTitle(boolean hideTitle) {
		this.hideTitle = hideTitle;
	}

	@Override
	protected void setupSimplePageView() {
		if (getSimpleGeneralFilters().getAgenciesFilter().isValid()) {
			agenciesExpandedListView = createAgenciesListView(getSimpleGeneralFilters().getAgenciesFilter(), hideTitle);
			addView(agenciesExpandedListView);
		}
	}

	@Override
	protected void setupOpenJawPageView() {
		if (getOpenJawGeneralFilters().getAgenciesFilter().isValid()) {
			agenciesExpandedListView = createAgenciesListView(getOpenJawGeneralFilters().getAgenciesFilter(), hideTitle);
			addView(agenciesExpandedListView);
		}
	}

	@Override
	public void clearFilters() {
		if (agenciesExpandedListView != null) {
			agenciesExpandedListView.notifyDataChanged();
		}
	}

	private ExpandedListView createAgenciesListView(AgenciesFilter agenciesFilter, boolean hideTitle) {
		ExpandedListView agenciesListView = new ExpandedListView(getContext(), null);
		AgencyAdapter adapter = new AgencyAdapter(getContext(), agenciesFilter.getAgenciesList(), hideTitle);
		agenciesListView.setAdapter(adapter);
		agenciesListView.setOnItemClickListener(new ExpandedListView.OnItemClickListener() {
			@Override
			public void onItemClick() {
				listener.onChange();
			}
		});
		return agenciesListView;
	}
}