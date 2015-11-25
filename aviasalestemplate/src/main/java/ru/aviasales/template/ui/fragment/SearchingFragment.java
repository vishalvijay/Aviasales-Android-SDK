package ru.aviasales.template.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.http.exception.ApiExceptions;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.core.search.searching.OnTicketsSearchListener;
import ru.aviasales.template.R;
import ru.aviasales.template.filters.manager.FiltersManager;

public class SearchingFragment extends BaseFragment {

	public static final int ANIMATION_FINISH_DURATION = 1000;
	public static final int PROGRESS_BAR_LENGTH = 1000;

	private ProgressBar progressBar;
	private boolean isPaused = false;

	public static SearchingFragment newInstance() {
		return new SearchingFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.searching_fragment, container, false);

		setupViews(rootView);
		showActionBar(true);
		setTextToActionBar(getString(R.string.searching_information));
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);

		return rootView;
	}

	private void setupViews(View rootView) {
		progressBar = (ProgressBar) rootView.findViewById(R.id.pb_searching);
		progressBar.setMax(PROGRESS_BAR_LENGTH);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		switch (AviasalesSDK.getInstance().getSearchingTicketsStatus()) {
			case SEARCHING:
				AviasalesSDK.getInstance().setOnTicketsSearchListener(new OnTicketsSearchListener() {
					@Override
					public void onSuccess(SearchData searchData) {
						FiltersManager.getInstance().initFilter(searchData, getActivity());
						onSearchSuccessFinish();
					}

					@Override
					public void onProgressUpdate(int i) {
						progressBar.setProgress(i);
					}

					@Override
					public void onCanceled() {

					}

					@Override
					public void onError(int errorCode, int responseCode, String searchId) {
						if (getActivity() == null) {
							return;
						}

						switch (errorCode) {
							case ApiExceptions.NO_RESULTS_EXCEPTION:
								showToastAndReturnToSearchForm(getString(R.string.alert_no_results));
								break;
							case ApiExceptions.SERVER_EXCEPTION:
							case ApiExceptions.API_EXCEPTION:
								showToastAndReturnToSearchForm(getString(R.string.toast_error_api));
								break;
							case ApiExceptions.CONNECTION_EXCEPTION:
								if (isDetached()) return;
								showToastAndReturnToSearchForm(getString(R.string.toast_error_connection));
								break;
							case ApiExceptions.WRONG_SIGNATURE_EXCEPTION:
								showToastAndReturnToSearchForm(getString(R.string.signature_toast));
								break;
							case ApiExceptions.IO_EXCEPTION:
							default:
								showToastAndReturnToSearchForm(getString(R.string.toast_error_unknown));
						}
					}
				});
				break;
		}
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {
	}

	private void onSearchSuccessFinish() {
		if (getActivity() == null) {
			return;
		}

		if (progressBar.getProgress() == progressBar.getMax()) {
			showResults();
		}

		ValueAnimator progressAnimator = ValueAnimator.ofInt(progressBar.getProgress(), PROGRESS_BAR_LENGTH);
		progressAnimator.setDuration(ANIMATION_FINISH_DURATION);
		progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
		progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int progress = (Integer) animation.getAnimatedValue();
				progressBar.setProgress(progress);
			}
		});
		progressAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				showResults();
			}
		});
		progressAnimator.start();
	}

	private void showToastAndReturnToSearchForm(String toast) {
		if (getActivity() == null) return;
		Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
		if (!isPaused) {
			getActivity().onBackPressed();
		}
	}

	private void showResults() {
		if (!isPaused) {
			popFragmentFromBackStack();
			startFragment(ResultsFragment.newInstance(), true);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		isPaused = false;
		switch (AviasalesSDK.getInstance().getSearchingTicketsStatus()) {
			case CANCELED:
			case ERROR:
				getActivity().onBackPressed();
				break;
			case FINISHED:
				showResults();
				break;
		}
	}

	@Override
	public void onPause() {
		isPaused = true;
		super.onPause();
	}
}
