package doublesoft.android.stu.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

public class DropHUD extends RelativeLayout {
	private long openTime = 0;
	public boolean allowToShow = true;
	private long timeDiff = 0;
	MyFunction myFunc = new MyFunction();
	MyHandler myHandler = new MyHandler();
	private Context myContext;

	public DropHUD(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		myContext = context;
	}

	public DropHUD(Context context, AttributeSet attrs) {
		super(context, attrs);
		myContext = context;
	}

	public DropHUD(Context context) {
		super(context);
		myContext = context;
	}

	public void showText(String text, int icon, final long duration) {
		ImageView iconImageView = (ImageView) this.findViewById(doublesoft.android.stu.R.id.DropHUDIcon);
		this.setBackgroundColor(getResources().getColor(doublesoft.android.stu.R.color.drophud));
		this.getBackground().setAlpha(125);
		if (iconImageView != null) {
			iconImageView.setImageDrawable(getResources().getDrawable(icon));
		}

		TextView textView = (TextView) this.findViewById(doublesoft.android.stu.R.id.DropHUDTitle);
		if (textView != null) {
			textView.setText(text);
			if (icon == doublesoft.android.stu.R.drawable.public_drop_fail) {
				textView.setTextColor(getResources().getColor(doublesoft.android.stu.R.color.drophudfail));
			}

			if (icon == doublesoft.android.stu.R.drawable.public_drop_success) {
				textView.setTextColor(getResources().getColor(doublesoft.android.stu.R.color.drophudsuccess));
			}
		}

		openTime = System.currentTimeMillis();
		timeDiff = duration;

		if (allowToShow) {
			allowToShow = false;

			AnimationSet set = new AnimationSet(false);
			TranslateAnimation showHUD = new TranslateAnimation(0, 0, 0, myFunc.dip2px(this.getContext(), 30));
			showHUD.setDuration(450);
			set.addAnimation(showHUD);
			set.setFillAfter(true);

			set.setAnimationListener(new AnimationListener() {

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
					myHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							hiddenText();
						}
					}, duration);
				}
			});

			this.startAnimation(set);
		} else {
			// 刷新动画
			AnimationSet set = new AnimationSet(false);
			TranslateAnimation showHUD = new TranslateAnimation(0, 0, 0, myFunc.dip2px(this.getContext(), 30));
			showHUD.setDuration(0);
			set.addAnimation(showHUD);
			set.setFillAfter(true);
			set.setAnimationListener(new AnimationListener() {

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
					myHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							hiddenText();
						}
					}, duration);
				}
			});
			this.startAnimation(set);
		}

	}

	public void hiddenText() {
		if (allowToShow == true) {
			return;
		}

		if (System.currentTimeMillis() - openTime >= timeDiff) {
			if (allowToShow == false) {
				AnimationSet set = new AnimationSet(false);

				TranslateAnimation hiddenHUD = new TranslateAnimation(0, 0, myFunc.dip2px(this.getContext(), 30), 0);
				hiddenHUD.setDuration(450);
				set.addAnimation(hiddenHUD);
				set.setFillBefore(true);
				set.setAnimationListener(new AnimationListener() {

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
						allowToShow = true;
						clearAnimation();
					}
				});

				this.startAnimation(set);
			}
		} else {
			myHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					hiddenText();
				}
			}, 100);
		}
	}

	public void showSuccessText(String text) {
		showText(text, doublesoft.android.stu.R.drawable.public_drop_success, 3000);
	}

	public void showFailText(String text) {
		showText(text, doublesoft.android.stu.R.drawable.public_drop_fail, 3000);
	}

	public void showNetworkFail() {
		showText("网络加载失败，请稍后再试！", doublesoft.android.stu.R.drawable.public_drop_fail, 3000);
	}

	public void showNoNetworkFail() {
		showText("网络没有连接，请稍后再试！", doublesoft.android.stu.R.drawable.public_drop_fail, 3000);
	}

	public void showNoWifiFail() {
		showText("该功能只能在WIFI环境下使用！", doublesoft.android.stu.R.drawable.public_drop_fail, 3000);
	}

	public void showHint(String text) {
		Toast toast = Toast.makeText(myContext.getApplicationContext(), text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
		v.setTextSize(15);
		toast.show();
	}
}
