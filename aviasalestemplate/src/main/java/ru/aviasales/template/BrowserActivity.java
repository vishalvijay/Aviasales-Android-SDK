package ru.aviasales.template;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.aviasales.template.ui.fragment.BrowserFragment;

public class BrowserActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aviasales_fragment_layout);
		initFragment();
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_child_place, BrowserFragment.newInstance(), null);
		fragmentTransaction.commit();
	}
}
