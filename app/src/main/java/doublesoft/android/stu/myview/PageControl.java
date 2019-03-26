package doublesoft.android.stu.myview;

import android.content.Context;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-14.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class PageControl extends LinearLayout {
	private int mIndicatorSize = 6;

	private ArrayList<ImageView> indicators;

	private int mPageCount = 0;
	private int mCurrentPage = 0;

	private Context mContext;
	private OnPageControlClickListener mOnPageControlClickListener = null;

	public PageControl(Context context) {
		super(context);
		mContext = context;
		initPageControl();
	}

	public PageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// will now wait until onFinishInflate to call initPageControl()
	}

	@Override
	protected void onFinishInflate() {
		initPageControl();
	}

	private void initPageControl() {
		indicators = new ArrayList<ImageView>();

		Shape s1 = new OvalShape();
		s1.resize(mIndicatorSize, mIndicatorSize);

		Shape s2 = new OvalShape();
		s2.resize(mIndicatorSize, mIndicatorSize);

		int i[] = new int[2];
		i[0] = android.R.attr.textColorSecondary;
		i[1] = android.R.attr.textColorSecondaryInverse;

		mIndicatorSize = (int) (mIndicatorSize * getResources()
				.getDisplayMetrics().density);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mOnPageControlClickListener != null) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:

						if (PageControl.this.getOrientation() == LinearLayout.HORIZONTAL) {
							if (event.getX() < (PageControl.this.getWidth() / 2)) {
								if (mCurrentPage > 0) {
									mOnPageControlClickListener.goBackwards();
								}
							} else // if on right of view
							{
								if (mCurrentPage < (mPageCount - 1)) {
									mOnPageControlClickListener.goForwards();
								}
							}
						} else {
							if (event.getY() < (PageControl.this.getHeight() / 2)) {
								if (mCurrentPage > 0) {
									mOnPageControlClickListener.goBackwards();
								}
							} else // if on bottom half of view
							{
								if (mCurrentPage < (mPageCount - 1)) {
									mOnPageControlClickListener.goForwards();
								}
							}
						}

						return false;
					}
				}
				return true;
			}
		});
	}

	public void setPageCount(int pageCount) {
		try {
			mPageCount = pageCount;
			indicators.clear();
			PageControl.this.removeAllViews();
			for (int i = 0; i < pageCount; i++) {
				final ImageView imageView = new ImageView(mContext);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						mIndicatorSize, mIndicatorSize);
				params.setMargins(mIndicatorSize / 2, mIndicatorSize / 2,
						mIndicatorSize / 2, mIndicatorSize / 2);
				imageView.setLayoutParams(params);
				imageView.setBackgroundResource(doublesoft.android.stu.R.drawable.public_pagecontrol);

				indicators.add(imageView);
				addView(imageView);
			}
		} catch (Exception e) {
		}

	}

	public int getPageCount() {
		return mPageCount;
	}

	public void setCurrentPage(int currentPage) {
		try {
			if (currentPage < mPageCount) {
				if (mCurrentPage < mPageCount) {
					indicators.get(mCurrentPage).setBackgroundResource(
							doublesoft.android.stu.R.drawable.public_pagecontrol);
				}
				indicators.get(currentPage).setBackgroundResource(
						doublesoft.android.stu.R.drawable.public_pagecontrol_on);
				mCurrentPage = currentPage;
			}
		} catch (Exception e) {
		}
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setIndicatorSize(int indicatorSize) {
		mIndicatorSize = indicatorSize;
		for (int i = 0; i < mPageCount; i++) {
			indicators.get(i).setLayoutParams(
					new LayoutParams(mIndicatorSize, mIndicatorSize));
		}
	}

	public int getIndicatorSize() {
		return mIndicatorSize;
	}

	public interface OnPageControlClickListener {
		/**
		 * Called when the PageControl should go forwards
		 * 
		 */
		public abstract void goForwards();

		/**
		 * Called when the PageControl should go backwards
		 * 
		 */
		public abstract void goBackwards();
	}

	public void setOnPageControlClickListener(
			OnPageControlClickListener onPageControlClickListener) {
		mOnPageControlClickListener = onPageControlClickListener;
	}

	public OnPageControlClickListener getOnPageControlClickListener() {
		return mOnPageControlClickListener;
	}

}