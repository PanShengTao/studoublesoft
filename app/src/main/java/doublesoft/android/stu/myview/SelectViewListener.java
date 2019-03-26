package doublesoft.android.stu.myview;

import doublesoft.android.stu.myui.MyButton;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-21.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public interface SelectViewListener {
	// 弹框即将显示
	public void popViewWillShow();

	// 弹框已经显示
	public void popViewDidShow();

	// 弹框即将隐藏(关闭)
	public void popViewWillHidden();

	// 弹框已经隐藏(关闭)
	public void popViewDidHidden();

	// 选中的选项发生变化
	public void itemSelectedChange();

	// 选项是否允许点击
	public boolean itemAllowClick(MyButton btn);
}
