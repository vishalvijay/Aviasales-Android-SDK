package ru.aviasales.template.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.aviasales.template.R;
import ru.aviasales.template.ads.AdsImplKeeper;

public class AdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int AD_BANNER_TYPE = 1;
	private static final int AD_POSITION = 3;

	private final RecyclerView.Adapter baseAdapter;
	private boolean shouldShowAdBanner = false;

	public AdAdapter(RecyclerView.Adapter baseAdapter) {
		this.baseAdapter = baseAdapter;

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

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == AD_BANNER_TYPE) {
			View itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.results_ad_view, parent, false);
			return new AdViewHolder(itemView);
		} else {
			return baseAdapter.onCreateViewHolder(parent, viewType);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (shouldShowAdBannerAtPosition(position)) {
		} else {
			baseAdapter.onBindViewHolder(holder, getRightItemPosition(position));
		}
	}

	@Override
	public int getItemCount() {
		int count = 0;
		if (baseAdapter != null) count += baseAdapter.getItemCount();
		if (shouldShowAdBanner) count += getAdBannersCount();
		return count;
	}

	@Override
	public int getItemViewType(int position) {
		return shouldShowAdBannerAtPosition(position) ? AD_BANNER_TYPE
				: baseAdapter.getItemViewType(getRightItemPosition(position));
	}

	private int getRightItemPosition(int position) {
		return baseAdapter == null ? 0 : position - getCardsCountBeforeCurrentPosition(position);
	}

	private int getCardsCountBeforeCurrentPosition(int position) {
		return shouldShowAdBanner && AD_POSITION < position ? 1 : 0;
	}

	private int getAdBannersCount() {
		return 1;
	}

	private boolean shouldShowAdBannerAtPosition(int position) {
		return shouldShowAdBanner && position == AD_POSITION;
	}

	public void setShouldShowAdBanner(boolean shouldShowAdBanner) {
		this.shouldShowAdBanner = shouldShowAdBanner;
	}

	public static class AdViewHolder extends RecyclerView.ViewHolder {
		CardView cardView;

		public AdViewHolder(View itemView) {
			super(itemView);
			cardView = (CardView) itemView.findViewById(R.id.cv_results_item);
			View adView = AdsImplKeeper.getInstance().getAdsInterface().getNativeAdView((Activity) itemView.getContext());
			if (adView != null) {
				cardView.addView(adView);
			}
		}
	}
}
