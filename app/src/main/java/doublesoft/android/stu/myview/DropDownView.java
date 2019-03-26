package doublesoft.android.stu.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

public class DropDownView extends LinearLayout {
	public boolean isShow = false;
	public int marginTop = 0;
	private DropDownViewListener dropDownViewListener = null;

	public DropDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DropDownView(Context context) {
		super(context);
	}

	public DropDownViewListener getDropDownViewListener() {
		return dropDownViewListener;
	}

	public void setDropDownViewListener(
			DropDownViewListener dropDownViewListener) {
		this.dropDownViewListener = dropDownViewListener;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.setVisibility(View.GONE);
	}

	public void showDropDownView() {
		this.setVisibility(View.VISIBLE);
		AnimationSet set = new AnimationSet(false);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0,
				-this.getMeasuredHeight(), 0);
		translateAnimation.setDuration(400);
		set.addAnimation(translateAnimation);
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
				isShow = true;
				if (dropDownViewListener != null) {
					dropDownViewListener.dropDownViewDidShow();
				}
			}
		});

		this.startAnimation(set);
	}

	public void hiddenDropDownView() {
		AnimationSet set = new AnimationSet(false);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
				-this.getMeasuredHeight());
		translateAnimation.setDuration(400);
		set.addAnimation(translateAnimation);
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
				isShow = false;
				setVisibility(View.GONE);
				clearAnimation();
				if (dropDownViewListener != null) {
					dropDownViewListener.dropDownViewDidHidden();
				}
			}
		});

		this.startAnimation(set);
	}
}
