package doublesoft.android.stu.myview;

import java.util.List;

public interface SwitchViewListener {
	public List<String> titleArrForSwitchView();
	public void didSelectItem(int index);
	public int defaultSelectItemIndex();
	public int backgroundColorForSelectedItem();
	public int backgroundColorForSwitchView();
	public int defaultItemColor();
}
