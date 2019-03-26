package doublesoft.android.stu.mode;

import android.content.ContentValues;
import android.content.Context;

import doublesoft.android.stu.inc.DB;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Push {
    private DB db = null;

    public Push(Context context) {
        db = DB.getDB(context);
    }

    // 字段
    public List<String> fields() {
        List<String> resultList = null;
        if (db.openDB()) {
            resultList = db.getFields("Push");
            db.closeDB();
        }

        return resultList;
    }

    // 详细
    public ContentValues detail(int ID) {
        ContentValues rowContentValues = null;
        if (db.openDB()) {
            rowContentValues = db.getRow("Push", "ID=" + ID);
            db.closeDB();
        }

        return rowContentValues;
    }

    public ContentValues detailByMsgID(String msgID) {
        ContentValues rowContentValues = null;
        if (db.openDB()) {
            rowContentValues = db.getRow("Push", "MsgID='" + msgID + "'");
            db.closeDB();
        }

        return rowContentValues;
    }

    // 添加
    public Boolean add(ContentValues rowContentValues) {
        long resultID = 0;
        if (db.openDB()) {
            resultID = db.insertRow("Push", rowContentValues);
            db.closeDB();
        }

        return resultID != -1;
    }

    // 修改
    public Boolean change(ContentValues rowContentValues, int ID) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.updateRow("Push", rowContentValues, "ID=" + ID);
            db.closeDB();
        }

        return resultCount > 0;
    }

    public Boolean changeByMsgID(ContentValues rowContentValues, String msgID) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.updateRow("Push", rowContentValues, "MsgID='" + msgID + "'");
            db.closeDB();
        }

        return resultCount > 0;
    }

    // 删除
    public Boolean del(int ID) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.deleteRow("Push", "ID=" + ID);
            db.closeDB();
        }

        return resultCount > 0;
    }

    public Boolean delByMsgID(String msgID) {
        int resultCount = 0;
        if (db.openDB()) {
            resultCount = db.deleteRow("Push", "MsgID='" + msgID + "'");
            db.closeDB();
        }

        return resultCount > 0;
    }

    public void delOld() {
        if (db.openDB()) {
            String sql = "DELETE FROM Push WHERE ID<(SELECT MIN(ID) FROM (SELECT ID FROM Push ORDER BY ID DESC LIMIT 10))";
            db.execSQL(sql);
            db.closeDB();
        }
    }

    // 分页查询
    public List<ContentValues> pageList(int page, int pageSize, ContentValues searchKey) {
        return this.pageList(page, pageSize, searchKey, "ID DESC");
    }

    public List<ContentValues> pageList(int page, int pageSize, ContentValues searchKey, String orderBy) {
        List<ContentValues> resultList = null;
        if (db.openDB()) {
            String condition = "";

            if (searchKey.size() > 0) {
                condition += " WHERE 1=1";
                Set<Entry<String, Object>> searchKeyValueSet = searchKey.valueSet();
                Iterator<Entry<String, Object>> searchKeyIterator = searchKeyValueSet.iterator();
                while (searchKeyIterator.hasNext()) {
                    Map.Entry<String, Object> rowSearchKey = searchKeyIterator.next();
                    String key = rowSearchKey.getKey().toString();
                    String value = rowSearchKey.getValue().toString();

                    if (key.equals("Status")) {
                        condition += " AND Status=" + Integer.valueOf(value).intValue();
                    } else {
                        if (value.length() != 0) {
                            condition += " AND " + key + " LIKE '%" + value + "%'";
                        }
                    }
                }
            }

            if (condition.indexOf("WHERE 1=1 AND") != -1) {
                condition = condition.replace("WHERE 1=1 AND", "");
            }

            if (condition.indexOf("WHERE 1=1") != -1) {
                condition = condition.replace("WHERE 1=1", "");
            }

            ContentValues sqlContentValue = new ContentValues();
            sqlContentValue.put("Fields", "*");
            sqlContentValue.put("Tables", "Push");
            sqlContentValue.put("Where", condition);
            sqlContentValue.put("OrderBy", orderBy);
            sqlContentValue.put("NowPage", page);
            sqlContentValue.put("PageSize", pageSize);

            resultList = db.list(sqlContentValue);
            db.closeDB();
        }

        return resultList;
    }
}
