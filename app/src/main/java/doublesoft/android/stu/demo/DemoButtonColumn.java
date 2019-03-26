package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.jpush.android.api.JPushInterface;
import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.ButtonColumn;
import doublesoft.android.stu.myview.ButtonColumnListener;

import java.util.ArrayList;

public class DemoButtonColumn extends MyFragment {
	protected ButtonColumn buttonColumnHorizontal = null;
	protected ButtonColumn buttonColumnVertical = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_buttoncolumn, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {

		}

		// 控件及页面设置
		setNavigationTitle("按钮导航ButtonColumn");
		setNavigationBackButton();

		// 创建一个水平ButtonColumn 一大堆参数 看看ButtonColumn里面的说明
		// setDefault 方法是做一些默认通用的设置
		// 水平和垂直ButtonColumn所依赖的布局器是不一样的
        // 大部分参数是选填
		buttonColumnHorizontal = (ButtonColumn) findViewById(R.id.ButtonColumnHorizontal);
		// buttonColumnHorizontal.setDefault(1);
		// 自定义标签 用于判断类型
		buttonColumnHorizontal.userTag = "ButtonColumnHorizontal";
		// 滚动图片 android比ios多这个属性
		buttonColumnHorizontal.itemMoveImage = R.drawable.column_selected_line;
		// 按钮与按钮间的水平距离 水平滚动时设置垂直距离无效
		buttonColumnHorizontal.itemHorizontalSpace = 6;
		// 节点高度 不设置则自适应
		buttonColumnHorizontal.itemHeight = 40.0f;
		// 文字字号
		buttonColumnHorizontal.itemFontSize = 15.0f;//普通字号，默认15
		buttonColumnHorizontal.itemSelectedFontSize = 15.0f;//选中字号，默认15
		//选中后是否加粗，默认否
		buttonColumnHorizontal.itemSelectedFontBold = true;
		//字体对齐方式
		buttonColumnHorizontal.itemGravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		// 选中未选中节点的文字颜色
		buttonColumnHorizontal.itemFontColor = R.color.selector_btn_darkgray_blue;
		// 是否显示滑动选中效果
		buttonColumnHorizontal.itemMoveEffect = true;
		// 选中未选中状态的图片
		buttonColumnHorizontal.itemBGImage = R.drawable.public_trans;
		// 初始选中的节点 代码中可用 buttonColumnHorizontal.selectItem(0) 方法，选中节点;
		buttonColumnHorizontal.itemSelectedIndex = 0;
		// 节点左右间隙Padding
		buttonColumnHorizontal.itemPaddingLeftAndRight = 10;
		// 滚动方向0水平滚动 1垂直滚动
		buttonColumnHorizontal.itemDirection = 0;
		// 保持按钮居中
		buttonColumnHorizontal.itemKeepCenter = false;
		// 节点标题设置
		buttonColumnHorizontal.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("全部");
				add("创建订单");
				add("已交定金");
				add("已发车");
				add("已付尾款");
				add("已验车");
			}
		};
		// 绘制节点 可重复调用
		buttonColumnHorizontal.reloadData();

		// 监听器，点击事件
		buttonColumnHorizontal.setButtonColumnListener(new ButtonColumnListener() {

			@Override
			public void itemClick(int itemIndex) {
				System.out.println("点击了水平方向导航:" + itemIndex);
			}
		});

		// 创建一个垂直的ButtonColumn
		// 水平和垂直ButtonColumn所依赖的布局器是不一样的
		buttonColumnVertical = (ButtonColumn) findViewById(R.id.ButtonColumnVertical);
		// 自定义标签 用于判断类型
		buttonColumnVertical.userTag = "ButtonColumnVertical";
		buttonColumnVertical.setDefault(3);
		buttonColumnVertical.itemSelectedIndex = 4;
		buttonColumnVertical.itemWidth = 100;// 直接写dip
		buttonColumnVertical.itemPaddingTopAndBottom = 10;
		buttonColumnVertical.itemDirection = 1;// 垂直方向
		buttonColumnVertical.itemMoveEffect = true;// 显示移动效果
		buttonColumnVertical.itemKeepCenter = true;// 保持按钮居中
		// 节点标题设置
		buttonColumnVertical.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("全部");
				add("服装");
				add("鞋帽");
				add("羽绒服");
				add("电脑");
				add("手机");
				add("汽车");
				add("家具");
			}
		};
		// 绘制节点 可重复调用
		buttonColumnVertical.reloadData();

		// 监听器，点击事件
		buttonColumnVertical.setButtonColumnListener(new ButtonColumnListener() {

			@Override
			public void itemClick(int itemIndex) {
				System.out.println("点击了垂直方向导航:" + itemIndex);
			}
		});
	}

}
