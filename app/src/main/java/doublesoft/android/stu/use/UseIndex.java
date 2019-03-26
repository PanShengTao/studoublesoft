package doublesoft.android.stu.use;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;

public class UseIndex extends MyFragment {


    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.use_index, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setNavigationTitle("应用示例");

        //右侧扫码按钮
        Button rightBtn = setNavigationRightButtonWithImageName(R.drawable.btn_icon_scan);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(new UseScan());
            }
        });

        if (isFirstStart) {

        }
    }
}
