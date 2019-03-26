package doublesoft.android.stu.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

public class NameTextView extends TextView {

    int mNameColor;
    String mName;


    public NameTextView(Context context) {
        super(context);

    }

    public NameTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(attrs);
    }

    public NameTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
    }


    private void initAttr(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, doublesoft.android.stu.R.styleable.NameTextView);
        mNameColor = array.getColor(doublesoft.android.stu.R.styleable.NameTextView_nameColor, getContext().getResources().getColor(doublesoft.android.stu.R.color.blackColor));
        mName = array.getText(doublesoft.android.stu.R.styleable.NameTextView_name).toString();
        array.recycle();
    }


    public void setTextAfterName(String text) {
        if (mName != null) {
            SpannableStringBuilder spannableText = new SpannableStringBuilder(mName);
            spannableText.setSpan(new ForegroundColorSpan(mNameColor), 0, mName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableText.append(text == null ? "" : text);
            setText(spannableText);
        }
    }


    public void setNameColor(int nameColor) {
        this.mNameColor = nameColor;
    }

    public void setName(String name) {
        this.mName = name;
    }
}
