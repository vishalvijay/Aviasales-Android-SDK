package ru.aviasales.template.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.locale.LocaleUtil;
import ru.aviasales.core.search_airports.interfaces.OnSearchPlacesListener;
import ru.aviasales.core.search_airports.object.PlaceData;
import ru.aviasales.core.search_airports.params.SearchByNameParams;
import ru.aviasales.template.R;
import ru.aviasales.template.ui.adapter.SelectAirportAdapter;
import ru.aviasales.template.ui.adapter.SelectAirportInfoAdapter;
import ru.aviasales.template.ui.listener.OnPlaceSelectedListener;

public class SelectAirportFragment extends BaseFragment {

	private OnPlaceSelectedListener onPlaceSelectedListener;

	private static final String EXTRA_FRAGMENT_TYPE = "extra_fragment_type";
	private static final String EXTRA_IS_COMPLEX_SEARCH = "extra_is_complex_search";
	private static final String EXTRA_SEGMENT_NUMBER = "extra_segment_number";
	private static final String EXTRA_PLACES_CONTENT = "extra_places_content";
	private static final String EXTRA_KEYBOARD_HIDDEN = "extra_keyboard_hidden";


	public static final int TYPE_DESTINATION = 301;
	public static final int TYPE_ORIGIN = 302;

	private RecyclerView recyclerView;
	private EditText editText;
	private SelectAirportAdapter adapter;
	private SelectAirportInfoAdapter infoAdapter;
	private boolean isKeyboardHidden = false;

	private List<PlaceData> placesFromServer = new ArrayList<PlaceData>();

	private int requestCode = TYPE_ORIGIN;
	private boolean isComplexSearch;
	private Integer segmentNumber;

	public static SelectAirportFragment newInstance(int fragmentType, boolean isComplexSearch, int segmentNumber) {
		SelectAirportFragment selectAirportFragment = new SelectAirportFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_FRAGMENT_TYPE, fragmentType);
		bundle.putInt(EXTRA_SEGMENT_NUMBER, segmentNumber);
		bundle.putBoolean(EXTRA_IS_COMPLEX_SEARCH, isComplexSearch);
		selectAirportFragment.setArguments(bundle);
		return selectAirportFragment;
	}


	private final View.OnClickListener onAirportSelectedListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			PlaceData placeData = (PlaceData) view.getTag();

			if (placeData == null || getActivity() == null) return;
			hideKeyboard();

			onAirportSelected(placeData);

			getActivity().onBackPressed();

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		Bundle bundle = getArguments();
		setPlaceSelectedListener();
		requestCode = bundle.getInt(EXTRA_FRAGMENT_TYPE);
		isComplexSearch = bundle.getBoolean(EXTRA_IS_COMPLEX_SEARCH);
		segmentNumber = bundle.getInt(EXTRA_SEGMENT_NUMBER);

		if (savedInstanceState != null) {
			isKeyboardHidden = savedInstanceState.getBoolean(EXTRA_KEYBOARD_HIDDEN);
			placesFromServer = savedInstanceState.getParcelableArrayList(EXTRA_PLACES_CONTENT);
		}
	}

	private void setPlaceSelectedListener() {
		onPlaceSelectedListener = ((AviasalesFragment) getParentFragment());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.select_airport_fragment, container, false);
		setupViews(rootView);
		showActionBar(true);
		if (!isKeyboardHidden) {
			showKeyboardAndFocusOnEditText();
		}
		return rootView;
	}

	@Override
	public void onDestroyView() {

		if (getActivity() != null && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
			((AppCompatActivity) getActivity()).getSupportActionBar().hide();
		}
		super.onDestroyView();
	}

	@Override
	public void onPause() {
		hideKeyboard();
		super.onPause();
	}

	@Override
	public void onResume() {

		super.onResume();
		updateAdapter();
	}

	private void setupViews(final View view) {

		recyclerView = (RecyclerView) view.findViewById(R.id.rv_select_airport_fragment);
		recyclerView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				hideKeyboard();
				return false;
			}
		});

		editText = (EditText) view.findViewById(R.id.etv_select_airport_fragment);
		FrameLayout findIconLayout = (FrameLayout) view.findViewById(R.id.fl_select_airport_fragment_find_icon_layout);
		FrameLayout cancelIconLayout = (FrameLayout) view.findViewById(R.id.fl_select_airport_fragment_cancel_icon_layout);
		findIconLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showKeyboardAndFocusOnEditText();
			}
		});
		cancelIconLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (editText.getText().length() == 0) {
					popFragmentFromBackStack();
				} else {
					editText.setText("");
				}
			}
		});

		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setHasFixedSize(false);
		recyclerView.setLayoutManager(layoutManager);

		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focused) {
				if (focused) {
					((EditText) view).setCursorVisible(true);
				} else {
					((EditText) view).setCursorVisible(false);
				}
			}
		});

		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

				placesFromServer = new ArrayList<PlaceData>();

				if (editable.length() > 0) {
					infoAdapter.setInfoViewActive(true);
					infoAdapter.setInfoText(null);
					updateAdapter();
					SearchByNameParams params = setSearchByNameParams();

					AviasalesSDK.getInstance().startPlacesSearch(params, new OnSearchPlacesListener() {
						@Override
						public void onSuccess(List<PlaceData> placeDates) {
							placesFromServer = placeDates;

							if (placesFromServer.isEmpty()) {
								infoAdapter.setInfoViewActive(true);
								infoAdapter.setInfoText(R.string.destination_no_results);
							} else {
								infoAdapter.setInfoViewActive(false);
								infoAdapter.setInfoText(null);
							}
							updateAdapter();
						}

						@Override
						public void onCanceled() {
						}

						@Override
						public void onError(int errorCode, int responseCode, String searchId) {
						}
					});
				} else {
					AviasalesSDK.getInstance().cancelPlacesSearch();
					infoAdapter.setInfoViewActive(false);
					infoAdapter.setInfoText(null);
					updateAdapter();
				}
			}
		});

		createRecyclerViewAdapter();

	}

	private SearchByNameParams setSearchByNameParams() {
		SearchByNameParams params = new SearchByNameParams();
		params.setName(editText.getText().toString());
		params.setContext(getActivity());
		params.setLocale(LocaleUtil.getLocale());
		return params;
	}

	private void createRecyclerViewAdapter() {
		adapter = new SelectAirportAdapter();
		adapter.setAirports(placesFromServer);
		infoAdapter = new SelectAirportInfoAdapter(getActivity(), adapter);

		adapter.setOnClickListener(onAirportSelectedListener);

		if (recyclerView != null) {
			recyclerView.setAdapter(infoAdapter);
		}

		updateAdapter();
	}

	private void updateAdapter() {
		if (getActivity() == null) return;
		adapter.setAirports(placesFromServer);
		infoAdapter.notifyDataSetChanged();

	}

	private void showKeyboardAndFocusOnEditText() {
		if (getActivity() != null) {
			isKeyboardHidden = false;
			editText.requestFocus();
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
	}

	private void hideKeyboard() {
		if (getActivity() == null) return;
		isKeyboardHidden = true;
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	private void onAirportSelected(PlaceData placeData) {
		if (onPlaceSelectedListener != null) {
			onPlaceSelectedListener.onAirportSelected(placeData, requestCode, segmentNumber, isComplexSearch);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_KEYBOARD_HIDDEN, isKeyboardHidden);
		outState.putParcelableArrayList(EXTRA_PLACES_CONTENT, (ArrayList<? extends android.os.Parcelable>) placesFromServer);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void resumeDialog(String removedDialogFragmentTag) {

	}

}
