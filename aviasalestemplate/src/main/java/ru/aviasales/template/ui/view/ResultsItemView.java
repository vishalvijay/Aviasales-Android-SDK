package ru.aviasales.template.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.ProposalSegment;
import ru.aviasales.template.R;
import ru.aviasales.template.api.AirlineLogoApi;
import ru.aviasales.template.api.params.AirlineLogoParams;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.StringUtils;
import ru.aviasales.template.utils.Utils;

public class ResultsItemView extends CardView {

	private TextView tvPrice;
	private TextView tvCurrency;
	private ImageView ivAirlineLogo;

	private LinearLayout llContent;

	private List<ResultsItemRouteView> routeViews;

	public ResultsItemView(Context context) {
		super(context);
	}

	public ResultsItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ResultsItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvCurrency = (TextView) findViewById(R.id.tv_currency);
		ivAirlineLogo = (ImageView) findViewById(R.id.iv_airline);

		llContent = (LinearLayout) findViewById(R.id.content);
	}

	public void setProposal(Proposal proposal, Context context, boolean isComplexSearch) {

		Map<String, Double> currencies = getCurrencyRates();

		tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
		changeTextSize(proposal.getBestPrice());

		tvPrice.setText(StringUtils.formatPriceInAppCurrency(proposal.getBestPrice(), getAppCurrency(), currencies));

		tvCurrency.setText(getAppCurrency());

		if (routeViews == null) {
			routeViews = generateRouteViews(proposal, isComplexSearch);
			for (ResultsItemRouteView routeView : routeViews) {
				llContent.addView(routeView);
			}
		}

		List<ProposalSegment> proposalSegments = proposal.getSegments();
		int i = 0;
		for (ProposalSegment proposalSegment : proposalSegments) {
			routeViews.get(i++).setRouteData(proposalSegment.getFlights(), isComplexSearch);
		}

		try {
			final AirlineLogoParams params = new AirlineLogoParams();
			params.setContext(context);
			params.setIata(proposal.getValidatingCarrier());
			params.setImage(ivAirlineLogo);
			params.setWidth(context.getResources().getDimensionPixelSize(R.dimen.airline_logo_width));
			params.setHeight(context.getResources().getDimensionPixelSize(R.dimen.airline_logo_height));
			new AirlineLogoApi().getAirlineLogo(setAdditionalParamsToImageLoader(params));
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	protected List<ResultsItemRouteView> generateRouteViews(Proposal proposal, boolean complexSearch) {
		List<ResultsItemRouteView> routeViews = new ArrayList<>();

		for (ProposalSegment ignored : proposal.getSegments()) {
			View view = complexSearch ? createComplexRouteView() : createRouteView();
			routeViews.add((ResultsItemRouteView) view);
		}
		return routeViews;
	}

	protected View createRouteView() {
		return LayoutInflater.from(getContext())
				.inflate(R.layout.result_item_route, this, false);
	}

	protected View createComplexRouteView() {
		return LayoutInflater.from(getContext())
				.inflate(R.layout.result_item_complex_route, this, false);
	}


	public void setAlternativePrice(long price) {
		tvPrice.setText(StringUtils.formatPriceInAppCurrency(price, getAppCurrency(), getCurrencyRates()));
	}

	private Map<String, Double> getCurrencyRates() {
		return CurrencyUtils.getCurrencyRates();
	}

	protected AirlineLogoParams setAdditionalParamsToImageLoader(AirlineLogoParams params) {
		return params;
	}

	protected String getAppCurrency() {
		return CurrencyUtils.getAppCurrency(getContext());
	}

	public ImageView getIvAirlineLogo() {
		return ivAirlineLogo;
	}

	private void changeTextSize(long price) {
		final String priceText = StringUtils.formatPriceInAppCurrency(price, getAppCurrency(), getCurrencyRates());
		changePriceTextViewSizeIfNeeded(priceText);
	}

	private void changePriceTextViewSizeIfNeeded(String priceText) {
		int width = ((ViewGroup) tvPrice.getParent()).getWidth() == 0 ?
				getMeasuredMaxPriceTextViewWidth() : ((ViewGroup) tvPrice.getParent()).getWidth();

		if (width != 0) {
			float finalTextSizeInPx = tvPrice.getTextSize();
			int maxWidth = width - getResources().getDimensionPixelSize(R.dimen.airline_logo_width);
			int tvPriceRightMargin = Utils.convertDPtoPixels(getContext(), 3);
			int tvCurrencyMarginLeft = Utils.convertDPtoPixels(getContext(), 4);
			int currencyWidth = (int) tvCurrency.getPaint().measureText(CurrencyUtils.getAppCurrency(getContext()));
			Paint textPaint = new Paint(tvPrice.getPaint());
			int priceWidth = (int) (currencyWidth + tvPriceRightMargin + textPaint.measureText(priceText)) + tvCurrencyMarginLeft;
			while (priceWidth >= maxWidth) {
				finalTextSizeInPx -= Utils.convertDPtoPixels(getContext(), 1);
				textPaint.setTextSize(finalTextSizeInPx);
				priceWidth = (int) (currencyWidth + tvPriceRightMargin + textPaint.measureText(priceText)) + tvCurrencyMarginLeft;
			}

			tvPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, finalTextSizeInPx);
		}

	}

	private int getMeasuredMaxPriceTextViewWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		Point size = new Point();
		Display display = windowManager.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
		} else {
			size.set(display.getWidth(), display.getHeight());
		}
		int cardViewMarginLeft = getResources().getDimensionPixelSize(R.dimen.results_item_margin_left);
		int cardViewMarginRight = getResources().getDimensionPixelSize(R.dimen.results_item_margin_right);

		int screenWidth = size.x - cardViewMarginLeft - cardViewMarginRight;
		measure(MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST));
		return ((ViewGroup) tvPrice.getParent()).getMeasuredWidth();
	}

}
