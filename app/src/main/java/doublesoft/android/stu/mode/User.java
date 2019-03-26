package doublesoft.android.stu.mode;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;

import doublesoft.android.stu.R;
import doublesoft.android.stu.inc.DES3Utils;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.user.UserLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Objects;

public class User {
    private Config config;
    private MyFunction myFunc = new MyFunction();
    private DBCache dbCache;

    public User(Context context) {
        config = new Config(context);
        dbCache = new DBCache(context);
    }

    // 用户详细
    public ContentValues detail() {
        ContentValues userDic = MyFunction.userDic;
        try {
            if (userDic.size() == 0) {
                String cacheValue = dbCache.get("GLOBAL_USER_LOGIN_INFO");
                if (cacheValue != null) {
                    userDic.clear();
                    cacheValue = new String(
                            Objects.requireNonNull(DES3Utils.decryptMode(Base64.decode(cacheValue.getBytes(), Base64.DEFAULT))));

                    JSONObject rowUser = new JSONObject(cacheValue);

                    Iterator<?> iterator = rowUser.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = rowUser.getString(key);
                        MyFunction.userDic.put(key, value);
                    }
                }
            }

            if (!MyFunction.userDic.containsKey("Name")) {
                MyFunction.userDic.put("Name", "");
            }

            if (!MyFunction.userDic.containsKey("UserName")) {
                MyFunction.userDic.put("UserName", "");
            }

            if (!MyFunction.userDic.containsKey("Mobile")) {
                MyFunction.userDic.put("Mobile", "");
            }

            if (!MyFunction.userDic.containsKey("Token")) {
                MyFunction.userDic.put("Token", "");
            }

            if (!MyFunction.userDic.containsKey("Role")) {
                MyFunction.userDic.put("Role", "User");
            }
        } catch (Exception ignored) {

        }

        return MyFunction.userDic;
    }

    // 权限数组
    public ContentValues rightArr() {
        try {
            if (this.isLogin()) {
                ContentValues rowUser = this.detail();
                if (rowUser != null) {
                    if (rowUser.containsKey("RightArr")) {
                        JSONObject jsonObject = new JSONObject(rowUser.getAsString("RightArr"));
                        return myFunc.jsonObjectToContentValues(jsonObject);
                    }
                }
            }

        } catch (Exception ignored) {
        }

        return new ContentValues();
    }

    // 登录者的Token
    public String loginToken() {
        if (this.isLogin()) {
            ContentValues rowUser = this.detail();
            return rowUser.getAsString("Token");
        }

        return "";
    }

    // 登录者的UserName
    public String loginUserName() {
        if (this.isLogin()) {
            ContentValues rowUser = this.detail();
            return rowUser.getAsString("UserName");
        }

        return "";
    }

    // 判断是否登录
    public Boolean isLogin() {
        ContentValues rowUser = this.detail();
        if (rowUser == null) {
            return false;
        }

        String userName = rowUser.getAsString("UserName");
        String token = rowUser.getAsString("Token");
        return userName != null && userName.length() != 0 && token != null && token.length() != 0;
    }

    // 检测登录 未登录弹出登录界面
    public Boolean checkLogin(android.support.v4.app.FragmentManager fm) {
        if (!this.isLogin()) {
            if (fm.findFragmentByTag("UserLogin") == null) {
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(R.anim.public_bottom_enter, R.anim.public_bottom_exit);
                transaction.add(R.id.IndexPageFrameLayout, new UserLogin(), "UserLogin");
                transaction.commit();
            }
            return false;
        }
        return true;
    }

    // 写入用户登录信息
    public void writeUserDic(JSONObject rowUser) {
        try {
            dbCache.set("GLOBAL_USER_LOGIN_INFO", Base64.encodeToString(DES3Utils.encryptMode(rowUser.toString().getBytes()), Base64.DEFAULT));
            MyFunction.userDic.clear();

            // 写入默认账号到数据库
            ContentValues infoDic = new ContentValues();
            infoDic.put("UserName", rowUser.getString("UserName"));
            infoDic.put("Token", rowUser.getString("Token"));
            config.change(infoDic);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }
    }

    // 清空用户登录信息
    public void clearUserDic() {
        // 退出登录
        ContentValues updateDic = new ContentValues();
        updateDic.put("Token", "");
        config.change(updateDic);

        MyFunction.userDic.clear();
        dbCache.del("GLOBAL_USER_LOGIN_INFO");
    }
}