package ru.aviasales.template.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.template.R;
import ru.aviasales.template.ui.view.ResultsItemView;
import ru.aviasales.template.utils.SortUtils;


public class ResultsRecycleViewAdapter extends RecyclerView.Adapter<ResultsRecycleViewAdapter.ViewHolder> {

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
	public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.result_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		viewHolder.resultsItemView.setProposal(getItem(position), context, isComplexSearch);
		viewHolder.resultsItemView.setAlternativePrice(getItem(position).getTotalWithFilters());
		viewHolder.resultsItemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (listener != null) {
					listener.onClick(getItem(position), position);
				}
			}
		});

	}

	@Override
	public int getItemCount() {
		if (proposals != null) {
			return proposals.size();
		} else {
			return 0;
		}
	}

	public Proposal getItem(int i) {
		return proposals.get(i);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public ResultsItemView resultsItemView;

		public ViewHolder(View itemView) {
			super(itemView);
			resultsItemView = (ResultsItemView) itemView.findViewById(R.id.cv_results_item);
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