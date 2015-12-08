package ru.aviasales.template.ui.dialog;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoCheckedTextView;

import ru.aviasales.core.search.params.SearchParams;
import ru.aviasales.core.search_v3.params.SearchParamsV3;
import ru.aviasales.template.R;

public class TripClassDialogFragment extends BaseDialogFragment {

	public final static String TAG = "fragment.TripClassDialogFragment";

	private RobotoCheckedTextView economy;
	private RobotoCheckedTextView business;

	private String tripClass = SearchParamsV3.TRIP_CLASS_ECONOMY;

	private OnTripClassChangedListener onTripClassChangedListener;

	public static TripClassDialogFragment newInstance(String tripClass, OnTripClassChangedListener onTripClassChangedListener) {
		TripClassDialogFragment tripClassDialogFragment = new TripClassDialogFragment();
		tripClassDialogFragment.setTripClass(tripClass);
		tripClassDialogFragment.setOnTripClassChangedListener(onTripClassChangedListener);
		return tripClassDialogFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
	}

	@Override
	public String getFragmentTag() {
		return TAG;
	}

	private void setCheckedItem() {
		switch (tripClass) {
			case SearchParamsV3.TRIP_CLASS_ECONOMY:
				economy.setChecked(true);
				business.setChecked(false);
				break;
			case SearchParamsV3.TRIP_CLASS_BUSINESS:
				economy.setChecked(false);
				business.setChecked(true);
				break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.trip_class_picker_fragment, container);

		economy = (RobotoCheckedTextView) view.findViewById(R.id.tv_economy);
		business = (RobotoCheckedTextView) view.findViewById(R.id.tv_business);
		economy.getBackground().setColorFilter(getResources().getColor(R.color.colorAviasalesMain), PorterDuff.Mode.SRC_ATOP);
		business.getBackground().setColorFilter(getResources().getColor(R.color.colorAviasalesMain), PorterDuff.Mode.SRC_ATOP);

		setCheckedItem();

		((View) economy.getParent()).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				economy.setChecked(true);
				business.setChecked(false);
				if(onTripClassChangedListener != null) {
					onTripClassChangedListener.onTripClassChanged(SearchParamsV3.TRIP_CLASS_ECONOMY);
				}
			}
		});

		((View) business.getParent()).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				economy.setChecked(false);
				business.setChecked(true);
				if (onTripClassChangedListener != null) {
					onTripClassChangedListener.onTripClassChanged(SearchParamsV3.TRIP_CLASS_BUSINESS);
				}
			}
		});

		return view;
	}

	public void setTripClass(String tripClass) {
		this.tripClass = tripClass;
	}

	public void setOnTripClassChangedListener(OnTripClassChangedListener onTripClassChangedListener) {
		this.onTripClassChangedListener = onTripClassChangedListener;
	}

	public interface OnTripClassChangedListener {
		void onTripClassChanged(String tripClass);
		void onCancel();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (getDialog() == null) {
			return;
		}

		int dialogWidth = getResources().getDimensionPixelSize(R.dimen.trip_class_dialog_width);
		int dialogHeight;

		dialogHeight = getResources().getDimensionPixelSize(R.dimen.trip_class_dialog_height);

		getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		onTripClassChangedListener.onCancel();
		super.onCancel(dialog);
	}
}
