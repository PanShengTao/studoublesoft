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
import doublesoft.android.stu.myview.PreScrollView;
import doublesoft.android.stu.myview.PreScrollViewListener;

import java.util.ArrayList;
import java.util.List;

public class DemoPreScrollView extends MyFragment {
	private PreScrollView mainPreScrollView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_prescrollview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("带预览ScrollView");
		setNavigationBackButton();

		mainPreScrollView = (PreScrollView) findViewById(R.id.PreScrollView);

		// 指定显示视图的宽度
		mainPreScrollView.viewWidth = 300;

		// 画几个视图出来 构造视图数组
		List<View> viewArr = new ArrayList<View>();

		TextView lable = new TextView(myContext);
		lable.setLayoutParams(new LayoutParams(myFunc.dip2px(myContext, 300), 100));
		lable.setBackgroundColor(getResources().getColor(R.color.redColor));
		lable.setGravity(Gravity.CENTER);
		lable.setTextSize(17);
		lable.setTextColor(getResources().getColor(R.color.whiteColor));
		lable.setText("第一页");
		viewArr.add(lable);

		lable = new TextView(myContext);
		lable.setLayoutParams(new LayoutParams(myFunc.dip2px(myContext, 300), 100));
		lable.setBackgroundColor(getResources().getColor(R.color.blueColor));
		lable.setGravity(Gravity.CENTER);
		lable.setTextSize(17);
		lable.setTextColor(getResources().getColor(R.color.whiteColor));
		lable.setText("第二页");
		viewArr.add(lable);

		lable = new TextView(myContext);
		lable.setLayoutParams(new LayoutParams(myFunc.dip2px(myContext, 300), 100));
		lable.setBackgroundColor(getResources().getColor(R.color.blackColor));
		lable.setGravity(Gravity.CENTER);
		lable.setTextSize(17);
		lable.setTextColor(getResources().getColor(R.color.whiteColor));
		lable.setText("第三页");
		viewArr.add(lable);

		lable = new TextView(myContext);
		lable.setLayoutParams(new LayoutParams(myFunc.dip2px(myContext, 300), 100));
		lable.setBackgroundColor(getResources().getColor(R.color.lightGrayColor));
		lable.setGravity(Gravity.CENTER);
		lable.setTextSize(17);
		lable.setTextColor(getResources().getColor(R.color.whiteColor));
		lable.setText("第四页");
		viewArr.add(lable);

		lable = new TextView(myContext);
		lable.setLayoutParams(new LayoutParams(myFunc.dip2px(myContext, 300), 100));
		lable.setBackgroundColor(getResources().getColor(R.color.greenColor));
		lable.setGravity(Gravity.CENTER);
		lable.setTextSize(17);
		lable.setTextColor(getResources().getColor(R.color.whiteColor));
		lable.setText("第五页");
		viewArr.add(lable);

		mainPreScrollView.viewArr = viewArr;

		// 绘制出来
		mainPreScrollView.reloadData();

		// 设置监听器
		mainPreScrollView.setPreScrollViewListener(new PreScrollViewListener() {

			@Override
			public void scrollToPage(int page) {
				System.out.println("滚动到 " + page + " 页");
			}
		});
	}
}
