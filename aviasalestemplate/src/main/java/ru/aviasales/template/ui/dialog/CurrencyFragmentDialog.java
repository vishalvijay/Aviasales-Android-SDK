package ru.aviasales.template.ui.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.template.R;
import ru.aviasales.template.currencies.Currency;
import ru.aviasales.template.ui.adapter.CurrencyListAdapter;

public class CurrencyFragmentDialog extends BaseDialogFragment {

	public static final String TAG = "aviasales.CurrencyFragmentDialog";

	private ListView listView;
	private CurrencyListAdapter currencyAdapter;
	private List<Currency> currencies = new ArrayList<Currency>();

	private OnCurrencyChangedListener listener;

	public static CurrencyFragmentDialog newInstance(OnCurrencyChangedListener listener) {
		CurrencyFragmentDialog dialog = new CurrencyFragmentDialog();
		dialog.setOnCurrencyChangedListener(listener);
		return dialog;
	}

	public void setOnCurrencyChangedListener(OnCurrencyChangedListener listener) {
		this.listener = listener;
	}

	public interface OnCurrencyChangedListener {
		void onCurrencyChanged(String code);

		void onCancel();
	}


	public CurrencyFragmentDialog() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.currency_fragment_dialog, container);
		listView = (ListView) view.findViewById(R.id.lv_currencies);

		setAdapter();
		listView.setAdapter(currencyAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (getActivity() != null) {
					listener.onCurrencyChanged(currencies.get(i).getCode());
					currencyAdapter.notifyDataSetChanged();
					dismiss();
				}
			}
		});
		return view;
	}

	public void setAdapter() {
		if (currencyAdapter == null) {
			currencyAdapter = new CurrencyListAdapter(getActivity(), currencies);
		}
		currencyAdapter.setItems(currencies);
	}

	public void setItems(List<Currency> currencies) {
		this.currencies = currencies;
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		listener.onCancel();
		super.onCancel(dialog);
	}


	@Override
	public String getFragmentTag() {
		return TAG;
	}
}