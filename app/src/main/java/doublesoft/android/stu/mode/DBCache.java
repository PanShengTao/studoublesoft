package doublesoft.android.stu.mode;

import android.content.ContentValues;
import android.content.Context;

import doublesoft.android.stu.inc.DB;

public class DBCache {
    private DB db;

    public DBCache(Context context) {
        db = DB.getDB(context);
    }

    // 获得缓存值
    public String get(String key) {
        ContentValues rowContentValues = this.detailByKey(key);
        if (rowContentValues != null) {
            return rowContentValues.getAsString("Value");
        }

        return null;
    }

    // 写入缓存值
    public Boolean set(String key, String value) {
        ContentValues infoArr = new ContentValues();
        infoArr.put("Key", key);
        infoArr.put("Value", value);

        ContentValues rowContentValues = this.detailByKey(key);
        if (rowContentValues != null) {
            return this.change(infoArr, key);
        } else {
            return this.add(infoArr);
        }
    }

    // 删除缓存
    public Boolean del(String key) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.deleteRow("DBCache", "Key='" + key + "'");
            db.closeDB();
        }

        return resultCount > 0;
    }

    // 批量删除
    public Boolean clear() {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.deleteRow("DBCache", "1=1");
            db.closeDB();
        }

        return resultCount > 0;
    }

    // 详细
    public ContentValues detailByKey(String key) {
        ContentValues rowContentValues = null;
        if (db.openDB()) {
            rowContentValues = db.getRow("DBCache", "Key='" + key + "'");
            db.closeDB();
        }

        return rowContentValues;
    }

    // 添加
    public Boolean add(ContentValues rowContentValues) {
        long resultID = 0;
        if (db.openDB()) {
            resultID = db.insertRow("DBCache", rowContentValues);
            db.closeDB();
        }

        return resultID == -1;
    }

    // 修改
    public Boolean change(ContentValues rowContentValues, String key) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.updateRow("DBCache", rowContentValues, "Key='" + key + "'");
            db.closeDB();
        }

        return resultCount > 0;
    }
}
