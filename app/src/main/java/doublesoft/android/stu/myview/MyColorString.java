package doublesoft.android.stu.myview;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

public class MyColorString {
	private SpannableStringBuilder sBuilder = new SpannableStringBuilder();

	// 添加值
	public void append(String text) {
		sBuilder.append(text);
	}

	public void append(String text, int color) {
		sBuilder.append(text);
		sBuilder.setSpan(new ForegroundColorSpan(color), sBuilder.length()
				- text.length(), sBuilder.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	// colorString.append("测试",new
	// ForegroundColorSpan(getResources().getColor(R.color.redColor)),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	public void append(String text, Object what, int flag) {
		sBuilder.append(text);
		sBuilder.setSpan(what, sBuilder.length() - text.length(),
				sBuilder.length(), flag);
	}

	// 获取值
	public SpannableStringBuilder getString() {
		return sBuilder;
	}
}
