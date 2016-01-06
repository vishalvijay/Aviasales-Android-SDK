package ru.aviasales.template.ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.legacy.search.object.OldFlightData;
import ru.aviasales.core.legacy.search.object.TicketData;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.params.SearchParams;
import ru.aviasales.template.utils.CurrencyUtils;
import ru.aviasales.template.utils.Defined;

@Deprecated
public class TicketManager {
	private static final TicketManager INSTANCE = new TicketManager();

	private TicketData ticketData;
	private List<String> agencies = new ArrayList<String>();
	private Map<String, Double> gatesWithNormalizedPrices;
	private List<GateData> gates;
	private Map<String, AirportData> airports;
	private Map<String, AirlineData> airlines;
	private SearchParams searchParams;

	public static TicketManager getInstance() {
		return INSTANCE;
	}

	public void init(TicketData ticketData, List<GateData> gates, Map<String, AirportData> airports,
	                 Map<String, AirlineData> airlines, SearchParams searchParams) {
		this.ticketData = ticketData;
		this.gates = gates;
		this.searchParams = searchParams;
		this.airports = airports;
		this.airlines = airlines;

		agencies.clear();
		gatesWithNormalizedPrices = null;

		initGates(gates);
		initAgencies(ticketData);
	}

	public void init(TicketData ticketData, List<GateData> gates, SearchParams searchParams) {
		this.ticketData = ticketData;
		this.gates = gates;
		this.searchParams = searchParams;
		this.airports = AviasalesSDK.getInstance().getSearchData().getAirports();
		this.airlines = AviasalesSDK.getInstance().getSearchData().getAirlines();

		agencies.clear();
		gatesWithNormalizedPrices = null;

		initGates(gates);
		initAgencies(ticketData);
	}

	private void initAgencies(TicketData ticketData) {
		this.ticketData = ticketData;

		agencies.addAll(ticketData.getFiltredNativePrices().keySet());
		Collections.sort(agencies, new Comparator<String>() {
			@Override
			public int compare(String s, String s1) {
				if (gatesWithNormalizedPrices.get(s).intValue() == gatesWithNormalizedPrices.get(s1).intValue()) {
					Integer sRates = getGate(s).getRates();
					Integer s1Rates = getGate(s1).getRates();
					if (sRates == null) {
						return 1;
					}
					if (s1Rates == null) {
						return -1;
					}
					return s1Rates - sRates;
				}
				return gatesWithNormalizedPrices.get(s).intValue() - gatesWithNormalizedPrices.get(s1).intValue();
			}
		});
	}

	private void initGates(List<GateData> gatesList) {
		Map<String, Double> nativePrices = ticketData.getNativePrices();

		Map<String, Double> gatesWithPrices = new HashMap<String, Double>();
		for (String gate : nativePrices.keySet()) {
			String currencyCode = null;
			for (GateData gateInfo : gatesList) {
				if (gateInfo.getId().equals(gate)) {
					currencyCode = gateInfo.getCurrencyCode();
					break;
				}
			}
			if (currencyCode == null) {
				continue;
			}
			gatesWithPrices.put(gate, (currencyCode.equalsIgnoreCase(Defined.RESPONSE_DEFAULT_CURRENCY) ?
					nativePrices.get(gate) :
					Math.round(nativePrices.get(gate) * CurrencyUtils.getCurrencyRates().get(currencyCode))));
		}
		gatesWithNormalizedPrices = gatesWithPrices;
	}

	private GateData getGate(String id) {
		for (GateData gate : gates) {
			if (gate.getId().equals(id)) return gate;
		}
		return new GateData();
	}

	public boolean isAgencyHasMobileVersion(String code) {
		return getGate(code).hasMobileVersion();
	}

	public List<String> getAgenciesCodes() {
		if (agencies == null) return new ArrayList<String>();
		return agencies;
	}

	public int getBestAgencyPrice() {
		return getAgencyPrice(getAgenciesCodes().get(0));
	}

	public int getAgencyPrice(String agency) {
		return gatesWithNormalizedPrices.get(agency).intValue();
	}

	public String getBestAgencyName() {
		return getAgencyName(agencies.get(0));
	}

	public String getAgencyName(String agency) {
		for (int i = 0; i < gates.size(); i++) {
			if (gates.get(i).getId().equals(agency)) {
				return gates.get(i).getLabel();
			}
		}
		return "";
	}

	public TicketData getTicket() {
		return ticketData;
	}

	public SearchParams getSearchParams() {
		return searchParams;
	}

	public Comparator<? super TicketData> getTicketComparator() {
		return new Comparator<TicketData>() {
			@Override
			public int compare(TicketData ticketData, TicketData ticketData1) {
				if (ticketData.getTotalWithFilters().equals(ticketData1.getTotalWithFilters())) {
					return compareDurations(ticketData, ticketData1);
				}
				return ticketData.getTotalWithFilters() - ticketData1.getTotalWithFilters();
			}

			private int compareDurations(TicketData ticketData, TicketData ticketData1) {
				int ticketDuration = getTicketDurationInMin(ticketData);
				int ticket1Duration = getTicketDurationInMin(ticketData1);
				if (ticketDuration == ticket1Duration) {
					return compareDepartureTime(ticketData, ticketData1);
				}
				return ticketDuration - ticket1Duration;
			}

			private int compareDepartureTime(TicketData ticketData, TicketData ticketData1) {
				return (int) (getDepartureTime(ticketData) - getDepartureTime(ticketData1));
			}

			private Long getDepartureTime(TicketData ticketData) {
				return ticketData.getDirectFlights().get(0).getDeparture();
			}

			private int getTicketDurationInMin(TicketData ticketData) {
				return getRouteDurationInMin(ticketData.getDirectFlights())
						+ getRouteDurationInMin(ticketData.getReturnFlights());
			}
		};
	}

	public int getRouteDurationInMin(List<OldFlightData> flights) {
		if (flights == null) return 0;
		int duration = 0;
		for (int i = 0; i < flights.size(); i++) {
			duration += flights.get(i).getDuration();
			if (i > 0) {
				duration += flights.get(i).getDelay();
			}
		}
		return duration;
	}

	public int getTicketDuration(TicketData ticket) {
		return getRouteDurationInMin(ticket.getDirectFlights()) + getRouteDurationInMin(ticket.getReturnFlights());
	}

	public Map<String, AirlineData> getAirlines() {
		return airlines;
	}

	public Map<String, AirportData> getAirports() {
		return airports;
	}
}
