package ru.aviasales.navdrawerdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.aviasales.navdrawerdemo.R;


public class EmptyFragment extends Fragment {

	public static final String TAG = "empty_fragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.content_main, null);
	}
}
