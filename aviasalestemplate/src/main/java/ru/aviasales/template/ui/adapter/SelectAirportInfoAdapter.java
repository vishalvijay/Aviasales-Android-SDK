package ru.aviasales.template.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.view.SelectAirportInfoView;
import ru.aviasales.template.utils.Utils;


public class SelectAirportInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int INFO_VIEW_TYPE = 0;

	private Context context;
	private RecyclerView.Adapter baseAdapter;
	private String infoText;
	private boolean isInfoViewActive = false;

	public SelectAirportInfoAdapter(Context context,
	                                RecyclerView.Adapter baseAdapter) {
		this.baseAdapter = baseAdapter;
		this.context = context;

		this.baseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				notifyDataSetChanged();
			}

			@Override
			public void onItemRangeChanged(int positionStart, int itemCount) {
				notifyItemRangeChanged(positionStart, itemCount);
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				notifyItemRangeInserted(positionStart, itemCount);
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				notifyItemRangeRemoved(positionStart, itemCount);
			}
		});
	}

	public void setInfoText(int resourceId) {
		this.infoText = context.getResources().getString(resourceId);
	}

	public void setInfoText(String text) {
		this.infoText = text;
	}

	public static class LocationViewHolder extends RecyclerView.ViewHolder {
		SelectAirportInfoView statusView;

		public LocationViewHolder(View view) {
			super(view);
			statusView = (SelectAirportInfoView) view;
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
		if (typeView == INFO_VIEW_TYPE) {
			View view = new SelectAirportInfoView(context);
			return new LocationViewHolder(view);
		} else {
			return baseAdapter.onCreateViewHolder(parent, typeView - 1);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
		if (isInfoViewPosition(position)) {

			if (infoText != null && isBottomView(position)) {
				((LocationViewHolder) sectionViewHolder).statusView.setMessage(infoText);
			} else if (isBottomView(position) && !Utils.isOnline(context)) {
				((LocationViewHolder) sectionViewHolder).statusView.setMessageResource(R.string.search_no_internet_connection);
			} else if (isBottomView(position)) {
				((LocationViewHolder) sectionViewHolder).statusView.showProgressBar();
				((LocationViewHolder) sectionViewHolder).statusView.kickProgressBar();
				((LocationViewHolder) sectionViewHolder).statusView.setViewEnabled(false);
				((LocationViewHolder) sectionViewHolder).statusView.allowClick(false);
			}
		} else {
			baseAdapter.onBindViewHolder(sectionViewHolder, infoPositionToPosition(position));
		}
	}

	@Override
	public int getItemViewType(int position) {
		return isInfoViewPosition(position)
				? INFO_VIEW_TYPE
				: baseAdapter.getItemViewType(infoPositionToPosition(position)) + 1;
	}

	private boolean isBottomView(int position) {
		return isInfoViewActive && position == getItemCount() - 1;
	}

	public boolean isInfoViewPosition(int position) {
		return isInfoViewActive && position == getItemCount() - 1;
	}

	public int infoPositionToPosition(int infoPosition) {
		return infoPosition;
	}

	@Override
	public long getItemId(int position) {
		return isInfoViewPosition(position)
				? Integer.MAX_VALUE
				: baseAdapter.getItemId(infoPositionToPosition(position));
	}

	@Override
	public int getItemCount() {
		int itemCount = baseAdapter.getItemCount();
		if (isInfoViewActive) itemCount++;
		return itemCount;
	}

	public boolean isInfoViewActive() {
		return isInfoViewActive;
	}

	public void setInfoViewActive(boolean isBottomInfoViewActive) {
		this.isInfoViewActive = isBottomInfoViewActive;
	}

}
