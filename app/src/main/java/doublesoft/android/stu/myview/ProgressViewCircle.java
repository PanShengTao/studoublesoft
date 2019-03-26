package doublesoft.android.stu.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import doublesoft.android.stu.R;
import doublesoft.android.stu.inc.MyFunction;

@SuppressLint("DrawAllocation")
public class ProgressViewCircle extends View {

    private Context mContext = null;
    private MyFunction myFunc = new MyFunction();
    private int pvcBorderWidth = 4;//进度条边框宽度
    private float pvcFontSize = 14;//进度条文字大小
    private float pvcFontSize100 = 12;//进度条文字大小 进度为100%时
    private String pvcText = "";//进度条文字
    private int pvcValue = 0;//进度0-100
    private boolean pvcWarning = false;//是否预警

    public ProgressViewCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("Recycle")
    public ProgressViewCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ProgressViewCircle);
            pvcBorderWidth = a.getInt(R.styleable.ProgressViewCircle_pvcBorderWidth, pvcBorderWidth);
            pvcFontSize = a.getFloat(R.styleable.ProgressViewCircle_pvcFontSize,
                    pvcFontSize);
            pvcFontSize100 = a.getFloat(R.styleable.ProgressViewCircle_pvcFontSize100,
                    pvcFontSize100);
            pvcText = a.getString(R.styleable.ProgressViewCircle_pvcText);
            pvcValue = a.getInt(R.styleable.ProgressViewCircle_pvcValue, pvcValue);
            pvcWarning = a.getBoolean(R.styleable.ProgressViewCircle_pvcWarning, pvcWarning);
        }
    }

    public ProgressViewCircle(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //宽度
        int width = super.getMeasuredWidth();
        int borderWidth = myFunc.dip2px(mContext, pvcBorderWidth);

        //画内圆 稍微画大点，不然有缝隙
        Paint p = new Paint();
        if (pvcWarning) {//警告
            p.setColor(Color.parseColor("#f79da1"));
        } else if (pvcValue == 100) {//进度100%
            p.setColor(Color.parseColor("#4bcc73"));
        } else {
            p.setColor(Color.parseColor("#ffffff"));
        }
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);

        canvas.drawCircle(width / 2, width / 2, (width - borderWidth * 2) / 2 + 2, p);

        //画进度条圆
        p = new Paint();
        p.setColor(Color.parseColor("#dddddd"));
        p.setAntiAlias(true);
        p.setStrokeWidth(borderWidth);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(width / 2, width / 2, (width - borderWidth * 2) / 2 + borderWidth / 2, p);

        //画进度条
        if (pvcValue >= 0 && pvcValue <= 100) {
            p = new Paint();
            if (pvcValue <= 40) {
                p.setColor(Color.parseColor("#fd9e02"));
            } else if (pvcValue <= 70) {
                p.setColor(Color.parseColor("#3b75d4"));
            } else {
                p.setColor(Color.parseColor("#4bcc73"));
            }
            p.setAntiAlias(true);
            p.setStrokeWidth(borderWidth);
            p.setStyle(Paint.Style.STROKE);

            RectF rectf_head = new RectF(borderWidth / 2, borderWidth / 2, width - borderWidth / 2, width - borderWidth / 2);
            canvas.drawArc(rectf_head, -90, 360.0f / 100 * pvcValue, false, p);
        }

        //画文字
        p = new Paint();
        p.setAntiAlias(true);
        if (pvcWarning || pvcValue == 100) {//警告 或 进度100%
            p.setColor(Color.parseColor("#ffffff"));
        } else {
            p.setColor(Color.parseColor("#585a66"));
        }
        p.setTextSize(myFunc.dip2px(mContext, pvcValue == 100 ? pvcFontSize100 : pvcFontSize));

        Rect rect = new Rect();
        p.getTextBounds(pvcText, 0, pvcText.length(), rect);

        int textWidth = rect.width();
        int textHeight = rect.height();

        if (textWidth >= width - borderWidth * 2) {//超过内圆宽度显示省略号
            textWidth = width - borderWidth * 2;
            pvcText = TextUtils.ellipsize(pvcText, new TextPaint(p), textWidth, TextUtils.TruncateAt.END).toString();
        }

        if (pvcValue != 100) {
            canvas.drawText(pvcText, width / 2 - textWidth / 2, width / 2 + textHeight / 2, p);
        } else {
            canvas.drawText(pvcText, width / 2 - textWidth / 2, width / 2 - 3, p);

            //画勾
            String rightText = "√";
            Rect rightRect = new Rect();
            p.getTextBounds(rightText, 0, rightText.length(), rightRect);

            int rightWidth = rightRect.width();
            int rightHeight = rightRect.height();

            canvas.drawText(rightText, width / 2 - rightWidth / 2, width / 2 + 8 + rightHeight, p);
        }
    }

    //设置进度条宽度
    public void setBorderWidth(int borderWidth) {
        pvcBorderWidth = borderWidth;
        invalidate();
    }

    //设置文字大小
    public void setFontSize(float fontSize) {
        pvcFontSize = fontSize;
        invalidate();
    }

    public void setFontSize100(float fontSize) {
        pvcFontSize100 = fontSize;
        invalidate();
    }

    //设置值
    public void setValue(int newValue) {
        pvcValue = newValue;
        invalidate();
    }

    //设置是否警告
    public void setWarning(boolean warning) {
        pvcWarning = warning;
        invalidate();
    }

    //设置文字
    public void setText(String text) {
        pvcText = text;
        invalidate();
    }
}