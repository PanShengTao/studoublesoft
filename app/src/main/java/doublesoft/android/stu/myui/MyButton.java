package doublesoft.android.stu.myui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import doublesoft.android.stu.inc.MyFunction;

import java.util.ArrayList;
import java.util.List;

public class MyButton extends Button {
	public Context mContext;
	public String groupTag = null;
	public String userTag = null;
	public Boolean disabled = false;
	public ColorStateList colors = null;

	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public MyButton(Context context) {
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

				// 是否选中
				Boolean defaultSelected = b.getBoolean(
						doublesoft.android.stu.R.styleable.MyState_selected, false);
				if (defaultSelected) {
					setSelected(true);
				}

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

	// 设置禁用 如胆码和拖码的互斥
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

	// 获取分组选中个数
	public static int selectedCount(String groupTag, View view) {
		return selectedArr(groupTag, view).size();
	}

	// 按名字查找View
	public static MyButton find(String userTag, View view) {
		if (view != null && userTag != null) {
			if (view.getClass().equals(MyButton.class)) {
				MyButton btn = (MyButton) view;
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
					MyButton btn = find(userTag, sonView);
					if (btn != null) {
						return btn;
					}
				}
			}
		}

		return null;
	}

	public static List<MyButton> findAll(String groupTag, View view) {
		List<MyButton> btnArr = new ArrayList<MyButton>();
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
						if (sonView.getClass().equals(MyButton.class)) {
							MyButton btn = (MyButton) sonView;
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

	public static List<MyButton> findAll(View view) {
		List<MyButton> btnArr = new ArrayList<MyButton>();
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
						if (sonView.getClass().equals(MyButton.class)) {
							MyButton btn = (MyButton) sonView;
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

	// 查找被选中的控件
	public static List<MyButton> selectedArr(String groupTag, View view) {
		List<MyButton> btnArr = findAll(groupTag, view);
		List<MyButton> selectedBtnArr = new ArrayList<MyButton>();
		for (int i = 0; i < btnArr.size(); i++) {
			MyButton chooseBtn = btnArr.get(i);
			if (chooseBtn.isSelected()) {
				selectedBtnArr.add(chooseBtn);
			}
		}

		return selectedBtnArr;
	}

	// 查找已被选中控件的标题
	public static List<String> selectedTitleArr(String groupTag, View view) {
		List<MyButton> btnArr = findAll(groupTag, view);
		List<String> selectedBtnTitleArr = new ArrayList<String>();
		for (int i = 0; i < btnArr.size(); i++) {
			MyButton chooseBtn = btnArr.get(i);
			if (chooseBtn.isSelected()) {
				selectedBtnTitleArr.add(chooseBtn.getText().toString());
			}
		}

		return selectedBtnTitleArr;
	}

	// 选中指定标题的控件
	public static void setSelected(String groupTag, View view,
                                   List<String> titleArr) {
		List<MyButton> chooseBtnArr = findAll(groupTag, view);
		for (int i = 0; i < chooseBtnArr.size(); i++) {
			MyButton chooseBtn = chooseBtnArr.get(i);
			if (titleArr != null
					&& titleArr.contains(chooseBtn.getText().toString())) {
				chooseBtn.setSelected(true);
			} else {
				chooseBtn.setSelected(false);
			}
		}
	}

	// 清除所有选择控件 不是很通用
	public static void clearAll(View view) {
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
						if (sonView.getClass().equals(MyButton.class)) {
							MyButton chooseBtn = (MyButton) sonView;
							if (chooseBtn.userTag != null
									&& chooseBtn.userTag.indexOf("Choose_") != -1) {
								chooseBtn.setSelected(false);
							}

							if (chooseBtn.userTag != null
									&& chooseBtn.userTag.indexOf("Clear_") != -1) {
								chooseBtn.setVisibility(View.INVISIBLE);
							}
						} else {
							clearAll(sonView);
						}
					}
				}
			}
		}
	}
}
