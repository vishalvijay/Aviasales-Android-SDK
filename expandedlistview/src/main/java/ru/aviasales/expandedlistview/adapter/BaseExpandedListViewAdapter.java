package ru.aviasales.expandedlistview.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.aviasales.expandedlistview.interfaces.ExpandableListViewInterface;

abstract public class BaseExpandedListViewAdapter implements ExpandableListViewInterface {

	public interface AdapterCallback {
		void onSelectAllPressed(Object state);

		void onViewPressed();
	}

	@Override
	abstract public View getItemView(View view, ViewGroup parent, int position);

	@Override
	abstract public View getSelectAllView(View selectAllView, ViewGroup parent);

	@Override
	abstract public Object getItem(int position);

	@Override
	abstract public Boolean isItemChecked(int position);

	@Override
	abstract public int getItemsCount();

	abstract public ViewGroup getTitleView(View view, ViewGroup parent);

	abstract public AdapterCallback getListener();

	abstract public void setListener(AdapterCallback listener);

	public Boolean areAllItemsChecked() {
		for (int i = 0; i < getItemsCount(); i++) {
			if (!isItemChecked(i)) return false;
		}
		return true;
	}

	public boolean hideTitle() {
		return false;
	}

	public View getSeparatorView(int index) {
		return null;
	}

	public List<Integer> getSeparatorIndexes() {
		return null;
	}

	abstract public Boolean hasSeparators();
}
