package ru.aviasales.template.ui.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.model.ComplexSearchParamsSegment;
import ru.aviasales.template.ui.model.SearchFormData;
import ru.aviasales.template.ui.view.complex_search.ComplexInfoBlockView;
import ru.aviasales.template.ui.view.complex_search.ComplexParamsView;
import ru.aviasales.template.ui.view.complex_search.ComplexSegmentView;

public class ComplexSearchFormView extends FrameLayout {

	private NestedScrollView scrollView;
	private ComplexParamsView complexParamsView;

	private ComplexSearchFormInterface listener;

	private SearchFormData searchFormData;

	public ComplexSearchFormView(Context context) {
		super(context);
		setupViews(context);
	}


	public ComplexSearchFormView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews(context);
	}

	public ComplexSearchFormView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupViews(context);
	}

	public void setupViews(Context context) {
		LayoutInflater.from(context).inflate(R.layout.complex_search_form_part,
				this, true);

		scrollView = (NestedScrollView) findViewById(R.id.search_form_scroll_view);
		complexParamsView = ((ComplexParamsView) findViewById(R.id.search_form));
		complexParamsView.setListener(new ComplexParamsView.ComplexSearchSegmentListener() {
			@Override
			public void segmentBlockClicked(int blockType, int segmentPosition) {
				if (blockType == ComplexInfoBlockView.TYPE_LOCATION_DEPARTURE) {
					if (listener != null) listener.complexOriginButtonPressed(segmentPosition);
				} else if (blockType == ComplexInfoBlockView.TYPE_LOCATION_ARRIVAL) {
					if (listener != null) listener.complexDestinationButtonPressed(segmentPosition);
				} else {
					if (listener != null) {
						listener.complexDateButtonPressed(segmentPosition);
					}
				}
			}

			@Override
			public void segmentChanged(ComplexParamsView view, ComplexSegmentView segmentView, int segmentPosition, ComplexSearchParamsSegment segment) {
				searchFormData.getComplexSearchSegments().set(segmentPosition, segment);
			}

			@Override
			public void segmentRemoved(ComplexParamsView view, int segmentPosition) {
				searchFormData.getComplexSearchSegments().remove(segmentPosition);
			}

			@Override
			public void newSegmentAdded(ComplexParamsView view, ComplexSegmentView segmentView, int segmentPosition, ComplexSearchParamsSegment segment) {
				searchFormData.getComplexSearchSegments().add(segmentPosition, segment);
			}
		});

		complexParamsView.setSizeListener(new ComplexParamsView.OnSizeChangedListener() {
			@Override
			public void onChanged(ComplexParamsView view) {
				scrollView.scrollTo(0, scrollView.getBottom());
			}
		});

	}

	public void setupData(SearchFormData searchFormData) {
		if (getContext() == null) return;

		this.searchFormData = searchFormData;
		complexParamsView.initSegments(this.searchFormData.getComplexSearchSegments());
	}

	public void setListener(ComplexSearchFormInterface listener) {
		this.listener = listener;
	}

	public interface ComplexSearchFormInterface {

		void complexDateButtonPressed(int segment);

		void complexOriginButtonPressed(int segment);

		void complexDestinationButtonPressed(int segment);
	}
}
