package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.core.search.params.SearchParams;
import ru.aviasales.template.R;
import ru.aviasales.template.utils.Utils;

public class TicketView extends LinearLayout {

	private final List<TicketFlightView> ticketFlightViewList = new ArrayList<>();
	private boolean isComplex;

	public TicketView(Context context) {
		super(context);
	}

	public TicketView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TicketView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setUpViews(Context context, Proposal proposal, SearchParams searchParams,
	                       SearchData searchData) {
		setOrientation(VERTICAL);

		isComplex = searchParams.isComplexSearch();

		if (proposal != null) {
			for (int i = 0; i < proposal.getSegments().size(); i++) {

				TicketFlightHeaderView thereToFlightHeader = (TicketFlightHeaderView) LayoutInflater.from(getContext())
						.inflate(R.layout.ticket_flight_header, this, false);
				thereToFlightHeader.setData(proposal.getSegmentFlights(i), proposal.getSegmentDurations().get(i), searchData, !isComplex && i == 1);

				addView(thereToFlightHeader);
				addView(createDivider());

				TicketFlightView thereToFlight = (TicketFlightView) LayoutInflater.from(context)
						.inflate(R.layout.ticket_flight, this, false);
				thereToFlight.setData(searchData, proposal, i);

				addView(thereToFlight);
				ticketFlightViewList.add(thereToFlight);
				addView(createDivider());
			}
		}
	}

	private View createDivider() {
		View divider = new View(getContext());
		divider.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.convertDPtoPixels(getContext(), 1)));
		divider.setBackgroundColor(getResources().getColor(R.color.grey_E4E4E4));
		return divider;
	}

}
