package doublesoft.android.stu.myui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class MaskButton extends Button {
	public MaskButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.getBackground().setAlpha(0);
	}

	public MaskButton(Context context) {
		super(context);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//if (super.getBackground().getClass()==android.graphics.drawable.BitmapDrawable.class) {
			if (super.getBackground()!=null) {
				super.getBackground().setAlpha(120);
			}
				
			//}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//if (super.getBackground().getClass()==android.graphics.drawable.BitmapDrawable.class) {
			if (super.getBackground()!=null) {
				super.getBackground().setAlpha(0);
			}
			//}
		}
		return super.onTouchEvent(event);
	}
}