package doublesoft.android.stu.myview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

import java.util.ArrayList;
import java.util.List;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-20.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class SegView extends LinearLayout {
	public MyFunction myFunc;
	public MyHandler myHandler;
	public Context myContext = null;

	// 保存子视图数组
	public List<View> viewArr = null;

	// 视图排版方向 0水平方向 1垂直方向
	public int viewDirection = 0;

	// 视图间距
	public int viewSpace = 0;

	// 用户标签
	public String userTag = "";

	public SegView(Context context) {
		super(context);
		setDefaultVars(context);
	}

	public SegView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultVars(context);
	}

	// 初始化
	public void setDefaultVars(Context context) {
		myContext = context;
		myFunc = new MyFunction();
		myHandler = new MyHandler();

		viewArr = new ArrayList<View>();
	}

	// 添加文字Label
	public void appendLabel(String text) {
		appendLabel(text, 16.0f);
	}

	public void appendLabel(String text, float fontSize) {
		appendLabel(text, fontSize, doublesoft.android.stu.R.color.darkGrayColor);
	}

	public void appendLabel(String text, float fontSize, int fontColor) {
		appendLabel(text, fontSize, fontColor, false);
	}

	public void appendLabel(String text, float fontSize, int fontColor, boolean isBold) {
		TextView textView = new TextView(myContext);
		textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textView.setText(text);
		textView.setTextSize(fontSize);
		textView.setTextColor((ColorStateList) getResources().getColorStateList(fontColor));
		if (isBold) {
			textView.setTypeface(Typeface.DEFAULT_BOLD);
		} else {
			textView.setTypeface(Typeface.DEFAULT);
		}

		appendView(textView);
	}

	// 添加加粗的Label
	public void appendLabelBold(String text, float fontSize, int fontColor) {
		appendLabel(text, fontSize, fontColor, true);
	}

	// 设置指定索引Label的文字
	public void setLabel(String text, int index) {
		View sonView = getView(index);
		if (sonView != null) {
			if (sonView.getClass().equals(TextView.class)) {
				TextView textView = (TextView) sonView;
				textView.setText(text);
			}
		}
	}

	// 添加一个View
	public void appendView(View view) {
		this.addView(view);
		viewArr.add(view);
		justPosition();
	}

	// 获取指定索引的视图
	public View getView(int index) {
		if (index >= 0 && index <= viewArr.size() - 1) {
			return viewArr.get(index);
		}

		return null;
	}

	// 移除一个View
	public void removeView(int index) {
		if (index >= 0 && index <= viewArr.size() - 1) {
			this.removeViewAt(index);
			viewArr.remove(index);
			justPosition();
		}
	}

	// 插入一个视图
	public void insertView(View view, int index) {
		if (index >= 0 && index <= viewArr.size()) {
			this.addView(view, index);
			viewArr.add(index, view);
			justPosition();
		}
	}

	// 更新替换一个视图
	public void replaceView(View view, int index) {
		if (index >= 0 && index <= viewArr.size() - 1) {
			this.removeViewAt(index);
			this.addView(view, index);
			viewArr.set(index, view);
			justPosition();
		}
	}

	// 调整位置
	public void justPosition() {
		// 对齐方式
		if (viewDirection == 0) {
			this.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			this.setOrientation(LinearLayout.VERTICAL);
		}

		// 间距
		for (int i = 1; i < viewArr.size(); i++) {
			View sonView = viewArr.get(i);

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) sonView.getLayoutParams();
			if (viewDirection == 0) {
				layoutParams.setMargins(myFunc.dip2px(myContext, viewSpace), 0, 0, 0);// 4个参数按顺序分别是左上右下
			} else {
				layoutParams.setMargins(0, myFunc.dip2px(myContext, viewSpace), 0, 0);// 4个参数按顺序分别是左上右下
			}
			sonView.setLayoutParams(layoutParams);
		}
	}

	// 水平居中对齐
	public void setAlignCenterHorizontal() {
		this.setGravity(Gravity.CENTER_HORIZONTAL);
	}

	// 垂直居中对齐
	public void setAlignCenterVertical() {
		this.setGravity(Gravity.CENTER_VERTICAL);
	}

	// 顶部对齐
	public void setAlignParentTop() {
		this.setGravity(Gravity.TOP);
	}

	// 底部对齐
	public void setAlignParentBottom() {
		this.setGravity(Gravity.BOTTOM);
	}

	// 左对齐
	public void setAlignParentLeft() {
		this.setGravity(Gravity.START);
	}

	// 右对齐
	public void setAlignParentRight() {
		this.setGravity(Gravity.END);
	}

	// 设置方向
	public void setViewDirection(int direction) {
		viewDirection = direction;
		justPosition();
	}

	// 设置间距
	public void setViewSpace(int space) {
		viewSpace = space;
		justPosition();
	}

	// 视图总数
	public int getViewCount() {
		return viewArr.size();
	}
}
