package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyAdapter;
import doublesoft.android.stu.myui.MyFragment;

public class DemoTableViewWithNetworkAdapter extends MyAdapter {

	public DemoTableViewWithNetworkAdapter(MyFragment fragement) {
		super(fragement);
	}

	// 配置每行对应的布局文件及绑定数据
	@Override
	public View getCellView(int row, View convertView, ViewGroup parent) {
		try {
			// 行数据
			final ContentValues rowValues = getItem(row);
			
			// 指定布局器
			convertView = mInflater.inflate(R.layout.demo_buttoncolumn_and_pagescrollview_and_tableview_cell, parent,
					false);

			// 查找控件赋值
			TextView cellTitle = (TextView) convertView.findViewById(R.id.CellTitle);
			cellTitle.setText(rowValues.getAsString("Name"));
		} catch (Exception e) {
			System.out.println("DemoTableViewWithNetworkAdapter getContentCellView:" + e);
		}

		return convertView;
	}

	// 配置行是否允许点击
	@Override
	public Boolean allowCellClick(int row) {
		return true;
	}
}
