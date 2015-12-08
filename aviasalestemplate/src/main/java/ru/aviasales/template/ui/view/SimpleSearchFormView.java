package ru.aviasales.template.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.model.SearchFormData;
import ru.aviasales.template.ui.model.SimpleSearchParams;

public class SimpleSearchFormView extends FrameLayout {

	private SearchFormPlaceButton btnOrigin;
	private SearchFormPlaceButton btnDestination;
	private SearchFormDateButton btnDepartDate;
	private SearchFormDateButton btnReturnDate;
	private CheckBox btnDateSwitch;
	private SearchFormData searchFormData;

	private SimpleSearchFormInterface listener;

	public SimpleSearchFormView(Context context) {
		super(context);
		setupViews(context);
	}

	public SimpleSearchFormView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews(context);
	}

	public SimpleSearchFormView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupViews(context);
	}

	public SimpleSearchFormInterface getListener() {
		return listener;
	}

	public void setListener(SimpleSearchFormInterface listener) {
		this.listener = listener;
	}

	private void setupViews(Context context) {
		LayoutInflater.from(context).inflate(R.layout.simple_search_part,
				this, true);

		btnOrigin = (SearchFormPlaceButton) findViewById(R.id.btn_origin);
		btnDestination = (SearchFormPlaceButton) findViewById(R.id.btn_destination);

		btnDepartDate = (SearchFormDateButton) findViewById(R.id.btn_depart_date);
		btnReturnDate = (SearchFormDateButton) findViewById(R.id.btn_return_date);

		btnDateSwitch = (CheckBox) findViewById(R.id.btn_return_date_switch);

		View.OnClickListener destinationOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (getContext() == null || listener == null)
					return;
				if (((SearchFormPlaceButton) v).getType() == SearchFormPlaceButton.TYPE_DEPART) {
					listener.originButtonPressed();
				} else {
					listener.destinationButtonPressed();
				}
			}
		};

		btnOrigin.setOnClickListener(destinationOnClickListener);
		btnDestination.setOnClickListener(destinationOnClickListener);

		View.OnClickListener dateOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (getContext() == null || listener == null)
					return;
				SimpleSearchParams searchParams = searchFormData.getSimpleSearchParams();
				if (((SearchFormDateButton) v).getType() == SearchFormDateButton.TYPE_DEPART) {
					listener.departDateButtonPressed();
				} else {
					listener.returnDateButtonPressed();
				}
				adjustReturnGroupLook();
			}
		};
		btnDepartDate.setOnClickListener(dateOnClickListener);
		btnReturnDate.setOnClickListener(dateOnClickListener);

		btnDateSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SimpleSearchParams searchParams = searchFormData.getSimpleSearchParams();
				searchParams.setReturnEnabled(((CheckBox) view).isChecked());

				adjustReturnGroupLook();
				if (listener != null && searchParams.getReturnDate() == null)
					listener.returnDateButtonPressed();
			}
		});

	}

	//must be called from onStart method
	public void setUpData(SearchFormData searchFormData) {
		if (getContext() == null) return;

		this.searchFormData = searchFormData;
		SimpleSearchParams searchParams = searchFormData.getSimpleSearchParams();
		btnOrigin.setData(searchParams.getOrigin());
		btnDestination.setData(searchParams.getDestination());
		btnDepartDate.setData(searchParams.getDepartDateString());
		btnReturnDate.setData(searchParams.getReturnDateString());
		adjustReturnGroupLook();
	}

	private void adjustReturnGroupLook() {
		boolean isReturnEnabled = searchFormData.getSimpleSearchParams().isReturnEnabled();
		btnReturnDate.setEnabled(isReturnEnabled);
		btnDateSwitch.setChecked(isReturnEnabled);
	}

	public interface SimpleSearchFormInterface {

		void departDateButtonPressed();

		void returnDateButtonPressed();

		void originButtonPressed();

		void destinationButtonPressed();

	}

}
