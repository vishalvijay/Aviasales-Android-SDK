package ru.aviasales.template.proposal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ru.aviasales.core.AviasalesSDKV3;
import ru.aviasales.core.search.object.AirlineData;
import ru.aviasales.core.search.object.AirportData;
import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search_v3.objects.Flight;
import ru.aviasales.core.search_v3.objects.Proposal;
import ru.aviasales.core.search_v3.objects.SearchDataV3;
import ru.aviasales.core.search_v3.objects.Terms;
import ru.aviasales.core.search_v3.params.SearchParamsV3;

public class ProposalManager {
	private static final ProposalManager INSTANCE = new ProposalManager();

	private Proposal proposalData;
	private final List<String> agencies = new ArrayList<String>();
	private Map<String, GateData> gates;
	private Map<String, AirportData> airports;
	private Map<String, AirlineData> airlines;
	private SearchParamsV3 searchParams;
	private Map<String, Terms> nativePrices;

	public static ProposalManager getInstance() {
		return INSTANCE;
	}

	public void init(SearchParamsV3 params, SearchDataV3 searchData, Proposal proposal) {
		init(proposal, searchData.getGatesInfo(), params,
				AviasalesSDKV3.getInstance().getSearchData().getAirports(),
				AviasalesSDKV3.getInstance().getSearchData().getAirlines());
	}

	public void init(Proposal proposal, Map<String, GateData> gates, Map<String, AirportData> airports,
	                 Map<String, AirlineData> airlines, SearchParamsV3 searchParams) {
		this.proposalData = proposal;
		this.gates = gates;
		this.searchParams = searchParams;
		this.airports = airports;
		this.airlines = airlines;

		agencies.clear();
		nativePrices = null;

		initGates();
		initAgencies(proposal);
	}

	public void init(Proposal proposal, Map<String, GateData> gates, SearchParamsV3 searchParams) {
		init(proposal, gates, searchParams,
				AviasalesSDKV3.getInstance().getSearchData().getAirports(),
				AviasalesSDKV3.getInstance().getSearchData().getAirlines());
	}

	public void init(Proposal proposal, Map<String, GateData> gates, SearchParamsV3 searchParams,
	                 Map<String, AirportData> airports, Map<String, AirlineData> airlines) {
		this.proposalData = proposal;
		this.gates = gates;
		this.searchParams = searchParams;
		this.airports = airports;
		this.airlines = airlines;

		agencies.clear();
		nativePrices = null;

		initGates();
		initAgencies(proposal);
	}

	private void initGates() {
		nativePrices = proposalData.getFiltredNativePrices();
	}

	private void initAgencies(Proposal proposal) {

		agencies.addAll(proposal.getFiltredNativePrices().keySet());
		List<String> agenciesToRemove = new ArrayList<>();
		for (String gateId : agencies) {
			if (gateId.contains("-")) agenciesToRemove.add(gateId.replace("-", ""));
		}
		agencies.removeAll(agenciesToRemove);

		Collections.sort(agencies, new Comparator<String>() {
			@Override
			public int compare(String s, String s1) {
				if (nativePrices.get(s).getUnifiedPrice().equals(nativePrices.get(s1).getUnifiedPrice())) {
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
				return (Long.valueOf(nativePrices.get(s).getUnifiedPrice() - nativePrices.get(s1).getUnifiedPrice())).intValue();
			}
		});
	}

	private GateData getGate(String id) {
		for (Map.Entry<String, GateData> gate : gates.entrySet()) {
			if (gate.getValue().getId().equals(id)) return gate.getValue();
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

	public long getAgencyPrice(String agency) {
		return proposalData.getTerms().get(agency).getUnifiedPrice();
	}

	public long getBestAgencyPrice() {
		if (agencies.isEmpty()) return -1;
		return nativePrices.get(agencies.get(0)).getUnifiedPrice();
	}

	public String getBestAgencyName() {
		return getAgencyName(agencies.get(0));
	}

	public String getBestAgencyCode() {
		return agencies.get(0);
	}

	public String getAgencyName(String agency) {
		GateData gateData = gates.get(agency);
		return gateData.getLabel();
	}

	public Proposal getProposal() {
		return proposalData;
	}

	public SearchParamsV3 getSearchParams() {
		return searchParams;
	}

	public Comparator<? super Proposal> getProposalComparator() {
		return new Comparator<Proposal>() {
			@Override
			public int compare(Proposal proposal, Proposal proposal1) {
				if (proposal.getTotalWithFilters() == proposal1.getTotalWithFilters()) {
					return compareDurations(proposal, proposal1);
				}
				return (int) (proposal.getTotalWithFilters() - proposal1.getTotalWithFilters());
			}

			private int compareDurations(Proposal proposal, Proposal proposal1) {
				int proposalDuration = Proposal.getTicketDuration(proposal);
				int proposal1Duration = Proposal.getTicketDuration(proposal1);
				if (proposalDuration == proposal1Duration) {
					return compareDepartureTime(proposal, proposal1);
				}
				return proposalDuration - proposal1Duration;
			}

			private int compareDepartureTime(Proposal proposal, Proposal proposal1) {
				return (int) (getDepartureTime(proposal) - getDepartureTime(proposal1));
			}

			private Long getDepartureTime(Proposal proposal) {
				return proposal.getSegmentFlights(0).get(0).getLocalDepartureTimestamp();
			}
		};
	}


	public int getProposalDuration(Proposal proposal) {
		int dur = 0;
		for (int i = 0; i < proposal.getSegments().size(); i++) {
			dur += getRouteDurationInMinRt(proposal.getSegmentFlights(i));
		}

		return dur;
	}

	public int getRouteDurationInMinRt(List<Flight> flights) {
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

	public int getRouteDurationInMin(int number) {
		return proposalData.getSegmentDurations().get(number);
	}

	public Map<String, AirlineData> getAirlines() {
		return airlines;
	}

	public Map<String, AirportData> getAirports() {
		return airports;
	}

	public Map<String, GateData> getGates() {
		return gates;
	}
}
