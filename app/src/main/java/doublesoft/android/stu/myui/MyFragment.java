package doublesoft.android.stu.myui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import doublesoft.android.stu.imageloader.ImageLoader;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.index.Index;
import doublesoft.android.stu.mode.APIRequest;
import doublesoft.android.stu.mode.Config;
import doublesoft.android.stu.mode.DBCache;
import doublesoft.android.stu.mode.User;
import doublesoft.android.stu.myview.DropHUD;
import doublesoft.android.stu.myview.LoadingView;
import doublesoft.android.stu.myview.MyNumKeyboard;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Objects;

public class MyFragment extends Fragment {
    public android.support.v4.app.FragmentManager fragmentManager;
    public MyFragment fromFragment = null;
    public Boolean isNeedToRefresh = false;
    public Context myContext = null;
    public LayoutInflater mInflater;
    public MyFunction myFunc = new MyFunction();
    public MyHandler myHandler = new MyHandler();
    public ImageLoader imageLoader = null;

    public APIRequest apiRequest = null;
    public Config config = null;
    public User user = null;
    public DBCache dbCache = null;

    public DropHUD dropHUD = null;
    public LoadingView loadingView = null;
    public MyNumKeyboard myNumKeyboard = null;

    public boolean isFirstStart = true;
    private BroadcastReceiver receiver = null;
    private OnBackStackChangedListener onBackStackChangedListener = null;

    private String callBackMethodName;

