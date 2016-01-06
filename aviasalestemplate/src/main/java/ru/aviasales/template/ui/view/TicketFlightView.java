package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Map;

import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.Flight;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.core.search.object.SearchData;
import ru.aviasales.template.R;

public class TicketFlightView extends LinearLayout {

	private List<Flight> flights;
	private Map<String, AirlineData> airlines;
	private Map<String, AirportData> airports;

	public TicketFlightView(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	public TicketFlightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}

	public TicketFlightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(VERTICAL);
	}

	public void setData(SearchData searchData,
	                    Proposal proposal, int index) {
		this.airports = searchData.getAirports();
		this.airlines = searchData.getAirlines();
		this.flights = proposal.getSegmentFlights(index);
		generateViews();
	}

	private void generateViews() {

		Flight prevFlight = null;
		for (Flight flight : flights) {
			if (prevFlight != null) {
				TicketTransferView transferView = (TicketTransferView) LayoutInflater.from(getContext())
						.inflate(R.layout.ticket_transfer_item, this, false);
				addView(transferView);
				if (!isInEditMode()) {
					transferView.setData(flight, airports);
				}
			}

			TicketFlightSegmentView flightSegmentView = (TicketFlightSegmentView) LayoutInflater.from(getContext())
					.inflate(R.layout.ticket_flight_segment, this, false);
			addView(flightSegmentView);
			if (!isInEditMode()) {
				flightSegmentView.setData(airlines, airports, flight);
			}
			prevFlight = flight;
		}
	}
}
