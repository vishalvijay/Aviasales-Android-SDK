package ru.aviasales.navdrawerdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.aviasales.navdrawerdemo.fragment.EmptyFragment;
import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.identification.IdentificationData;
import ru.aviasales.template.ui.fragment.AviasalesFragment;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private AviasalesFragment aviasalesFragment;

	// replace to your travel payout credentiials
	private final static String TRAVEL_PAYOUTS_MARKER = "your_travel_payouts_marker";
	private final static String TRAVEL_PAYOUTS_TOKEN = "your_travel_payouts_token";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		init(savedInstanceState);
	}

	private void init(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			AviasalesSDK.getInstance().init(this, new IdentificationData(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN));
			showAviasalesSdkFragment();
		}
	}

	private void showAviasalesSdkFragment() {
		FragmentManager fm = getSupportFragmentManager();

		aviasalesFragment = (AviasalesFragment) fm.findFragmentByTag(AviasalesFragment.TAG);
		FragmentTransaction fragmentTransaction = fm.beginTransaction();

		if (aviasalesFragment == null) {
			aviasalesFragment = (AviasalesFragment) AviasalesFragment.newInstance();
		}
		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		fragmentTransaction.replace(R.id.content_main, aviasalesFragment, AviasalesFragment.TAG);
		fragmentTransaction.commit();
	}


	private void showEmptyFragment() {
		FragmentManager fm = getSupportFragmentManager();

		EmptyFragment emptyFragment = (EmptyFragment) fm.findFragmentByTag(EmptyFragment.TAG);

		FragmentTransaction fragmentTransaction = fm.beginTransaction();

		if (emptyFragment == null) {
			emptyFragment = new EmptyFragment();
			fragmentTransaction.addToBackStack(null);

		}
		fragmentTransaction.replace(R.id.content_main, emptyFragment, EmptyFragment.TAG);
		fragmentTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			if (!(getAviasalesFragment() != null && getAviasalesFragment().onBackPressed())) {
				super.onBackPressed();
			}
		}
	}

	@Nullable
	private AviasalesFragment getAviasalesFragment() {
		if (aviasalesFragment == null) {
			aviasalesFragment = (AviasalesFragment) getSupportFragmentManager().findFragmentByTag(AviasalesFragment.TAG);
			if (aviasalesFragment != null) {
				return aviasalesFragment;
			} else {
				return null;
			}
		} else {
			return aviasalesFragment;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.nav_sdk) {
			showAviasalesSdkFragment();
		} else if (id == R.id.nav_empty) {
			showEmptyFragment();
		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}
}
