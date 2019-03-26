package doublesoft.android.stu.myview;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyNumKeyboard extends RelativeLayout {
	public MyNumKeyboard thisBGView = null;
	public RelativeLayout thisKeyboard = null;

	public Object inputObj = null;

	// public View mParentView = null;

	public Context mContext = null;
	public View parentView;

	public int myNumKeyboardFHType = 0;
	public float kbViewHeight = 200;
	public boolean showStatus = false;
	public boolean willHide = false;

	public MyNumKeyboardListener myNumKeyboardListener = null;

	public MyNumKeyboardShowListener myNumKeyboardShowListener = null;

	public MyNumKeyboardListener getMyNumKeyboardListener() {
		return myNumKeyboardListener;
	}

	public void setMyNumKeyboardListener(
			MyNumKeyboardListener myNumKeyboardListener) {
		this.myNumKeyboardListener = myNumKeyboardListener;
	}

	public MyNumKeyboardShowListener getMyNumKeyboardShowListener() {
		return myNumKeyboardShowListener;
	}

	public void setMyNumKeyboardShowListener(
			MyNumKeyboardShowListener myNumKeyboardShowListener) {
		this.myNumKeyboardShowListener = myNumKeyboardShowListener;
	}

	public MyNumKeyboard(Context context) {
		super(context);
		init(context, null);
	}

	public MyNumKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public MyNumKeyboard(Context context, View parentView) {
		super(context);
		this.parentView = parentView;
		init(context, null);
	}

	// 初始化
	public void init(Context context, AttributeSet attrs) {
		try {
			mContext = context;
			if (attrs == null) {// 手动创建时初始化才有值
				if (parentView == null) {
					parentView = ((ViewGroup) findViewById(android.R.id.content))
							.getChildAt(0);// 不一定能得到，未验证
				}

				thisBGView = (MyNumKeyboard) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard);
				thisKeyboard = (RelativeLayout) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view);

				loadKeyboardView();
			}

		} catch (Exception e) {
			System.out.println("init Exception:" + e);
		}

	}

	// 加载键盘布局
	public void loadKeyboardView() {
		try {
			thisBGView.bringToFront();
			thisBGView.setVisibility(View.VISIBLE);
			thisKeyboard.removeAllViews();

			final LayoutInflater mInflater = LayoutInflater.from(mContext);
			LinearLayout kbLayout;

			if (myNumKeyboardFHType == 1) { // 逗号键盘
				kbLayout = (LinearLayout) mInflater.inflate(
						doublesoft.android.stu.R.layout.public_mynumkeyboard_comma, null).findViewById(
						doublesoft.android.stu.R.id.mynumkeyboard_view_typecomma);
			} else if (myNumKeyboardFHType == 2) { // 纯数字键盘
				kbLayout = (LinearLayout) mInflater.inflate(
						doublesoft.android.stu.R.layout.public_mynumkeyboard_none, null).findViewById(
						doublesoft.android.stu.R.id.mynumkeyboard_view_typenone);
			} else if (myNumKeyboardFHType == 3) { // 字符串键盘
				kbLayout = (LinearLayout) mInflater.inflate(
						doublesoft.android.stu.R.layout.public_mynumkeyboard_string, null).findViewById(
						doublesoft.android.stu.R.id.mynumkeyboard_view_typestring);
			} else { // 小数点键盘
				kbLayout = (LinearLayout) mInflater.inflate(
						doublesoft.android.stu.R.layout.public_mynumkeyboard_point, null).findViewById(
						doublesoft.android.stu.R.id.mynumkeyboard_view_typepoint);
			}
			thisKeyboard.addView(kbLayout);

			if (showStatus == true) {
				thisKeyboard.setVisibility(View.VISIBLE);
			} else {
				thisKeyboard.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
			System.out.println("loadKeyboardView Exception:" + e);
		}

	}

	// 初始化键盘
	public void initView(Object theInputObj) {

		try {
			loadKeyboardView();
			inputObj = theInputObj;

			// 按键部分==================
			// 收起键盘
			ImageButton btn_Hide = (ImageButton) parentView
					.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn91);
			btn_Hide.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					hideKeyboard();
				}
			});
			// 确定
			Button btn_OK = (Button) parentView
					.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn93);
			btn_OK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btn_OK_Onclick();
				}
			});
			// 退格
			ImageButton btn_BackSpace = (ImageButton) parentView
					.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn92);
			btn_BackSpace.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btn_BackSpace_Onclick();
				}
			});

			if (myNumKeyboardFHType == 1) { // 逗号键盘
				// 逗号按钮
				Button btn_Comma = (Button) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn95);
				btn_Comma.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_Comma_Onclick();
					}
				});
			} else if (myNumKeyboardFHType == 2) { // 纯数字键盘

			} else if (myNumKeyboardFHType == 3) { // 字符串键盘
				// 逗号按钮
				Button btn_Comma = (Button) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn95);
				btn_Comma.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_Comma_Onclick();
					}
				});
				// 加号按钮
				Button btn_Add = (Button) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn97);
				btn_Add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_Add_Onclick();
					}
				});
				// 换行按钮
				Button btn_LF = (Button) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn96);
				btn_LF.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_LF_Onclick();
					}
				});

			} else { // 小数点键盘
				// 小数点按钮
				Button btn_Point = (Button) parentView
						.findViewById(doublesoft.android.stu.R.id.mynumkeyboard_view_btn94);
				btn_Point.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_Point_Onclick();
					}
				});
			}

			// 0~9数字数组
			int[] numArr = { doublesoft.android.stu.R.id.mynumkeyboard_view_btn0,
					doublesoft.android.stu.R.id.mynumkeyboard_view_btn1, doublesoft.android.stu.R.id.mynumkeyboard_view_btn2,
					doublesoft.android.stu.R.id.mynumkeyboard_view_btn3, doublesoft.android.stu.R.id.mynumkeyboard_view_btn4,
					doublesoft.android.stu.R.id.mynumkeyboard_view_btn5, doublesoft.android.stu.R.id.mynumkeyboard_view_btn6,
					doublesoft.android.stu.R.id.mynumkeyboard_view_btn7, doublesoft.android.stu.R.id.mynumkeyboard_view_btn8,
					doublesoft.android.stu.R.id.mynumkeyboard_view_btn9 };
			for (int i = 0; i < numArr.length; i++) {
				Button btn_Num = (Button) parentView.findViewById(numArr[i]);
				btn_Num.setTag((Object) i);
				btn_Num.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						btn_Num_Onclick(v.getTag());
					}
				});
			}
		} catch (Exception e) {
			System.out.println("initView Exception:" + e);
		}
	}

	// 确定按钮事件
	public void btn_OK_Onclick() {
		try {
			// 监听事件
			if (this.myNumKeyboardListener != null) {
				myNumKeyboardListener.myNumKeyboardOKBtnOnClick(inputObj);
			} else {
				hideKeyboard();
			}
		} catch (Exception e) {
			System.out.println("btn_OK_Onclick Exception:" + e);
		}
	}

	// 退格按钮事件
	public void btn_BackSpace_Onclick() {
		try {
			EditText ed = (EditText) inputObj;

			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			int end = ed.getSelectionEnd();
			if (editable != null && editable.length() > 0) {
				if (end != start) {
					editable.delete(start, end);
				} else if (start > 0) {
					editable.delete(start - 1, start);
				}
			}
		} catch (Exception e) {
			System.out.println("btn_BackSpace_Onclick Exception:" + e);
		}
	}

	// 小数点按钮事件
	public void btn_Point_Onclick() {
		inputText((String) ".");
	}

	// 逗号按钮事件
	public void btn_Comma_Onclick() {
		inputText((String) ",");
	}

	// 加号按钮事件
	public void btn_Add_Onclick() {
		inputText((String) "+");
	}

	// 换行按钮事件
	public void btn_LF_Onclick() {
		inputText((String) "\n");
	}

	// 数字按钮事件
	public void btn_Num_Onclick(Object object) {
		inputText((String) (object + ""));
	}

	// 输入字符
	public void inputText(String textData) {
		try {
			EditText ed = (EditText) inputObj;

			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			int end = ed.getSelectionEnd();

			editable.delete(start, end);
			editable.insert(start, textData + "");
		} catch (Exception e) {
			System.out.println("inputText Exception:" + e);
		}
	}

	// 展开键盘
	public void showKeyboard() {
		try {
			int visibility = thisKeyboard.getVisibility();
			if (visibility == View.GONE || visibility == View.INVISIBLE) {
				keyboardAnimation(0);
			}
			showStatus = true;
		} catch (Exception e) {
			System.out.println("showKeyboard Exception:" + e);
		}
	}

	// 收起键盘
	public void hideKeyboard() {
		try {
			int visibility = thisKeyboard.getVisibility();
			if (visibility == View.VISIBLE) {
				if (showStatus == true) {
					keyboardAnimation(1);
				}
				MyNumEditText editText = (MyNumEditText) inputObj;
				editText.clearFocus();
				editText.endInput();
			}
			showStatus = false;
		} catch (Exception e) {
			System.out.println("hideKeyboard Exception:" + e);
		}
	}

	// 键盘展开、收起动画
	public void keyboardAnimation(final int moveKind) {

		try {
			float fromY = 0;
			float toY = 0;
			float kbHeight = thisKeyboard.getHeight();

			if ((int) moveKind == 1) { // 收起
				fromY = 0;
				toY = kbHeight;
			} else { // 展开
				fromY = kbHeight;
				toY = 0;
			}

			Animation translateAnimation = new TranslateAnimation(0, 0, fromY,
					toY);

			translateAnimation.setAnimationListener(new AnimationListener() {

				public void onAnimationEnd(Animation animation) {

					thisKeyboard.clearAnimation();

					if ((int) moveKind == 1) { // 收起
						thisKeyboard.setVisibility(View.INVISIBLE);
					} else { // 展开
						thisKeyboard.setVisibility(View.VISIBLE);
					}

					setEdListener(moveKind);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
					setWillListener(moveKind);
				}
			});

			AnimationSet animationSet = new AnimationSet(true);

			translateAnimation.setDuration((long) 200);
			translateAnimation.setRepeatCount(0);
			translateAnimation.setFillAfter(true);
			translateAnimation.setFillEnabled(true);
			animationSet.addAnimation(translateAnimation);
			thisKeyboard.startAnimation(animationSet);
		} catch (Exception e) {
			System.out.println("keyboardAnimation Exception:" + e);
		}
	}

	public void setWillListener(final int moveKind) {

		try {
			// 监听事件
			if (this.myNumKeyboardShowListener != null) {
				if ((int) moveKind == 1) { // 收起
					myNumKeyboardShowListener.myNumKeyboardWillHidden(inputObj);
				} else { // 展开
					myNumKeyboardShowListener.myNumKeyboardWillShow(inputObj);
				}
			}
		} catch (Exception e) {
			System.out.println("setWillListener Exception:" + e);
		}
	}

	public void setEdListener(final int moveKind) {
		try {
			// 监听事件
			if (this.myNumKeyboardShowListener != null) {
				if ((int) moveKind == 1) { // 收起
					myNumKeyboardShowListener.myNumKeyboardDidHidden(inputObj);
				} else { // 展开
					myNumKeyboardShowListener.myNumKeyboardDidShow(inputObj);
				}
			}
		} catch (Exception e) {
			System.out.println("setEdListener Exception:" + e);
		}
	}

}
