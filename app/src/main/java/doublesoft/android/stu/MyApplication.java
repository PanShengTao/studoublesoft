package doublesoft.android.stu;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tencent.smtt.sdk.QbSdk;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private List<Activity> activityList = new ArrayList<>();

    public static Context context;

    public static String DEVELOPER_ID = "11910386";
    public static String APP_ID = "anyrtckAsTDn1V6M7p";
    public static String APP_KEY = "3w+MgMA4s0QdAxs5vUuyuWUXz2hiJzYZn1vNiLkgKC4";
    public static String APP_TOKEN = "d03983d6b441df69eae7fe566575d479";

    public static int THEME_COLOR;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // 以下用来捕获程序崩溃异常
//		if (!Config.DEBUG) {
//			Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程
//		}

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                //Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
        }
    };

    // activity管理：从列表中移除activity
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // activity管理：添加activity到列表
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // activity管理：结束所有activity，彻底关闭应用
    public void finishProgram() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // activity管理：结束所有activity
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
    }

    // 重启应用
    @SuppressWarnings("WrongConstant")
    public void restartApp() {
        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName(getPackageName(), "doublesoft.android.stu.index.Index");
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        finishProgram(); // 自定义方法，关闭当前打开的所有avtivity
    }
}
