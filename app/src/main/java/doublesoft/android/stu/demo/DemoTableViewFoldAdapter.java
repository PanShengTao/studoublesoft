package doublesoft.android.stu.demo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;

public class DemoTableViewFoldAdapter extends BaseExpandableListAdapter {

    public MyFragment fragement = null;
    public Context myContext;
    public LayoutInflater mInflater;

    // 保存展开按钮对象
    public SparseArray<Button> foldBtnArr = null;

    // 分组定义
    public String[] groupStrings = {"第 0 组标题", "第 1 组标题", "第 2 组标题", "第 3 组标题"};
    public String[][] childStrings = {{"第 0 组，第 0 行", "第 0 组，第 1 行", "第 0 组，第 2 行", "第 0 组，第 3 行"},
            {"第 1 组，第 0 行", "第 1 组，第 1 行", "第 1 组，第 2 行", "第 1 组，第 3 行"},
            {"第 2 组，第 0 行", "第 2 组，第 1 行", "第 2 组，第 2 行", "第 2 组，第 3 行"},
            {"第 3 组，第 0 行", "第 3 组，第 1 行", "第 3 组，第 2 行", "第 3 组，第 3 行"},};

    // 初始化
    // 该参数指定了是由哪个对象来调用的，用于事件回传等
    public DemoTableViewFoldAdapter(MyFragment fragement) {
        this.fragement = fragement;
        this.myContext = fragement.getContext();
        this.mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        foldBtnArr = new SparseArray<Button>();
    }

    // 获取分组的个数
    @Override
    public int getGroupCount() {
        return groupStrings.length;
    }

    // 获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return childStrings[groupPosition].length;
    }

    // 获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return groupStrings[groupPosition];
    }

    // 获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childStrings[groupPosition][childPosition];
    }

    // 获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }

    // 获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.demo_tableview_fold_group_cell, parent, false);

        TextView groupTitle = (TextView) convertView.findViewById(R.id.GroupTitle);
        groupTitle.setText(groupStrings[groupPosition]);

        // 设置展开按钮
        Button foldBtn = (Button) convertView.findViewById(R.id.GroupFoldBtn);
        foldBtnArr.put(groupPosition, foldBtn);
        setFoldBtn(groupPosition, isExpanded);

        return convertView;
    }

    // 获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.demo_tableview_fold_child_cell, parent, false);
        TextView childTitle = (TextView) convertView.findViewById(R.id.ChildTitle);
        childTitle.setText(childStrings[groupPosition][childPosition]);

        return convertView;
    }

    // 指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // 根据分组的展开闭合状态设置指示器
    public void setFoldBtn(int groupPosition, boolean isExpanded) {
        Button foldBtn = foldBtnArr.get(groupPosition);
        if (foldBtn != null) {
            if (isExpanded) {
                Drawable drawable = myContext.getResources().getDrawable(R.drawable.fold_close);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
                foldBtn.setCompoundDrawables(null, null, drawable, null);
                foldBtn.setText("关闭");
            } else {
                Drawable drawable = myContext.getResources().getDrawable(R.drawable.fold_open);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
                foldBtn.setCompoundDrawables(null, null, drawable, null);
                foldBtn.setText("展开");
            }
        }
    }

}
