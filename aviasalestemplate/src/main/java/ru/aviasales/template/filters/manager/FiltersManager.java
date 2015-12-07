package ru.aviasales.template.filters.manager;

import android.content.Context;
import android.os.Handler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.aviasales.core.AviasalesSDKV3;
import ru.aviasales.core.search_v3.objects.Proposal;
import ru.aviasales.core.search_v3.objects.SearchDataV3;
import ru.aviasales.template.filters.GeneralFilter;
import ru.aviasales.template.filters.PreInitializeFilters;
import ru.aviasales.template.proposal.ProposalManager;

public class FiltersManager {
	private static volatile FiltersManager sInstance;

	public interface OnFilterResultListener {
		void onFilteringFinished(List<Proposal> filteredTicketsData);
	}

	private GeneralFilter mFilter;

	private ExecutorService pool;

	private Handler mHandler = new Handler();
	private OnFilterResultListener mOnFilterResultsListener;

	private List<Proposal> mFilteredProposals;

	public static FiltersManager getInstance() {
		if (sInstance == null) {
			synchronized (FiltersManager.class) {
				if (sInstance == null) {
					sInstance = new FiltersManager();
				}
			}
		}
		return sInstance;
	}


	public void filterSearchData(final SearchDataV3 searchData, OnFilterResultListener listener) {

		mOnFilterResultsListener = listener;

		createPool();

		pool.submit(new Runnable() {
			@Override
			public void run() {
				// TODO: 12/3/15 починить
//				List<Proposal> filteredTickets = mFilter.applyFilters(searchData);
//				mHandler.post(new EndRunnable(filteredTickets));

			}
		});

	}

	private void createPool() {
		if (pool == null) {
			pool = Executors.newCachedThreadPool();
		}
	}

	public void setOnFilterResultsListener(OnFilterResultListener onFilterResultsListener) {
		this.mOnFilterResultsListener = onFilterResultsListener;
	}

	public List<Proposal> getFilteredTickets() {
		return mFilteredProposals;
	}

	public class EndRunnable implements Runnable {

		public EndRunnable(List<Proposal> filteredTickets) {
			mFilteredProposals = filteredTickets;
		}

		@Override
		public void run() {
			if (mOnFilterResultsListener != null) {
				mOnFilterResultsListener.onFilteringFinished(mFilteredProposals);
			}
		}
	}

	public GeneralFilter getFilters() {
		return mFilter;
	}

	public void initFilter(final SearchDataV3 searchData, final Context context) {

		createPool();

		mFilteredProposals = AviasalesSDKV3.getInstance().getSearchData().getProposals();
		pool.submit(new Runnable() {
			@Override
			public void run() {
				if (context == null) {
					return;
				}
				mFilter = new GeneralFilter(context);

				if (searchData.getProposals() != null) {
					// TODO: 12/3/15 починить
//					PreInitializeFilters preInitializeFilters = new PreInitializeFilters(context, searchData);
//					preInitializeFilters.setupFilters();
					// TODO: 12/3/15 починить
//					mFilter.init(searchData, preInitializeFilters);
					// TODO: 12/3/15 починить
//					List<Proposal> filteredTickets = mFilter.applyFilters(searchData);
//					Collections.sort(filteredTickets, ProposalManager.getInstance().getProposalComparator());

//					mFilteredProposals = filteredTickets;
				}

			}
		});

	}

}
