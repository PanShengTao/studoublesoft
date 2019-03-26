package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;

public class DemoTableViewFold extends MyFragment {
	private DemoTableViewFoldAdapter adapter = null;
	private ExpandableListView mainTableView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_tableview_fold, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
			adapter = new DemoTableViewFoldAdapter(this);
		}

		// 控件及页面设置
		setNavigationTitle("TableView折叠");
		setNavigationBackButton();

		mainTableView = (ExpandableListView) findViewById(R.id.ExpandableListView);
		mainTableView.setAdapter(adapter);

		// 设置分组点击事件
		mainTableView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				boolean groupExpanded = parent.isGroupExpanded(groupPosition);
				if (groupExpanded) {
					parent.collapseGroup(groupPosition);
				} else {
					parent.expandGroup(groupPosition, true);
				}
				
				adapter.setFoldBtn(groupPosition, groupExpanded);
				
				return true;

			}
		});
	}

}
