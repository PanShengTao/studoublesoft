package doublesoft.android.stu.myui;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import doublesoft.android.stu.imageloader.ImageLoader;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.mode.User;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//
//MyAdapter
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-11.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class MyAdapter extends BaseAdapter {
    public MyFragment fragement = null;
    public Context myContext;
    public LayoutInflater mInflater;
    public MyFunction myFunc;
    public User user;
    public ImageLoader imageLoader;

    public View.OnClickListener onItemSubClick;

    public List<ContentValues> listArr = null;
    public ContentValues pageArr = null;
    public int tag = -1;
    public int maxRows = 500;// 最大记录数
    public int colCount = 1;// 每行列数
    public HashMap<String, View> cacheCellViewDic = null;

    // 初始化
    // 该参数指定了是由哪个对象来调用的，用于事件回传等
    public MyAdapter(MyFragment fragement) {
        this.fragement = fragement;
        this.myContext = fragement.getContext();
        this.mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.myFunc = new MyFunction();
        this.user = new User(myContext);
        this.imageLoader = myFunc.getImageLoader(myContext);

        this.listArr = new ArrayList<ContentValues>();
        this.pageArr = new ContentValues();
        cacheCellViewDic = new HashMap<String, View>();
    }

    // 调用fragement中的方法，用于事件传递
    public void callFragementMethod(String methodName, View view) {
        try {
            Method sel = fragement.getClass().getMethod(methodName, View.class);
            sel.invoke(fragement, view);
        } catch (Exception e) {

        }
    }

    // 对比列表MD5值是否一致
    public Boolean compareList(List<ContentValues> fromListArr) {
        return myFunc.md5(myFunc.listToJSONArr(listArr).toString())
                .equals(myFunc.md5(myFunc.listToJSONArr(fromListArr).toString()));
    }

    // 清空数据
    public void clearList() {
        listArr.clear();
    }

    // 设置数据
    public void setList(List<ContentValues> fromListArr) {
        listArr.clear();
        listArr.addAll(fromListArr);
    }

    // 后部追加数据
    // 三个参数 来源数据列表 是否检测重复 用于比较的字段名称
    public void appendList(List<ContentValues> fromListArr) {
        appendList(fromListArr, true);
    }

    public void appendList(List<ContentValues> fromListArr, Boolean repeatCheck) {
        appendList(fromListArr, repeatCheck, "ID");
    }

    public void appendList(List<ContentValues> fromListArr, Boolean repeatCheck, String compareKey) {
        try {
            // 直接追加
            if (!repeatCheck) {
                listArr.addAll(fromListArr);
            } else {
                // 检测追加
                List<Object> compareKeyArr = compareKeyArr(compareKey);
                for (int i = 0; i < fromListArr.size(); i++) {
                    ContentValues fromDic = fromListArr.get(i);
                    if (fromDic.containsKey(compareKey)) {
                        if (!compareKeyArr.contains(fromDic.get(compareKey))) {
                            listArr.add(fromDic);
                        }
                    }
                }

            }
        } catch (Exception e) {

        }
    }

    // 前部插入数据源（未支持中间插入，一般也用不上，即默认都是从0行开始插入）
    // 三个参数 来源数据列表 是否检测重复 用于比较的字段名称
    public void insertList(List<ContentValues> fromListArr) {
        insertList(fromListArr, true);
    }

    public void insertList(List<ContentValues> fromListArr, Boolean repeatCheck) {
        insertList(fromListArr, repeatCheck, "ID");
    }

    public void insertList(List<ContentValues> fromListArr, Boolean repeatCheck, String compareKey) {
        try {
            List<ContentValues> tempListArr = new ArrayList<ContentValues>();

            // 直接插入
            if (!repeatCheck) {
                tempListArr.addAll(fromListArr);
            } else {
                // 检测追加
                List<Object> compareKeyArr = compareKeyArr(compareKey);
                for (int i = 0; i < fromListArr.size(); i++) {
                    ContentValues fromDic = fromListArr.get(i);
                    if (fromDic.containsKey(compareKey)) {
                        if (!compareKeyArr.contains(fromDic.get(compareKey))) {
                            tempListArr.add(fromDic);
                        }
                    }
                }

            }

            tempListArr.addAll(listArr);
            listArr.clear();
            listArr.addAll(tempListArr);
        } catch (Exception e) {

        }
    }

    // 获取compareKey数组
    public List<Object> compareKeyArr(String compareKey) {
        List<Object> compareKeyArr = new ArrayList<Object>();
        try {
            for (int i = 0; i < listArr.size(); i++) {
                ContentValues rowDic = listArr.get(i);
                if (rowDic.containsKey(compareKey)) {
                    compareKeyArr.add(rowDic.get(compareKey));
                }
            }
        } catch (Exception e) {
        }

        return compareKeyArr;
    }

    // 设置默认分页数据
    public void setPageArr(ContentValues fromPageArr) {
        pageArr.clear();
        pageArr.putAll(fromPageArr);
    }

    // 设置默认分页数据，用于静态表格
    public void setPageArrDefault() {
        try {
            pageArr.clear();
            pageArr.put("TotalCount", String.valueOf(listArr.size()));
            pageArr.put("ThisPage", "1");
            pageArr.put("TotalPage", "1");
        } catch (Exception e) {

        }
    }

    // 是否第一次加载
    public Boolean isFirstLoad() {
        return !pageArr.containsKey("ThisPage");
    }

    // 是否还有下一页
    public Boolean hasNextPage() {
        try {
            if (!isFirstLoad()) {
                if (pageArr.getAsInteger("ThisPage") < pageArr.getAsInteger("TotalPage")
                        && this.listArr.size() < this.maxRows) {
                    return true;
                }
            }
        } catch (Exception e) {

        }

        return false;
    }

    // 获得当前页码
    public int getThisPage() {
        try {
            if (!isFirstLoad()) {
                return pageArr.getAsInteger("ThisPage");
            }
        } catch (Exception e) {

        }
        return 1;
    }

    // 获得下一页码
    public int getNextPage() {
        try {
            if (!isFirstLoad()) {
                return Math.min(pageArr.getAsInteger("ThisPage") + 1, pageArr.getAsInteger("TotalPage"));
            }
        } catch (Exception e) {

        }
        return 1;
    }

    // 获得实际行数
    public int getRowCount() {
        if (listArr.size() > 0) {
            if (listArr.size() % colCount != 0) {
                return listArr.size() / colCount + 1;
            } else {
                return listArr.size() / colCount;
            }
        }

        return 0;
    }

    public void remove(ContentValues values) {
        listArr.remove(values);
    }

    // 列表总行数
    @Override
    public int getCount() {
        try {
            if (isFirstLoad()) {
                return 0;
            }

            if (hasNextPage()) {// 没有空白行，但是有下一页
                return getRowCount() + 1;
            } else {
                // 没有下一页
                if (getRowCount() == 0) {
                    return 1;
                } else {
                    return getRowCount();
                }
            }
        } catch (Exception e) {
        }

        return 0;
    }

    // 获取行数据
    // 行号 列号
    @Override
    public ContentValues getItem(int row) {
        try {
            return getItem(row, 0);
        } catch (Exception e) {

        }

        return null;
    }

    // 多列获取行数据
    public ContentValues getItem(int row, int col) {
        try {
            if (row * colCount + col <= listArr.size() - 1) {
                return listArr.get(row * colCount + col);
            }
        } catch (Exception e) {

        }

        return null;
    }

    // 插入行数据
    public void insertItem(ContentValues itemValues, int row) {
        insertItem(itemValues, row, 0);
    }

    public void insertItem(ContentValues itemValues, int row, int col) {
        try {
            if (row * colCount + col >= 0 && row * colCount + col <= listArr.size()) {
                listArr.add(row * colCount + col, itemValues);
            }
        } catch (Exception e) {
        }
    }

    // 更新行数据
    public void updateItem(ContentValues itemValues, int row) {
        updateItem(itemValues, row, 0);
    }

    public void updateItem(ContentValues itemValues, int row, int col) {
        try {
            if (row * colCount + col >= 0 && row * colCount + col <= listArr.size() - 1) {
                listArr.set(row * colCount + col, itemValues);
            }
        } catch (Exception e) {
        }
    }

    // 删除行数据
    public void deleteItem(int row) {
        deleteItem(row, 0);
    }

    public void deleteItem(int row, int col) {
        try {
            if (row * colCount + col >= 0 && row * colCount + col <= listArr.size() - 1) {
                listArr.remove(row * colCount + col);
            }
        } catch (Exception e) {
        }
    }

    // 获取缓存视图
    public View getCacheCellView(int row) {
        return cacheCellViewDic.get("Cell_" + row);
    }

    public void removeCacheCellView(int row) {
        cacheCellViewDic.remove("Cell_" + row);
    }

    // 没用到过这个方法
    @Override
    public long getItemId(int row) {
        return row;
    }

    // 屏蔽点击事件
    @Override
    public boolean isEnabled(int row) {
        if (row < getRowCount() && this.listArr.size() > 0) {
            return allowCellClick(row);
        }

        return false;
    }

    @Override
    public void notifyDataSetChanged() {
        // 清空缓存视图
        cacheCellViewDic.clear();
        super.notifyDataSetChanged();
    }

    // 获取行视图
    @Override
    public View getView(int row, View convertView, ViewGroup parent) {
        try {
            if (getCount() == 1 && listArr.size() == 0) {// 没有内容
                convertView = getNoContentCellView(parent);
            } else {
                if (hasNextPage() && row == getRowCount()) {// 加载更多行
                    // 加载更多行
                    convertView = mInflater.inflate(doublesoft.android.stu.R.layout.public_cell_more, parent, false);
                } else {
                    // 判断是否存在缓存
                    if (cacheCellViewDic.containsKey("Cell_" + row)) {
                        return cacheCellViewDic.get("Cell_" + row);
                    } else {
                        // 取新的缓存视图
                        View cellView = getCacheCellView(row, convertView, parent);
                        if (cellView != null) {
                            cacheCellViewDic.put("Cell_" + row, cellView);
                            return cellView;
                        } else {
                            // 取普通视图
                            return getCellView(row, convertView, parent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("MyAdapter Exception:" + e);
        }

        return convertView;
    }

    // 以下内容在自己的adaper中重写，达到配置的目的

    // 配置普通行
    public View getCellView(int row, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(doublesoft.android.stu.R.layout.public_cell_default, parent, false);
        return convertView;
    }

    // 配置可被缓存下来的普通行
    public View getCacheCellView(int row, View convertView, ViewGroup parent) {
        return null;
    }

    // 配置无内容行
    public View getNoContentCellView(ViewGroup parent) {
        return mInflater.inflate(doublesoft.android.stu.R.layout.public_cell_nocontent, parent, false);
    }

    // 配置行是否允许点击
    public Boolean allowCellClick(int row) {
        return true;
    }
}
