package doublesoft.android.stu.myview;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-14.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class PageScrollViewScroller extends Scroller {
	private int mDuration = 300; // 图片转动时间为500ms

	public PageScrollViewScroller(Context context) {
		super(context);
	}

	public PageScrollViewScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}
}
