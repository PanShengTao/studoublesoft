package doublesoft.android.stu.myview;

import android.content.ContentValues;

import java.util.List;

public interface RightChooseViewListener {
	public List<ContentValues> arrForParentTableView();

	public void rightChooseViewDidShow();

	public void rightChooseViewDidHidden();

	public void rightChooseViewDidSelectID(int ID);
}
