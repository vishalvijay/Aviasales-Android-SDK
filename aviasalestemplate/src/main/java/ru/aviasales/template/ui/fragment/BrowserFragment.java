package ru.aviasales.template.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.dialog.BrowserLoadingDialogFragment;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class BrowserFragment extends BaseFragment {

	public static final String PROPERTY_BUY_URL = "BUY_URL";
	public static final String PROPERTY_BUY_AGENCY = "BUY_AGENCY";

	private boolean needToDismissDialog = false;
	private WebView webView;
	private WebView secondaryWebView;

	private BrowserLoadingDialogFragment dialog;
	private String agency;
	private boolean loadingFinished = false;
	private MenuItem btnBack;
	private MenuItem btnForward;
	private ProgressBar progressbar;
	private FrameLayout webViewPlaceHolder;

	public static BrowserFragment newInstance() {
		BrowserFragment browserFragment = new BrowserFragment();
		Bundle bundle = new Bundle();
		return browserFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		agency = getPreferences().getString(PROPERTY_BUY_AGENCY, null);

		super.onCreate(savedInstanceState);

		setRetainInstance(true);
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.browser_fragment, container, false);
		setupViews(layout);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
		setTextToActionBar(String.format(getString(R.string.browser_title), agency));

		return layout;
	}

	private void setupViews(ViewGroup layout) {
		webViewPlaceHolder = (FrameLayout) layout.findViewById(R.id.webview_placeholder);
		progressbar = (ProgressBar) layout.findViewById(R.id.progressbar);
		progressbar.setAlpha(0);

		String url = getPreferences().getString(PROPERTY_BUY_URL, null);
		setupWebView(webViewPlaceHolder, url);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setupWebView(final FrameLayout webViewPlaceHolder, String url) {
		if (webView == null) {
			webView = new WebView(getActivity());
			webView.setLayoutParams(getWebViewLayoutParams());

			webView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					super.onProgressChanged(view, newProgress);

					if (progressbar.getAlpha() == 0) {
						progressbar.setVisibility(View.VISIBLE);
						progressbar.animate().alpha(1).setDuration(200).start();
					}
					progressbar.setProgress(newProgress);

					if (newProgress == 100) {
						progressbar.animate().alpha(0).setDuration(200).setListener(new AnimatorListenerAdapter() {
							@Override
							public void onAnimationEnd(Animator animation) {
								progressbar.setVisibility(View.GONE);
							}
						}).start();
					}
				}

				// Add new webview in same window
				@Override
				public boolean onCreateWindow(WebView view, boolean dialog,
				                              boolean userGesture, Message resultMsg) {
					if (secondaryWebView != null) webViewPlaceHolder.removeView(secondaryWebView);
					secondaryWebView = new WebView(getActivity());
					secondaryWebView.getSettings().setJavaScriptEnabled(true);
					secondaryWebView.setWebChromeClient(this);
					secondaryWebView.setWebViewClient(new WebViewClient());
					secondaryWebView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
					webViewPlaceHolder.addView(secondaryWebView);
					WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
					transport.setWebView(secondaryWebView);
					resultMsg.sendToTarget();
					return true;
				}

				// remove new added webview whenever onCloseWindow gets called for new webview.
				@Override
				public void onCloseWindow(WebView window) {
					if (secondaryWebView != null) webViewPlaceHolder.removeView(secondaryWebView);
				}
			});
			webView.setWebViewClient(new AsWebViewClient());
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setLoadWithOverviewMode(true);
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setDisplayZoomControls(false);
			webView.getSettings().setDomStorageEnabled(true);
			CookieManager.getInstance().setAcceptCookie(true);

			webView.getSettings().setSupportMultipleWindows(true);

			webView.loadUrl(url);
		} else {
			if (webView.getParent() != null) {
				((ViewGroup) webView.getParent()).removeView(webView);
			}
		}

		webViewPlaceHolder.removeAllViews();
		webViewPlaceHolder.addView(webView);
	}

	private ViewGroup.LayoutParams getWebViewLayoutParams() {
		return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onResume() {
		if (!loadingFinished) {
			showLoadingDialog();
		}

		if (needToDismissDialog) {
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		dismissDialogFragment();
		super.onPause();
	}

	private void showLoadingDialog() {
		if (getActivity() == null
				|| getActivity().isFinishing()) {
			return;
		}
		if (dialog != null) {
			try {
				dialog.dismiss();
				dialog = null;
			} catch (Exception ignore) {
			}
		}
		if (dialog == null || !dialog.isAdded()) {
			FragmentManager fm = getActivity().getFragmentManager();
			dialog = new BrowserLoadingDialogFragment();
			dialog.setCancelable(false);
			dialog.setAgency(agency);
			dialog.show(fm, "browser_dialog");
		}

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (dialog != null) {
					dismissDialogFragment();
				}
			}
		};
		new Timer().schedule(timerTask, 4000);
	}

	@Override
	public boolean onBackPressed() {
		dismissDialogFragment();
		if (webView != null) {
			webView.setVisibility(View.INVISIBLE);
			((ViewGroup) webView.getParent()).removeAllViews();
			webView.clearHistory();
			webView.clearCache(true);
			webView.destroy();
			webView = null;
		}
		return false;
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {

	}

	private void dismissDialogFragment() {

		if (getActivity() == null
				|| getActivity().isFinishing()) {
			return;
		}
		if (dialog != null) {
			try {
				dialog.dismiss();
				dialog = null;
			} catch (IllegalStateException e) {
				needToDismissDialog = true;
			}
		}
	}

	private class AsWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			setBrowserNav();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			loadingFinished = true;

			dismissDialogFragment();
			setBrowserNav();
		}
	}

	private void setBrowserNav() {
		if (webView != null && btnBack != null && btnForward != null) {
			btnBack.setEnabled(webView.canGoBack());
			btnForward.setEnabled(webView.canGoForward());
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (getActivity() != null) {
			inflater.inflate(R.menu.browser_menu, menu);
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		btnBack = menu.findItem(R.id.back);
		btnForward = menu.findItem(R.id.forward);

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.back) {
			webView.goBack();
			return true;
		} else if (i == R.id.forward) {
			webView.goForward();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
