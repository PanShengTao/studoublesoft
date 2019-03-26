package doublesoft.android.stu.myui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {
	public Context mContext = null;
	public boolean isFirstDraw = true;
	public MyLinearLayoutListener myLinearLayoutListener = null;

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyLinearLayout(Context context) {
		super(context);
		init(context);
	}

	public MyLinearLayoutListener getMyLinearLayoutListener() {
		return myLinearLayoutListener;
	}

	public void setMyLinearLayoutListener(
			MyLinearLayoutListener myLinearLayoutListener) {
		this.myLinearLayoutListener = myLinearLayoutListener;
	}

	// 初始化
	public void init(Context context) {
		mContext = context;
		isFirstDraw = true;
		setWillNotDraw(false);
	}

	// 绘制
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isFirstDraw) {
			isFirstDraw = false;
			if (myLinearLayoutListener != null) {
				myLinearLayoutListener.onFirstDraw();
			}
		}
	}

}
