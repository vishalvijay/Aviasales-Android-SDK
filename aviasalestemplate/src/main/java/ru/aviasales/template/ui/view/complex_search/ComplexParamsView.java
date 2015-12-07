package ru.aviasales.template.ui.view.complex_search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.aviasales.template.R;
import ru.aviasales.template.ui.model.ComplexSearchParamsSegment;

public class ComplexParamsView extends FrameLayout implements ComplexSegmentView.OnBlockClickListener {
	private static final int MIN_SEGMENTS_COUNT = 2;
	private static final int MAX_SEGMENTS_COUNT = 8;

	private LinearLayout segmentsLayout;
	private final Map<Integer, ComplexSegmentView> segmentViews = new TreeMap<>();
	private final List<ComplexSearchParamsSegment> segments = new ArrayList<>();
	private Button addButton;
	private Button removeButton;
	private ComplexSearchSegmentListener listener;
	private OnSizeChangedListener sizeListener;

	public ComplexParamsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LayoutInflater.from(context).inflate(R.layout.complex_search_params_view, this, true);
	}

	public ComplexParamsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.complex_search_params_view, this, true);
	}

	public ComplexParamsView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.complex_search_params_view, this, true);
	}

	public void setSizeListener(OnSizeChangedListener sizeListener) {
		this.sizeListener = sizeListener;
	}

	@Override
	public void onBlockClick(ComplexSegmentView view, int blockType) {
		if (listener != null) {
			listener.segmentBlockClicked(blockType, (Integer) view.getTag());
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		segmentsLayout = (LinearLayout) findViewById(R.id.ll_complex_search_segments);
		addButton = (Button) findViewById(R.id.btn_complex_search_add);
		removeButton = (Button) findViewById(R.id.btn_complex_search_remove);

		addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (segmentViews.size() < MAX_SEGMENTS_COUNT) {
					ComplexSearchParamsSegment prevSegment = segments.get(segments.size() - 1);
					addSegmentView(segmentViews.size(),
							new ComplexSearchParamsSegment(prevSegment.getDestination(), null, null));

					if (listener != null) {
						listener.newSegmentAdded(ComplexParamsView.this, segmentViews.get(segmentViews.size() - 1),
								segmentViews.size() - 1, segments.get(segmentViews.size() - 1));
					}

				}
				if (segments.size() == 3) {
					ComplexSearchParamsSegment firstSegment = segments.get(0);
					if (firstSegment.getOrigin() != null && segments.get(1).getDestination() == null) {
						segments.get(1).setOrigin(firstSegment.getDestination());
						segmentViews.get(1).changeDepartureData(segments.get(1).getOrigin().getIata(),
								segments.get(1).getOrigin().getName(), false);
						if (listener != null) {
							listener.segmentChanged(ComplexParamsView.this, segmentViews.get(1), 1, segments.get(1));
						}
					}
				}
				if (segmentViews.size() == MAX_SEGMENTS_COUNT) {
					addButton.setEnabled(false);
				}
				removeButton.setEnabled(true);
			}
		});

		removeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (segmentViews.size() > MIN_SEGMENTS_COUNT) {
					removeSegmentView(segmentViews.size() - 1, segments.get(segments.size() - 1));
				} else if (segmentViews.size() == MIN_SEGMENTS_COUNT) {
					clearSegmentData(segmentViews.size() - 1);
				}

				if (segmentViews.size() == MIN_SEGMENTS_COUNT &&
						segments.get(1).getOrigin() == null &&
						segments.get(1).getDestination() == null &&
						segments.get(1).getDate() == null) {
					removeButton.setEnabled(false);
				}
				addButton.setEnabled(true);
			}
		});
	}

	public void initSegments(List<ComplexSearchParamsSegment> segments) {
		for (int i = 0; i < segments.size(); i++) {
			createOrUpdateSegment(i, segments.get(i));
		}

		removeButton.setEnabled(segmentViews.size() > MIN_SEGMENTS_COUNT ||
				(segments.get(1).getDestination() != null ||
						segments.get(1).getOrigin() != null ||
						segments.get(1).getDate() != null));
		addButton.setEnabled(segmentViews.size() != MAX_SEGMENTS_COUNT);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (sizeListener != null) sizeListener.onChanged(this);
	}

	private void createOrUpdateSegment(int i, ComplexSearchParamsSegment segment) {
		if (i < this.segments.size() && this.segments.get(i) != null && this.segmentViews.get(i) != null) {
			if (segment.getOrigin() != null) {
				segmentViews.get(i).changeDepartureData(segment.getOrigin().getIata(), segment.getOrigin().getName(), false);
			}
			if (segment.getDestination() != null) {
				segmentViews.get(i).changeArrivalData(segment.getDestination().getIata(), segment.getDestination().getName(), false);
			}
			if (segment.getStringDate() != null) {
				segmentViews.get(i).changeDateData(segment.getDateInMM_ddFormat(), segment.getYear(), false);
			}
		} else {
			addSegmentView(i, segment);
		}
	}

	private void clearSegmentData(Integer id) {
		segmentViews.get(id).clearAllData(false);
		segments.get(id).clearAllData();
		if (listener != null)
			listener.segmentChanged(this, segmentViews.get(id), id, segments.get(id));
	}

	private void addSegmentView(Integer id, ComplexSearchParamsSegment segment) {
		ComplexSegmentView segmentView = new ComplexSegmentView(getContext());
		if (segment.getOrigin() != null) {
			segmentView.changeDepartureData(segment.getOrigin().getIata(), segment.getOrigin().getName(), false);
		}

		if (segment.getDestination() != null) {
			segmentView.changeArrivalData(segment.getDestination().getIata(), segment.getDestination().getName(), false);
		}

		if (segment.getStringDate() != null) {
			segmentView.changeDateData(segment.getDateInMM_ddFormat(), segment.getYear(), false);
		}

		segmentViews.put(id, segmentView);
		segmentView.setOnBlockClickListener(this);
		segmentView.setTag(id);
		segmentsLayout.addView(segmentView, segmentsLayout.getChildCount() - 1);
		segments.add(segment);
	}

	private void removeSegmentView(Integer id, ComplexSearchParamsSegment segment) {
		ComplexSegmentView segmentView = segmentViews.get(id);
		segmentsLayout.removeView(segmentView);
		segmentViews.remove(id);
		segments.remove(segment);
		if (listener != null) listener.segmentRemoved(this, id);
	}

	public ComplexSearchSegmentListener getListener() {
		return listener;
	}

	public void setListener(ComplexSearchSegmentListener listener) {
		this.listener = listener;
	}

	public interface ComplexSearchSegmentListener {
		void segmentBlockClicked(int blockType, int segmentPosition);

		void segmentChanged(ComplexParamsView view, ComplexSegmentView segmentView, int segmentPosition, ComplexSearchParamsSegment segment);

		void segmentRemoved(ComplexParamsView view, int segmentPosition);

		void newSegmentAdded(ComplexParamsView view, ComplexSegmentView segmentView, int segmentPosition, ComplexSearchParamsSegment segment);
	}

	public interface OnSizeChangedListener {
		void onChanged(ComplexParamsView view);
	}

}
