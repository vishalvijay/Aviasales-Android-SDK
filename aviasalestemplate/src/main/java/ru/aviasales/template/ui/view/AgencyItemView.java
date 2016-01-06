package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.template.R;
import ru.aviasales.template.proposal.ProposalManager;
import ru.aviasales.template.utils.StringUtils;

public class AgencyItemView extends RelativeLayout {
	private TextView tvBestPrice;
	private TextView tvBestAgency;
	private ImageView ivMobileVersion;
	private String agency;

	private View topView;
	private View bottomView;

	public AgencyItemView(Context context) {
		super(context);
	}

	public AgencyItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AgencyItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();

		tvBestPrice = (TextView) findViewById(R.id.tv_price);
		tvBestAgency = (TextView) findViewById(R.id.tv_agency);
		ivMobileVersion = (ImageView) findViewById(R.id.iv_mobile_version);
		topView = findViewById(R.id.top_view);
		bottomView = findViewById(R.id.bottom_view);
	}

	public void setData(String agency, boolean hasMobileVersion) {
		this.agency = agency;
		tvBestPrice.setText(StringUtils.formatPriceInAppCurrency(ProposalManager.getInstance().getAgencyPrice(agency), getContext()));

		tvBestAgency.setText(ProposalManager.getInstance().getAgencyName(agency));
		if (hasMobileVersion) {
			ivMobileVersion.setVisibility(View.VISIBLE);
		} else {
			ivMobileVersion.setVisibility(View.GONE);
		}
	}

	public void setupTopAndBottomViews(boolean isFirstItem, boolean isLastItem) {
		if (topView == null || bottomView == null) return;

		if (isFirstItem) {
			topView.setVisibility(View.VISIBLE);
		} else {
			topView.setVisibility(View.GONE);
		}

		if (isLastItem) {
			bottomView.setVisibility(View.VISIBLE);
		} else {
			bottomView.setVisibility(View.GONE);
		}
	}

	public String getAgency() {
		return agency;
	}

}
