package doublesoft.android.stu.mode;

import android.content.ContentValues;
import android.content.Context;
import android.util.Base64;

import doublesoft.android.stu.inc.DB;
import doublesoft.android.stu.inc.DES3Utils;

import java.util.List;
import java.util.Objects;

public class Config {
    private DB db;

    public Config(Context context) {
        db = DB.getDB(context);
    }

    // 字段
    public List<String> fields() {
        List<String> resultList = null;
        if (db.openDB()) {
            resultList = db.getFields("Config");
            db.closeDB();
        }

        return resultList;
    }

    // 详细
    public ContentValues detail() {
        ContentValues rowContentValues = null;
        if (db.openDB()) {
            rowContentValues = db.getRow("Config");
            try {
                // 解密用户名
                if (rowContentValues.containsKey("UserName") && rowContentValues.getAsString("UserName") != null) {
                    rowContentValues.put("UserName", new String(Objects.requireNonNull(DES3Utils.decryptMode(
                            Base64.decode(rowContentValues.getAsString("UserName").getBytes(), Base64.DEFAULT)))));
                }

                // 解密密码
                if (rowContentValues.containsKey("Token") && rowContentValues.getAsString("Token") != null) {
                    rowContentValues.put("Token", new String(Objects.requireNonNull(DES3Utils.decryptMode(
                            Base64.decode(rowContentValues.getAsString("Token").getBytes(), Base64.DEFAULT)))));
                }
            } catch (Exception ignored) {
            }
        }

        return rowContentValues;
    }

    // 修改
    public Boolean change(ContentValues rowContentValues) {
        try {
            // 加密用户名
            if (rowContentValues.containsKey("UserName")) {
                rowContentValues.put("UserName",
                        Base64.encodeToString(
                                DES3Utils.encryptMode(rowContentValues.getAsString("UserName").getBytes()),
                                Base64.DEFAULT));
            }

            // 加密密码
            if (rowContentValues.containsKey("Token")) {
                rowContentValues.put("Token",
                        Base64.encodeToString(
                                DES3Utils.encryptMode(rowContentValues.getAsString("Token").getBytes()),
                                Base64.DEFAULT));
            }
        } catch (Exception ignored) {
        }

        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.updateRow("Config", rowContentValues, "");
            db.closeDB();
        }

        return resultCount > 0;
    }
}
