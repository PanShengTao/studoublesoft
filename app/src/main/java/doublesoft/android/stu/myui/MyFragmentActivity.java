package doublesoft.android.stu.myui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;

public class MyFragmentActivity extends FragmentActivity {
    private long exitTime = 0;
    public Context myContext;
    public MyFunction myFunc = new MyFunction();
    public MyHandler myHandler = new MyHandler();
    public android.support.v4.app.FragmentManager fragmentManager;
    public boolean isPause = true;

    // 程序初始化的一些操作
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏 设置视图
        hideStatusBar();
        setAndroidNativeLightStatusBar(true);

        fragmentManager = getSupportFragmentManager();
        myContext = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        MyFunction.screenWidth = myFunc.px2dip(this, dm.widthPixels);
        MyFunction.screenHeight = myFunc.px2dip(this, dm.heightPixels);
    }

    // 按键事件传递
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        try {
            // 双击退出
            if (fragmentManager.getBackStackEntryCount() == 0) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    } else {
                        System.exit(0);

                        // Intent intent = new Intent();
                        // intent.setAction("android.intent.action.MAIN");
                        // intent.addCategory("android.intent.category.HOME");
                        // startActivity(intent);

                        return false;
                    }
                    return true;
                }
            }

            // 给栈顶Fragment传递事件
            if (fragmentManager.getBackStackEntryCount() > 0) {
                if (((MyFragment) fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 1))
                        .onKeyDown(keyCode, event)) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    return false;
                }
            }
        } catch (Exception ignored) {
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.isPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.isPause = true;
    }

    //只透明状态栏
    private void hideStatusBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //设置状态栏文字颜色
    private void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
