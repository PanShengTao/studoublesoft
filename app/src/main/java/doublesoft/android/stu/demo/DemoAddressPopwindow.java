package doublesoft.android.stu.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.ChangeAddressPopwindow;

public class DemoAddressPopwindow extends MyFragment {

    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_addresspopwindow, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isFirstStart) {
        }

        // 控件及页面设置
        setNavigationTitle("地址选择");
        setNavigationBackButton();

        final TextView addressTextView = (TextView) findViewById(R.id.AddressTextView);
        // 选择城市
        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initProvince = "";
                String initCity = "";
                String initArea = "";

                if (addressTextView.getText().length() > 0) {
                    String[] temp = addressTextView.getText().toString().split(" ");
                    if (temp.length == 3) {
                        initProvince = temp[0];
                        initCity = temp[1];
                        initArea = temp[2];
                    }
                }

                ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(myContext,
                        initProvince, initCity, initArea);

                mChangeAddressPopwindow.showAtLocation(addressTextView, Gravity.BOTTOM, 0, 0);
                mChangeAddressPopwindow
                        .setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {

                            @Override
                            public void onClick(String province, String city, String area) {
                                addressTextView.setText(province + " " + city + " " + area);
                            }
                        });
            }
        });

    }
}
