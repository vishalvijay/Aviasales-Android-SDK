package ru.aviasales.template.ui.view.filters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.aviasales.template.R;

public class SegmentExpandableView extends FrameLayout {
	private static final long ANIMATION_HALF_DURATION = 150l;

	private RelativeLayout rlTitle;
	private LinearLayout contentLayout;

	private TextView tvTitle;
	private ImageView ivExpandIcon;
	private ImageView ivPlaneIcon;
	private boolean isExpanded;

	private boolean someAnimationInProgress = false;

	private OnClickListener onClickListener;

	public SegmentExpandableView(Context context) {
		super(context);
		setupViews();
	}

	public SegmentExpandableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupViews();
	}

	public SegmentExpandableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setupViews();
	}

	private void setupViews() {
		LayoutInflater.from(getContext())
				.inflate(R.layout.filters_expandable_view, this, true);

		rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
		contentLayout = (LinearLayout) findViewById(R.id.ll_content_layout);
		ivExpandIcon = (ImageView) findViewById(R.id.iv_expand_icon);
		ivPlaneIcon = (ImageView) findViewById(R.id.iv_plane);
		tvTitle = (TextView) findViewById(R.id.tv_segment_iatas);

		rlTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isCollapsed()) {
					startExpandAnimation();
				} else {
					startCollapseAnimation();
				}
			}
		});

		collapseWithoutAnimation();
	}

	public void showPlanesIcon(boolean showIcon) {
		if (showIcon) {
			ivPlaneIcon.setVisibility(View.VISIBLE);
		} else {
			ivPlaneIcon.setVisibility(View.GONE);
		}
	}

	public void setTitleText(String text) {
		tvTitle.setText(text);
	}

	public void setContent(View content) {
		contentLayout.removeAllViews();
		contentLayout.addView(content);
	}

	public void addContentView(View content) {
		contentLayout.addView(content);
	}

	public void expandWithoutAnimation() {
		contentLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int height = contentLayout.getMeasuredHeight();

		contentLayout.setAlpha(1);
		contentLayout.getLayoutParams().height = height;
		contentLayout.requestLayout();

		ivExpandIcon.setRotation(180f);
		isExpanded = true;
	}

	public void collapseWithoutAnimation() {
		contentLayout.setAlpha(0);
		contentLayout.getLayoutParams().height = 0;
		contentLayout.requestLayout();

		ivExpandIcon.setRotation(0f);
		isExpanded = false;
	}

	private void startExpandAnimation() {
		if (someAnimationInProgress) return;
		someAnimationInProgress = true;

		contentLayout.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int height = contentLayout.getMeasuredHeight();

		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(contentLayout, "alpha", 0f, 1f);
		ValueAnimator heightAnimator = ValueAnimator.ofInt(0, height);
		ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(ivExpandIcon,
				"rotation", 0f, 180f);

		heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				contentLayout.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
				contentLayout.requestLayout();
			}
		});
		heightAnimator.setDuration(ANIMATION_HALF_DURATION);
		alphaAnimator.setStartDelay(ANIMATION_HALF_DURATION);
		alphaAnimator.setDuration(ANIMATION_HALF_DURATION);
		imageViewObjectAnimator.setDuration(ANIMATION_HALF_DURATION);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(alphaAnimator, heightAnimator, imageViewObjectAnimator);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				someAnimationInProgress = false;
				contentLayout.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
				contentLayout.requestLayout();
				isExpanded = true;
			}
		});
		animatorSet.start();
	}

	private void startCollapseAnimation() {
		if (someAnimationInProgress) return;
		someAnimationInProgress = true;

		ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(contentLayout, "alpha", 1f, 0f);
		ValueAnimator heightAnimator = ValueAnimator.ofInt(contentLayout.getHeight(), 0);
		ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(ivExpandIcon,
				"rotation", 180f, 0);

		heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				contentLayout.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();
				contentLayout.requestLayout();
			}
		});
		heightAnimator.setDuration(ANIMATION_HALF_DURATION);
		heightAnimator.setStartDelay(ANIMATION_HALF_DURATION);
		alphaAnimator.setDuration(ANIMATION_HALF_DURATION);
		imageViewObjectAnimator.setDuration(ANIMATION_HALF_DURATION);
		imageViewObjectAnimator.setStartDelay(ANIMATION_HALF_DURATION);

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(alphaAnimator, heightAnimator, imageViewObjectAnimator);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				someAnimationInProgress = false;
				isExpanded = false;
			}
		});
		animatorSet.start();
	}

	public boolean isCollapsed() {
		return !isExpanded;
	}

	public void setOnButtonClickListener(OnClickListener listener) {
		onClickListener = listener;
	}

	public LinearLayout getContentLayout() {
		return contentLayout;
	}
}
