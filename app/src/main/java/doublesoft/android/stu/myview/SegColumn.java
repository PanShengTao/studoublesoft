package doublesoft.android.stu.myview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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

public class SegColumn extends LinearLayout {
	public MyFunction myFunc;
	public MyHandler myHandler;
	public Context myContext = null;
	public SegColumnListener segColumnListener = null;

	// 节点按钮数组
	public List<Button> itemBtnArr = null;

	// 节点标题数组
	public List<String> itemTitleArr = null;

	// 节点宽度高度，如果不指定则根据title自适应
	public float itemWidth = 0;
	public float itemHeight = 0;

	// 选中的节点索引
	public int itemSelectedIndex = 0;

	// 节点字体大小
	public float itemFontSize = 16;

	// 节点分割线宽度 单位像素
	public int itemSegLineWidth = 1;

	// 节点标题的Padding
	public int itemPaddingLeftAndRight = 0;
	public int itemPaddingTopAndBottom = 0;

	// 节点左中右两种状态的图片
	public int itemBGImageLeft;
	public int itemBGImageMid;
	public int itemBGImageRight;

	// 节点分割线图片
	public int itemBGImageSegLine;

	// 节点普通和选中时的文字颜色
	public int itemFontColor;

	// 用户标签
	public String userTag = null;

	public SegColumn(Context context) {
		super(context);
		setDefaultVars(context);
	}

	public SegColumn(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultVars(context);
	}

	public SegColumnListener getSegColumnListener() {
		return segColumnListener;
	}

	public void setSegColumnListener(SegColumnListener segColumnListener) {
		this.segColumnListener = segColumnListener;
	}

	// 初始化
	public void setDefaultVars(Context context) {
		myContext = context;
		myFunc = new MyFunction();
		myHandler = new MyHandler();

		itemBtnArr = new ArrayList<Button>();
		itemBGImageLeft = doublesoft.android.stu.R.drawable.public_selector_segcolumn_left_gray_red;
		itemBGImageMid = doublesoft.android.stu.R.drawable.public_selector_segcolumn_mid_gray_red;
		itemBGImageRight = doublesoft.android.stu.R.drawable.public_selector_segcolumn_right_gray_red;
		itemBGImageSegLine = doublesoft.android.stu.R.drawable.public_segcolumn_line;
		itemFontColor = doublesoft.android.stu.R.color.selector_btn_darkgray_white;
	}

	// 读取数据
	public void reloadData() {
		try {
			if (this.itemTitleArr == null || this.itemTitleArr.size() == 0) {
				return;
			}

			// 移除全部子视图
			this.removeAllViews();
			itemBtnArr.clear();

			// 创建按钮
			for (int i = 0; i < this.itemTitleArr.size(); i++) {
				final Button itemBtn = new Button(myContext);

				// 赋值
				itemBtn.setText(itemTitleArr.get(i));
				itemBtn.setTextSize(this.itemFontSize);
				itemBtn.setTextColor((ColorStateList) getResources().getColorStateList(this.itemFontColor));

				// 背景
				if (i == 0) {
					itemBtn.setBackgroundResource(this.itemBGImageLeft);
				} else if (i == itemTitleArr.size() - 1) {
					itemBtn.setBackgroundResource(this.itemBGImageRight);
				} else {
					itemBtn.setBackgroundResource(this.itemBGImageMid);
				}

				// 设置宽度高度
				itemBtn.setLayoutParams(new LinearLayout.LayoutParams(
						this.itemWidth != 0
								? myFunc.dip2px(myContext, this.itemWidth + this.itemPaddingLeftAndRight * 2)
								: android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						this.itemHeight != 0
								? myFunc.dip2px(myContext, this.itemHeight + this.itemPaddingTopAndBottom * 2)
								: android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

				// 设置Padding
				itemBtn.setPadding(myFunc.dip2px(myContext, this.itemPaddingLeftAndRight),
						myFunc.dip2px(myContext, this.itemPaddingTopAndBottom),
						myFunc.dip2px(myContext, this.itemPaddingLeftAndRight),
						myFunc.dip2px(myContext, this.itemPaddingTopAndBottom));

				// 选中
				if (this.itemSelectedIndex == i) {
					itemBtn.setSelected(true);
				}

				itemBtn.setTag(i);

				// 点击事件
				itemBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						itemClick(Integer.parseInt(arg0.getTag().toString()));
					}
				});
				itemBtnArr.add(itemBtn);

				this.addView(itemBtn);

				if (itemTitleArr.size() > 2 && i <= itemTitleArr.size() - 2) {
					View rightSegLine = new View(myContext);
					rightSegLine.setLayoutParams(new LinearLayout.LayoutParams(this.itemSegLineWidth, -1));
					rightSegLine.setBackgroundResource(this.itemBGImageSegLine);
					this.addView(rightSegLine);
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 点击事件
	public void itemClick(final int itemIndex) {
		// 设置选中
		this.selectItem(itemIndex);
		myHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				notifyClick(itemIndex);
			}
		}, 100);
	}

	// 通知
	public void notifyClick(int itemIndex) {
		if (this.segColumnListener != null) {
			this.segColumnListener.itemClick(itemIndex);
		}
	}

	// 选中指定节点
	public void selectItem(final int itemIndex) {
		if (itemIndex >= 0 && itemIndex < this.itemTitleArr.size()) {
			// 状态
			for (int i = 0; i < this.itemBtnArr.size(); i++) {
				final Button itemBtn = itemBtnArr.get(i);
				if (Integer.parseInt(itemBtn.getTag().toString()) == itemIndex) {
					this.itemSelectedIndex = itemIndex;
					itemBtn.setSelected(true);
				} else {
					itemBtn.setSelected(false);
				}
			}
		}
	}

	// 设置默认参数
	public void setDefault(int kind) {
		if (kind == 1) {// 红灰 文章字号选择
			this.itemHeight = 22f;
			this.itemFontSize = 14.0f;
		} else if (kind == 2) {

		}
	}
}
