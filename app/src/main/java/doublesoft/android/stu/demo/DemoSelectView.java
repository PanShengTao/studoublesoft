package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyButton;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.PopView;
import doublesoft.android.stu.myview.SelectView;
import doublesoft.android.stu.myview.SelectViewListener;

import java.util.ArrayList;

public class DemoSelectView extends MyFragment {
	private SelectView mainSelectView = null;
	private Button btnReload = null;
	private Button btnChoose = null;
	private Button clearChoose = null;

	private SelectView fullSelectView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_selectview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("下拉选项SelectView");
		setNavigationBackButton();

		// 按钮事件绑定
		btnReload = (Button) findViewById(R.id.DemoBtnReload);
		btnReload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnReloadClick();
			}
		});

		btnChoose = (Button) findViewById(R.id.DemoBtnChoose);
		btnChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnChooseClick();
			}
		});

		clearChoose = (Button) findViewById(R.id.DemoBtnClearChoose);
		clearChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearChooseClick();
			}
		});

		// 下拉选项配置
		mainSelectView = (SelectView) findViewById(R.id.SelectView);

		// 选项title
		mainSelectView.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("贵阳公司");
				add("兴义公司");
				add("凯里公司");
				add("毕节公司");
			}
		};

		// 选项value
		mainSelectView.itemValueArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("1001");
				add("1002");
				add("1003");
				add("1004");
			}
		};

		// 可选
		mainSelectView.selectLabelTitle = "请选择公司";
		mainSelectView.selectPopViewTitle = "请选择所属公司";

		// 设置Lable根据内容变长显示的宽度范围
		mainSelectView.selectViewWidth = 1;//0默认 自适应，1填满
		mainSelectView.selectLabelMinWidth = 50;// 最小宽度，不足部分留空隙 最小宽度加上54.5就是整个SelectView的宽度，因为有间隙和下拉箭头的宽度
		mainSelectView.selectLabelMaxWidth = 100;// 最大Label宽度，超出后省略号显示，同时设置最小和最大宽度，就把SelectView的宽度定死了
		mainSelectView.itemPerRowCount = 1;// 每行显示几个按钮 默认就是1

		// 一个页面有多个selectView 可以用userTag加个标识
		mainSelectView.userTag = "SelectCompany";

		// 设置选项模式 默认是单选
		// mainSelectView.setSingleSelect();//设置单选 最少选一个 程序默认选中第一个
		mainSelectView.setMultSelect();// 设置多选
		// mainSelectView.setMaxOneSelect();//设置最多选择一个 可以一个都不选

		// 设置对应的弹框控件，iOS是在内部创建，android必须外部传入
		mainSelectView.selectPopView = (PopView) findViewById(R.id.PopView);
		mainSelectView.reloadData();

		//设置对齐方式，一定要在reloadData之后
		mainSelectView.selectLabel.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);

		// 设置监听器
		mainSelectView.setSelectViewListener(new SelectViewListener() {

			@Override
			public void popViewWillShow() {
				System.out.println("弹框即将显示");
			}

			@Override
			public void popViewWillHidden() {
				System.out.println("弹框即将关闭");
			}

			@Override
			public void popViewDidShow() {
				System.out.println("弹框已经显示");
			}

			@Override
			public void popViewDidHidden() {
				System.out.println("弹框已经关闭");
			}

			@Override
			public void itemSelectedChange() {
				System.out.println("选中值发生变化");
				System.out.println(mainSelectView.itemSelectedIndexArr());
				System.out.println(mainSelectView.itemSelectedTitleArr());
				System.out.println(mainSelectView.itemSelectedValueArr());
			}

			@Override
			public boolean itemAllowClick(MyButton btn) {
				// 是否允许选择，可以加判断，哪些选项在什么情况下不允许被选中
				return true;
			}
		});
	}

	// 重绘按钮点击
	public void btnReloadClick() {
		// 限制控件宽度
		mainSelectView.selectViewWidth = 100;

		// 控件高度 默认30 改变了高度就要去设置下拉箭头图片
		mainSelectView.selectViewHeight = 30;
		mainSelectView.selectBtn.setBackgroundResource(R.drawable.public_select);

		mainSelectView.selectLabelTitle = "请选择性别";
		mainSelectView.selectPopViewTitle = "请选择性别";
		mainSelectView.itemTitleArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("未知");
				add("男");
				add("女");
			}
		};

		// 选项value
		mainSelectView.itemValueArr = new ArrayList<String>() {
			private static final long serialVersionUID = -2751833678987905238L;

			{
				add("未知");
				add("男");
				add("女");
			}
		};
		mainSelectView.setSingleSelect();
		mainSelectView.reloadData();
	}

	// 选中按钮点击
	public void btnChooseClick() {

		// 根据单个索引 0 到 Count-1
		mainSelectView.selectItemByIndex(2);

		// 根据索引数组 注意索引值必须是 String “1” 类型，下同
		// mainSelectView.selectItemByIndexArr(myFunc.strArrToList(new String[] { "0",
		// "1" }));

		// 根据标题数组
		// mainSelectView.selectItemByTitleArr(myFunc.strArrToList(new String[] {
		// "兴义公司", "凯里公司" }));

		// 根据标题数组
		// mainSelectView.selectItemByValueArr(myFunc.strArrToList(new String[] {
		// "1003", "1004" }));
	}

	// 清空按钮点击
	public void clearChooseClick() {
		mainSelectView.clearItem();
	}

}
