package ru.aviasales.template.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.search.params.Passengers;
import ru.aviasales.core.search.searching.SimpleSearchListener;
import ru.aviasales.template.R;
import ru.aviasales.template.ui.dialog.DatePickerDialogFragment;
import ru.aviasales.template.ui.dialog.PassengersDialogFragment;
import ru.aviasales.template.ui.dialog.TripClassDialogFragment;
import ru.aviasales.template.ui.listener.AviasalesImpl;
import ru.aviasales.template.ui.model.ComplexSearchParamsSegment;
import ru.aviasales.template.ui.model.SearchFormData;
import ru.aviasales.template.ui.model.SimpleSearchParams;
import ru.aviasales.template.ui.view.ComplexSearchFormView;
import ru.aviasales.template.ui.view.SearchFormPassengersButton;
import ru.aviasales.template.ui.view.SimpleSearchFormView;
import ru.aviasales.template.utils.DateUtils;
import ru.aviasales.template.utils.Utils;

public class SearchFormFragment extends BaseFragment implements SimpleSearchFormView.SimpleSearchFormInterface,
		ComplexSearchFormView.ComplexSearchFormInterface {

	private final static String EXTRA_DIALOG_SEGMENT_SHOWED = "extra_dialog_segment_showed";
	private final static String EXTRA_IS_COMPLEX_SEARCH_SELECTED = "extra_is_complex_search_selected";

	private final static int DIALOG_DEPART_SEGMENT_NUMBER = 0;
	private final static int DIALOG_RETURN_SEGMENT_NUMBER = 1;

	private boolean isComplexSearchSelected = false;
	private AviasalesImpl aviasalesImpl;

	private SearchFormPassengersButton btnPassengers;
	private ViewGroup btnTripClass;
	private TextView tvTripClass;
	private Button btnSearch;

	private RadioGroup rgSearchType;

	private SearchFormData searchFormData;

	private ComplexSearchFormView complexSearchFormView;
	private SimpleSearchFormView simpleSearchFormView;

	private int dialogSegmentNumber;

	public static SearchFormFragment newInstance() {
		return new SearchFormFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		if (savedInstanceState != null) {
			dialogSegmentNumber = savedInstanceState.getInt(EXTRA_DIALOG_SEGMENT_SHOWED);
			isComplexSearchSelected = savedInstanceState.getBoolean(EXTRA_IS_COMPLEX_SEARCH_SELECTED);
		}

		ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.search_form_fragment, container, false);

		setHasOptionsMenu(true);
		showActionBar(true);

		aviasalesImpl = (AviasalesImpl) getParentFragment();

		setupViews(layout);

		setupSearchFormsVisibility();

		return layout;
	}

	@Override
	public void onStart() {
		super.onStart();

		setupData();
	}

	private void setupViews(final ViewGroup layout) {

		btnPassengers = (SearchFormPassengersButton) layout.findViewById(R.id.btn_passengers);

		btnTripClass = (ViewGroup) layout.findViewById(R.id.btn_trip_class);
		tvTripClass = (TextView) layout.findViewById(R.id.tv_trip_class);

		btnSearch = (Button) layout.findViewById(R.id.btn_search);

		simpleSearchFormView = (SimpleSearchFormView) layout.findViewById(R.id.simple_search_view);
		complexSearchFormView = (ComplexSearchFormView) layout.findViewById(R.id.complex_search_view);

		btnPassengers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createPassengersDialog();
			}
		});

		btnTripClass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				createTripClassPickerDialog();
			}
		});

		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getActivity() == null) return;


				if (isHaveRestrictions()) {
					return;
				}

				if (!Utils.isOnline(getActivity())) {
					Toast.makeText(getActivity(), getString(R.string.search_no_internet_connection), Toast.LENGTH_LONG)
							.show();
					return;
				}

				AviasalesSDK.getInstance().startTicketsSearch(searchFormData.createSearchParams(isComplexSearchSelected), new SimpleSearchListener() {

				});
				startFragment(SearchingFragment.newInstance(), true);

			}
		});

		rgSearchType = (RadioGroup) layout.findViewById(R.id.radio_group);
		rgSearchType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				isComplexSearchSelected = checkedId == R.id.rb_complex_search;

				setupSearchFormsVisibility();
			}
		});
	}

	private void setupSearchFormsVisibility() {
		if (isComplexSearchSelected) {
			complexSearchFormView.setVisibility(View.VISIBLE);
			simpleSearchFormView.setVisibility(View.GONE);
		} else {
			complexSearchFormView.setVisibility(View.GONE);
			simpleSearchFormView.setVisibility(View.VISIBLE);
		}
	}

	private void setupData() {
		if (getActivity() == null) return;

		searchFormData = aviasalesImpl.getSearchFormData();
		simpleSearchFormView.setUpData(searchFormData);
		simpleSearchFormView.setListener(this);

		complexSearchFormView.setupData(searchFormData);
		complexSearchFormView.setListener(this);

		btnPassengers.setData(searchFormData.getPassengers());
		tvTripClass.setText(searchFormData.getTripClassName());

	}

	private void createPassengersDialog() {
		PassengersDialogFragment passengersDialogFragment = new PassengersDialogFragment(
				searchFormData.getPassengers(),
				new PassengersDialogFragment.OnPassengersChangedListener() {

					@Override
					public void onPassengersChanged(Passengers passengers) {
						btnPassengers.setData(passengers);

						searchFormData.setPassengers(passengers);
						dismissDialog();
					}

					@Override
					public void onCancel() {
						dismissDialog();
					}
				}
		);

		createDialog(passengersDialogFragment);
	}


	private void createTripClassPickerDialog() {
		TripClassDialogFragment tripClassDialogFragment = TripClassDialogFragment.newInstance(
				searchFormData.getTripClass(),
				new TripClassDialogFragment.OnTripClassChangedListener() {
					@Override
					public void onTripClassChanged(String tripClass) {
						searchFormData.setTripClass(tripClass);
						tvTripClass.setText(searchFormData.getTripClassName());
						dismissDialog();
					}

					@Override
					public void onCancel() {
						dismissDialog();
					}
				}
		);

		createDialog(tripClassDialogFragment);
	}

	private void createDatePickerDialog(Calendar minDate, Calendar maxDate, Calendar currentDate, DatePickerDialogFragment.OnDateChangedListener listener) {
		if (getActivity() == null) return;

		DatePickerDialogFragment dateDialog = DatePickerDialogFragment.newInstance(minDate, maxDate, currentDate);
		dateDialog.setOnDateChangedListener(listener);
		createDialog(dateDialog);
	}

	private void createDateDialog(Date currentDate, Date minDate, final int segmentNumber, final boolean isComplexSearch) {
		Calendar minCalendarDate = DateUtils.convertToCalendar(minDate);
		Calendar maxCalendarDate = DateUtils.getMaxCalendarDate();
		Calendar currentCalendarDate = DateUtils.convertToCalendar(currentDate);

		if (currentCalendarDate == null) {
			currentCalendarDate = minCalendarDate;
		}

		createDatePickerDialog(minCalendarDate, maxCalendarDate, currentCalendarDate, new DatePickerDialogFragment.OnDateChangedListener() {
			@Override
			public void onDateChanged(Calendar calendar) {

				if (isComplexSearch) {
					searchFormData.getComplexSearchSegments().get(segmentNumber).setCalendarDate(calendar);
					searchFormData.checkAndFixComplexSearchDates();
				} else {
					if (segmentNumber == DIALOG_DEPART_SEGMENT_NUMBER) {
						searchFormData.getSimpleSearchParams().setDepartDate(calendar);
						searchFormData.getSimpleSearchParams().checkReturnDate();

					} else if (segmentNumber == DIALOG_RETURN_SEGMENT_NUMBER) {
						searchFormData.getSimpleSearchParams().setReturnDate(calendar);
						searchFormData.getSimpleSearchParams().setReturnEnabled(true);
					}

				}
				simpleSearchFormView.setUpData(searchFormData);
				complexSearchFormView.setupData(searchFormData);
				dismissDialog();
			}

			@Override
			public void onCancel() {
				dismissDialog();
			}
		});
	}

	private void createDateDialogFromSearchData(int dialogSegmentNumber) {
		this.dialogSegmentNumber = dialogSegmentNumber;
		if (isComplexSearchSelected) {
			Date minimalAvailableDate = getMinimalDateForComplexSearch(dialogSegmentNumber);
			ComplexSearchParamsSegment currentSearchParamsSegment = searchFormData.getComplexSearchSegments().get(dialogSegmentNumber);

			createDateDialog(currentSearchParamsSegment.getDate(), minimalAvailableDate, dialogSegmentNumber, isComplexSearchSelected);
		} else {
			SimpleSearchParams searchParams = searchFormData.getSimpleSearchParams();
			switch (dialogSegmentNumber) {
				case DIALOG_DEPART_SEGMENT_NUMBER:
					createDateDialog(searchParams.getDepartDate(), DateUtils.getMinDate(), dialogSegmentNumber, isComplexSearchSelected);
					break;
				case DIALOG_RETURN_SEGMENT_NUMBER:
					createDateDialog(searchParams.getReturnDate(), searchParams.getDepartDate(), dialogSegmentNumber, isComplexSearchSelected);
					break;
			}
		}
	}

	private Date getMinimalDateForComplexSearch(int segmentNumber) {
		if (segmentNumber == 0) {
			return DateUtils.getCurrentDateInGMTMinus11Timezone();
		}

		for (int i = segmentNumber - 1; i >= 0; i--) {
			ComplexSearchParamsSegment segment = searchFormData.getComplexSearchSegments().get(i);
			if (segment.getDate() != null) return segment.getDate();
		}
		return new Date();
	}

	private boolean isHaveRestrictions() {
		if (searchFormData.areDestinationsSet(isComplexSearchSelected)) {
			showConditionFailedToast(R.string.search_toast_destinations);
			return true;
		}
		if (searchFormData.areDestinationsEqual(isComplexSearchSelected)) {
			showConditionFailedToast(R.string.search_toast_destinations_equality);
			return true;
		}

		if (searchFormData.isDepartureDateNotSet(isComplexSearchSelected)) {
			showConditionFailedToast(R.string.search_toast_depart_date);
			return true;
		}

		if (searchFormData.isDepartDatePassed(isComplexSearchSelected)) {
			showConditionFailedToast(R.string.search_toast_wrong_depart_date);
			return true;
		}

		if (!isComplexSearchSelected && searchFormData.getSimpleSearchParams().isReturnEnabled()) {
			if (searchFormData.isSimpleParamsNoReturnDateSet()) {
				showConditionFailedToast(R.string.search_toast_return_date);
				return true;
			}

			if (searchFormData.isSimpleSearchReturnDatePassed()) {
				showConditionFailedToast(R.string.search_toast_wrong_return_date);
				return true;
			}

			if (searchFormData.isSimpleSearchReturnEarlierThanDeparture()) {
				showConditionFailedToast(R.string.search_toast_return_date_less_than_depart);
				return true;
			}

			if (searchFormData.isSimpleSearchDatedMoreThanYearAhead()) {
				showConditionFailedToast(R.string.search_toast_dates_more_than_1year);
				return true;
			}
		}
		return false;
	}

	private void showConditionFailedToast(int stringId) {
		Toast.makeText(getActivity(), getResources().getString(stringId), Toast.LENGTH_LONG).show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		aviasalesImpl.saveState();
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();

		getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.white)));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(EXTRA_DIALOG_SEGMENT_SHOWED, dialogSegmentNumber);
		outState.putBoolean(EXTRA_IS_COMPLEX_SEARCH_SELECTED, isComplexSearchSelected);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {
		switch (removedDialogFragmentTag) {
			case PassengersDialogFragment.TAG:
				createPassengersDialog();
				break;
			case TripClassDialogFragment.TAG:
				createTripClassPickerDialog();
				break;
			case DatePickerDialogFragment.TAG:
				createDateDialogFromSearchData(dialogSegmentNumber);
				break;
		}
	}

	@Override
	public void departDateButtonPressed() {
		createDateDialogFromSearchData(DIALOG_DEPART_SEGMENT_NUMBER);
	}

	@Override
	public void returnDateButtonPressed() {
		createDateDialogFromSearchData(DIALOG_RETURN_SEGMENT_NUMBER);
	}

	@Override
	public void originButtonPressed() {
		if (getActivity() == null) return;
		startFragment(SelectAirportFragment.newInstance(SelectAirportFragment.TYPE_ORIGIN, isComplexSearchSelected, -1), true);
	}

	@Override
	public void destinationButtonPressed() {
		if (getActivity() == null) return;
		startFragment(SelectAirportFragment.newInstance(SelectAirportFragment.TYPE_DESTINATION, isComplexSearchSelected, -1), true);
	}

	@Override
	public void complexDateButtonPressed(int segment) {
		createDateDialogFromSearchData(segment);
	}

	@Override
	public void complexOriginButtonPressed(int segment) {
		if (getActivity() == null) return;
		startFragment(SelectAirportFragment.newInstance(SelectAirportFragment.TYPE_ORIGIN, isComplexSearchSelected, segment), true);
	}

	@Override
	public void complexDestinationButtonPressed(int segment) {
		if (getActivity() == null) return;
		startFragment(SelectAirportFragment.newInstance(SelectAirportFragment.TYPE_DESTINATION, isComplexSearchSelected, segment), true);
	}
}
