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
import doublesoft.android.stu.myview.ButtonColumn;
import doublesoft.android.stu.myview.ButtonColumnListener;
import doublesoft.android.stu.myview.PageScrollView;
import doublesoft.android.stu.myview.PageScrollViewListener;
import android.widget.AbsListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DemoButtonColumnAndPageScrollViewAndTableView extends MyFragment {
	protected ButtonColumn mainButtonColumn = null;
	private PageScrollView mainPageScrollView = null;
	private List<DemoButtonColumnAndPageScrollViewAndTableViewAdapter> adapterList = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_buttoncolumn_and_pagescrollview_and_tableview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
			// adapter初始化
			adapterList = new ArrayList<DemoButtonColumnAndPageScrollViewAndTableViewAdapter>();
			adapterList.add(new DemoButtonColumnAndPageScrollViewAndTableViewAdapter(this));
			adapterList.add(new DemoButtonColumnAndPageScrollViewAndTableViewAdapter(this));
			adapterList.add(new DemoButtonColumnAndPageScrollViewAndTableViewAdapter(this));
		}

		// 控件及页面设置
		setNavigationTitle("分类滑动列表综合");
		setNavigationBackButton();

		// 绘制分类导航
		mainButtonColumn = (ButtonColumn) findViewById(R.id.ButtonColumnHorizontal);
		mainButtonColumn.itemMoveImage = R.drawable.column_selected_line;
		mainButtonColumn.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("省");
				add("市");
				add("区");
			}
		};
		mainButtonColumn.itemSelectedIndex = 0;
		mainButtonColumn.itemFontColor = R.color.selector_btn_darkgray_blue;
		mainButtonColumn.itemWidth = myFunc.screenWidth(myContext) / mainButtonColumn.itemTitleArr.size();
		mainButtonColumn.itemHeight = 40;
		mainButtonColumn.itemFontSize = 15;
		mainButtonColumn.itemMoveEffect = true;
		mainButtonColumn.reloadData();
		mainButtonColumn.setButtonColumnListener(new ButtonColumnListener() {

			@Override
			public void itemClick(int itemIndex) {
				if (mainPageScrollView.currentPage != mainButtonColumn.itemSelectedIndex) {
					mainPageScrollView.selectPage(mainButtonColumn.itemSelectedIndex);
				}
			}
		});

		// 滑动配置
		mainPageScrollView = (PageScrollView) findViewById(R.id.PageScrollView);
		mainPageScrollView.setPageScrollViewListener(new PageScrollViewListener() {
			@Override
			public View viewForPage(final int page) {
				MyTableView myTableView = (MyTableView) mInflater.inflate(R.layout.public_mytableview,
						mainPageScrollView, false);

				myTableView.setTag(page);
				myTableView.setMyAdapter(adapterList.get(page));
				myTableView.setMyTableViewListener(new MyTableViewListener() {

					// 加载下一页
					@Override
					public void loadNextPage(int nextpage) {
						loadList(page, nextpage);
					}

					// 开始刷新
					@Override
					public void startRefresh() {
						loadList(page, 1);
					}

					// 行点击
					@Override
					public void cellItemClick(int row) {
						listCellItemClick(page, row);
					}

					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

					}
				});

				myHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 加载第一页
						loadList(page, 1);
					}
				}, 500);

				return myTableView;
			}

			@Override
			public int totalPage() {
				return mainButtonColumn.itemTitleArr.size();
			}

			@Override
			public boolean showPageControll() {
				return false;
			}

			@Override
			public int defaultPage() {
				return mainButtonColumn.itemSelectedIndex;
			}

			@Override
			public void changeToPage(int page) {
				if (mainButtonColumn.itemSelectedIndex != page) {
					mainButtonColumn.selectItem(page);
				}
			}

			@Override
			public boolean cachePageView() {
				return true;
			}

			@Override
			public int autoScrollSeconds() {
				return 0;
			}
		});
		mainPageScrollView.reloadData();
	}

	// 加载网络数据
	public void loadList(int columnIndex, int page) {

		// 开起加载动画
		loadingView.startAnimation();

		// 提交请求
		ContentValues requestDataDic = new ContentValues();
		requestDataDic.put("MainUrl", "http://stu.doublesoft.cn/");
		requestDataDic.put("LessUrl", "http://stu.doublesoft.cn/");
		requestDataDic.put("ScriptPath", "api/demo/TableViewWithNetwork.php");

		// 其余键值对 用户自定义
		requestDataDic.put("Page", page);
		requestDataDic.put("PageSize", 20);

		// Level=1省，=2市，=3区
		requestDataDic.put("Level", columnIndex + 1);

		// 多传个ColumnIndex参数，服务器那边没什么用，但是在loadListCall里面可以根据ColumnIndex定位出控件，用于刷新列表
		requestDataDic.put("ColumnIndex", columnIndex);

		apiRequest.post(requestDataDic, "loadListCall");
	}

	public void loadListCall(final ContentValues requestDataDic, final String returnText) {
		// myHandler封装起来，是为了在主线程中跑。android中操作UI的改变，只能在主线程中
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				// 取出提交时候的ColumnIndex 定位出tableView
				int columnIndex = requestDataDic.getAsInteger("ColumnIndex");
				MyTableView myTableView = (MyTableView) mainPageScrollView.getPageView(columnIndex);
				
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
	public void listCellItemClick(int columnIndex, int row) {
		try {
			MyTableView myTableView = (MyTableView) mainPageScrollView.getPageView(columnIndex);

			System.out.println("第 " + columnIndex + " 页，行点击:" + row + "，数据:" + myTableView.myAdapter.getItem(row));
		} catch (Exception e) {
		}
	}

}
