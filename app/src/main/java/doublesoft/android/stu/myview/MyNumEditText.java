package doublesoft.android.stu.myview;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import doublesoft.android.stu.inc.MyHandler;

import java.lang.reflect.Method;

public class MyNumEditText extends EditText {
	private Activity mActivity = null;
	public MyNumKeyboard myNumKeyboard = null;
	public MyNumEditText myNumEditText = null;
	private MyHandler myHandler = new MyHandler();

	public int myNumKeyboardFHType = 0; // 0小数点 1逗号 2整数 3字符串
	public int maxInputLength = 0;

	public int editStatus = 0; // 1:开始编辑 0:结束编辑

	public MyNumEditTextListener myNumEditTextListener = null;

	public MyNumEditTextListener getMyNumEditTextListener() {
		return myNumEditTextListener;
	}

	public void setMyNumEditTextListener(
			MyNumEditTextListener myNumEditTextListener) {
		this.myNumEditTextListener = myNumEditTextListener;
	}

	public MyNumEditText(Context context) {
		super(context);
	}

	public MyNumEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 初始化
	public void init() {
		try {
			myNumEditText = this;
			if (myNumKeyboard != null) {
				mActivity = (Activity) myNumKeyboard.mContext;

				// 隐藏系统键盘，保留光标
				hideSoftInputMethod(myNumEditText, mActivity);

				// 输入框焦点监听
				myNumEditText
						.setOnFocusChangeListener(new OnFocusChangeListener() {
							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (hasFocus) {
									myNumKeyboard.willHide = false;
									if (editStatus == 0) {
										beginInput();
									}
								} else {
									myHandler.postDelayed(new Runnable() {

										@Override
										public void run() {
											if (!myNumEditText.isFocused()) {
												myNumKeyboard.willHide = true;
												myHandler.postDelayed(
														new Runnable() {
															@Override
															public void run() {
																checkHideKeyboard();
															}
														}, 200);

												if (editStatus == 1) {
													endInput();
												}
											}
										}
									}, 100);

								}
							}
						});

				// 输入框点击监听
				myNumEditText.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {

						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:

							// if (editStatus == 0) {
							beginInput();
							// }
							// beginInput();

							break;

						case MotionEvent.ACTION_MOVE:

							break;

						default:
							break;
						}

						return false;
					}
				});

				myNumEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
						if (start + before + count == 0) {
							return;
						}

						// 监听事件
						if (myNumEditText.myNumEditTextListener != null) {
							myNumEditText.myNumEditTextListener
									.myNumEditTextDidChange();
						} else {

						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub

					}
				});

			}

		} catch (Exception e) {
			System.out.println("init Exception:" + e);
		}
	}

	// 隐藏系统键盘
	public void hideSoftInputMethod(EditText ed, Activity act) {
		try {

			act.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			int currentVersion = android.os.Build.VERSION.SDK_INT;
			String methodName = null;
			if (currentVersion >= 16) {
				// 4.2
				methodName = "setShowSoftInputOnFocus";
			} else if (currentVersion >= 14) {
				// 4.0
				methodName = "setSoftInputShownOnFocus";
			}

			if (methodName == null) {
				int inputTypeNum = ed.getInputType();
				ed.setInputType(InputType.TYPE_NULL);
				if (inputTypeNum == 131073 || inputTypeNum == 131072) {
					ed.setSingleLine(false);
				}

			} else {
				Class<EditText> cls = EditText.class;
				Method setShowSoftInputOnFocus;
				try {
					setShowSoftInputOnFocus = cls.getMethod(methodName,
							boolean.class);
					setShowSoftInputOnFocus.setAccessible(true);
					setShowSoftInputOnFocus.invoke(ed, false);
				} catch (Exception e) {
					ed.setInputType(InputType.TYPE_NULL);
					System.out.println("hideSoftInputMethod Exception B:" + e);
				}
			}
		} catch (Exception e) {
			System.out.println("hideSoftInputMethod Exception A:" + e);
		}
	}

	// 关闭系统键盘
	public static void closeKeyboard(Activity activity) {
		try {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			// 得到InputMethodManager的实例
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			System.out.println("closeKeyboard Exception:" + e);
		}
	}

	// 长度限制
	public void setInputLimit(int length) {
		this.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
	}

	// 清除
	public void clearInput() {
		this.setText("");
		this.clearFocus();
	}

	// 开始编辑
	public void beginInput() {

		try {
			closeKeyboard(mActivity);

			myNumKeyboard.myNumKeyboardFHType = myNumKeyboardFHType;
			myNumKeyboard.initView(myNumEditText);
			if (myNumKeyboard.showStatus == false) {
				myNumKeyboard.showKeyboard();
			}

			myNumKeyboard.setMyNumKeyboardListener(new MyNumKeyboardListener() {

				@Override
				public void myNumKeyboardOKBtnOnClick(Object edit) {
					// TODO Auto-generated method stub
					if (edit == myNumEditText) {
						// 监听事件
						if (myNumEditText.myNumEditTextListener != null) {
							myNumEditText.myNumEditTextListener
									.myNumEditTextOKBtnOnClick();
						} else {
							myNumKeyboard.hideKeyboard();
						}
					}
				}
			});

			// 监听事件
			if (myNumEditText.myNumEditTextListener != null && editStatus == 0) {
				myNumEditText.myNumEditTextListener
						.myNumEditTextDidBeginEditing();
				editStatus = 1;
			} else {

			}
		} catch (Exception e) {
			System.out.println("beginInput Exception:" + e);
		}
	}

	// 结束编辑
	public void endInput() {
		try {
			// 监听事件
			if (myNumEditText.myNumEditTextListener != null && editStatus == 1) {
				myNumEditText.myNumEditTextListener
						.myNumEditTextDidEndEditing();
				editStatus = 0;
			} else {

			}
		} catch (Exception e) {
			System.out.println("endInput Exception:" + e);
		}

	}

	// 延时判断收起键盘
	public void checkHideKeyboard() {
		try {
			if (myNumKeyboard.willHide == true) {
				myNumKeyboard.hideKeyboard();
			}
		} catch (Exception e) {
			System.out.println("checkHideKeyboard Exception:" + e);
		}
	}

}
