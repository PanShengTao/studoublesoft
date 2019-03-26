package doublesoft.android.stu.myui;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ImageButton extends Button {
    public ImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageButton(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // if
            // (super.getBackground().getClass()==android.graphics.drawable.BitmapDrawable.class)
            // {
            if (super.getBackground() != null) {
                super.getBackground().setAlpha(120);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        resetBackgroud();
                    }

                }, 2000);
            }

            // }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // if
            // (super.getBackground().getClass()==android.graphics.drawable.BitmapDrawable.class)
            // {
            if (super.getBackground() != null) {
                super.getBackground().setAlpha(255);
            }
            // }
        }
        return super.onTouchEvent(event);
    }

    public void resetBackgroud() {
        super.getBackground().setAlpha(255);
    }
}