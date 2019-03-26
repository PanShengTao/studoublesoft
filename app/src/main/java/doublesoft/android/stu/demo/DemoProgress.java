package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.ProgressView;
import doublesoft.android.stu.myview.ProgressViewCircle;
import doublesoft.android.stu.myview.SetNumView;

public class DemoProgress extends MyFragment {
    private Button btnSetNum = null;

    private SetNumView mainSetNumView = null;

    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_progress, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isFirstStart) {
        }

        // 控件及页面设置
        setNavigationTitle("进度条");
        setNavigationBackButton();

        //设置进度值0-100
        final ProgressView progressView = (ProgressView) findViewById(R.id.ProgressView2);
        Button demoSetProgressBtn = (Button) findViewById(R.id.DemoSetProgressBtn);
        demoSetProgressBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setValue(60);
            }
        });


        //设置进度值0-100
        final ProgressViewCircle progressViewCircle = (ProgressViewCircle) findViewById(R.id.ProgressViewCircle2);
        Button demoSetProgressCircleBtn = (Button) findViewById(R.id.DemoSetProgressCircleBtn);
        demoSetProgressCircleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressViewCircle.setText("A123");
                progressViewCircle.setValue(60);
                progressViewCircle.setWarning(true);
            }
        });
    }

}
