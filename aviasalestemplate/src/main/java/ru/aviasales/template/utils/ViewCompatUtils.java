package ru.aviasales.template.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

public class ViewCompatUtils {

	public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
		} else {
			view.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
		}
	}
}
