package ru.aviasales.template.filters;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.aviasales.core.search.object.GateData;
import ru.aviasales.core.search.object.Proposal;
import ru.aviasales.expandedlistview.view.BaseCheckedText;


public class PayTypeFilter implements Serializable {

	private transient Context context;

	private List<BaseCheckedText> payTypeList;
	private List<String> restrictedGates = new ArrayList<>();

	public PayTypeFilter(Context context) {
		payTypeList = new ArrayList<>();
		this.context = context;
	}

	public PayTypeFilter(Context context, PayTypeFilter payTypeFilter) {
		this.context = context;
		if (payTypeFilter.getPayTypeList() == null) return;

		payTypeList = new ArrayList<>();
		for (int i = 0; i < payTypeFilter.getPayTypeList().size(); i++) {
			payTypeList.add(new BaseCheckedText(payTypeFilter.getPayTypeList().get(i)));
		}

		restrictedGates = payTypeFilter.getRestrictedGates();
	}

	public void mergeFilter(PayTypeFilter payTypeFilter) {
		if (payTypeFilter.isActive()) {
			for (BaseCheckedText checkedText : payTypeList) {
				for (BaseCheckedText checkedText1 : payTypeFilter.getPayTypeList()) {
					if (checkedText.getName().equals(checkedText1.getName())) {
						checkedText.setChecked(checkedText1.isChecked());
					}
				}
			}
		}
	}

	// must be called before isActual()
	public void calculateRestrictedAgencies(List<GateData> gates) {
		for (GateData gate : gates) {
			if (!restrictedGates.contains(gate.getId())) {
				restrictedGates.add(gate.getId());
			}

			if (gate.getPaymentMethods() == null) continue;
			for (String paymentMethod : gate.getPaymentMethods()) {
				if (isPaymentMethodAccepted(paymentMethod) && restrictedGates.contains(gate.getId())) {
					restrictedGates.remove(gate.getId());
				}
			}
		}
	}

	private boolean isPaymentMethodAccepted(String paymentMethod) {
		for (BaseCheckedText BaseCheckedText : payTypeList) {
			if (BaseCheckedText.getName().equals(PaymentHelper.getPaymentStringByCode(context, paymentMethod))) {
				return BaseCheckedText.isChecked();
			}
		}
		return true;
	}

	public void addPayType(String payType) {
		payTypeList.add(new BaseCheckedText(payType));
	}

	public void sortByName() {
		Collections.sort(payTypeList, BaseCheckedText.sortByName);
	}

	public List<String> getRestrictedGates() {
		return restrictedGates;
	}

	public void setRestrictedGates(List<String> restrictedGates) {
		this.restrictedGates = restrictedGates;
	}

	public List<BaseCheckedText> getPayTypeList() {
		return payTypeList;
	}

	public void setPayTypesFromGsonClass(List<String> payTypes) {
		for (String payType : payTypes) {
			payTypeList.add(new BaseCheckedText(PaymentHelper.getPaymentStringByCode(context, payType)));
		}
		sortByName();
	}

	public boolean isActual(Proposal proposal) {
		for (String agencyToRemove : restrictedGates) {
			if (proposal.getFiltredNativePrices().containsKey(agencyToRemove)) {
				proposal.getFiltredNativePrices().remove(agencyToRemove);
			}
		}

		return proposal.getFiltredNativePrices().size() != 0;
	}

	public boolean isActive() {
		for (BaseCheckedText payType : payTypeList) {
			if (!payType.isChecked()) {
				return true;
			}
		}
		return false;
	}

	public void clearFilter() {
		for (BaseCheckedText payType : payTypeList) {
			payType.setChecked(true);
			restrictedGates.clear();
		}
	}

	public boolean isValid() {
		return !payTypeList.isEmpty();
	}
}
