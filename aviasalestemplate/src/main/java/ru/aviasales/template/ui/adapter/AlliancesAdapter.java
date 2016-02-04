package ru.aviasales.template.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.aviasales.expandedlistview.adapter.BaseExpandedListViewAdapter;
import ru.aviasales.expandedlistview.interfaces.OnSomethingChange;
import ru.aviasales.expandedlistview.listener.OnChangeState;
import ru.aviasales.expandedlistview.view.BaseCheckedText;
import ru.aviasales.expandedlistview.view.BaseFiltersListViewItem;
import ru.aviasales.expandedlistview.view.SelectAllView;
import ru.aviasales.template.R;

public class AlliancesAdapter extends BaseExpandedListViewAdapter {

	private final Context context;
	private final List<BaseCheckedText> items;
	private AdapterCallback listener;
	private final BaseCheckedText selectAll;
	private final boolean hideTitle;

	public AlliancesAdapter(Context context, List<BaseCheckedText> items, boolean hideTitle) {
		this.context = context;
		this.items = items;
		this.hideTitle = hideTitle;
		this.selectAll = new BaseCheckedText();
		this.selectAll.setChecked(areAllItemsChecked());
		this.selectAll.setName(context.getString(R.string.select_all_alliances));
	}

	@Override
	public View getItemView(View view, ViewGroup parent, int position) {
		if (view == null) {
			view = new BaseFiltersListViewItem(context);
			((BaseFiltersListViewItem) view).setOnClickListener(new OnSomethingChange() {
				@Override
				public void onChange() {
					if (listener != null) {
						listener.onViewPressed();
					}
				}
			});
		}

		BaseCheckedText checkedTextView = (BaseCheckedText) getItem(position);

		((BaseFiltersListViewItem) view).setCheckedText(checkedTextView);
//		((BaseFiltersListViewItem) view).getRatingBar().setVisibility(View.GONE);
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
		return items.get(position);
	}

	@Override
	public Boolean isItemChecked(int position) {
		return items.get(position).isChecked();
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public ViewGroup getTitleView(View view, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.expanded_listview_title, parent, false);

		((TextView) view.findViewById(R.id.txtv_airports_view_list_title)).setText(R.string.alliances);
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
	public Boolean hasSeparators() {
		return false;
	}

	@Override
	public boolean hideTitle() {
		return hideTitle;
	}
}
