package doublesoft.android.stu.myview;

import android.widget.Button;

import java.util.List;

public interface PopChooseViewListener {
	public String titleFromPopChooseView();

	public List<Button> btnArrFromPopChooseView();

	public void popChooseViewDidHidden();

}
