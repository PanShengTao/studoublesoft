package doublesoft.android.stu.myui;

import android.widget.AbsListView;

public interface MyTableViewListener {
    // 下拉刷新开始
    public void startRefresh();

    // 加载下一页
    public void loadNextPage(int page);

    // 行点击
    public void cellItemClick(int row);

    // 行滚动
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
}
