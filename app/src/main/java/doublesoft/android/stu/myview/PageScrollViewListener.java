package doublesoft.android.stu.myview;

import android.view.View;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-14.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public interface PageScrollViewListener {
	// 总页数
	public int totalPage();

	// 默认页数
	public int defaultPage();

	// 第page页的视图
	public View viewForPage(int page);

	// 是否缓存pageView，默认不缓存，每次reload都去请求viewForPage方法重绘
	public boolean cachePageView();

	// 是否显示pageControl 小圆点，默认不显示
	public boolean showPageControll();

	// 自动滚动的秒数，大于0自动滚动，默认等于0
	public int autoScrollSeconds();

	// 滚动到第page页的通知事件
	public void changeToPage(int page);
}
