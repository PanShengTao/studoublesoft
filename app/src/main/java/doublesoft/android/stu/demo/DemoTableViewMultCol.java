package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myui.MyTableView;
import doublesoft.android.stu.myui.MyTableViewListener;
import android.widget.AbsListView;

import org.json.JSONException;
import org.json.JSONObject;

public class DemoTableViewMultCol extends MyFragment {
    // 下来刷新 及 列表对象
    MyTableView myTableView = null;

    // 适配器
    DemoTableViewMultColAdapter adapter = null;

    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_tableview_multcol, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isFirstStart) {
            // 唯一参数 fragment 用于事件传递
            adapter = new DemoTableViewMultColAdapter(this);

            // 指定每行显示3列
            adapter.colCount = 3;
        }

        // 控件及页面设置
        setNavigationTitle("多列的TableView");
        setNavigationBackButton();

        // MyTableView
        myTableView = (MyTableView) findViewById(R.id.MyTableView);
        myTableView.setMyAdapter(adapter);
        myTableView.setMyTableViewListener(new MyTableViewListener() {

            // 加载下一页
            @Override
            public void loadNextPage(int page) {
                loadList(page);
            }

            // 开始刷新
            @Override
            public void startRefresh() {
                loadList(1);
            }

            // 行点击
            @Override
            public void cellItemClick(int row) {
                listCellItemClick(row);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        // 加载第一页
        loadList(1);
    }

    // 加载网络数据
    public void loadList(int page) {
        // 开起加载动画
        loadingView.startAnimation();

        // 提交请求
        ContentValues requestDataDic = new ContentValues();
        requestDataDic.put("MainUrl", "http://stu.doublesoft.cn/");
        requestDataDic.put("LessUrl", "http://stu.doublesoft.cn/");
        requestDataDic.put("ScriptPath", "api/demo/TableViewWithNetwork.php");

        // 其余键值对 用户自定义
        requestDataDic.put("Page", page);
        requestDataDic.put("PageSize", 60);// 分页数量要能被colCount整除，不然咋排列？
        requestDataDic.put("Level", 2);// 查询市

        apiRequest.post(requestDataDic, "loadListCall");
    }

    // 得到请求结果
    public void loadListCall(final ContentValues requestDataDic, final String returnText) {
        // myHandler封装起来，是为了在主线程中跑。android中操作UI的改变，只能在主线程中
        myHandler.post(new Runnable() {

            @Override
            public void run() {
                // 停止刷新动画
                myTableView.finishRefresh();
                loadingView.stopAnimation();

                // 请求结果
                if (returnText.length() != 0) {
                    try {
                        JSONObject resultDic = new JSONObject(returnText);
                        if (resultDic.has("Result")) {
                            if (resultDic.getInt("Result") == 1) {
                                // 把JSONObject转为ContentValues，然后设置分页代码
                                myTableView.myAdapter.setPageArr(
                                        myFunc.jsonObjectToContentValues(resultDic.getJSONObject("PageArr")));

                                // 如果是第一页 调用 setList 重新设置数据列表
                                if (myTableView.myAdapter.getThisPage() == 1) {
                                    myTableView.myAdapter.setList(
                                            myFunc.jsonArrToListContentValues(resultDic.getJSONArray("ListArr")));
                                } else {
                                    // 如果不是第一页 调用append追加到列表
                                    myTableView.myAdapter.appendList(
                                            myFunc.jsonArrToListContentValues(resultDic.getJSONArray("ListArr")));
                                }

                                // 重绘表格
                                myTableView.myAdapter.notifyDataSetChanged();
                            } else if (resultDic.getInt("Result") == -1) {// -1 是指token验证失败，需要登录
                                user.clearUserDic();
                                user.checkLogin(fragmentManager);
                            } else {// 0 直接显示错误提示
                                dropHUD.showFailText(resultDic.getString("Message"));
                            }
                        }
                    } catch (JSONException e) {

                    }
                } else {// 如果请求得到结果长度为0，一般是网络连接错误
                    dropHUD.showNetworkFail();
                }
            }
        });
    }

    // 列表行点击
    public void listCellItemClick(int row) {
        try {
            System.out.println("行点击:" + row);
        } catch (Exception e) {
        }
    }
}
