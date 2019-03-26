package doublesoft.android.stu.inc;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import doublesoft.android.stu.imageloader.ImageLoader;

public class MyFunction {
    //公司参数 登录和启动的时候会重置
    public static String mainApiUrl = "http://stu.doublesoft.cn/";
    public static String lessApiUrl = "http://stu.doublesoft.cn/";

    //平台参数
    public final static String appKind = "DoubleSoftAndroid";
    public final static float version = 1.0f;
    public final static int sdkVersion = android.os.Build.VERSION.SDK_INT;

    public static final String[] allActivityPaths = new String[]{"doublesoft.android.stu.deal", "doublesoft.android.stu.demo", "doublesoft.android.stu.index", "doublesoft.android.stu.work", "doublesoft.android.stu.user", "doublesoft.android.stu.meeting"};// 用于查找完整的路径
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};
    public static ImageLoader loader = null;
    public static Vibrator shaker;
    public static final ContentValues userDic = new ContentValues();// 登录用户信息
    public static HashMap<String, List<ContentValues>> articleListDic = new HashMap<String, List<ContentValues>>();// 文章列表
    public static HashMap<String, ContentValues> articleLoadDic = new HashMap<String, ContentValues>();// 文章加载情况
    public static int screenWidth = 0;// 屏幕宽度 程序启动时初始化 dip
    public static int screenHeight = 0;// 屏幕高度
    public static double latitude = 0;
    public static double longitude = 0;

    /**
     * 将json下包含的json展开再填进来
     */
    public static ContentValues jsonObjectToContentValuesFull(String jsonStr) {
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator<?> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                if (value.charAt(0) == "{".charAt(0) && value.charAt(value.length() - 1) == "}".charAt(0)) {
                    contentValues.putAll(jsonObjectToContentValuesFull(value));
                } else {
                    contentValues.put(key, value);
                }
            }
            return contentValues;
        } catch (Exception e) {
            e.printStackTrace();
            return contentValues;
        }
    }

    public static ArrayList<String> jsonArrStrToList(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<String> strList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                strList.add(jsonArray.getString(i));
            }
            return strList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static int strToint(String str, int defaultInt) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultInt;
        }
    }

    public static float strTofloat(String str, float defaultFloat) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return defaultFloat;
        }
    }

    // Uri To Path
    public static String uriToPath(Context context, Uri uri) {
        if (uri == null)
            return null;
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals(ContentResolver.SCHEME_FILE))
            return uri.getPath();
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        return null;
    }

    public static void logError(Exception e) {
        Log.e("Error", Log.getStackTraceString(e));
    }

    public boolean isNullObject(Object object) {
        boolean result = false;
        try {
            if (object == null) {
                result = true;
            } else if (object instanceof ContentValues) {
                result = ((ContentValues) object).size() == 0;
            } else if (object instanceof String) {
                if (((String) object).length() == 0) {
                    return true;
                }
                if (object.equals("")) {
                    return true;
                }
                try {
                    result = Double.valueOf(object.toString()) == 0;
                } catch (Exception ignored) {

                }
            } else if (object instanceof Integer) {
                result = (Integer) object == 0;
            } else if (object instanceof Double) {
                result = (Double) object == 0;
            }
        } catch (Exception e) {
            logError(e);
        }

        return result;
    }

    // 处理Mark 替换<br>
    public String getMarkText(String mark) {
        try {
            List<String> markList = new ArrayList<>();
            String[] markArr = mark.split("<br>");

            for (String aMarkArr : markArr) {
                if (aMarkArr.length() > 0) {
                    markList.add(aMarkArr);
                }
            }

            return this.joinArr(markList, "\n");
        } catch (Exception e) {
            logError(e);
        }

        return "";
    }

    // 获取缓存对象
    public ImageLoader getImageLoader(Context context) {
        if (loader == null) {
            loader = new ImageLoader(context);
        }
        return loader;
    }

    // 获取缓存大小
    public long getImageCacheSize() {
        return loader.size();
    }

    // 清除缓存
    public void clearImageCache() {
        loader.clearCache();
    }

    // 判断是否存在SD卡
    public boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    // 强制转int
    public int intValue(String str) {
        int value = 0;
        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
            logError(e);
        }

        return value;
    }

    // 根据tag查找
    public View findViewByTag(String tag, View view) {
        if (view != null && tag != null) {
            String targetTag = null;
            try {
                targetTag = view.getTag().toString();
            } catch (Exception ignored) {

            }

            if (targetTag != null && targetTag.equals(tag)) {
                return view;
            }
        }

        ViewGroup viewGroup = null;
        try {
            viewGroup = (ViewGroup) view;
        } catch (Exception e) {
            logError(e);
        }

        if (viewGroup != null) {
            if (viewGroup.getChildCount() > 0) {
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View sonView = viewGroup.getChildAt(i);
                    View targetView = findViewByTag(tag, sonView);
                    if (targetView != null) {
                        return targetView;
                    }
                }
            }
        }

        return null;
    }

    // 得到使用该paint写上text的时候,像素为多少
    public float getTextViewLength(TextView textView, String text) {
        TextPaint paint = textView.getPaint();
        return paint.measureText(text);
    }

    // 颜色转16进制
    public String hexFromIntColor(int intColor) {
        return String.format("#%06X", 0xFFFFFF & intColor);
    }

    // 是否启用WIFI
    public Boolean isEnableWIFI(Context mContext) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            logError(e);
        }

        return false;
    }

    // 是否启用网络
    public Boolean isEnableInternet(Context mContext) {
        try {
            ConnectivityManager manger = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            assert manger != null;
            NetworkInfo activeNetInfo = manger.getActiveNetworkInfo();
            return (activeNetInfo != null && activeNetInfo.isConnected());
        } catch (Exception e) {
            logError(e);
        }

        return false;
    }

    // ContentValues JSONObject转换
    public JSONObject contentValuesToJSONObject(ContentValues contentValues) {
        JSONObject jsonObject = new JSONObject();
        try {
            Set<Entry<String, Object>> valueSet = contentValues.valueSet();
            for (Entry<String, Object> row : valueSet) {
                String key = row.getKey();
                Object value = row.getValue();
                jsonObject.put(key, value);
            }
        } catch (Exception e) {
            logError(e);
        }
        return jsonObject;
    }

    public ContentValues jsonObjectToContentValues(JSONObject jsonObject) {
        ContentValues contentValues = new ContentValues();
        try {
            Iterator<?> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                contentValues.put(key, value);
            }
        } catch (Exception e) {
            logError(e);
        }

        return contentValues;
    }

    public ContentValues jsonObjectToContentValues(String json) {
        ContentValues contentValues = new ContentValues();
        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<?> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = jsonObject.getString(key);
                contentValues.put(key, value);
            }
            return contentValues;
        } catch (Exception e) {
            logError(e);
            return null;
        }
    }

    // hashMap转Json 支持List<String>,List<ContentValues>,String的对象集合
    public JSONObject hashMapToJSONObject(HashMap<?, ?> map) {

        JSONObject jsonObject = new JSONObject();
        for (Entry<?, ?> entry : map.entrySet()) {

            try {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                if (value.getClass().equals(ArrayList.class)) {
                    jsonObject.put(key, listToJSONArr((List<?>) value));
                } else {
                    jsonObject.put(key, value);
                }

            } catch (Exception e2) {
                logError(e2);
            }
        }
        return jsonObject;
    }

    public JSONArray listToJSONArr(List<?> list) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getClass().equals(ContentValues.class)) {
                jsonArray.put(contentValuesToJSONObject((ContentValues) list.get(i)));
            } else {
                jsonArray.put(list.get(i));
            }

        }
        return jsonArray;
    }

    public List<Object> jsonArrToList(JSONArray jsonArray) {
        List<Object> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.get(i).getClass().equals(JSONObject.class)) {
                    list.add(jsonObjectToContentValues(jsonArray.getJSONObject(i)));
                } else {
                    list.add(jsonArray.get(i));
                }
            }
        } catch (Exception e) {
            logError(e);
        }

        return list;
    }

    public List<ContentValues> jsonArrToListContentValues(JSONArray jsonArray) {
        List<ContentValues> list = new ArrayList<>();
        try {
            List<Object> resultList = jsonArrToList(jsonArray);
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getClass().equals(ContentValues.class)) {
                    list.add((ContentValues) resultList.get(i));
                }
            }
        } catch (Exception e) {
            logError(e);
        }

        return list;
    }

    // 数组转List
    public List<String> strArrToList(String[] arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

    // 查找类
    public Class<?> findClass(String shortName) {
        for (String allActivityPath : allActivityPaths) {
            try {
                String className = allActivityPath + "." + shortName;
                return Class.forName(className);
            } catch (Exception ignored) {

            }
        }

        return null;
    }

    // 根据名字获取资源ID
    public int getResID(Context context, String folderName, String fileName) {
        try {
            ApplicationInfo appInfo = context.getApplicationInfo();
            return context.getResources().getIdentifier(fileName, folderName, appInfo.packageName);
        } catch (Exception e) {
            logError(e);
        }

        return 0;
    }

    // 获取控件列表
    public List<View> findViewInLayout(View view, Class<?> viewClass) {
        List<View> list = new ArrayList<>();
        try {

            if (view.getClass().equals(LinearLayout.class)) {
                LinearLayout layout = (LinearLayout) view;
                int viewCount = layout.getChildCount();
                for (int i = 0; i < viewCount; i++) {
                    View sonView = layout.getChildAt(i);
                    if (sonView.getClass().equals(viewClass)) {
                        list.add(sonView);
                    } else {
                        List<View> sonList = findViewInLayout(sonView, viewClass);
                        list.addAll(sonList);
                    }
                }
            } else if (view.getClass().equals(RelativeLayout.class)) {
                RelativeLayout layout = (RelativeLayout) view;
                int viewCount = layout.getChildCount();
                for (int i = 0; i < viewCount; i++) {
                    View sonView = layout.getChildAt(i);
                    if (sonView.getClass().equals(viewClass)) {
                        list.add(sonView);
                    } else {
                        List<View> sonList = findViewInLayout(sonView, viewClass);
                        list.addAll(sonList);
                    }
                }
            }
        } catch (Exception e) {
            logError(e);
        }

        return list;
    }

    // 获取Button列表
    public List<Button> findButtonInLayout(View view) {
        List<Button> list = new ArrayList<>();
        try {
            List<View> findList = findViewInLayout(view, Button.class);
            for (int i = 0; i < findList.size(); i++) {
                list.add((Button) findList.get(i));
            }
        } catch (Exception e) {
            logError(e);
        }

        return list;
    }

    // 复制到剪贴板
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public boolean setClipboardText(Context context, String title, String text) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(title, text);
                assert cmb != null;
                cmb.setPrimaryClip(clip);
            } else {
                android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                assert cmb != null;
                cmb.setText(text);
            }
            return true;
        } catch (Exception e) {
            logError(e);
        }

        return false;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public String getClipboardText(Context context) {
        try {
            if (MyFunction.sdkVersion >= 11) {
                android.content.ClipboardManager cmb = (android.content.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                return cmb.getText().toString();
            } else {
                android.text.ClipboardManager cmb = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                return cmb.getText().toString();
            }
        } catch (Exception e) {
            logError(e);
        }

        return null;
    }

    // px 与 dip 转换
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 读asset模版内容
    public String getAsset(Context context, String filePath) {
        String res = "";
        try {
            InputStream in = context.getResources().getAssets().open(filePath);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            res = new String(buffer, "UTF-8");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    // InputStream 转 String
    public String StreamToString(InputStream is) {
        return StreamToString(is, "GBK");
    }

    public String StreamToString(InputStream is, String charSet) {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new InputStreamReader(is, charSet), 10 * 1024);
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert buffer != null;
                buffer.close();
            } catch (Exception e) {
                logError(e);
            }
        }
        return sb.toString();
    }

    // byte 转 String
    public String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(HEX_DIGITS[(aB & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[aB & 0x0f]);
        }
        return sb.toString();
    }

    // 连接字符串前后
    public String cString(Object pre, Object end) {
        return String.valueOf(pre) +
                end;
    }

    public String cString(Object pre, Object str, Object end) {
        return String.valueOf(pre) +
                str +
                end;
    }

    // 求数组A与B的重复个数
    public int equalCount(List<String> arrA, List<String> arrB) {
        int tempCount = 0;
        for (int i = 0; i < arrA.size(); i++) {
            for (int j = 0; j < arrB.size(); j++) {
                if (arrA.get(i).equals(arrB.get(j))) {
                    tempCount++;
                }
            }
        }

        return tempCount;
    }

    // 字符串转换成数组
    public List<String> getStrArr(String str) {
        return this.getStrArr(str, 1);
    }

    public List<String> getStrArr(String str, int len) {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < str.length(); i += len) {
            arr.add(mid(str, i, len));
        }

        return arr;
    }

    // 获得排序字符串
    public String getSortStr(String str) {
        return this.getSortStr(str, 1);
    }

    public String getSortStr(String str, int len) {
        List<String> arr = this.getStrArr(str, len);
        return this.joinArr(this.sortNumArr(arr));
    }

    // 秒转时间
    public String secondsTimeText(int seconds) {
        int s = seconds % 60;
        int m = (seconds / 60) % 60;
        int h = seconds / 3600;

        return formatNumTo2W(h) + ":" + formatNumTo2W(m) + ":" + formatNumTo2W(s);
    }

    // 格式化大数字
    public String largeNumText(String numStr) {
        Double numDouble = Double.parseDouble(numStr);
        return largeNumText(numDouble.longValue());
    }

    public String largeNumText(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        } else {
            return String.format(Locale.getDefault(), "%.2f万", num * 1.0 / 10000);
        }
        // if (num < 100) {
        // return String.valueOf(num);
        // } else if (num < 1000) {
        // return String.format(Locale.getDefault(), "%.1f百", num * 1.0 / 100);
        // } else if (num < 10000) {
        // return String.format(Locale.getDefault(), "%.1f千", num * 1.0 / 1000);
        // } else if (num < 100000) {
        // return String.format(Locale.getDefault(), "%.2f万", num * 1.0 / 10000);
        // } else if (num < 1000000) {
        // return String.format(Locale.getDefault(), "%.1f十万", num * 1.0 / 100000);
        // } else if (num < 10000000) {
        // return String.format(Locale.getDefault(), "%.1f百万", num * 1.0 / 1000000);
        // } else if (num < 100000000) {
        // return String.format(Locale.getDefault(), "%.1f千万", num * 1.0 / 10000000);
        // } else {
        // return String.format(Locale.getDefault(), "%.1f亿", num * 1.0 / 100000000);
        // }
    }

    // 是否闰年
    public boolean isRN(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // 字符串中 某一年字符 的个数
    public int countStr(String str1, String str2) {
        if (!str1.contains(str2)) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < str1.length(); i++) {
                String s = String.valueOf(str1.charAt(i)); // char 类型转String
                if (s.equals(str2)) {
                    count++;
                }
            }
            return count;
        }
    }

    // 还原HTML实体
    public String transHtmlCode(String str) {
        if (str == null) {
            return "";
        }

        String fromStr = str;
        fromStr = fromStr.replace("&lt;", "<");
        fromStr = fromStr.replace("&gt;", ">");
        fromStr = fromStr.replace("&amp;", "&");
        fromStr = fromStr.replace("&quot;", "\"");
        fromStr = fromStr.replace("&nbsp;", " ");

        return fromStr;
    }

    // 格式化缓存大小
    public String formatSizeText(long size) {
        DecimalFormat df1 = new DecimalFormat("0.00");
        if (size < 1024) {
            return df1.format(size) + "B";
        }

        if (size < 1024 * 1024) {
            return df1.format(size / 1024.) + "K";
        }

        if (size < 1024 * 1024 * 1024) {
            return df1.format(size / 1024. / 1024.) + "M";
        }

        return df1.format(size / 1024. / 1024. / 1024.) + "G";

    }

    // 格式化金额，保留两位小数
    public String formatMoneyText(String numStr) {
        Double numDouble = Double.parseDouble(numStr);
        return formatMoneyText(numDouble);
    }

    public String formatMoneyText(Double num) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
        format.setMaximumFractionDigits(2);

        return format.format(num).replace("￥", "").replaceAll("0+?$", "").replaceAll("[.]$", "");
    }

    // 去掉金额小数点后面的0
    public String removeMoneyZero(String num) {
        Double numDouble = Double.parseDouble(num);
        return removeMoneyZero(numDouble);
    }

    public String removeMoneyZero(Double num) {
        return num.toString().replaceAll("0+?$", "").replaceAll("[.]$", "");
    }

    // 获取MD5值
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            return (toHexString(messageDigest)).toLowerCase(Locale.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    // MD5转数字
    public int md5ToNum(String md5Str) {
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int num = 0;
        for (int i = 0; i < md5Str.length(); i++) {
            String w = md5Str.substring(i, i + 1);
            int r = str.indexOf(w);
            if (r > -1) {
                num += r;
            }
        }

        return num;
    }

    // MD5转数字串
    public String md5ToNumStr(String md5Str) {
        String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < md5Str.length(); i++) {
            String w = md5Str.substring(i, i + 1);
            int r = str.indexOf(w);
            if (r > -1) {
                sb.append(r % 10);
            }
        }

        return sb.toString();
    }

    // Mid函数
    public String mid(String str, int fromIndex, int length) {
        return str.substring(fromIndex, Math.min(fromIndex + length, str.length()));
    }

    // 排序数组
    public List<String> sortNumArr(List<String> numArr) {
        String[] sortNumArr = new String[numArr.size()];
        numArr.toArray(sortNumArr);

        Arrays.sort(sortNumArr, new Comparator<String>() {
            @Override
            public int compare(String n1, String n2) {
                // Sort by date
                return Integer.valueOf(n1).compareTo(Integer.valueOf(n2));
            }
        });

        return new ArrayList<String>(Arrays.asList(sortNumArr));
    }

    public List<String> rsortNumArr(List<String> numArr) {
        String[] sortNumArr = new String[numArr.size()];
        numArr.toArray(sortNumArr);

        Arrays.sort(sortNumArr, new Comparator<String>() {
            @Override
            public int compare(String n1, String n2) {
                // Sort by date
                return Integer.valueOf(n1).compareTo(Integer.valueOf(n2));
            }
        });

        List<String> returnList = new ArrayList<String>();
        for (int i = sortNumArr.length - 1; i >= 0; i--) {
            returnList.add(sortNumArr[i]);
        }

        return returnList;
    }

    // 将号码格式化为两位
    public String formatNumTo2W(int num) {
        StringBuilder sb = new StringBuilder();
        if (num < 10) {
            sb.append("0");
        }
        sb.append(num);

        return sb.toString();
    }

    public String formatNumToLength(String num, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= length - num.length(); i++) {
            sb.append("0");
        }
        sb.append(num);

        return sb.toString();
    }

    // 获取系统时间
    public ContentValues getNowTime() {
        ContentValues returnValue = new ContentValues();
        Time mTime = new Time();// "GMT+8"
        mTime.setToNow(); // 取得系统时间。
        returnValue.put("weekday", mTime.weekDay);
        returnValue.put("year", mTime.year);
        returnValue.put("month", mTime.month + 1);
        returnValue.put("day", mTime.monthDay);
        returnValue.put("hour", mTime.hour);
        returnValue.put("minute", mTime.minute);
        returnValue.put("second", mTime.second);
        return returnValue;
    }

    // 字符串转日期
    public Date strToTime(String timeStr) {
        return strToTime(timeStr, "yyyy-MM-dd HH:mm:ss");
    }

    public Date strToTime(String timeStr, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return dateFormat.parse(timeStr);
        } catch (Exception e) {
            logError(e);
        }

        return null;
    }

    // 获取当前时间戳
    public String getNowTimeStr() {
        long nowTime = System.currentTimeMillis();// long now =
        // android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date d1 = new Date(nowTime);
        return format.format(d1);
    }

    public String getDateStr() {
        return getDateStr("yyyy-MM-dd", new Date());
    }

    public String getDateStr(String format) {
        return getDateStr(format, new Date());
    }

    public String getDateStr(String format, Date date) {
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.getDefault());
        return f.format(date);
    }

    public String timeToDate(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.format(sdf.parse(timeStr));
        } catch (Exception e) {
            logError(e);
        }

        return "";
    }

    // 获取时间差(精确到秒)
    public long getStringTimeDiff(String timeFrom, String timeTo) {
        try {
            SimpleDateFormat fromDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date fromDate = fromDateFormat.parse(timeFrom);
            Date toDate = toDateFormat.parse(timeTo);

            return toDate.getTime() / 1000 - fromDate.getTime() / 1000;
        } catch (Exception e) {
            logError(e);
        }

        return 0;
    }

    // bitmap转为base64
    public String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // base64转为bitmap
    public Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // 连接数组为字符串
    public String joinArr(List<String> arr) {
        return this.joinArr(arr, "");
    }

    public String joinArr(List<String> arr, String split) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            sb.append(arr.get(i));
            if (i < arr.size() - 1) {
                sb.append(split);
            }
        }

        return sb.toString();
    }

    // 判断是否为数字
    public boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        Pattern pattern = Pattern.compile("-?[0-9]*.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    // Drawable → Bitmap
    public Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        // canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    // bitmap 缩放
    public Bitmap getZoomImage(Bitmap orgBitmap, double newWidth, double newHeight) {
        if (null == orgBitmap) {
            return null;
        }
        if (orgBitmap.isRecycled()) {
            return null;
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null;
        }

        // 获取图片的宽和高
        float width = orgBitmap.getWidth();
        float height = orgBitmap.getHeight();
        // 创建操作图片的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(orgBitmap, 0, 0, (int) width, (int) height, matrix, true);
    }

    // 替换颜色
    public Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
        // 相关说明可参考 http://xys289187120.blog.51cto.com/3361352/657590/
        Bitmap mBitmap = oldBitmap.copy(Config.ARGB_8888, true);
        // 循环获得bitmap所有像素点
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        for (int i = 0; i < mBitmapHeight; i++) {
            for (int j = 0; j < mBitmapWidth; j++) {
                // 获得Bitmap 图片中每一个点的color颜色值
                // 将需要填充的颜色值如果不是
                // 在这说明一下 如果color 是全透明 或者全黑 返回值为 0
                // getPixel()不带透明通道 getPixel32()才带透明部分 所以全透明是0x00000000
                // 而不透明黑色是0xFF000000 如果不计算透明部分就都是0了
                int color = mBitmap.getPixel(j, i);
                // 将颜色值存在一个数组中 方便后面修改
                if (color != Color.TRANSPARENT) {// 跳过透明颜色
                    if (color == oldColor || oldColor == -1) {
                        mBitmap.setPixel(j, i, newColor); // 将白色替换成透明色
                    }
                }
            }
        }
        return mBitmap;
    }

    // 从Uri获取图片
    public Bitmap getBitmapFromUri(Context context, Uri uri, boolean adjustOritation) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            if (adjustOritation) {
                int digree = 0;
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(uriToPath(context, uri));
                } catch (IOException ignored) {
                }

                if (exif != null) {
                    // 读取图片中相机方向信息
                    int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                    // 计算旋转角度
                    switch (ori) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            digree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            digree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            digree = 270;
                            break;
                        default:
                            digree = 0;
                            break;
                    }
                }

                if (digree != 0) {
                    // 旋转图片
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                }
            }

            return bitmap;
        } catch (Exception e) {
            logError(e);
            return null;
        }
    }

    // 缩放并压缩图片
    public Bitmap compress(Bitmap image) {
        return compress(image, 300);// 默认300KB
    }

    public Bitmap compress(Bitmap image, int max_kb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float ww = 640f;// 这里设置宽度为480f
        float hh = 960f;// 这里设置高度为800f

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }

        if (be <= 0)
            be = 1;

        newOpts.inSampleSize = be;// 设置缩放比例

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        return compressImage(bitmap, max_kb);// 压缩好比例大小后再进行质量压缩
    }

    public Bitmap compressImage(Bitmap image) {
        return compressImage(image, 300);
    }

    public Bitmap compressImage(Bitmap image, int max_kb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int options = 100;
        while (baos.toByteArray().length / 1024 > max_kb) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中

        return BitmapFactory.decodeStream(isBm, null, null);
    }

    // 二维码生成
    public Bitmap qrEncode(Context mContex, String text, int widthDip, int heightDip) {

        Bitmap bitmap = null;
        try {
            if (text != null && text.length() > 0) {
                // 把输入的文本转为二维码
                int QR_WIDTH = dip2px(mContex, widthDip);
                int QR_HEIGHT = dip2px(mContex, heightDip);

                Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                hints.put(EncodeHintType.MARGIN, 0);

                BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT,
                        hints);
                bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < QR_HEIGHT; i++) {
                    for (int j = 0; j < QR_WIDTH; j++) {
                        bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
            }
        } catch (WriterException e) {
            logError(e);
        }

        return bitmap;
    }

    // 二维码图片添加LOGO
    public Bitmap addLogo(Bitmap qrBitmap, Bitmap logoBitmap) {
        int qrBitmapWidth = qrBitmap.getWidth();
        int qrBitmapHeight = qrBitmap.getHeight();
        int logoBitmapWidth = logoBitmap.getWidth();
        int logoBitmapHeight = logoBitmap.getHeight();

        Bitmap blankBitmap = Bitmap.createBitmap(qrBitmapWidth, qrBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blankBitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(logoBitmap, (qrBitmapWidth - logoBitmapWidth) / 2, (qrBitmapHeight - logoBitmapHeight) / 2,
                null);
        canvas.restore();

        return blankBitmap;
    }

    // 生成随机串
    public String randomKeys(int length) {
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        String pattern = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLOMNOPQRSTUVWXYZ";

        for (int i = 0; i < length; i++) {
            stringBuffer.append(pattern.charAt(random.nextInt(35)));
        }

        return stringBuffer.toString();
    }

    // 屏幕宽度(dip)
    public int screenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return this.px2dip(context, dm.widthPixels);
    }

    // 屏幕高度(dip)
    public int screenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return this.px2dip(context, dm.heightPixels);
    }

    // 布局margin
    public void margin(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public boolean checkRight(String modelName, Integer right) {
        synchronized (userDic) {
            boolean result = true;
            try {
                JSONObject jsonObject = new JSONObject(userDic.getAsString("RightArr"));
                ContentValues rightArr = jsonObjectToContentValues(jsonObject);

                if (rightArr.size() == 0) {
                    result = false;
                } else {
                    JSONObject modelRightObject = new JSONObject(rightArr.getAsString(modelName));
                    ContentValues modelRightArr = jsonObjectToContentValues(modelRightObject);
                    if (modelRightArr.getAsInteger(right.toString()) == 0) {
                        result = false;
                    }
                }
            } catch (Exception e) {
                logError(e);
                result = false;
            }
            return result;
        }
    }

    // 秒转时间
    public String secondsTimeText(Integer seconds) {
        Integer s = seconds % 60;
        Integer m = (seconds / 60) % 60;
        Integer h = seconds / 3600;

        return h + ":" + m + ":" + s;
    }

    // 正常号转银行卡号 － 增加4位间的空格
    public String normalNumToBankNum(String normalNum) {
        // 去除空格
        normalNum = bankNumToNormalNum(normalNum);
        Integer size = normalNum.length();

        return normalNum.substring(0, 4) + "        ****        ****        " + normalNum.substring(size - 4, size);
    }

    // 银行卡号转正常号 － 去除4位间的空格
    public String bankNumToNormalNum(String bankNum) {
        return bankNum.replace(" ", "");
    }

    // 正常号转银行卡号 － 增加4位间的空格
    public String normalNumsToBankNum(String normalNum) {
        StringBuilder str = new StringBuilder(normalNum.replace(" ", ""));

        int i = str.length() / 4;
        int j = str.length() % 4;

        for (int x = (j == 0 ? i - 1 : i); x > 0; x--) {
            str = str.insert(x * 4, "  ");
        }
        return str.toString();
    }

    //隐藏电话号码中间部分
    public String hideMobile(String mobile) {
        if (mobile.length() == 11) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mobile.length(); i++) {
                char c = mobile.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        return mobile;
    }
}
