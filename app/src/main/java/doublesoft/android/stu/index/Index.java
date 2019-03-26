package doublesoft.android.stu.index;

import android.Manifest;
import android.annotation.SuppressLint;

import doublesoft.android.stu.demo.DemoIndex;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.mode.APIRequest;
import doublesoft.android.stu.mode.DBCache;
import doublesoft.android.stu.mode.Push;
import doublesoft.android.stu.mode.UpdateApp;
import doublesoft.android.stu.mode.User;
import doublesoft.android.stu.myui.MyDialog;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myui.MyFragmentActivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import doublesoft.android.stu.use.UseIndex;

public class Index extends MyFragmentActivity {
    private APIRequest apiRequest = null;
    private DBCache dbCache = null;
    private MyFragment myFragment = null;
    private UpdateApp updateApp = null;
    private Push push;

    public FragmentTabHost mTabHost;
    public List<View> mTabHostViews;
    private LayoutInflater mLayoutInflater;

    public Class<?> mFragmentArray[] = {DemoIndex.class, UseIndex.class};
    public int mImageArray[] = {doublesoft.android.stu.R.drawable.public_selector_tabbar_item_1, doublesoft.android.stu.R.drawable.public_selector_tabbar_item_2};
    public String mTextArray[] = {"基础学习", "应用示例"};

    // ==========================================================
    // 页面创建
    // ==========================================================
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(doublesoft.android.stu.R.layout.index);

        // 变量初始化
        User user = new User(myContext);
        push = new Push(myContext);
        updateApp = new UpdateApp(myContext);
        myFragment = new MyFragment();
        myFragment.fragmentManager = fragmentManager;
        myFragment.myContext = this;
        dbCache = new DBCache(myContext);
        apiRequest = new APIRequest(myContext);

        // 推送设置
        JPushInterface.init(this);
        registerMessageReceiver();

        // 显示登录
        //user.clearUserDic();
        //user.checkLogin(fragmentManager);

        // 绘制tabHost
        initTabHost();

        //请求权限
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

        //每10秒加载一次
        runPer10Seconds();
    }

    @Override
    protected void onResume() {
        super.onResume();

        JPushInterface.onResume(myContext);

        // 检查版本更新
        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                updateApp.autoCheckUpdate();
            }
        }, 3000);
    }

    @Override
    public void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    // ==========================================================
    // 绘制tabHost
    // ==========================================================
    private void initTabHost() {
        mLayoutInflater = LayoutInflater.from(this);

        // 找到TabHost
        if (mTabHostViews == null) {
            mTabHostViews = new ArrayList<>();
        }
        mTabHostViews.clear();
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, fragmentManager, doublesoft.android.stu.R.id.IndexFrameLayout);

        // 得到fragment的个数
        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            //记录到数组中 方便后期修改
            mTabHostViews.add(getTabItemView(i));

            // 给每个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextArray[i]).setIndicator(mTabHostViews.get(i));

            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
        }
    }

    // 给每个Tab按钮设置图标和文字
    @SuppressLint("InflateParams")
    private View getTabItemView(int index) {
        View view = mLayoutInflater.inflate(doublesoft.android.stu.R.layout.public_tabbar_item, null);
        ImageView imageView = (ImageView) view.findViewById(doublesoft.android.stu.R.id.ImageView);
        imageView.setImageResource(mImageArray[index]);
        TextView textView = (TextView) view.findViewById(doublesoft.android.stu.R.id.TextView);
        textView.setText(mTextArray[index]);

        return view;
    }

    //设置图标显示隐藏
    public void setTabItemRedPoint(int index, boolean isShow) {
        try {
            View tabView = mTabHostViews.get(index);
            View redPoint = tabView.findViewById(doublesoft.android.stu.R.id.RedPoint);
            redPoint.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            MyFunction.logError(e);
        }
    }

    // ==========================================================
    // 接收push广播
    // ==========================================================
    private MessageReceiver messageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "doublesoft.android.stu.MESSAGE_RECEIVED_ACTION";

    public void registerMessageReceiver() {
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(messageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                try {
                    doPushMessage(intent);
                } catch (Exception ignored) {
                }
            }
        }
    }

    // 处理推送消息 由MyReceiver广播过来
    public void doPushMessage(Intent intent) {
        try {
            assert intent != null;
            if (intent.hasExtra("Kind")) {
                Bundle bundle = intent.getExtras();
                String kind = intent.getStringExtra("Kind");// 收到提醒、收到消息、点击唤醒、点击启动
                assert bundle != null;
                String msgID = bundle.getString(JPushInterface.EXTRA_MSG_ID);
                ContentValues rowPushContentValues = push.detailByMsgID(msgID);

                // 判断消息存在
                if (rowPushContentValues != null
                        && (kind.equals("收到消息") || kind.equals("点击唤醒") || kind.equals("点击启动"))) {
                    if (!this.isPause) {// 页面未暂停
                        JSONObject rowMessageJSONObject = new JSONObject(rowPushContentValues.getAsString("Message"));
                        // 弹框提醒Message 一定会弹
                        if (rowMessageJSONObject.getString(JPushInterface.EXTRA_CONTENT_TYPE).equals("Dialog")
                                && (rowMessageJSONObject.getString(JPushInterface.EXTRA_TITLE).length() > 0
                                || rowMessageJSONObject.getString(JPushInterface.EXTRA_MESSAGE).length() > 0)) {
                            MyDialog.Builder builder = new MyDialog.Builder(myContext);
                            if (rowMessageJSONObject.getString(JPushInterface.EXTRA_TITLE).length() > 0) {
                                builder.setTitle(rowMessageJSONObject.getString(JPushInterface.EXTRA_TITLE));
                            }

                            if (rowMessageJSONObject.getString(JPushInterface.EXTRA_MESSAGE).length() > 0) {
                                builder.setMessage(rowMessageJSONObject.getString(JPushInterface.EXTRA_MESSAGE));
                            }

                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.create().show();
                        }

                        // 跳转页面 点击进入的，强行跳转，否则询问用户
                        if (rowMessageJSONObject.getString(JPushInterface.EXTRA_EXTRA).contains("Controller")) {
                            JSONObject extraJsonObject = new JSONObject(
                                    rowMessageJSONObject.getString(JPushInterface.EXTRA_EXTRA));
                            final String linkUrl = extraJsonObject.getString("UserParam");

                            if (kind.equals("收到消息")) {
                                // 不跳转了，不友好
                            } else {
                                myFragment.openLinkUrl(linkUrl);
                            }
                        }

                        // 删除该消息
                        push.delByMsgID(msgID);

                        // 删除老的推送信息
                        push.delOld();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }

    //每10秒钟执行一次的方法
    public void runPer10Seconds() {
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isPause) {

                }

                runPer10Seconds();
            }
        }, 10000);
    }
}