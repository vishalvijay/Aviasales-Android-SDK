package ru.aviasales.template.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.buy.object.BuyData;
import ru.aviasales.core.buy.query.BuyProcessListener;
import ru.aviasales.core.http.exception.ApiExceptions;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.core.search.params.SearchParams;
import ru.aviasales.core.search.searching.SimpleSearchListener;
import ru.aviasales.template.BrowserActivity;
import ru.aviasales.template.R;
import ru.aviasales.template.proposal.ProposalManager;
import ru.aviasales.template.ui.dialog.ProgressDialogWindow;
import ru.aviasales.template.ui.view.AgencySpinner;
import ru.aviasales.template.ui.view.TicketView;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.StringUtils;
import ru.aviasales.template.utils.Utils;

public class TicketDetailsFragment extends BaseFragment {

	private final static int VALID_SEARCH_CACHE_TIME = 15 * 60 * 1000;

	private final String UPDATE_DIALOG_TAG = "update_dialog_tag";
	private Proposal proposalData;

	private AlertDialog updateDialog;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	public static TicketDetailsFragment newInstance() {
		return new TicketDetailsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.ticket_fragment, container, false);


		proposalData = getProposalData();

		setUpViews(layout);

		return layout;
	}


	private void setUpAgencySpinner(ViewGroup layout) {
		AgencySpinner agencySpinner = (AgencySpinner) layout.findViewById(R.id.agency_spinner);

		String agencyName = ProposalManager.getInstance().getAgencyName(ProposalManager.getInstance().getAgenciesCodes().get(0));

		TextView agencyNameTextView = (TextView) layout.findViewById(R.id.agency_text_name);
		agencyNameTextView.setText(String.format(getString(R.string.ticket_title_agency), agencyName));

		if (ProposalManager.getInstance().getAgenciesCodes().size() == 1) {
			agencySpinner.setVisibility(View.GONE);
		} else {
			agencySpinner.setVisibility(View.VISIBLE);

			agencySpinner.setupAgencies(ProposalManager.getInstance().getAgenciesCodes(), ProposalManager.getInstance().getGates());
			agencySpinner.setOnAgencyClickedListener(new AgencySpinner.OnAgencyClickedListener() {
				@Override
				public void onAgencyClick(String agency, int position) {
					launchBrowser(agency);
				}
			});
		}

	}

	public interface OnAgencySelected {
		void onClick(View view, boolean isAdditional);
	}

	private void setUpViews(ViewGroup layout) {
		final OnAgencySelected buyListener = new OnAgencySelected() {
			@Override
			public void onClick(View view, boolean isAdditional) {

				if (getActivity() == null) return;
				launchBrowser((String) view.getTag());
			}
		};

		String price = StringUtils.formatPriceInAppCurrency(ProposalManager.getInstance().getBestAgencyPrice(), getActivity());
		String currency = CurrencyUtils.getAppCurrency(getActivity());

		setBuyBtn(layout, buyListener);

		setUpBestPrice(layout, price, currency);

		setUpTicketView(layout);

		setUpAgencySpinner(layout);

	}

	private void setUpTicketView(ViewGroup layout) {
		TicketView ticketView = (TicketView) layout.findViewById(R.id.ticket);
		ticketView.setUpViews(getActivity(), proposalData, getSearchParams(), getSearchData());
	}

	private void setUpBestPrice(ViewGroup layout, String price, String currency) {
		TextView tvPrice = (TextView) layout.findViewById(R.id.tv_price);
		TextView tvCurrency = (TextView) layout.findViewById(R.id.tv_currency);

		tvPrice.setText(price);
		tvCurrency.setText(currency);
	}

	private void setBuyBtn(ViewGroup layout, final OnAgencySelected buyListener) {
		Button buyButton = (Button) layout.findViewById(R.id.btn_buy);
		buyButton.setTag(ProposalManager.getInstance().getAgenciesCodes().get(0));
		buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				buyListener.onClick(view, false);
			}
		});
	}

	protected Map<String, AirportData> getAirports() {
		return getSearchData().getAirports();
	}

	protected Map<String, AirlineData> getAirlines() {
		return getSearchData().getAirlines();
	}

	protected String getAgencyName(String key) {
		return getGateById(key).getLabel();
	}

	public GateData getGateById(String id) {
		for (GateData gateData : getGatesInfo().values()) {
			if (gateData.getId().equals(id)) {
				return gateData;
			}
		}
		return null;
	}

	public void launchBrowser(final String gateKey) {
		if (checkTimeAndShowDialogIfNeed()) {
			return;
		}
		AviasalesSDK.getInstance().startBuyProcess(proposalData, gateKey, listener);
		createProgressDialog();
	}

	private void createProgressDialog() {
		ProgressDialogWindow dialog = new ProgressDialogWindow();
		dialog.setCancelable(false);
		createDialog(dialog);
	}

	protected void openBrowser(String url, String gateKey) {
		onOpenBrowser(url, getAgencyName(gateKey));
	}

	private boolean checkTimeAndShowDialogIfNeed() {
		int expTime = getExpTimeInMls();
		if (System.currentTimeMillis() - getSearchTime() > expTime) {
			createRefreshDialog();
			return true;
		}
		return false;
	}

	private void createRefreshDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(getString(R.string.ticket_alert_results_old))
				.setPositiveButton(getString(R.string.ticket_alert_update), getUpdateSearchListener())
				.setNegativeButton(getString(R.string.ticket_alert_return), getReturnToSearchFormListener());
		updateDialog = builder.create();
		updateDialog.show();
	}

	protected int getExpTimeInMls() {
		return VALID_SEARCH_CACHE_TIME;
	}

	protected DialogInterface.OnClickListener getReturnToSearchFormListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				popBackStackInclusive(ResultsFragment.class.getSimpleName());
				dialog.dismiss();
			}
		};
	}

	protected DialogInterface.OnClickListener getUpdateSearchListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (DateUtils.isDateBeforeDateShiftLine(getSearchParams().getSegments().get(0).getDate())) {
					Toast.makeText(getActivity(), getString(R.string.ticket_refresh_dates_passed), Toast.LENGTH_SHORT).show();
					startFragment(SearchFormFragment.newInstance(), true);
				} else {

					if (!Utils.isOnline(getActivity())) {
						Toast.makeText(getActivity(), getString(R.string.search_no_internet_connection), Toast.LENGTH_LONG)
								.show();
						return;
					}

					AviasalesSDK.getInstance().startTicketsSearch(AviasalesSDK.getInstance().getSearchParamsOfLastSearch(), new SimpleSearchListener() {

					});

					popBackStackInclusive(ResultsFragment.class.getSimpleName());

					startFragment(SearchingFragment.newInstance(), true);

				}
				dialog.dismiss();
			}
		};
	}

	@Override
	public void onPause() {
		if (updateDialog != null && updateDialog.isShowing()) {
			removedDialogFragmentTag = UPDATE_DIALOG_TAG;
		}
		super.onPause();
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	protected long getSearchTime() {
		return getSearchData().getSearchCompletionTime();
	}

	protected Map<String, GateData> getGatesInfo() {
		return getSearchData().getGatesInfo();
	}

	protected SearchData getSearchData() {
		return AviasalesSDK.getInstance().getSearchData();
	}

	protected Proposal getProposalData() {
		return ProposalManager.getInstance().getProposal();
	}

	protected SearchParams getSearchParams() {
		return AviasalesSDK.getInstance().getSearchParamsOfLastSearch();
	}

	@Override
	public boolean onBackPressed() {
		if (dialogIsVisible()) {
			AviasalesSDK.getInstance().cancelBuyProcess();
			return true;
		}
		return false;
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {
		if (removedDialogFragmentTag.equals(ProgressDialogWindow.TAG)) {
			createProgressDialog();
			AviasalesSDK.getInstance().setOnBuyProcessListener(listener, true);
		} else if (removedDialogFragmentTag.equals(UPDATE_DIALOG_TAG)) {
			createRefreshDialog();
		}
	}

	private BuyProcessListener listener = new BuyProcessListener() {
		@Override
		public void onSuccess(BuyData data, String gateKey) {
			dismissDialog();
			if (getActivity() == null) {
				return;
			}

			String url = data.generateBuyUrl();
			if (url == null) {
				Toast.makeText(getActivity(), R.string.agency_adapter_server_error, Toast.LENGTH_SHORT).show();
			} else {
				if (getActivity() != null) {
					openBrowser(url, gateKey);
				}
			}
		}

		@Override
		public void onError(int errorCode) {
			dismissDialog();
			if (getActivity() == null) {
				return;
			}
			switch (errorCode) {
				case ApiExceptions.API_EXCEPTION:
					Toast.makeText(getActivity(), getResources().getText(R.string.toast_error_api), Toast.LENGTH_SHORT).show();
					break;
				case ApiExceptions.CONNECTION_EXCEPTION:
					Toast.makeText(getActivity(), getResources().getText(R.string.toast_error_connection), Toast.LENGTH_SHORT).show();
					break;
				case ApiExceptions.UNKNOWN_EXCEPTION:
				default:
					Toast.makeText(getActivity(), getResources().getText(R.string.toast_error_unknown), Toast.LENGTH_SHORT).show();
					break;
			}
		}

		@Override
		public void onCanceled() {
			dismissDialog();
		}
	};


	private void onOpenBrowser(String url, String agency) {
		if (getActivity() == null || url == null || agency == null) return;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Utils.getPreferences(getActivity())
					.edit()
					.putString(BrowserFragment.PROPERTY_BUY_URL, url)
					.putString(BrowserFragment.PROPERTY_BUY_AGENCY, agency)
					.commit();
			launchInternalBrowser();
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			getActivity().startActivity(intent);
		}
	}

	private void launchInternalBrowser() {
		Intent intent = new Intent(getActivity(), BrowserActivity.class);
		getActivity().startActivity(intent);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (updateDialog != null && updateDialog.isShowing()) {
				removedDialogFragmentTag = UPDATE_DIALOG_TAG;
			}
		}

		super.onSaveInstanceState(outState);
	}

}
