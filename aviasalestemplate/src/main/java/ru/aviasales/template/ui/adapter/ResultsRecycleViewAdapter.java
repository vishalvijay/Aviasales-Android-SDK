package ru.aviasales.template.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appodeal.ads.native_ad.views.NativeAdViewNewsFeed;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.template.R;
import ru.aviasales.template.ads.AppodealManager;
import ru.aviasales.template.ui.view.ResultsItemView;
import ru.aviasales.template.utils.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class ResultsRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final int AD_BANNER_TYPE = 1;
	private static final int PROPOSAL_TYPE = 2;

	private static final int AD_POSITION = 3;

	private final Context context;
	private List<Proposal> proposals = new ArrayList<>();
	private boolean isComplexSearch;
	private OnClickListener listener;

	public interface OnClickListener {
		void onClick(Proposal proposal, int position);
	}

	public ResultsRecycleViewAdapter(Context context, List<Proposal> proposals, boolean isComplexSearch) {
		this.context = context.getApplicationContext();
		this.proposals = proposals;
		this.isComplexSearch = isComplexSearch;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == AD_BANNER_TYPE) {
			View itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.results_ad_view, parent, false);
			return new AdViewHolder(itemView);
		} else {
			View itemView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.result_item, parent, false);
			return new ProposalViewHolder(itemView);
		}
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
		if (getItemViewType(position) == AD_BANNER_TYPE && viewHolder instanceof AdViewHolder) {
			bindAdView((AdViewHolder) viewHolder);
		} else {
			bindProposalView((ProposalViewHolder) viewHolder, position);
		}
	}

	private void bindAdView(AdViewHolder viewHolder) {
		AppodealManager.getInstance().showResultsAdsIfAvailable(viewHolder.nativeAdViewNewsFeed);
	}

	private void bindProposalView(final ProposalViewHolder viewHolder, int position) {
		ResultsItemView itemView = viewHolder.resultsItemView;
		itemView.setProposal(getItem(position), context, isComplexSearch);
		itemView.setAlternativePrice(getItem(position).getTotalWithFilters());
		itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (listener != null) {
					int adapterPosition = viewHolder.getAdapterPosition();
					listener.onClick(getItem(adapterPosition), adapterPosition);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		if (proposals != null) {
			return proposals.size() + (canShowAdBanner() ? 1 : 0);
		} else {
			return 0;
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (canShowAdBanner() && isAdPosition(position)) {
			return AD_BANNER_TYPE;
		} else {
			return PROPOSAL_TYPE;
		}
	}

	private boolean isAdPosition(int position) {
		return position == AD_POSITION;
	}

	private boolean canShowAdBanner() {
		AppodealManager appodealManager = AppodealManager.getInstance();
		return appodealManager.isResultsAdsEnabled() && appodealManager.areResultsReadyToShow() && proposals.size() >= AD_POSITION;
	}

	public Proposal getItem(int i) {
		return proposals.get(i);
	}

	public static class ProposalViewHolder extends RecyclerView.ViewHolder {
		ResultsItemView resultsItemView;

		public ProposalViewHolder(View itemView) {
			super(itemView);
			resultsItemView = (ResultsItemView) itemView.findViewById(R.id.cv_results_item);
		}
	}

	public static class AdViewHolder extends RecyclerView.ViewHolder {
		NativeAdViewNewsFeed nativeAdViewNewsFeed;

		public AdViewHolder(View itemView) {
			super(itemView);
			nativeAdViewNewsFeed = (NativeAdViewNewsFeed) itemView.findViewById(R.id.native_ad_view_news_feed);
		}
	}

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}

	public void sortProposals(int savedSortingType) {
		if (proposals != null) {
			SortUtils.sortProposals(proposals, savedSortingType, isComplexSearch);
		}
		super.notifyDataSetChanged();
	}

	public void reloadFilteredTickets(List<Proposal> filteredTickets, int savedSortingType) {
		proposals = filteredTickets;
		sortProposals(savedSortingType);
	}
}