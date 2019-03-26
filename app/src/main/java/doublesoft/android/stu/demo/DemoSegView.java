package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.SegView;

public class DemoSegView extends MyFragment {
	private Button btnAppend = null;
	private Button btnDirection = null;
	private Button btnAlign = null;
	private Button btnRemove = null;
	private Button btnUpdate = null;
	private Button btnReplace = null;

	private SegView segView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_segview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("分段视图SegView");
		setNavigationBackButton();

		// 添加几个按钮 展示下用法，更多用法请参考SegView中的方法说明
		btnAppend = (Button) findViewById(R.id.DemoBtnAppend);
		btnAppend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnAppendClick();
			}
		});

		btnDirection = (Button) findViewById(R.id.DemoBtnDirection);
		btnDirection.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnDirectionClick();
			}
		});

		btnAlign = (Button) findViewById(R.id.DemoBtnAlign);
		btnAlign.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnAlignClick();
			}
		});

		btnRemove = (Button) findViewById(R.id.DemoBtnRemove);
		btnRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnRemoveClick();
			}
		});

		btnUpdate = (Button) findViewById(R.id.DemoBtnUpdate);
		btnUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnUpdateClick();
			}
		});

		btnReplace = (Button) findViewById(R.id.DemoBtnReplace);
		btnReplace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnReplaceClick();
			}
		});

		// 分段视图配置
		segView = (SegView) findViewById(R.id.SegView);
		segView.appendLabel("共计:", 16, R.color.darkGrayColor);
		segView.appendLabel("128", 16, R.color.redColor);
		segView.appendLabel("元", 16, R.color.darkGrayColor);
	}

	// 追加视图按钮点击
	public void btnAppendClick() {
		// 追加文字，常用的
		segView.appendLabel("文字", 16, R.color.blueColor);

		// 追加自定义的视图
		View view = new View(myContext);
		view.setBackgroundColor(getResources().getColor(R.color.redColor));
		view.setLayoutParams(new LinearLayout.LayoutParams(myFunc.dip2px(myContext, 50), myFunc.dip2px(myContext, 50)));
		segView.appendView(view);
	}

	// 改变方向按钮点击
	public void btnDirectionClick() {
		// 0是水平方向默认的，1是垂直方向
		segView.setViewDirection(segView.viewDirection == 0 ? 1 : 0);

		// 设置顶部对齐
		if (segView.viewDirection == 0) {
			segView.setAlignParentTop();
		}

		// 设置左对齐
		if (segView.viewDirection == 1) {
			segView.setAlignParentLeft();
		}
	}

	// 改变对齐按钮点击
	public void btnAlignClick() {
		// 其它对齐方式自己去尝试
		// 改变下 userTag 记录下对齐方式，下次点了按钮才会变，userTag 只是一个用户自定义标识，和对齐本身并没有什么关系

		// 设置底部对齐
		if (segView.viewDirection == 0) {
			if (!segView.userTag.equals("setAlignParentBottom")) {
				segView.userTag = "setAlignParentBottom";
				segView.setAlignParentBottom();
			} else {
				segView.userTag = "setAlignParentTop";
				segView.setAlignParentTop();
			}
		}

		// 设置右对齐
		if (segView.viewDirection == 1) {
			if (!segView.userTag.equals("setAlignParentRight")) {
				segView.userTag = "setAlignParentRight";
				segView.setAlignParentRight();
			} else {
				segView.userTag = "setAlignParentLeft";
				segView.setAlignParentLeft();
			}
		}
	}

	// 移除视图按钮点击
	public void btnRemoveClick() {
		// 每次都移除最后一个视图
		segView.removeView(segView.getViewCount() - 1);
	}

	// 更新文字按钮点击
	public void btnUpdateClick() {
		// 设置金额未888元，尝试设置，万一被删了呢？或者序号发生变化了呢？自己去维护视图，判断序号
		segView.setLabel("888", 1);
	}

	// 替换视图按钮点击
	public void btnReplaceClick() {
		View view = new View(myContext);
		view.setBackgroundColor(getResources().getColor(R.color.redColor));
		view.setLayoutParams(new LinearLayout.LayoutParams(myFunc.dip2px(myContext, 50), myFunc.dip2px(myContext, 50)));
		segView.replaceView(view, segView.getViewCount() - 1);
	}
}
