package ru.aviasales.template.ui.view.filters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.expandedlistview.interfaces.OnSomethingChange;
import ru.aviasales.expandedlistview.view.BaseCheckedText;
import ru.aviasales.template.R;

public class BaseFiltersListViewItem extends LinearLayout {

	private final Context context;
	protected RelativeLayout layout;
	protected TextView textView;
	protected CheckBox checkBox;
	protected BaseCheckedText checkedText;
	protected ViewStub airlineViewStub;

	public BaseFiltersListViewItem(Context context) {
		this(context, null);
	}

	public BaseFiltersListViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;
		setUpView(context);
	}

	protected void setUpView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.base_filter_list_item,
				this, true);

		layout = (RelativeLayout) findViewById(R.id.rlay_base_filter_list_item);
		textView = (TextView) findViewById(R.id.txtv_base_filter_list_item);
		checkBox = (CheckBox) findViewById(R.id.cbox_base_filter_list_item);
		airlineViewStub = (ViewStub) findViewById(R.id.stub_import_airline);
		checkBox.setSaveEnabled(false);
	}

	public void changeHeight(int heightInPx) {
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightInPx);
		layout.setLayoutParams(params);
	}

	public void setCheckedText(BaseCheckedText checkedText) {
		this.checkedText = checkedText;
		textView.setText(checkedText.getName());
		checkBox.setChecked(checkedText.isChecked());
	}

	public void setChecked(boolean checked) {
		checkedText.setChecked(checked);
		checkBox.setChecked(checked);
	}

	public void setOnClickListener(final OnSomethingChange listener) {
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				checkBox.setChecked(!checkBox.isChecked());
				checkedText.setChecked(!checkedText.isChecked());
				listener.onChange();
			}
		});
	}

	public void changeTextViewSize(int newSize) {
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
	}

	public boolean isChecked() {
		return checkedText.isChecked();
	}
}