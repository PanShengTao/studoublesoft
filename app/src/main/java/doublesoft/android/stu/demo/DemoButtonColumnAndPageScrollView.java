package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.ButtonColumn;
import doublesoft.android.stu.myview.ButtonColumnListener;
import doublesoft.android.stu.myview.PageScrollView;
import doublesoft.android.stu.myview.PageScrollViewListener;

import java.util.ArrayList;

public class DemoButtonColumnAndPageScrollView extends MyFragment {
	protected ButtonColumn mainButtonColumn = null;
	private PageScrollView mainPageScrollView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_buttoncolumn_and_pagescrollview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {

		}

		// 控件及页面设置
		setNavigationTitle("ButtonColumn与PageScrollView");
		setNavigationBackButton();

		// 绘制分类导航
		mainButtonColumn = (ButtonColumn) findViewById(R.id.ButtonColumnHorizontal);
		mainButtonColumn.itemMoveImage = R.drawable.column_selected_line;
		mainButtonColumn.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("全部");
				add("已创建");
//				add("配送中");
//				add("已接收");
//				add("已完成");
			}
		};
		mainButtonColumn.itemSelectedIndex = 0;
		mainButtonColumn.itemFontColor = R.color.selector_btn_darkgray_blue;
		mainButtonColumn.itemWidth = myFunc.screenWidth(myContext) / mainButtonColumn.itemTitleArr.size();
		mainButtonColumn.itemHeight = 40;
		mainButtonColumn.itemFontSize = 15;
		mainButtonColumn.itemMoveEffect = true;
		mainButtonColumn.reloadData();
		mainButtonColumn.setButtonColumnListener(new ButtonColumnListener() {

			@Override
			public void itemClick(int itemIndex) {
				if (mainPageScrollView.currentPage != mainButtonColumn.itemSelectedIndex) {
					mainPageScrollView.selectPage(mainButtonColumn.itemSelectedIndex);
				}
			}
		});

		// 滑动配置
		mainPageScrollView = (PageScrollView) findViewById(R.id.PageScrollView);
		mainPageScrollView.setPageScrollViewListener(new PageScrollViewListener() {
			@Override
			public View viewForPage(int page) {
				TextView textView = new TextView(myContext);
				textView.setText("显示 “" + mainButtonColumn.itemTitleArr.get(page) + "” 数据");
				textView.setTextColor(myContext.getResources().getColor(R.color.whiteColor));
				textView.setGravity(Gravity.CENTER);
				textView.setLayoutParams(new LayoutParams(-1, -1));
				if (page % 2 == 0) {
					textView.setBackgroundColor(myContext.getResources().getColor(R.color.darkGrayColor));
				} else {
					textView.setBackgroundColor(myContext.getResources().getColor(R.color.lightGrayColor));
				}

				return textView;
			}

			@Override
			public int totalPage() {
				return mainButtonColumn.itemTitleArr.size();
			}

			@Override
			public boolean showPageControll() {
				return false;
			}

			@Override
			public int defaultPage() {
				return mainButtonColumn.itemSelectedIndex;
			}

			@Override
			public void changeToPage(int page) {
				if (mainButtonColumn.itemSelectedIndex != page) {
					mainButtonColumn.selectItem(page);
				}
			}

			@Override
			public boolean cachePageView() {
				return true;
			}

			@Override
			public int autoScrollSeconds() {
				return 0;
			}
		});
		mainPageScrollView.reloadData();
	}

}
