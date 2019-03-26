package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.SegColumn;
import doublesoft.android.stu.myview.SegColumnListener;

import java.util.ArrayList;

public class DemoSegColumn extends MyFragment {
	private SegColumn mainSegColumn = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_segcolumn, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("分段导航SegColumn");
		setNavigationBackButton();

		// 分段视图配置
		mainSegColumn = (SegColumn) findViewById(R.id.SegColumn);

		// 按钮标题数组
		mainSegColumn.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("已创建");
				add("待处理");
				add("已处理");
				add("已完成");
			}
		};

		// 按钮高度 强制这么高 也可以不设置 配合 itemPaddingTopAndBottom 设置Padding
		mainSegColumn.itemHeight = 36;

		// 不设置宽度 让他自适应，但是左右各留10的间隙
		mainSegColumn.itemPaddingLeftAndRight = 10;

		// 分割线宽度
		mainSegColumn.itemSegLineWidth = 1;// 像素

		// 文字大小
		mainSegColumn.itemFontSize = 16;

		// 文字颜色
		mainSegColumn.itemFontColor = R.color.selector_btn_darkgray_white;

		// 左中右图片
		mainSegColumn.itemBGImageLeft = R.drawable.public_selector_segcolumn_left_gray_blue;
		mainSegColumn.itemBGImageMid = R.drawable.public_selector_segcolumn_mid_gray_blue;
		mainSegColumn.itemBGImageRight = R.drawable.public_selector_segcolumn_right_gray_blue;

		// 初始选中的节点 代码中可用 [mainSegColumn selectItem:0] 方法，选中节点;
		mainSegColumn.itemSelectedIndex = 0;

		// 绘制视图
		mainSegColumn.reloadData();

		// 绑定监听器
		mainSegColumn.setSegColumnListener(new SegColumnListener() {

			@Override
			public void itemClick(int itemIndex) {
				System.out.println("点击了按钮索引：" + itemIndex);
			}
		});

		// SegColumn 与 PageScrollView TableView结合使用与ButtonColumn差不多
		// 不再重复写了
	}

}
