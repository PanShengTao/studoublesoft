package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.PageScrollView;
import doublesoft.android.stu.myview.PageScrollViewListener;

public class DemoPageScrollView extends MyFragment {
	private Button reloadBtn = null;
	private Button selectBtn = null;
	private Button getViewBtn = null;
	private PageScrollView pageScrollView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_pagescrollview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("分页滑动视图");
		setNavigationBackButton();

		// 按钮绑定
		reloadBtn = (Button) findViewById(R.id.DemoReloadBtn);
		reloadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reloadBtnClick();
			}
		});

		selectBtn = (Button) findViewById(R.id.DemoSelectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectBtnClick();
			}
		});

		getViewBtn = (Button) findViewById(R.id.DemoGetViewBtn);
		getViewBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getViewBtnClick();
			}
		});

		// 分页视图的配置
		// 滑动配置
		pageScrollView = (PageScrollView) findViewById(R.id.PageScrollView);
		pageScrollView.setPageScrollViewListener(new PageScrollViewListener() {

			// 每页的视图，自己画
			@Override
			public View viewForPage(int page) {
				TextView textView = new TextView(myContext);
				textView.setText("第 " + page + " 页");
				textView.setGravity(Gravity.CENTER);
				textView.setLayoutParams(new LayoutParams(-1, -1));
				if (page % 2 == 0) {
					textView.setBackgroundColor(myContext.getResources().getColor(R.color.darkGrayColor));
				} else {
					textView.setBackgroundColor(myContext.getResources().getColor(R.color.lightGrayColor));
				}

				System.out.println("绘制第 " + page + " 页");

				return textView;
			}

			// 滚动视图的总页数
			@Override
			public int totalPage() {
				return 5;
			}

			// 是否显示 小圆点 pageControll
			@Override
			public boolean showPageControll() {
				return true;
			}

			// 默认显示第几页
			@Override
			public int defaultPage() {
				return 0;
			}

			// 滚动到第几页，有个事件
			@Override
			public void changeToPage(int page) {
				System.out.println("滚动到第 " + page + " 页");
			}

			// 是否缓存pageView，默认不开启，每次都重绘
			@Override
			public boolean cachePageView() {
				return true;
			}

			// 自动滚动时间 多少秒 默认0 不自动滚动
			@Override
			public int autoScrollSeconds() {
				return 3;
			}
		});
		pageScrollView.reloadData();
	}

	// 重新加载按钮点击
	public void reloadBtnClick() {
		pageScrollView.reloadData();
	}

	// 选择页码按钮点击
	public void selectBtnClick() {
		pageScrollView.selectPage(3);
	}

	// 获取视图按钮点击
	public void getViewBtnClick() {

		// 获取指定页面的视图
		System.out.println(pageScrollView.getPageView(3));

		// 获取全部视图
		System.out.println(pageScrollView.getAllPageView());
	}
}
