package doublesoft.android.stu.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import doublesoft.android.stu.inc.MyFunction;

//
//SetNumView
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-22.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class SetNumView extends LinearLayout {
	// 控件
	public MyFunction myFunc = new MyFunction();
	public Context myContext;

	public MyNumEditText textField;
	public Button leftBtn;
	public Button rightBtn;
	public int preValue = -999999;
	public MyNumKeyboard myNumKeyboard;// 外部传入

	// 视图高度
	public int viewHeight = 30;

	// 视图宽度 宽度设置后 inputWidth 无效，自动计算，以适应整体宽度
	public int viewWidth = 0;

	// 视图宽度 = buttonWidth+inputWidth+buttonWidth+2

	// 按钮宽度
	public int buttonWidth = 40;

	// 输入框宽度
	public int inputWidth = 40;

	// 默认值
	public int defaultNum = 0;

	// 设置的最小值
	public int minNum = 0;

	// 设置的最大值
	public int maxNum = 99;

	// 用户自定义信息
	public String userTag = "";
	public String groupTag = "";

	public SetNumViewListener setNumViewListener = null;

	public SetNumView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myContext = context;
	}

	public SetNumView(Context context) {
		super(context);
		myContext = context;
	}

	public SetNumViewListener getSetNumViewListener() {
		return setNumViewListener;
	}

	public void setSetNumViewListener(SetNumViewListener setNumViewListener) {
		this.setNumViewListener = setNumViewListener;
	}

	// 变量初始化
	public void setDefaultVars() {
		// 文本输入框
		textField = (MyNumEditText) this.findViewById(doublesoft.android.stu.R.id.SetNumViewEditText);
		if (myNumKeyboard != null) {
			textField.myNumKeyboard = myNumKeyboard;
			textField.myNumKeyboardFHType = 2;
			textField.init();
		}
		textField.setMyNumEditTextListener(new MyNumEditTextListener() {

			@Override
			public void myNumEditTextOKBtnOnClick() {
				textField.clearFocus();
			}

			// 编辑完成
			@Override
			public void myNumEditTextDidEndEditing() {
				justValue();
				setNumChange();
				textField.clearFocus();
			}

			// 键盘编辑
			@Override
			public void myNumEditTextDidChange() {
				setNumChange();
			}

			// 键盘开始编辑
			@Override
			public void myNumEditTextDidBeginEditing() {
				if (setNumViewListener != null) {
					setNumViewListener.didBeginEditing();
				}
			}
		});

		// 左侧按钮
		leftBtn = (Button) findViewById(doublesoft.android.stu.R.id.SetNumViewSubBtn);
		leftBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				subNum();
			}
		});

		// 右侧按钮
		rightBtn = (Button) findViewById(doublesoft.android.stu.R.id.SetNumViewAddBtn);
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addNum();
			}
		});
	}

	// 设置默认样式
	public void setDefaultStyle() {
		// 一般在布局文件中直接设置
	}

	// 重绘
	public void reloadData() {
		setDefaultVars();
		setDefaultStyle();

		// 没有设置间歇

		// 左侧按钮
		LayoutParams leftBtnLayoutParams = (LayoutParams) leftBtn.getLayoutParams();
		leftBtnLayoutParams.width = myFunc.dip2px(myContext, buttonWidth);
		leftBtnLayoutParams.height = myFunc.dip2px(myContext, viewHeight - 1);
		leftBtn.setLayoutParams(leftBtnLayoutParams);

		// 文本输入框
		LayoutParams textFieldLayoutParams = (LayoutParams) textField.getLayoutParams();
		if (viewWidth > 0) {
			textFieldLayoutParams.width = myFunc.dip2px(myContext, viewWidth - (buttonWidth + buttonWidth));
		} else {
			textFieldLayoutParams.width = myFunc.dip2px(myContext, inputWidth);
		}
		textFieldLayoutParams.height = leftBtnLayoutParams.height;
		textField.setLayoutParams(textFieldLayoutParams);

		// 右侧按钮
		LayoutParams rightBtnLayoutParams = (LayoutParams) rightBtn.getLayoutParams();
		rightBtnLayoutParams.width = leftBtnLayoutParams.width;
		rightBtnLayoutParams.height = leftBtnLayoutParams.height;
		rightBtn.setLayoutParams(rightBtnLayoutParams);

		// 设置默认值
		textField.setText(String.valueOf(defaultNum));
		justValue();

		// 自身宽高度
		LayoutParams thisLayoutParams = (LayoutParams) this.getLayoutParams();
		thisLayoutParams.width = leftBtnLayoutParams.width + textFieldLayoutParams.width + rightBtnLayoutParams.width;
		thisLayoutParams.height = myFunc.dip2px(myContext, viewHeight);
	}

	// 屏蔽设置
	public void setLimit(Boolean limit) {
		if (limit) {
			leftBtn.setEnabled(false);
			rightBtn.setEnabled(false);
			textField.clearFocus();
			textField.setEnabled(false);
		} else {
			leftBtn.setEnabled(true);
			rightBtn.setEnabled(true);
			textField.setEnabled(true);
		}
	}

	// 数字减小
	public void subNum() {
		if (textField.getText().toString().length() != 0 && Integer.parseInt(textField.getText().toString()) > minNum
				&& Integer.parseInt(textField.getText().toString()) <= maxNum) {
			textField.setText(String.valueOf(Integer.parseInt(textField.getText().toString()) - 1));
		} else {
			textField.setText(String.valueOf(minNum));
		}
		setNumChange();
	}

	// 数字增加
	public void addNum() {
		if (textField.getText().toString().length() != 0 && Integer.parseInt(textField.getText().toString()) < maxNum
				&& Integer.parseInt(textField.getText().toString()) >= minNum) {
			textField.setText(String.valueOf(Integer.parseInt(textField.getText().toString()) + 1));
		} else {
			textField.setText(String.valueOf(maxNum));
		}
		setNumChange();
	}

	// 调整显示值
	public void justValue() {
		int value = 0;
		try {
			value = Integer.parseInt(textField.getText().toString());
		} catch (Exception e) {
		}

		if (value < minNum) {
			textField.setText(String.valueOf(minNum));
		}

		if (value > maxNum) {
			textField.setText(String.valueOf(maxNum));
		}
	}

	// 数字改变
	public void setNumChange() {
		if (textField.getText().length() != 0) {
			justValue();
			int value = 0;
			try {
				value = Integer.parseInt(textField.getText().toString());
			} catch (Exception e) {
			}
			if (value != preValue) {
				preValue = value;
				if (setNumViewListener != null) {
					setNumViewListener.numDidChange();
				}
			}
		}
	}

	// 设置值
	public void setNum(int num) {
		textField.setText(String.valueOf(num));
		justValue();
	}

	// 取值
	public int currentNum() {
		justValue();
		int value = 0;
		try {
			value = Integer.parseInt(textField.getText().toString());
		} catch (Exception e) {
		}
		return value;
	}
}
