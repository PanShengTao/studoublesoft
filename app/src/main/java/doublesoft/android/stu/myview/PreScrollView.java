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

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myui.MyViewPager;

import java.lang.reflect.Field;
import java.util.List;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-25.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class PreScrollView extends LinearLayout {
	public MyFunction myFunc = new MyFunction();
	public LayoutInflater mInflater = null;
	public MyHandler myHandler = null;
	private Context myContext;
	public PreScrollViewAdapter viewAdapter = null;
	public PreScrollViewListener preScrollViewListener = null;

	// 内部控件
	public MyViewPager viewPager = null;

	// 视图宽度
	public int viewWidth = 0;

	// 预览区域宽度 自动计算
	public int preViewWidth = 0;

	// 当前页面
	public int currentPage = 0;

	// 视图数组
	public List<View> viewArr = null;

	public PreScrollView(Context context) {
		super(context);
		setDefaultVars(context);
	}

	public PreScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultVars(context);
	}

	public PreScrollViewListener getPageScrollViewListener() {
		return preScrollViewListener;
	}

	public void setPreScrollViewListener(PreScrollViewListener preScrollViewListener) {
		this.preScrollViewListener = preScrollViewListener;
	}

	public void setDefaultVars(Context context) {
		myContext = context;
		myHandler = new MyHandler();
		mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		viewArr = null;
		viewWidth = 0;
	}

	@Override
	protected void onFinishInflate() {
		viewPager = (MyViewPager) this.findViewById(doublesoft.android.stu.R.id.ViewPager);
		// 滚动速度调节
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			Interpolator sInterpolator = new LinearInterpolator();
			PreScrollViewScroller scroller = new PreScrollViewScroller(myContext, sInterpolator);
			mScroller.set(viewPager, scroller);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			// 计算预览宽度
			if (preViewWidth == 0 && this.getMeasuredWidth() != 0) {
				int totalWidth = this.getMeasuredWidth();
				preViewWidth = totalWidth - myFunc.dip2px(myContext, viewWidth);
				viewPager.setPadding(0, 0, preViewWidth, 0);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// 重新加载数据
	public void reloadData() {
		try {
			currentPage = 0;
			preViewWidth = 0;
			viewPager = (MyViewPager) this.findViewById(doublesoft.android.stu.R.id.ViewPager);

			// 视图数组
			if (viewArr == null || viewArr.size() == 0) {
				return;
			}

			// 视图宽度
			if (viewWidth == 0) {
				return;
			}

			// 配置
			if (viewAdapter == null) {
				viewAdapter = new PreScrollViewAdapter(myContext, viewArr);
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
							int currentItem = viewPager.getCurrentItem() % viewArr.size();
							if (currentPage != currentItem) {
								currentPage = currentItem;
								if (preScrollViewListener != null) {
									preScrollViewListener.scrollToPage(currentPage);
								}
							}
						}
					}
				});
			} else {
				try {
					Field mFirstLayout = ViewPager.class.getDeclaredField("mFirstLayout");
					mFirstLayout.setAccessible(true);
					mFirstLayout.set(viewPager, true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				viewAdapter.setList(viewArr);
				viewAdapter.notifyDataSetChanged();
			}

			// 滚动到第0页
			viewPager.setCurrentItem(0, false);
		} catch (Exception e) {
			System.out.println("PreScrollView reloadData:" + e);
		}
	}

}
