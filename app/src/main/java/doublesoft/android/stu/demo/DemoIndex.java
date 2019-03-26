package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;

import java.util.ArrayList;
import java.util.List;

public class DemoIndex extends MyFragment {
	private ListView listView;
	private List<ContentValues> dataList;
	private DemoIndexAdapter adapter = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_index, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
			dataList = new ArrayList<ContentValues>();
			adapter = new DemoIndexAdapter(this);
		}

		// 控件及页面设置
		setNavigationTitle("基础学习");

		// 列表 原始的写法
		listView = (ListView) findViewById(R.id.ListView);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				listViewItemClick(arg2);
			}
		});

		// 初始化数据
		if (isFirstStart) {
			initDataList();
		}
	}

	// 列表数组初始化
	public void initDataList() {
		try {
			dataList.clear();
			ContentValues value;

			value = new ContentValues();
			value.put("CellKind", "Description");
			value.put("CellTitle", "一、常用控件的学习");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "1、网络请求 APIRequest");
			value.put("CellController", "DemoAPIRequest");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "2、分页滑动视图 PageScrollView");
			value.put("CellController", "DemoPageScrollView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "3、加载网络数据的 TableView");
			value.put("CellController", "DemoTableViewWithNetwork");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "4、多列的 TableView");
			value.put("CellController", "DemoTableViewMultCol");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "5、按钮导航 ButtonColumn");
			value.put("CellController", "DemoButtonColumn");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "6、ButtonColumn与PageScrollView");
			value.put("CellController", "DemoButtonColumnAndPageScrollView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "7、ButtonColumn与PageScrollView与TableView");
			value.put("CellController", "DemoButtonColumnAndPageScrollViewAndTableView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "8、分段导航 SegColumn");
			value.put("CellController", "DemoSegColumn");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "9、分段视图 SegView");
			value.put("CellController", "DemoSegView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "10、下拉选项 SelectView");
			value.put("CellController", "DemoSelectView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "11、设置数字 SetNumView");
			value.put("CellController", "DemoSetNumView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "12、预览滚动 PreScrollView");
			value.put("CellController", "DemoPreScrollView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "13、图片上传 UploadImageView");
			value.put("CellController", "DemoUploadImageView");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "14、TableView折叠");
			value.put("CellController", "DemoTableViewFold");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "15、进度条");
			value.put("CellController", "DemoProgress");
			dataList.add(value);

			value = new ContentValues();
			value.put("CellKind", "Link");
			value.put("CellTitle", "16、地址选择");
			value.put("CellController", "DemoAddressPopwindow");
			dataList.add(value);

            value = new ContentValues();
            value.put("CellKind", "Link");
            value.put("CellTitle", "17、查看视频文件");
            value.put("CellController", "DemoViewFile");
            dataList.add(value);

			adapter.setList(dataList);
			adapter.setPageArrDefault();
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 列表点击
	public void listViewItemClick(int row) {
		try {
			ContentValues rowValues = adapter.getItem(row);
			if (rowValues.getAsString("CellKind").equals("Link")) {
				if (rowValues.getAsString("CellController").equals("DemoAPIRequest")) {
					// 普通打开方式 并传递参数
					// 此处put的变量类型 和 接收时 get的变量类型要完全一致
					Bundle bundle = new Bundle();
					bundle.putInt("TestInt", 1);
					bundle.putString("TestString", "This is teststring");
					startFragment(new DemoAPIRequest(), bundle);
				} else {
					// 用JSON的方式打开二级页面
					openLinkUrl("{\"Controller\":\"" + rowValues.getAsString("CellController") + "\"}");
				}
			}
		} catch (Exception e) {
		}

	}
}
