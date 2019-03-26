package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import doublesoft.android.stu.R;
import doublesoft.android.stu.common.TbsFileFragment;
import doublesoft.android.stu.common.TbsFunction;
import doublesoft.android.stu.myui.MyButton;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.PopView;
import doublesoft.android.stu.myview.SelectView;
import doublesoft.android.stu.myview.SelectViewListener;

public class DemoViewFile extends MyFragment {
	private Button btnViewFile = null;
    private Button btnViewVideo = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_viewfile, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("查看视频文件");
		setNavigationBackButton();

        // 按钮事件绑定
        btnViewVideo = (Button) findViewById(R.id.DemoViewVideoBtn);
        btnViewVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TbsFunction.startPlay(getContext(), "http://ch.levycloud.cn/upload/file/201901/eqbf_1548819127.mp4");
            }
        });

        btnViewFile = (Button) findViewById(R.id.DemoViewFileBtn);
        btnViewFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("FileUrl", "http://ch.levycloud.cn/upload/file/201812/7l1q_1545803432.docx");
                startFragment(new TbsFileFragment(), bundle);
			}
		});
	}
}
