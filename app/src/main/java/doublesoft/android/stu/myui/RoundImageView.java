package doublesoft.android.stu.myui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

	private Paint paint;
	private int roundWidth = 5;
	private int roundHeight = 5;
	private boolean drawLiftUp = true;
	private boolean drawRightUp = true;
	private boolean drawLiftDown = true;
	private boolean drawRightDown = true;
	private Paint paint2;

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public RoundImageView(Context context) {
		super(context);
		init(context, null);
	}

	@SuppressLint("Recycle")
	private void init(Context context, AttributeSet attrs) {

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					doublesoft.android.stu.R.styleable.RoundImageView);
			roundWidth = a.getDimensionPixelSize(
					doublesoft.android.stu.R.styleable.RoundImageView_roundWidth, roundWidth);
			roundHeight = a.getDimensionPixelSize(
					doublesoft.android.stu.R.styleable.RoundImageView_roundHeight, roundHeight);
			drawLiftUp = a.getBoolean(doublesoft.android.stu.R.styleable.RoundImageView_drawLiftUp,
					true);
			drawRightUp = a.getBoolean(doublesoft.android.stu.R.styleable.RoundImageView_drawRightUp,
					true);
			drawLiftDown = a.getBoolean(
					doublesoft.android.stu.R.styleable.RoundImageView_drawLiftDown, true);
			drawRightDown = a.getBoolean(
					doublesoft.android.stu.R.styleable.RoundImageView_drawRightDown, true);
		} else {
			float density = context.getResources().getDisplayMetrics().density;
			roundWidth = (int) (roundWidth * density);
			roundHeight = (int) (roundHeight * density);
		}

		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

		paint2 = new Paint();
		paint2.setXfermode(null);
	}

	@Override
	public void draw(Canvas canvas) {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Config.ARGB_8888);
		Canvas canvas2 = new Canvas(bitmap);
		super.draw(canvas2);

		if (drawLiftUp) {
			drawLiftUp(canvas2);
		}

		if (drawRightUp) {
			drawRightUp(canvas2);
		}

		if (drawLiftDown) {
			drawLiftDown(canvas2);
		}

		if (drawRightDown) {
			drawRightDown(canvas2);
		}

		canvas.drawBitmap(bitmap, 0, 0, paint2);
		bitmap.recycle();
	}

	private void drawLiftUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, roundHeight);
		path.lineTo(0, 0);
		path.lineTo(roundWidth, 0);
		path.arcTo(new RectF(0, 0, roundWidth * 2, roundHeight * 2), -90, -90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawLiftDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(0, getHeight() - roundHeight);
		path.lineTo(0, getHeight());
		path.lineTo(roundWidth, getHeight());
		path.arcTo(new RectF(0, getHeight() - roundHeight * 2,
				0 + roundWidth * 2, getHeight()), 90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightDown(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth() - roundWidth, getHeight());
		path.lineTo(getWidth(), getHeight());
		path.lineTo(getWidth(), getHeight() - roundHeight);
		path.arcTo(new RectF(getWidth() - roundWidth * 2, getHeight()
				- roundHeight * 2, getWidth(), getHeight()), 0, 90);
		path.close();
		canvas.drawPath(path, paint);
	}

	private void drawRightUp(Canvas canvas) {
		Path path = new Path();
		path.moveTo(getWidth(), roundHeight);
		path.lineTo(getWidth(), 0);
		path.lineTo(getWidth() - roundWidth, 0);
		path.arcTo(new RectF(getWidth() - roundWidth * 2, 0, getWidth(),
				0 + roundHeight * 2), -90, 90);
		path.close();
		canvas.drawPath(path, paint);
	}
}