package ru.aviasales.template.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import ru.aviasales.template.ui.dialog.BaseDialogFragment;
import ru.aviasales.template.utils.BackPressable;

public abstract class BaseFragment extends Fragment implements BackPressable {

	public static final String DIALOG_TAG = "aviasales_dialog";
	public static final String PREFERENCES_NAME = "ru.aviasales";
	public static final String EXTRA_REMOVED_DIALOG = "removed_dialog";

	private BaseDialogFragment dialogFragment;
	protected String removedDialogFragmentTag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			removedDialogFragmentTag = savedInstanceState.getString(EXTRA_REMOVED_DIALOG);
		}
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {

		if (removedDialogFragmentTag != null) {
			resumeDialog(removedDialogFragmentTag);
			removedDialogFragmentTag = null;
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		dismissDialogWithSave();
		super.onPause();
	}

	private void showDialog() {
		dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		// removing dialog in pre-honeycomb because onSavedInstanceState called before onPause
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			dismissDialogWithSave();
		}

		if (removedDialogFragmentTag != null) {
			outState.putString(EXTRA_REMOVED_DIALOG, removedDialogFragmentTag);
		}
		super.onSaveInstanceState(outState);
	}

	protected void createDialog(final BaseDialogFragment dialogFragment) {
		if (dialogFragment == null || getActivity() == null)
			return;

		removeDialogFragment();

		this.dialogFragment = dialogFragment;
		showDialog();
	}

	protected void showActionBar(boolean isShowActionBar) {
		getActionBar().setShowHideAnimationEnabled(false);
		if (isShowActionBar) {
			getActionBar().show();
		} else {
			getActionBar().hide();
		}
	}

	protected ActionBar getActionBar() {
		return ((AppCompatActivity) getActivity()).getSupportActionBar();
	}

	protected void setTextToActionBar(String textToActionBar) {
		getActionBar().setTitle(textToActionBar);
	}

	private void removeDialogFragment() {
		if (getActivity() != null) {
			FragmentTransaction ft = getParentFragmentManager().beginTransaction();
			Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
			if (prev != null) {
				ft.remove(prev);
			}
			ft.commitAllowingStateLoss();
		}
	}

	protected void dismissDialog() {
		if (dialogFragment != null && dialogFragment.isVisible()) {
			dialogFragment.dismissAllowingStateLoss();
		}
		removeDialogFragment();
		dialogFragment = null;
	}

	private void dismissDialogWithSave() {
		if (dialogFragment != null) {
			removedDialogFragmentTag = dialogFragment.getFragmentTag();
		}
		dismissDialog();
	}

	protected boolean dialogIsVisible() {
		return dialogFragment != null && dialogFragment.isVisible();
	}

	protected SharedPreferences getPreferences() {
		return getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	protected void startFragment(BaseFragment fragment, boolean shouldAddToBackStack) {

		Fragment parentFragment = getParentFragment();

		if (parentFragment instanceof AviasalesFragment) {
			((AviasalesFragment) parentFragment).startFragment(fragment, shouldAddToBackStack);
		}

	}

	protected void popFragmentFromBackStack() {
		if (getActivity() != null) {
			Fragment parentFragment = getParentFragment();

			if (parentFragment instanceof AviasalesFragment) {
				((AviasalesFragment) parentFragment).popFragmentFromBackStack();
			}
		}
	}

	protected void popBackStackInclusive(String tag) {
		if (getActivity() != null) {
			Fragment parentFragment = getParentFragment();

			if (parentFragment instanceof AviasalesFragment) {
				((AviasalesFragment) parentFragment).popBackStackInclusive(tag);
			}
		}
	}


	private FragmentManager getParentFragmentManager() {

		Fragment parentFragment = getParentFragment();

		if (parentFragment instanceof AviasalesFragment) {
			return ((AviasalesFragment) parentFragment).getAviasalesFragmentManager();
		} else {
			return getFragmentManager();
		}
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	protected abstract void resumeDialog(String removedDialogFragmentTag);

}
