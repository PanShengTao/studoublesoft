package doublesoft.android.stu.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myui.MyViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-14.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class PageScrollView extends RelativeLayout {
	public LayoutInflater mInflater = null;
	public MyHandler myHandler = null;
	private Context myContext;
	public PageScrollViewAdapter viewAdapter = null;
	public PageScrollViewListener pageScrollViewListener = null;
	public MyViewPager viewPager = null;
	public PageControl pageControl = null;

	// 总页数
	public int totalPage = 0;

	// 当前页
	public int currentPage = 0;

	// 用户标签 用于标识
	public String userTag = "";

	// 缓存所有子视图
	public HashMap<String, View> pageViewDic = null;

	// 记录三个主要的子视图
	public List<View> pageMainViewArr = null;

	// 自动滚动参数
	public int autoScrollSeconds = 0;
	public long preScrollTime = 0;

	public PageScrollView(Context context) {
		super(context);
		setDefaultVars(context);
	}

	public PageScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultVars(context);
	}

	public PageScrollViewListener getPageScrollViewListener() {
		return pageScrollViewListener;
	}

	public void setPageScrollViewListener(PageScrollViewListener pageScrollViewListener) {
		this.pageScrollViewListener = pageScrollViewListener;
	}

	public void setDefaultVars(Context context) {
		myContext = context;
		myHandler = new MyHandler();
		mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pageMainViewArr = new ArrayList<View>();
		pageViewDic = new HashMap<String, View>();
	}

	@Override
	protected void onFinishInflate() {
		viewPager = (MyViewPager) this.findViewById(doublesoft.android.stu.R.id.ViewPager);
		// 滚动速度调节
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			Interpolator sInterpolator = new LinearInterpolator();
			PageScrollViewScroller scroller = new PageScrollViewScroller(myContext, sInterpolator);
			mScroller.set(viewPager, scroller);
		} catch (Exception e) {
		}

		autoScroll();
	}

	// 重新加载数据
	public void reloadData() {
		try {
			if (this.pageScrollViewListener != null) {
				viewPager = (MyViewPager) this.findViewById(doublesoft.android.stu.R.id.ViewPager);

				// 总页数
				totalPage = this.pageScrollViewListener.totalPage();

				// 当前页
				currentPage = this.pageScrollViewListener.defaultPage();

				// 控制器
				pageControl = (PageControl) this.findViewById(doublesoft.android.stu.R.id.PageControl);
				pageControl.setPageCount(totalPage);
				pageControl.setCurrentPage(currentPage);
				if (this.pageScrollViewListener.showPageControll()) {
					pageControl.setVisibility(View.VISIBLE);
				} else {
					pageControl.setVisibility(View.GONE);
				}

				// 自动滚动
				autoScrollSeconds = this.pageScrollViewListener.autoScrollSeconds();
				preScrollTime = System.currentTimeMillis() / 1000;

				// 是否缓存 pageView 开启缓存的情况下不会每次都去请求 viewForPage 默认开启
				Boolean cachePageView = this.pageScrollViewListener.cachePageView();
				if (!cachePageView) {
					pageViewDic.clear();
				}

				// 移除子视图
				pageMainViewArr.clear();

				for (int i = 0; i < Math.min(3, totalPage); i++) {
					int page = 0;
					if (totalPage >= 3) {
						if (i == 0) {
							page = (totalPage + currentPage - 1) % totalPage;
						} else if (i == 1) {
							page = currentPage;
						} else {
							page = (totalPage + currentPage + 1) % totalPage;
						}
					} else {// 兼容1页或2页
						if (totalPage == 2 && currentPage == 1) {
							page = i == 0 ? 1 : 0;
						} else {
							page = i;
						}
					}

					// 读取一个Page视图
					LinearLayout pageMainView = (LinearLayout) mInflater.inflate(doublesoft.android.stu.R.layout.public_linearlayout, this,
							false);
					pageMainView.setTag(page);
					pageMainViewDisplayPageView(pageMainView, page);
					pageMainViewArr.add(pageMainView);
				}

				// 配置
				if (viewAdapter == null) {
					viewAdapter = new PageScrollViewAdapter(myContext, pageMainViewArr);
					viewPager.setAdapter(viewAdapter);
					viewPager.addOnPageChangeListener(new OnPageChangeListener() {

						@Override
						public void onPageSelected(int arg0) {
						}

						@Override
						public void onPageScrolled(int arg0, float arg1, int arg2) {
						}

						@Override
						public void onPageScrollStateChanged(int arg0) {
							if (arg0 == 0) {// 滚动结束
								preScrollTime = System.currentTimeMillis() / 1000;
								int viewTag = Integer
										.parseInt(pageMainViewArr.get(viewPager.getCurrentItem()).getTag().toString());
								if (viewTag != currentPage) {
									currentPage = viewTag;
									pageControl.setCurrentPage(currentPage);
								}

								if (viewPager.getCurrentItem() >= 1 || totalPage >= 3) {// 兼容1或2页
									resetObjPosition();
									if (pageScrollViewListener != null) {
										pageScrollViewListener.changeToPage(currentPage);
									}
								}
							}
						}
					});
				} else {
					viewAdapter.setList(pageMainViewArr);
					viewAdapter.notifyDataSetChanged();
				}

				if (totalPage >= 3) {
					viewPager.setCurrentItem(1, false);
				} else {// 兼容1页或2页
					viewPager.setCurrentItem(0, false);
				}
			}
		} catch (Exception e) {
			System.out.println("PageScrollView reloadData Exception:" + e);
		}
	}

	// 获取指定页视图
	public View getPageView(int pageIndex) {
		if (pageViewDic.containsKey(pageViewSign(pageIndex))) {
			return pageViewDic.get(pageViewSign(pageIndex));
		}

		return null;
	}

	// 获取全部视图
	public List<View> getAllPageView() {
		return getAllPageView(true);
	}

	public List<View> getAllPageView(Boolean currentPageScrollView) {
		List<View> allPageViewArr = new ArrayList<View>();

		try {
			for (Iterator<?> it = pageViewDic.entrySet().iterator(); it.hasNext();) {
				Map.Entry<?, ?> e = (Map.Entry<?, ?>) it.next();
				try {
					String key = e.getKey().toString();
					View view = (View) e.getValue();

					if (currentPageScrollView) {
						if (key.contains("_" + (this.userTag == null ? "" : this.userTag) + "_")) {
							allPageViewArr.add(view);
						}
					} else {
						allPageViewArr.add(view);
					}
				} catch (Exception e2) {

				}
			}
		} catch (Exception e) {
			System.out.println("getAllPageView Exception:" + e.toString());
		}

		return allPageViewArr;
	}

	// 返回PageViewSign
	public String pageViewSign(int page) {
		return "PageView_" + (this.userTag == null ? "" : this.userTag) + "_" + page;
	}

	// 拉取并添加指定页的视图
	public void pageMainViewDisplayPageView(LinearLayout pageMainView, int page) {
		try {
			// 移除子视图
			pageMainView.removeAllViews();
			// 拉page视图
			if (!pageViewDic.containsKey(pageViewSign(page))) {
				pageViewDic.put(pageViewSign(page), this.pageScrollViewListener.viewForPage(page));
			}
			// 添加page视图
			if (pageViewDic.containsKey(pageViewSign(page))) {
				// 从父层移除
				LinearLayout parentLayout = (LinearLayout) pageViewDic.get(pageViewSign(page)).getParent();
				if (parentLayout != null) {
					parentLayout.removeAllViews();
				}

				pageMainView.addView(pageViewDic.get(pageViewSign(page)));
			}
		} catch (Exception e) {
			System.out.println("pageMainViewDisplayPageView Exception:" + e.toString());
		}
	}

	// 选中指定页
	public void selectPage(int pageIndex) {
		try {
			if (pageIndex != currentPage) {
				if (pageIndex >= 0 && pageIndex < totalPage) {
					if (totalPage >= 3) {
						// 向右滚
						View pageMainView = null;
						if (pageIndex > currentPage) {
							pageMainView = pageMainViewArr.get(2);
						} else {// 向左滚
							pageMainView = pageMainViewArr.get(0);
						}

						// 通知更新视图
						int fromPage = Integer.parseInt(pageMainView.getTag().toString());
						if (fromPage != pageIndex) {
							pageMainView.setTag(pageIndex);
							pageMainViewDisplayPageView((LinearLayout) pageMainView, pageIndex);
						}

						// 滚动
						if (pageIndex > currentPage) {
							viewPager.setCurrentItem(2, true);
						} else {
							viewPager.setCurrentItem(0, true);
						}
					} else {// 兼容1页或2页
						viewPager.setCurrentItem(1, true);
						// viewPager.setCurrentItem(pageIndex, true);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("selectPage Exception:" + e.toString());
		}
	}

	// 重新设定位置 支持循环滚动
	public void resetObjPosition() {
		try {
			viewPager.setScrollable(false);

			for (int i = 0; i < pageMainViewArr.size(); i++) {
				View pageMainView = pageMainViewArr.get(i);
				int fromPage = Integer.parseInt(pageMainView.getTag().toString());
				if (totalPage >= 3) {
					if (i == 0) {
						pageMainView.setTag((totalPage + currentPage - 1) % totalPage);
					} else if (i == 1) {
						pageMainView.setTag(currentPage);
					} else {
						pageMainView.setTag((totalPage + currentPage + 1) % totalPage);
					}
				} else {// 兼容1页或2页
					if (totalPage == 2) {
						pageMainView.setTag(Integer.parseInt(pageMainView.getTag().toString()) == 0 ? 1 : 0);
					} else {
						pageMainView.setTag(i);
					}
				}

				int toPage = Integer.parseInt(pageMainView.getTag().toString());
				if (this.pageScrollViewListener != null) {
					if (fromPage != toPage) {
						pageMainViewDisplayPageView((LinearLayout) pageMainView, toPage);
					}
				}
			}

			if (totalPage >= 3) {
				viewPager.setCurrentItem(1, false);
			} else {// 兼容1页或2页
				viewPager.setCurrentItem(0, false);
			}
			viewPager.setScrollable(true);
		} catch (Exception e) {
			System.out.println("resetObjPosition Exception:" + e.toString());
		}
	}

	// 自动滚动
	public void autoScroll() {
		if (autoScrollSeconds > 0 && preScrollTime > 0 && totalPage > 1) {
			long secodesDiff = System.currentTimeMillis() / 1000 - preScrollTime;
			if (secodesDiff >= autoScrollSeconds) {
				preScrollTime = System.currentTimeMillis() / 1000;
				if (secodesDiff == autoScrollSeconds) {
					if (totalPage >= 3) {
						viewPager.setCurrentItem(2, true);
					} else {// 兼容1页或2页
						viewPager.setCurrentItem(1, true);
					}
				}
			}
		}

		myHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				autoScroll();
			}
		}, 1000);
	}

}
