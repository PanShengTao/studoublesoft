package doublesoft.android.stu.user;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.myui.MyFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import cn.jpush.android.api.JPushInterface;

public class UserLogin extends MyFragment {
    private EditText userNameEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(doublesoft.android.stu.R.layout.user_login, container, false);
    }

    // 初始化
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 变量初始化
        userNameEditText = (EditText) findViewById(doublesoft.android.stu.R.id.UserName);
        passwordEditText = (EditText) findViewById(doublesoft.android.stu.R.id.Password);

        Button loginButton = (Button) findViewById(doublesoft.android.stu.R.id.LoginButton);
        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loginButtonClick();
            }
        });

        // 初始化登录值
        this.setDefaultLoginValue();
    }

    // 隐藏键盘
    public void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(userNameEditText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
        }
        userNameEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    // 初始化登录值
    public void setDefaultLoginValue() {
        ContentValues rowConfig = config.detail();
        String defaultCompanyNum = rowConfig.getAsString("CompanyNum");

        String defaultUserName = rowConfig.getAsString("UserName");
        if (defaultUserName != null && defaultUserName.length() > 0) {
            userNameEditText.setText(defaultUserName);
        }
    }

    // 登录点击
    public void loginButtonClick() {
        try {
            if (userNameEditText.getText().length() == 0) {
                dropHUD.showHint("请输入登录账号！");
                userNameEditText.requestFocus();
                return;
            }

            if (passwordEditText.getText().length() == 0) {
                dropHUD.showHint("请输入登录密码！");
                passwordEditText.requestFocus();
                return;
            }

            // 隐藏键盘
            hiddenKeyboard();

            // 开始提交验证
            ContentValues requestDataDic = new ContentValues();
            requestDataDic.put("MainUrl", MyFunction.mainApiUrl);
            requestDataDic.put("LessUrl", MyFunction.lessApiUrl);
            requestDataDic.put("ScriptPath", "api/user/ProduceLogin.php");
            requestDataDic.put("UserName", userNameEditText.getText().toString());
            requestDataDic.put("Password", myFunc.md5(passwordEditText.getText().toString()));
            requestDataDic.put("JPushAlias", JPushInterface.getRegistrationID(myContext));

            loadingView.startAnimation();
            apiRequest.post(requestDataDic, "loginCall");
        } catch (Exception e) {
            MyFunction.logError(e);
        }
    }

    public void loginCall(final ContentValues requestDataDic, final String returnText) {
        myHandler.post(new Runnable() {

            @Override
            public void run() {
                loadingView.stopAnimation();

                if (returnText.length() != 0) {
                    try {
                        JSONObject resultDic = new JSONObject(returnText);
                        if (resultDic.has("Result")) {
                            if (resultDic.getInt("Result") == 1) {
                                //登录信息
                                JSONObject rowUser = resultDic.getJSONObject("RowUser");
                                user.writeUserDic(rowUser);

                                // 发送通知
                                Intent intent = new Intent();
                                intent.setAction(broadcastActionUserLogin);
                                intent.putExtra("Result", "Success");
                                sendBroadcast(intent);

                                // 注册别名 和 分组
                                Set<String> tagSet = new TreeSet<>();
                                String jpushTag = rowUser.getString("JPushTag");
                                String[] jpushTagArr = jpushTag.split(",");

                                Collections.addAll(tagSet, jpushTagArr);

                                JPushInterface.setAlias(myContext, 0, JPushInterface.getRegistrationID(myContext));
                                JPushInterface.setTags(myContext, 1, tagSet);

                                // 移除登录
                                removeUserLogin();
                            } else if (resultDic.getInt("Result") == -1) {
                                user.clearUserDic();
                                user.checkLogin(fragmentManager);
                            } else {
                                dropHUD.showHint(resultDic.getString("Message"));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("UserLogin loginCall:" + e);
                    }
                } else {
                    dropHUD.showHint("网络加载失败，请重试！");
                }
            }
        });
    }

    // 移除用户登录界面
    public void removeUserLogin() {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(doublesoft.android.stu.R.anim.public_bottom_enter, doublesoft.android.stu.R.anim.public_bottom_exit);
            transaction.remove(fragmentManager.findFragmentByTag("UserLogin"));
            transaction.commit();
        } catch (Exception e) {
            MyFunction.logError(e);
        }
    }
}
