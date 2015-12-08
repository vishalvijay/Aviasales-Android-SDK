package ru.aviasales.template.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.buy.object.BuyData;
import ru.aviasales.core.buy.query.OnBuyProcessListener;
import ru.aviasales.core.http.exception.ApiExceptions;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.core.search.object.TicketData;
import ru.aviasales.core.search.params.SearchParams;
import ru.aviasales.core.search.searching.OnTicketsSearchListener;
import ru.aviasales.template.BrowserActivity;
import ru.aviasales.template.R;
import ru.aviasales.template.ticket.TicketManager;
import ru.aviasales.template.ui.adapter.AgencySpinnerAdapter;
import ru.aviasales.template.ui.dialog.ProgressDialogWindow;
import ru.aviasales.template.ui.view.TicketView;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.StringUtils;
import ru.aviasales.template.utils.Utils;

public class TicketDetailsFragment extends BaseFragment {

	private final String UPDATE_DIALOG_TAG = "update_dialog_tag";
	private TicketData ticketData;

	private Spinner agencySpinner;

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

		setAgencySpinnerInActionBar();

		ticketData = getTicketData();

		setupViews(layout);

		return layout;
	}


	private void setAgencySpinnerInActionBar() {
		final FrameLayout spinnerLayout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.ticket_agency_spinner, null);
		agencySpinner = (Spinner) spinnerLayout.findViewById(R.id.spinner);
		spinnerLayout.findViewById(R.id.fl_spinner_container).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				agencySpinner.performClick();
			}
		});
		agencySpinner.setClickable(false);
		agencySpinner.setFocusable(false);
		agencySpinner.setFocusableInTouchMode(false);
		final TextView spinnerTextView = (TextView) spinnerLayout.findViewById(R.id.tv_spinner);
		final AgencySpinnerAdapter agencyAdapter = new AgencySpinnerAdapter();
		if (TicketManager.getInstance().getAgenciesCodes().size() == 1) {
			agencySpinner.setVisibility(View.GONE);
			spinnerTextView.setVisibility(View.VISIBLE);
			spinnerTextView.setText(TicketManager.getInstance().getAgencyName(TicketManager.getInstance().getAgenciesCodes().get(0)));
		} else {
			agencySpinner.setVisibility(View.VISIBLE);
			spinnerTextView.setVisibility(View.GONE);

			agencyAdapter.setOnAgencyClickListener(new AgencySpinnerAdapter.OnAgencyClickListener() {
				@Override
				public void onAgencyClick(String agency, int position) {
					hideSpinner();
					launchBrowser(agency);
				}
			});
			agencySpinner.setAdapter(agencyAdapter);
		}

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(spinnerLayout);
	}

	public interface OnAgencySelected {
		void onClick(View view, boolean isAdditional);
	}

	private void setupViews(ViewGroup layout) {
		final OnAgencySelected buyListener = new OnAgencySelected() {
			@Override
			public void onClick(View view, boolean isAdditional) {

				if (getActivity() == null) return;
				launchBrowser((String) view.getTag());
			}
		};

		String price = StringUtils.formatPriceInAppCurrency(TicketManager.getInstance().getBestAgencyPrice(), getActivity());
		String currency = CurrencyUtils.getAppCurrency(getActivity());

		setBuyBtn(layout, buyListener);

		setupBestPrice(layout, price, currency);

		setupTicketView(layout);

	}

	private void setupTicketView(ViewGroup layout) {
		TicketView ticketView = (TicketView) layout.findViewById(R.id.ticket);
		ticketView.setUpViews(getActivity(), ticketData);
	}

	private void setupBestPrice(ViewGroup layout, String price, String currency) {
		TextView tvPrice = (TextView) layout.findViewById(R.id.tv_price);
		TextView tvCurrency = (TextView) layout.findViewById(R.id.tv_currency);

		tvPrice.setText(price);
		tvCurrency.setText(currency);
	}

	private void setBuyBtn(ViewGroup layout, final OnAgencySelected buyListener) {
		Button buyButton = (Button) layout.findViewById(R.id.btn_buy);
		buyButton.setTag(TicketManager.getInstance().getAgenciesCodes().get(0));
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
		for (GateData gateData : getGatesInfo()) {
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
		AviasalesSDK.getInstance().startBuyProcess(ticketData, gateKey, listener);
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
		return getSearchData().getSearchCacheTime() * 60 * 1000;
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
				if (DateUtils.isDateBeforeDateShiftLine(getSearchParams().getDepartCalendarObject())) {
					Toast.makeText(getActivity(), getString(R.string.ticket_refresh_dates_passed), Toast.LENGTH_SHORT).show();
					startFragment(SearchFormFragment.newInstance(), true);
				} else {

					if (!Utils.isOnline(getActivity())) {
						Toast.makeText(getActivity(), getString(R.string.search_no_internet_connection), Toast.LENGTH_LONG)
								.show();
						return;
					}

					AviasalesSDK.getInstance().startTicketsSearch(AviasalesSDK.getInstance().getSearchParamsOfLastSearch(), new OnTicketsSearchListener() {
						@Override
						public void onSuccess(SearchData searchData) {
						}

						@Override
						public void onProgressUpdate(int i) {

						}

						@Override
						public void onCanceled() {
						}

						@Override
						public void onError(int i, int i2, String s) {
						}
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
		return getSearchData().getLastSearchFinishTime();
	}

	protected List<GateData> getGatesInfo() {
		return getSearchData().getGatesInfo();
	}

	protected SearchData getSearchData() {
		return AviasalesSDK.getInstance().getSearchData();
	}

	protected TicketData getTicketData() {
		return TicketManager.getInstance().getTicket();
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

	private OnBuyProcessListener listener = new OnBuyProcessListener() {
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

	private void hideSpinner() {
		try {
			Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
			method.setAccessible(true);
			method.invoke(agencySpinner);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
