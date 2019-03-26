package doublesoft.android.stu.inc;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import doublesoft.android.stu.index.Index;
import doublesoft.android.stu.mode.Push;
import doublesoft.android.stu.mode.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;


/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        // 组织数据
        JSONObject jsonObject = new JSONObject();
        try {
            assert bundle != null;
            Set<String> keySet = bundle.keySet();
            for (String key : keySet) {
                jsonObject.put(key, Objects.requireNonNull(bundle.get(key)).toString());
            }
        } catch (Exception e) {
            MyFunction.logError(e);
        }

        // Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " +
        // printBundle(bundle));

        switch (Objects.requireNonNull(intent.getAction())) {
            case JPushInterface.ACTION_REGISTRATION_ID:
                // 得到ID，什么都不做
                break;
            case JPushInterface.ACTION_MESSAGE_RECEIVED:
                // 记录消息到数据库
                try {
                    if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
                        Push push = new Push(context);
                        ContentValues infoArr = new ContentValues();
                        infoArr.put("Message", jsonObject.toString());

                        if (push.detailByMsgID(bundle.getString(JPushInterface.EXTRA_MSG_ID)) == null) {
                            infoArr.put("MsgID", bundle.getString(JPushInterface.EXTRA_MSG_ID));
                            infoArr.put("Status", 0);
                            push.add(infoArr);
                        } else {
                            push.changeByMsgID(infoArr, bundle.getString(JPushInterface.EXTRA_MSG_ID));
                        }
                    }
                } catch (Exception e) {
                    MyFunction.logError(e);
                }

                processCustomMessage(context, bundle, "收到消息");
                break;
            case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                // 记录提醒到数据库
                try {
                    if (bundle.containsKey(JPushInterface.EXTRA_MSG_ID)) {
                        try {
                            if (jsonObject.getString(JPushInterface.EXTRA_ALERT).contains("新的订单")) {
                                // 开启震动
                                User user = new User(context);
                                if (user.isLogin()) {
                                    ContentValues rowUser = user.detail();
                                    if (rowUser.getAsInteger("ShakeRemind") == 1 && MyFunction.shaker != null) {
                                        MyHandler myHandler = new MyHandler();
                                        myHandler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                MyFunction.shaker.vibrate(new long[]{1000, 3000, 1000, 3000, 1000, 3000,
                                                        1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000,
                                                        3000, 1000, 3000}, -1);

                                            }
                                        }, 1500);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            MyFunction.logError(e);
                        }

                        Push push = new Push(context);
                        ContentValues infoArr = new ContentValues();
                        infoArr.put("Notification", jsonObject.toString());

                        if (push.detailByMsgID(bundle.getString(JPushInterface.EXTRA_MSG_ID)) == null) {
                            infoArr.put("MsgID", bundle.getString(JPushInterface.EXTRA_MSG_ID));
                            infoArr.put("Status", 0);
                            push.add(infoArr);
                        } else {
                            push.changeByMsgID(infoArr, bundle.getString(JPushInterface.EXTRA_MSG_ID));
                        }
                    }
                } catch (Exception e) {
                    MyFunction.logError(e);
                }

                // 收到消息直接发广播
                processCustomMessage(context, bundle, "收到提醒");
                break;
            case JPushInterface.ACTION_NOTIFICATION_OPENED:
                // 点击事件
                try {
                    boolean isAppRunning = false;
                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    assert am != null;
                    @SuppressWarnings("deprecation")
                    List<RunningTaskInfo> list = am.getRunningTasks(100);
                    for (RunningTaskInfo info : list) {
                        if (info.topActivity.getPackageName().equals(context.getPackageName())
                                && info.baseActivity.getPackageName().equals(context.getPackageName())) {
                            isAppRunning = true;

                            // 恢复到前台
                            String className = info.topActivity.getClassName();
                            intent.setAction(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setComponent(new ComponentName(context, Class.forName(className)));
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            context.startActivity(intent);

                            // 延迟发广播
                            new MyHandler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    processCustomMessage(context, bundle, "点击唤醒");
                                }
                            }, 500);

                            break;
                        }
                    }

                    // 启动一个新实例
                    if (!isAppRunning) {
                        Intent intentNew = new Intent(context, Index.class);
                        intentNew.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentNew);

                        // 延迟发广播
                        new MyHandler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                processCustomMessage(context, bundle, "点击启动");
                            }
                        }, 500);
                    }

                } catch (Exception e) {
                    MyFunction.logError(e);
                }

                break;
            case JPushInterface.ACTION_RICHPUSH_CALLBACK:
                // Log.d(TAG, "用户收到到RICH PUSH CALLBACK: " +
                // bundle.getString(JPushInterface.EXTRA_EXTRA));
                // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
                // 打开一个网页等..

                break;
            case JPushInterface.ACTION_CONNECTION_CHANGE:

                break;
            default:
                // Log.d(TAG, "Unhandled intent - " + intent.getAction());
                break;
        }

    }

    private void processCustomMessage(Context context, Bundle bundle, String kind) {
        Intent msgIntent = new Intent(Index.MESSAGE_RECEIVED_ACTION);
        msgIntent.putExtras(bundle);
        msgIntent.putExtra("Kind", kind);
        context.sendBroadcast(msgIntent);
    }

    // 判断程序是否运行
    public boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        @SuppressWarnings("deprecation")
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())
                    && info.baseActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