    // 变量初始化
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (isFirstStart) {// 避免重复初始化
                fragmentManager = getFragmentManager();
                myContext = this.getActivity();
                mInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                apiRequest = new APIRequest(myContext, this);
                config = new Config(myContext);
                user = new User(myContext);
                dbCache = new DBCache(myContext);
                imageLoader = myFunc.getImageLoader(myContext);

                // 堆栈变化监听器
                onBackStackChangedListener = new OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {
                        onFragmentManagerBackStackChanged();
                    }
                };
                fragmentManager.addOnBackStackChangedListener(onBackStackChangedListener);
            }

            // UI重绘后，这些方法需要重复赋值
            if (findViewById(doublesoft.android.stu.R.id.DropHUD) != null) {
                dropHUD = (DropHUD) findViewById(doublesoft.android.stu.R.id.DropHUD);
            }

            if (findViewById(doublesoft.android.stu.R.id.LoadingView) != null) {
                loadingView = (LoadingView) findViewById(doublesoft.android.stu.R.id.LoadingView);
                loadingView.setText("数据加载中...");
            }

            if (findViewById(doublesoft.android.stu.R.id.mynumkeyboard) != null) {
                myNumKeyboard = new MyNumKeyboard(myContext, getView());
            }
        } catch (Exception e) {
            MyFunction.logError(e);
        }
    }

    // 事件点击 由 MyFragmentActivity传递过来
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            return onBackPressed();
        }
        return true;
    }

    // 此方法返回 false 会拦截物理的返回按钮
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDestroy() {
        try {
            // 移除堆栈变化监听器
            fragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener);
//            callback();
            // 取消广播接收
            if (receiver != null) {
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(myContext);
                broadcastManager.unregisterReceiver(receiver);
            }
        } catch (Exception ignored) {
        }

        super.onDestroy();
    }

    public void onFragmentManagerBackStackChanged() {

    }

    // 是否真实可见 系统那方法不可靠 根据回退栈来判断
    public boolean isTrueVisible() {
        try {
            if (this.isVisible()) {
                if (fragmentManager.getBackStackEntryCount() == 0) {
                    return true;
                } else {
                    BackStackEntry backStackEntry = fragmentManager
                            .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
                    if (backStackEntry.getName().equals(getClass().getName())) {
                        return true;
                    }
                }
            }

        } catch (Exception ignored) {
        }

        return false;
    }

    // 重载系统查找控件方法
    public View findViewById(int id) {
        try {
            return Objects.requireNonNull(this.getView()).findViewById(id);
        } catch (Exception e) {
            return null;
        }
    }

    // 设置背景颜色
    public void setNavigationBackgroundColor(int color) {
        try {
            LinearLayout navigationBar = (LinearLayout) findViewById(doublesoft.android.stu.R.id.NavigationBar);
            if (navigationBar != null) {
                navigationBar.setBackgroundColor(getResources().getColor(color));
            }
        } catch (Exception ignored) {
        }
    }

    // 设置页面标题
    public void setNavigationTitle(String title) {
        try {
            TextView pageTitle = (TextView) findViewById(doublesoft.android.stu.R.id.PageTitle);
            if (pageTitle != null) {
                pageTitle.setText(title);
            }
        } catch (Exception ignored) {
        }
    }

    // 设置页面图标
    public void setNavigationImage(int imageResid) {
        try {
            ImageView pageImage = (ImageView) findViewById(doublesoft.android.stu.R.id.PageImage);
            if (pageImage != null) {
                pageImage.setVisibility(View.VISIBLE);
                pageImage.setBackgroundResource(imageResid);
            }
        } catch (Exception ignored) {
        }
    }

    // 设置右侧图片按钮
    public Button setNavigationRightButtonWithImageName(int imageResid) {
        try {
            // 设置默认返回操作
            Button rightBtn = (Button) findViewById(doublesoft.android.stu.R.id.PageRightBtn);
            rightBtn.setVisibility(View.VISIBLE);

            Drawable pressImageDrawable = myContext.getResources().getDrawable(imageResid);
            pressImageDrawable.setAlpha(122);
            Drawable normalImageDrawable = myContext.getResources().getDrawable(imageResid);

            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, pressImageDrawable);
            drawable.addState(new int[]{}, normalImageDrawable);
            rightBtn.setBackground(drawable);

            return rightBtn;
        } catch (Exception ignored) {
        }

        return null;
    }

    // 设置右侧文字图片按钮
    public Button setNavigationRightButtonWithImageNameAndText(int imageResid, String text) {
        try {
            // 设置默认返回操作
            Button rightBtn = (Button) findViewById(doublesoft.android.stu.R.id.PageRightBtn);
            rightBtn.setVisibility(View.VISIBLE);

            // 图片画在左边
            rightBtn.setCompoundDrawablesWithIntrinsicBounds(imageResid, 0, 0, 0);

            // 文字在右边
            rightBtn.setText(text);
            rightBtn.setTextSize(14);
            rightBtn.setTextColor(getResources().getColor(doublesoft.android.stu.R.color.blueColor));

            return rightBtn;
        } catch (Exception ignored) {
        }

        return null;
    }

    // 设置右侧文字图片按钮 字体颜色
    public Button setNavigationRightButtonWithImageNameAndText(int imageResid, String text, int color) {
        try {
            // 设置默认返回操作
            Button rightBtn = (Button) findViewById(doublesoft.android.stu.R.id.PageRightBtn);
            rightBtn.setVisibility(View.VISIBLE);

            // 图片画在左边
            rightBtn.setCompoundDrawablesWithIntrinsicBounds(imageResid, 0, 0, 0);

            // 文字在右边
            rightBtn.setText(text);
            rightBtn.setTextSize(14);
            rightBtn.setTextColor(getResources().getColor(color));

            return rightBtn;
        } catch (Exception ignored) {
        }

        return null;
    }

    public Button setNavigationRightButtonWithTextAndColor(String text, int color) {
        try {
            // 设置默认返回操作
            Button rightBtn = (Button) findViewById(doublesoft.android.stu.R.id.PageRightBtn);
            rightBtn.setVisibility(View.VISIBLE);

            // 文字
            rightBtn.setText(text);
            rightBtn.setTextSize(14);
            rightBtn.setTextColor(color);

            return rightBtn;
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * 仅仅用于视频管理
     *
     * @param text
     * @return
     */
    public Button setNavigationRightWithWhiteBg(String text) {
        Button rightBtn = setNavigationRightButtonWithTextAndColor(text, getResources().getColor(doublesoft.android.stu.R.color.theme_color));
        rightBtn.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rightBtn.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        rightBtn.setBackgroundResource(doublesoft.android.stu.R.drawable.white_corner_bg);
        return rightBtn;
    }

    // 设置默认返回操作
    public Button setNavigationBackButton() {
        try {
            // 设置默认返回操作
            ImageButton backBtn = (ImageButton) findViewById(doublesoft.android.stu.R.id.PageLeftBtn);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    closeFragment();
                }
            });

            return backBtn;
        } catch (Exception ignored) {
        }

        return null;
    }

    public Button setNavigationLeftButtonWithTextAndColor(String text, int color) {
        try {
            // 设置默认返回操作
            Button leftBtn = (Button) findViewById(doublesoft.android.stu.R.id.PageLeftTextBtn);
            leftBtn.setVisibility(View.VISIBLE);

            // 文字
            leftBtn.setText(text);
            leftBtn.setTextSize(14);
            leftBtn.setTextColor(color);

            return leftBtn;
        } catch (Exception ignored) {
        }

        return null;
    }


    // 弹出键盘
    public void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception ignored) {
        }
    }

    // 关闭键盘
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
        } catch (Exception ignored) {
        }

    }

    // 广播标示
    // 用户登录成功
    public final static String broadcastActionUserLogin = "doublesoft.android.stu.UserLogin";
    // 天气更新
    public final static String broadcastActionWeatherUpdate = "doublesoft.android.stu.WeatherUpdate";

    // 注册广播 接收 发送
    public void registerBroadcast() {
        try {
            if (receiver == null) {
                receiver = new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        assert action != null;
                        if (action.equals(broadcastActionUserLogin)) {
                            receiveBroadcastUserLogin(context, intent);
                        }

                        if (action.equals(broadcastActionWeatherUpdate)) {
                            receiveBroadcastWeatherUpdate(context, intent);
                        }
                    }
                };

                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(myContext);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(broadcastActionUserLogin);
                intentFilter.addAction(broadcastActionWeatherUpdate);
                broadcastManager.registerReceiver(receiver, intentFilter);
            }
        } catch (Exception ignored) {
        }
    }

    protected void receiveBroadcastUserLogin(Context context, Intent intent) {

    }

    protected void receiveBroadcastWeatherUpdate(Context context, Intent intent) {

    }

    public void sendBroadcast(Intent intent) {
        try {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(myContext);
            broadcastManager.sendBroadcast(intent);
        } catch (Exception ignored) {
        }
    }

    // 打开新的Fragment
    public void startFragment(MyFragment newFragment) {
        startFragment(newFragment, null, 1, false);
    }

    public void startFragment(MyFragment newFragment, Bundle bundle) {
        startFragment(newFragment, bundle, 1, false);
    }

    public void startFragment(MyFragment newFragment, Bundle bundle, Integer animKind, Boolean allowRepeat) {
        try {
            if (fragmentManager.findFragmentByTag(newFragment.getClass().getName()) == null || allowRepeat) {
                // 参数
                if (bundle != null) {
                    newFragment.setArguments(bundle);
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                // 动画
                if (animKind == 2) {
                    transaction.setCustomAnimations(doublesoft.android.stu.R.anim.public_bottom_enter, doublesoft.android.stu.R.anim.public_bottom_exit,
                            doublesoft.android.stu.R.anim.public_bottom_enter, doublesoft.android.stu.R.anim.public_bottom_exit);
                } else {
                    transaction.setCustomAnimations(doublesoft.android.stu.R.anim.public_anim_enter, doublesoft.android.stu.R.anim.public_back_exit,
                            doublesoft.android.stu.R.anim.public_anim_enter, doublesoft.android.stu.R.anim.public_back_exit);
                }
                newFragment.fromFragment = this;
                transaction.addToBackStack(newFragment.getClass().getName());
                transaction.add(doublesoft.android.stu.R.id.IndexPageFrameLayout, newFragment, newFragment.getClass().getName());
                transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 退出Fragment
    public void closeFragment() {
        closeFragment(true);
    }

    public void closeFragment(Boolean anim) {
        hideKeyboard();
        try {
            if (!anim) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(this);
                transaction.commitAllowingStateLoss();
                fragmentManager.popBackStackImmediate();
            } else {
                fragmentManager.popBackStack();
            }
        } catch (Exception ignored) {
        }
    }

    // 页面事件部分
    @Override
    public void onResume() {
        super.onResume();

        try {
            // 打开接口
            if (isFirstStart) {
                isFirstStart = false;

                try {
                    Bundle bundle = getArguments();
                    if (bundle.containsKey("LinkUrl")) {
                        JSONObject linkUrl = new JSONObject(bundle.getString("LinkUrl"));
                        openByLinkUrl(linkUrl);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }

    }

    // 打开URL {"Controller":"Buy3D"}
    public void openLinkUrl(String linkUrl) {
        try {
            if (linkUrl.contains("http") && !linkUrl.contains("Controller")) {
                Uri uri = Uri.parse(linkUrl);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
            } else {
                if (!linkUrl.contains("\\n")) {
                    linkUrl = linkUrl.replace("\\", "");
                }
                linkUrl = linkUrl.replace("%7B", "{");
                linkUrl = linkUrl.replace("%22", "\"");
                linkUrl = linkUrl.replace("&quot;", "\"");
                linkUrl = linkUrl.replace("%7D", "}");
                linkUrl = linkUrl.replace("controller", "Controller");

                linkUrl = linkUrl.substring(linkUrl.indexOf("{"), linkUrl.lastIndexOf("}") + 1);

                final JSONObject linkDic = new JSONObject(linkUrl);

                // 移除未写，直接传递尝试打开
                final Class<?> targetClass = myFunc.findClass(linkDic.getString("Controller"));

                // 特殊页面处理 比较是否为底部的几个Fragment
                if (myContext != null) {
                    Index index = (Index) myContext;
                    if (targetClass.equals(index)) {
                        return;
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString("LinkUrl", linkUrl);
                startFragment((MyFragment) targetClass.newInstance(), bundle);
            }
        } catch (Exception e) {
            System.out.println("openLinkUrl Exception" + e);
        }
    }

    // 被接口打开
    public void openByLinkUrl(JSONObject linkDic) {

    }

    // 拨打电话
    public void callPhone(final String phoneNumber) {
        new MyDialog.Builder(myContext).setTitle("拨打电话").setMessage(phoneNumber)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("拨打", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        }).create().show();
    }

    public void startFragmentForCallback(MyFragment toFragment, String callBackMethodName) {
        // 请求回调
        toFragment.callBackMethodName = callBackMethodName;
        startFragment(toFragment);
    }

    public void callback() {
        if (TextUtils.isEmpty(callBackMethodName)) {
            return;
        }
        try {
            Method sel = fromFragment.getClass().getMethod(callBackMethodName, Object.class);
            sel.invoke(fromFragment, onCallback());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            callBackMethodName = null;
        }
    }

    protected Object onCallback() {
        return null;
    }

    private OnFragmentCallbackListener onFragmentCallbackListener = null;

    public MyFragment setOnFragmentCallbackListener(OnFragmentCallbackListener onFragmentCallbackListener) {
        this.onFragmentCallbackListener = onFragmentCallbackListener;
        return this;
    }

    public interface OnFragmentCallbackListener {
        void onCallback(MyFragment fragment, Object data);
    }
}
