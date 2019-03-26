package doublesoft.android.stu.myview;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RightChooseViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater mInflater;
	private List<ContentValues> recordList;
	public int selectItem = -1;

	public RightChooseViewAdapter(Context context, List<ContentValues> recordList) {
		this.context = context;
		this.recordList = recordList;
		mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setList(List<ContentValues> recordList) {
		this.recordList = recordList;
	}

	@Override
	public int getCount() {
		return recordList.size();
	}

	@Override
	public ContentValues getItem(int position) {
		return recordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 内容行
		if (convertView == null) {
			convertView = mInflater.inflate(doublesoft.android.stu.R.layout.public_rightchooseview_cell, parent, false);
		}

		try {
			ContentValues rowValues = this.getItem(position);

			// ICON
			if (rowValues.containsKey("Icon")) {
				ImageView cellImage = (ImageView) convertView.findViewById(doublesoft.android.stu.R.id.CellImage);
				cellImage.setVisibility(View.VISIBLE);
				cellImage.setBackgroundResource(rowValues.getAsInteger("Icon"));
			}

			// 标题
			TextView cellTitle = (TextView) convertView.findViewById(doublesoft.android.stu.R.id.CellTitle);
			cellTitle.setText(rowValues.getAsString("Name"));

			// 选中
			if (position == selectItem) {
				convertView.setBackgroundResource(doublesoft.android.stu.R.drawable.public_cell_pressbg);

			} else {
				convertView.setBackgroundResource(doublesoft.android.stu.R.drawable.public_cell_bg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}
}
