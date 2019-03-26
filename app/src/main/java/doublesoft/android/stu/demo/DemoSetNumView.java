package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.SetNumView;
import doublesoft.android.stu.myview.SetNumViewListener;

public class DemoSetNumView extends MyFragment {
	private Button btnSetNum = null;

	private SetNumView mainSetNumView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_setnumview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("设置数字SetNumView");
		setNavigationBackButton();

		// 添加几个按钮 展示下用法，更多用法请参考SegView中的方法说明
		btnSetNum = (Button) findViewById(R.id.DemoBtnSetNum);
		btnSetNum.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btnSetNumClick();
			}
		});

		// 分段视图配置
		mainSetNumView = (SetNumView) findViewById(R.id.SetNumView);

		// 传入键盘 必须的
		mainSetNumView.myNumKeyboard = myNumKeyboard;

		// 一般都要设置
		mainSetNumView.defaultNum = 1;// 默认值
		mainSetNumView.minNum = 1;// 最小值
		mainSetNumView.maxNum = 100;// 最大值

		// 可选
		mainSetNumView.viewHeight = 51;// 控件高度
		mainSetNumView.viewWidth = 150;// 控件宽度 宽度设置后 inputWidth 无效，自动适应
		mainSetNumView.buttonWidth = 50;// 左右按钮宽度
		mainSetNumView.inputWidth = 30;// 输入框宽度

		// 绘制
		mainSetNumView.reloadData();

		// 设置监听器
		mainSetNumView.setSetNumViewListener(new SetNumViewListener() {
			@Override
			public void didBeginEditing() {
				System.out.println("开始编辑:" + mainSetNumView.currentNum());
			}

			@Override
			public void numDidChange() {
				System.out.println("数字改变:" + mainSetNumView.currentNum());
			}
		});

	}

	// 设置数字点击
	public void btnSetNumClick() {
		// 设置新值
		mainSetNumView.setNum(11);

		// 取值
		System.out.println("设置后的值:" + mainSetNumView.currentNum());
	}

}
