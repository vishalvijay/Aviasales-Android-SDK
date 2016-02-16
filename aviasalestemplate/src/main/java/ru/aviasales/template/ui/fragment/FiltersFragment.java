package ru.aviasales.template.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.FiltersSet;
import ru.aviasales.template.filters.manager.FiltersManager;
import ru.aviasales.template.ui.view.filters.BaseFiltersScrollView;
import ru.aviasales.template.ui.view.filters.ClearFiltersView;
import ru.aviasales.template.ui.view.filters.FilterView;

public class FiltersFragment extends BaseFragment implements BaseFiltersScrollView.OnSomethingChangeListener {

	private static final String EXTRA_LOCAL_FILTERS_SAVED_STATE = "extra_local_filters_saved_state";

	private ViewGroup filtersParentView;
	private LinearLayout filtersContainer;

	private TextView tvApplyFilters;
	private ClearFiltersView clearFiltersView;
	private List<Proposal> filteredLocalProposals;

	@Nullable
	private FiltersSet localFiltersSet = null;

	private FilterView filterView;

	public static FiltersFragment newInstance() {
		return new FiltersFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		filtersParentView = (ViewGroup) inflater.inflate(R.layout.filters_fragment, container, false);

		tvApplyFilters = (TextView) filtersParentView.findViewById(R.id.btn_apply);
		clearFiltersView = (ClearFiltersView) filtersParentView.findViewById(R.id.clear_filters_view);

		tvApplyFilters.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveFiltersAndReturnToResults();
			}
		});
		clearFiltersView.setOnClearButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backToResultsWithoutSave();
			}
		});

		if (savedInstanceState != null) {
			localFiltersSet = (FiltersSet) savedInstanceState.getSerializable(EXTRA_LOCAL_FILTERS_SAVED_STATE);
		}

		setUpFilters();
		setHasOptionsMenu(true);
		applyFilters();

		return filtersParentView;
	}

	private void clearFilters() {
		if (localFiltersSet != null) {
			localFiltersSet.clearFilters();
		}

		FiltersManager.getInstance().setFilteredProposals(AviasalesSDK.getInstance().getSearchData().getProposals());
		FiltersManager.getInstance().setFiltersSet(localFiltersSet);
	}

	private void saveFiltersAndReturnToResults() {
		FiltersManager.getInstance().setFilteredProposals(getProposalsCopy(filteredLocalProposals));
		FiltersManager.getInstance().setFiltersSet(localFiltersSet);
		getActivity().onBackPressed();
	}

	private void setUpFilters() {
		if (localFiltersSet == null) {
			localFiltersSet = getCopyOfFilters();
		}

		if (filtersContainer != null) {
			filtersContainer.removeAllViews();
		}
		filtersContainer = (LinearLayout) filtersParentView.findViewById(R.id.ll_filters_container);
		createTabletOpenJawView();
		filtersContainer.addView(filterView);

		if (filteredLocalProposals != null) {
			setFoundTicketsText(filteredLocalProposals.size());
		} else {
			setFoundTicketsText(FiltersManager.getInstance().getFilteredProposals().size());
		}
	}

	private FiltersSet getCopyOfFilters() {
		return FiltersManager.getInstance().getFiltersSet().getCopy(getActivity().getApplicationContext());
	}

	private void createTabletOpenJawView() {
		filterView = new FilterView(getActivity());
		filterView.init(localFiltersSet, AviasalesSDK.getInstance().getSearchData().getSegments(), this);
	}

	private void applyFilters() {
		if (getActivity() == null) return;

		if (localFiltersSet != null) {
			FiltersManager.getInstance().filterSearchData(localFiltersSet, getSearchData(), new FiltersManager.OnFilterResultListener() {
				@Override
				public void onFilteringFinished(List<Proposal> filteredTicketsData) {
					if (getActivity() == null) return;

					FiltersFragment.this.filteredLocalProposals = filteredTicketsData;
					if (!localFiltersSet.isActive()) {
						clearFiltersView.hideClearButton();
					} else {
						clearFiltersView.showClearButton();
					}
					setFoundTicketsText(filteredTicketsData.size());
					tvApplyFilters.setEnabled(!filteredTicketsData.isEmpty());
				}
			});
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(EXTRA_LOCAL_FILTERS_SAVED_STATE, localFiltersSet);
		super.onSaveInstanceState(outState);
	}

	private void setFoundTicketsText(int count) {
		clearFiltersView.setFilteredTicketsCount(count);
	}

	private SearchData getSearchData() {
		return AviasalesSDK.getInstance().getSearchData();
	}

	@Override
	public void onChange() {
		applyFilters();
	}

	private List<Proposal> getProposalsCopy(List<Proposal> originalProposals) {
		List<Proposal> copiedProposals = new ArrayList<>();
		for (Proposal proposal : originalProposals) {
			Proposal copiedProposal = new Proposal(proposal);
			copiedProposal.setFilteredNativePrices(new HashMap<>(proposal.getFiltredNativePrices()));
			copiedProposal.setTotalWithFilters(proposal.getTotalWithFilters());
			copiedProposals.add(copiedProposal);
		}
		return copiedProposals;
	}

	private void backToResultsWithoutSave() {
		clearFilters();
		getActivity().onBackPressed();
	}

}
