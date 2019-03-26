package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyAdapter;
import doublesoft.android.stu.myui.MyFragment;

import java.util.ArrayList;
import java.util.List;

public class DemoTableViewMultColAdapter extends MyAdapter {

	public DemoTableViewMultColAdapter(MyFragment fragement) {
		super(fragement);
	}

	// 配置每行对应的布局文件及绑定数据
	@Override
	public View getCellView(int row, View convertView, ViewGroup parent) {
		try {
			// 指定布局器
			convertView = mInflater.inflate(R.layout.demo_tableview_multcol_cell, parent, false);

			// 查找控件赋值 与 单列不同的是对各Cell的处理
			List<TextView> cellTitleList = new ArrayList<TextView>();
			cellTitleList.add((TextView) convertView.findViewById(R.id.CellTitle1));
			cellTitleList.add((TextView) convertView.findViewById(R.id.CellTitle2));
			cellTitleList.add((TextView) convertView.findViewById(R.id.CellTitle3));

			for (int i = 0; i < colCount; i++) {
				// 行数据
				ContentValues rowValues = getItem(row, i);

				if (rowValues != null) {
					cellTitleList.get(i).setVisibility(View.VISIBLE);
					cellTitleList.get(i).setText(rowValues.getAsString("Name"));
				} else {
					// 这里要用 INVISIBLE占着空间，不然排版要乱，GONE也是隐藏但不占用空间
					cellTitleList.get(i).setVisibility(View.INVISIBLE);
				}
			}
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
