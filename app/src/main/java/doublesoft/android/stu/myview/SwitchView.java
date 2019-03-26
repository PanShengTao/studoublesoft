package doublesoft.android.stu.myview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

import java.util.ArrayList;
import java.util.List;

public class SwitchView extends RelativeLayout {
	public MyFunction myFunc;
	public MyHandler myHandler;
	public Context mContext = null;
	public SwitchViewListener switchViewListener = null;
	public List<String> list = null;
	public List<TextView> viewsList = null;
	private int currentLeft = 0;
	public int selectedIndex = -1;

	public SwitchView(Context context) {
		super(context);
		init(context);
	}

	public SwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SwitchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SwitchViewListener getSwitchViewListener() {
		return switchViewListener;
	}

	public void setSwitchViewListener(SwitchViewListener switchViewListener) {
		this.switchViewListener = switchViewListener;
	}

	// 初始化
	public void init(Context context) {
		mContext = context;
		myFunc = new MyFunction();
		myHandler = new MyHandler();
		viewsList = new ArrayList<TextView>();
	}

	// 读取数据
	public void reloadData() {
		try {
			if (this.switchViewListener != null) {
				LinearLayout linearLayout = (LinearLayout) this
						.findViewById(doublesoft.android.stu.R.id.SwitchViewLinearLayout);

				// 背景颜色
				this.setBackgroundColor(this.switchViewListener
						.backgroundColorForSwitchView());

				// 绘制控件
				list = this.switchViewListener.titleArrForSwitchView();
				linearLayout.removeAllViews();
				viewsList.clear();
				for (int i = 0; i < list.size(); i++) {
					TextView titleView = new TextView(mContext);
					titleView.setLayoutParams(new LinearLayout.LayoutParams(0,
							-1, 1));
					titleView.setGravity(Gravity.CENTER_HORIZONTAL
							| Gravity.CENTER_VERTICAL);
					titleView.setText(list.get(i));
					titleView.setTextColor(this.switchViewListener
							.defaultItemColor());
					titleView.setTextSize(15);

					titleView.setTag(i);
					linearLayout.addView(titleView);

					titleView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							selectItem(
									Integer.parseInt(arg0.getTag().toString()),
									true);
						}
					});
					viewsList.add(titleView);
				}

				// 选中默认控件
				selectItem(this.switchViewListener.defaultSelectItemIndex(),
						false);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 选中节点
	public void selectItem(final int index, boolean animated) {
		try {
			if (viewsList.size() > index) {
				final ImageView moveImageView = (ImageView) this
						.findViewById(doublesoft.android.stu.R.id.SwitchViewImageView);
				moveImageView.setBackgroundColor(this.switchViewListener
						.backgroundColorForSelectedItem());

				// 状态
				for (int i = 0; i < viewsList.size(); i++) {
					TextView textView = viewsList.get(i);
					if (i == index) {
						textView.setTextColor(Color.WHITE);
					} else {
						textView.setTextColor(this.switchViewListener
								.defaultItemColor());
						textView.setBackgroundColor(Color.TRANSPARENT);
					}
				}

				// 移动背景
				final TextView selectedTextView = viewsList.get(index);
				if (animated) {
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) moveImageView
							.getLayoutParams();
					layoutParams.width = selectedTextView.getMeasuredWidth();
					moveImageView.setLayoutParams(layoutParams);

					AnimationSet animationSet = new AnimationSet(true);

					TranslateAnimation translateAnimation = new TranslateAnimation(
							currentLeft, selectedTextView.getLeft(), 0f, 0f);

					animationSet.addAnimation(translateAnimation);
					animationSet.setFillBefore(true);
					animationSet.setFillAfter(true);
					animationSet.setDuration(100);
					animationSet.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							selectedFinish(index);
						}
					});
					moveImageView.startAnimation(animationSet);

				} else {

					myHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							moveImageView.layout(
									selectedTextView.getLeft(),
									moveImageView.getTop(),
									selectedTextView.getLeft()
											+ selectedTextView
													.getMeasuredWidth(),
									moveImageView.getBottom());
						}
					}, 1000);
					selectedFinish(index);
					selectItem(index, true);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	// 选中完成
	public void selectedFinish(int index) {
		if (index != selectedIndex) {
			selectedIndex = index;
			if (this.switchViewListener != null) {
				this.switchViewListener.didSelectItem(selectedIndex);
			}
		}

		final TextView selectedTextView = viewsList.get(selectedIndex);
		selectedTextView.setBackgroundColor(this.switchViewListener
				.backgroundColorForSelectedItem());
		currentLeft = selectedTextView.getLeft();
	}
}
