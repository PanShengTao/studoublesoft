package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyAdapter;
import doublesoft.android.stu.myui.MyFragment;

public class DemoIndexAdapter extends MyAdapter {

	public DemoIndexAdapter(MyFragment fragement) {
		super(fragement);
	}

	// 配置每行对应的布局文件及绑定数据
	@Override
	public View getCellView(int row, View convertView, ViewGroup parent) {
		try {
			// 行数据
			final ContentValues rowValues = getItem(row);

			// 描述行
			if (rowValues.getAsString("CellKind").equals("Description")) {
				convertView = mInflater.inflate(R.layout.demo_index_cell_description, parent, false);
			}
			// 链接行
			else if (rowValues.getAsString("CellKind").equals("Link")) {
				convertView = mInflater.inflate(R.layout.demo_index_cell_link, parent, false);
			}

			TextView cellTitle = (TextView) convertView.findViewById(R.id.CellTitle);
			cellTitle.setText(rowValues.getAsString("CellTitle"));
		} catch (Exception e) {
			System.out.println("DemoIndexAdapter getContentCellView:" + e);
		}

		return convertView;
	}

	// 配置行是否允许点击
	@Override
	public Boolean allowCellClick(int row) {
		final ContentValues rowValues = getItem(row);
		if (rowValues.getAsString("CellKind").equals("Link")) {
			return true;
		}

		return false;
	}
}
