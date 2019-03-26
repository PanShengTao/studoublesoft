package doublesoft.android.stu.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myview.PullRefreshView;

public class MyTableView extends PullRefreshView {
    public MyTableViewListener myTableViewListener = null;
    public MyHandler myHandler = new MyHandler();
    public ListView listView = null;
    public MyAdapter myAdapter = null;

    // 用于解决 加载下一页时候 频繁通知的问题
    public Boolean isLoading = false;

    public MyTableView(Context context) {
        super(context);
    }

    public MyTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTableViewListener getMyTableViewListener() {
        return myTableViewListener;
    }

    public void setMyTableViewListener(MyTableViewListener myTableViewListener) {
        this.myTableViewListener = myTableViewListener;
    }

    // 设置适配器，建立适配器与TableView的关联
    public void setMyAdapter(MyAdapter myAdapter) {
        // listView 与 adapter的关联
        listView = (ListView) findViewById(doublesoft.android.stu.R.id.MyTableListView);
        listView.setAdapter(myAdapter);
        this.myAdapter = myAdapter;

        // 事件设定
        // 下拉刷新事件
        this.setRefreshListener(new RefreshListener() {

            @Override
            public void onRefresh(PullRefreshView view) {
                myHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (myTableViewListener != null) {
                            myTableViewListener.startRefresh();
                        }
                    }
                }, 200);
            }
        });

        // 行点击事件
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (myTableViewListener != null) {
                    myTableViewListener.cellItemClick(arg2);
                }
            }
        });

        // 加载下一页事件
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (myTableViewListener != null) {
                    myTableViewListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if (totalItemCount > 1) {
                    try {
                        if (firstVisibleItem + visibleItemCount >= totalItemCount
                                && ((MyAdapter) listView.getAdapter()).hasNextPage()) {
                            if (myTableViewListener != null) {
                                if (!isLoading) {
                                    isLoading = true;
                                    myHandler.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            isLoading = false;
                                        }
                                    }, 5000);
                                    myTableViewListener.loadNextPage(((MyAdapter) listView.getAdapter()).getNextPage());
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    // 结束刷新
    @Override
    public void finishRefresh() {
        isLoading = false;
        super.finishRefresh();
    }
}
