package doublesoft.android.stu.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

public class PopView extends LinearLayout {
	public MyFunction myFunc = new MyFunction();
	public MyHandler myHandler = new MyHandler();
	public Context mContext = null;
	public boolean isShowing = false;
	public PopViewListener popViewListener = null;
	public View contentView = null;
	private boolean allowClick = true;

	public PopView(Context context) {
		super(context);
		init(context);
	}

	public PopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PopViewListener getPopViewListener() {
		return popViewListener;
	}

	public void setPopViewListener(PopViewListener popViewListener) {
		this.popViewListener = popViewListener;
	}

	// 初始化
	public void init(Context context) {
		mContext = context;
		this.getBackground().setAlpha(128);
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hiddenPopView();
			}
		});
	}

	// 插入视图
	public void setWindowView(View view) {
		setWindowView(view, -1);
	}

	public void setWindowView(View view, int height) {
		this.contentView = view;
		final ViewGroup windowView = (ViewGroup) this
				.findViewById(doublesoft.android.stu.R.id.PopViewContent);
		windowView.removeAllViews();
		windowView.addView(view);
		view.setLayoutParams(new LayoutParams(-1, height));
	}

	// 打开
	public void showPopView() {
		if (!allowClick) {
			return;
		}
		final View backgroundView = this;
		final View windowView = this.findViewById(doublesoft.android.stu.R.id.PopViewContent);
		windowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		if (!isShowing) {
			allowClick = false;
			this.setVisibility(View.VISIBLE);
			AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
			alphaAnimation.setDuration(300);
			this.startAnimation(alphaAnimation);

			ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1.0f,
					0.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(300);
			windowView.startAnimation(scaleAnimation);
			scaleAnimation.setAnimationListener(new AnimationListener() {

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
					isShowing = true;
					allowClick = true;

					backgroundView.clearAnimation();
					windowView.clearAnimation();
					if (popViewListener != null) {
						popViewListener.popViewDidShow();
					}
				}
			});
		}
	}

	// 关闭
	public void hiddenPopView() {
		if (!allowClick) {
			return;
		}
		boolean allowHidden = true;
		if (popViewListener != null) {
			allowHidden = popViewListener.popViewAllowHidden();
		}

		if (allowHidden) {
			final View backgroundView = this;
			final View windowView = this.findViewById(doublesoft.android.stu.R.id.PopViewContent);
			if (isShowing) {
				allowClick = false;
				this.setVisibility(View.VISIBLE);
				AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
				alphaAnimation.setDuration(300);
				this.startAnimation(alphaAnimation);

				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.1f,
						1.0f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(300);
				windowView.startAnimation(scaleAnimation);
				scaleAnimation.setAnimationListener(new AnimationListener() {

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
						isShowing = false;
						allowClick = true;

						backgroundView.clearAnimation();
						windowView.clearAnimation();
						backgroundView.setVisibility(View.INVISIBLE);
						if (popViewListener != null) {
							popViewListener.popViewDidHidden();
						}
					}
				});
			}
		}
	}
}
