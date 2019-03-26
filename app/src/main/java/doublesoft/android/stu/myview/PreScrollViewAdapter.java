package doublesoft.android.stu.myview;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-25.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class PreScrollViewAdapter extends PagerAdapter {
	public List<View> views;
	Context mContext;

	public PreScrollViewAdapter(Context context, List<View> views) {
		this.mContext = context;
		setList(views);
	}

	public void setList(List<View> views) {
		this.views = views;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(View collection, int position, Object arg2) {
		((ViewPager) collection).removeView((View) arg2);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		try {
			((ViewPager) collection).addView(views.get(position % views.size()), 0);
		} catch (Exception e) {
		}

		return views.get(position % views.size());
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
