package doublesoft.android.stu.myui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doublesoft.android.stu.inc.MyFunction;

import java.util.ArrayList;
import java.util.List;

public class MyTextView extends TextView {
	public Context mContext;
	public String groupTag = null;
	public String userTag = null;
	public Boolean disabled = false;
	public ColorStateList colors = null;

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public MyTextView(Context context) {
		super(context);
		init(context, null);
	}

	// 初始化
	@SuppressLint("Recycle")
	private void init(Context context, AttributeSet attrs) {
		mContext = context;
		colors = this.getTextColors();
		try {
			if (attrs != null) {
				TypedArray a = mContext.obtainStyledAttributes(attrs,
						doublesoft.android.stu.R.styleable.MyTag);
				TypedArray b = mContext.obtainStyledAttributes(attrs,
						doublesoft.android.stu.R.styleable.MyState);

				// TAG
				groupTag = a.getString(doublesoft.android.stu.R.styleable.MyTag_groupTag);
				userTag = a.getString(doublesoft.android.stu.R.styleable.MyTag_userTag);

				// 是否屏蔽
				Boolean defaultDisabled = b.getBoolean(
						doublesoft.android.stu.R.styleable.MyState_disabled, false);
				if (defaultDisabled) {
					setDisabled(true);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 实现按钮屏蔽
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.disabled) {
			return true;
		}

		return super.onTouchEvent(event);
	}

	@SuppressLint("NewApi")
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
		if (MyFunction.sdkVersion >= 11) {
			if (disabled) {
				this.setAlpha(0.3f);
			} else {
				this.setAlpha(1.0f);
			}
		} else {
			// 背景
			if (this.getBackground() != null) {
				if (disabled) {
					this.getBackground().setAlpha(Math.round(255 * 0.3f));
				} else {
					this.getBackground().setAlpha(255);
				}
			}

			// 文字颜色
			if (colors != null) {
				if (disabled) {
					this.setTextColor(getResources().getColor(
							doublesoft.android.stu.R.color.darkGrayColorDisabled));
				} else {
					this.setTextColor(colors);
				}
			}
		}
	}

	// 按名字查找View
	public static MyTextView find(String userTag, View view) {
		if (view != null && userTag != null) {
			if (view.getClass().equals(MyTextView.class)) {
				MyTextView btn = (MyTextView) view;
				if (btn.userTag != null && btn.userTag.equals(userTag)) {
					return btn;
				}
			}
		}

		ViewGroup viewGroup = null;
		try {
			viewGroup = (ViewGroup) view;
		} catch (Exception e) {
		}

		if (viewGroup != null) {
			if (viewGroup.getChildCount() > 0) {
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					View sonView = viewGroup.getChildAt(i);
					MyTextView btn = find(userTag, sonView);
					if (btn != null) {
						return btn;
					}
				}
			}
		}

		return null;
	}

	public static List<MyTextView> findAll(String groupTag, View view) {
		List<MyTextView> btnArr = new ArrayList<MyTextView>();
		if (view != null && groupTag != null) {
			ViewGroup viewGroup = null;
			try {
				viewGroup = (ViewGroup) view;
			} catch (Exception e) {
			}

			if (viewGroup != null) {
				if (viewGroup.getChildCount() > 0) {
					for (int i = 0; i < viewGroup.getChildCount(); i++) {
						View sonView = viewGroup.getChildAt(i);
						if (sonView.getClass().equals(MyTextView.class)) {
							MyTextView btn = (MyTextView) sonView;
							if (btn.groupTag != null
									&& btn.groupTag.equals(groupTag)) {
								btnArr.add(btn);
							}
						}

						ViewGroup sonViewGroup = null;
						try {
							sonViewGroup = (ViewGroup) sonView;
						} catch (Exception e) {
						}

						if (sonViewGroup != null
								&& sonViewGroup.getChildCount() > 0) {
							btnArr.addAll(findAll(groupTag, sonView));
						}
					}
				}
			}
		}

		return btnArr;
	}

	public static List<MyTextView> findAll(View view) {
		List<MyTextView> btnArr = new ArrayList<MyTextView>();
		if (view != null) {
			ViewGroup viewGroup = null;
			try {
				viewGroup = (ViewGroup) view;
			} catch (Exception e) {
			}

			if (viewGroup != null) {
				if (viewGroup.getChildCount() > 0) {
					for (int i = 0; i < viewGroup.getChildCount(); i++) {
						View sonView = viewGroup.getChildAt(i);
						if (sonView.getClass().equals(MyTextView.class)) {
							MyTextView btn = (MyTextView) sonView;
							btnArr.add(btn);
						}

						ViewGroup sonViewGroup = null;
						try {
							sonViewGroup = (ViewGroup) sonView;
						} catch (Exception e) {
						}

						if (sonViewGroup != null
								&& sonViewGroup.getChildCount() > 0) {
							btnArr.addAll(findAll(sonView));
						}
					}
				}
			}
		}

		return btnArr;
	}
}