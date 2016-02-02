package ru.aviasales.template.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.expandedlistview.adapter.BaseExpandedListViewAdapter;
import ru.aviasales.expandedlistview.interfaces.OnSomethingChange;
import ru.aviasales.expandedlistview.listener.OnChangeState;
import ru.aviasales.expandedlistview.view.BaseCheckedText;
import ru.aviasales.expandedlistview.view.BaseFiltersListViewItem;
import ru.aviasales.expandedlistview.view.SelectAllView;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.FilterCheckedAirport;
import ru.aviasales.template.ui.view.filters.airports_filter.AirportItemView;
import ru.aviasales.template.ui.view.filters.airports_filter.AirportSectionHeader;

public class AirportsAdapter extends BaseExpandedListViewAdapter {

	private final Context context;
	private final List<FilterCheckedAirport> allAirports;
	private final Map<Integer, String> separatorsPositions;
	private AdapterCallback listener;
	private final BaseCheckedText selectAll;
	private final boolean hideTitle;

	public AirportsAdapter(Context context, List<FilterCheckedAirport> origin, List<FilterCheckedAirport> destination,
	                       List<FilterCheckedAirport> stopOver, boolean hideTitle) {
		this.context = context;
		allAirports = new ArrayList<FilterCheckedAirport>();
		separatorsPositions = new HashMap<Integer, String>();
		if (!origin.isEmpty()) {
			separatorsPositions.put(allAirports.size(), origin.get(0).getCity());
		}
		allAirports.addAll(origin);
		if (!destination.isEmpty()) {
			separatorsPositions.put(allAirports.size(), destination.get(0).getCity());
		}
		allAirports.addAll(destination);
		separatorsPositions.put(allAirports.size(), context.getResources().getString(R.string.stop_over));
		allAirports.addAll(stopOver);
		this.hideTitle = hideTitle;
		this.selectAll = new BaseCheckedText();
		this.selectAll.setChecked(areAllItemsChecked());
		this.selectAll.setName(context.getString(R.string.select_all) + " (" +
				Integer.toString(origin.size() + destination.size() + stopOver.size()) + ")");
	}

	@Override
	public View getItemView(View view, ViewGroup parent, int position) {
		if (view == null) {
			view = new AirportItemView(context);
			((BaseFiltersListViewItem) view).setOnClickListener(new OnSomethingChange() {
				@Override
				public void onChange() {
					if (listener != null) {
						listener.onViewPressed();
					}
				}
			});
		}

		FilterCheckedAirport checkedTextView = (FilterCheckedAirport) getItem(position);

		((AirportItemView) view).setCheckedText(checkedTextView);
		((AirportItemView) view).setRating(checkedTextView.getRating());
		((AirportItemView) view).setIata(checkedTextView.getIata());
		((AirportItemView) view).setAirportName(checkedTextView.getCity());

		AirportData airport = AviasalesSDK.getInstance().getSearchData().getAirports().get(checkedTextView.getIata());
		if (airport == null) {
			((AirportItemView) view).setCityText(checkedTextView.getCity() + ", " + checkedTextView.getCountry());
		} else {
			((AirportItemView) view).setCityText(airport.getName() + ", " + checkedTextView.getCountry());
		}



		return view;
	}

	@Override
	public View getSelectAllView(View selectAllView, ViewGroup parent) {

		if (selectAllView == null) {
			selectAllView = new SelectAllView(context);
			((SelectAllView) selectAllView).setOnClickListener(new OnChangeState() {
				@Override
				public void onChange(Object object) {
					if (listener != null) {
						listener.onSelectAllPressed(object);
					}
				}
			});
		}
		selectAll.setChecked(areAllItemsChecked());

		((SelectAllView) selectAllView).setCheckedText(selectAll);
		((SelectAllView) selectAllView).setText(selectAll.getName());

		return selectAllView;
	}

	@Override
	public Object getItem(int position) {
		return allAirports.get(position);
	}

	@Override
	public Boolean isItemChecked(int position) {
		return allAirports.get(position).isChecked();
	}

	@Override
	public int getItemsCount() {
		return allAirports.size();
	}

	@Override
	public ViewGroup getTitleView(View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.expanded_listview_title, parent, false);

		((TextView) view.findViewById(R.id.txtv_airports_view_list_title)).setText(R.string.airports);
		return (ViewGroup) view;
	}

	@Override
	public AdapterCallback getListener() {
		return listener;
	}

	@Override
	public void setListener(AdapterCallback listener) {
		this.listener = listener;
	}

	@Override
	public View getSeparatorView(int index) {
		AirportSectionHeader sectionHeader = new AirportSectionHeader(context);
		sectionHeader.setHeaderText(separatorsPositions.get(index));

		return sectionHeader;
	}

	@Override
	public Boolean hasSeparators() {
		return true;
	}

	@Override
	public List<Integer> getSeparatorIndexes() {
		return new ArrayList<Integer>(separatorsPositions.keySet());
	}

	@Override
	public boolean hideTitle() {
		return hideTitle;
	}
}