package doublesoft.android.stu.mode;

import android.content.ContentValues;
import android.content.Context;

import doublesoft.android.stu.inc.MyFunction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-07-05.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class APIRequest {
    private MyFunction myFunc = new MyFunction();
    private Context myContext;
    private Object callObject;
    private DBCache dbCache;
    private OkHttpClient client;

    // 构造函数
    public APIRequest(Context context) {
        myContext = context;
        callObject = myContext;
        dbCache = new DBCache(myContext);
        client = new OkHttpClient();
    }

    // 指定回传调用对象
    public APIRequest(Context context, Object object) {
        myContext = context;
        callObject = object;
        dbCache = new DBCache(myContext);
        client = new OkHttpClient();
    }

    // GET请求
    public void get(ContentValues requestDataDic, String methodName) {
        this.get(requestDataDic, methodName, true);
    }

    public void get(ContentValues requestDataDic, final String methodName, final Boolean isMainUrl) {
        try {
            StringBuilder urlString = new StringBuilder();
            if (isMainUrl) {
                urlString.append(requestDataDic.get("MainUrl"));
                urlString.append(requestDataDic.get("ScriptPath"));
                urlString.append("?");
            } else {
                urlString.append(requestDataDic.get("LessUrl"));
                urlString.append(requestDataDic.get("ScriptPath"));
                urlString.append("?");
            }

            // 加上版本号及APP类型 及设备编码
            if (!requestDataDic.containsKey("Version")) {
                requestDataDic.put("Version", String.format(Locale.getDefault(), "%.1f", MyFunction.version));
            }

            if (!requestDataDic.containsKey("AppKind")) {
                requestDataDic.put("AppKind", MyFunction.appKind);
            }

            // 构造请求参数
            StringBuilder urlData = new StringBuilder();
            Set<Entry<String, Object>> requestValueSet = requestDataDic.valueSet();
            for (Entry<String, Object> rowRequest : requestValueSet) {
                String key = rowRequest.getKey();
                String value = rowRequest.getValue().toString();

                if (!key.equals("MainUrl") && !key.equals("LessUrl") && !key.equals("Delegate")
                        && !key.equals("ScriptPath") && !key.equals("RequestUrl") && !key.equals("IsOpenCache")) {
                    if (urlData.length() > 0) {
                        urlData.append("&");
                    }
                    urlData.append(key);
                    urlData.append("=");
                    urlData.append(value);
                }
            }

            // 默认加上版本号及APP类型
            urlString.append(urlData);

            String url = urlString.toString();

            requestDataDic.put("RequestUrl", url);
            requestDataDic.put("IsMainUrl", isMainUrl ? 1 : 0);

            Request request = new Request.Builder().get().url(url).tag(requestDataDic).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                // 失败
                @Override
                public void onFailure(Call call, IOException e) {
                    try {
                        ContentValues requestDataDic = (ContentValues) call.request().tag();

                        if (requestDataDic.getAsInteger("IsMainUrl") == 1 && requestDataDic.containsKey("LessUrl")) {
                            get(requestDataDic, methodName, false);
                        } else {// 返回通知，失败与否还得判断
                            returnNotify(requestDataDic, methodName, "");
                        }
                    } catch (Exception ignored) {
                    }
                }

                // 成功
                @Override
                public void onResponse(Call call, final Response response) {
                    try {
                        assert response.body() != null;
                        final String returnText = response.body().string();
                        ContentValues requestDataDic = (ContentValues) call.request().tag();

                        if ((returnText == null || !returnText.contains("Result"))
                                && requestDataDic.getAsInteger("IsMainUrl") == 1
                                && requestDataDic.containsKey("LessUrl")) {
                            get(requestDataDic, methodName, false);
                        } else {// 返回通知，失败与否还得判断
                            returnNotify(requestDataDic, methodName, returnText);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("get Exception:" + e);
        }
    }

    // POST请求
    public void post(ContentValues requestDataDic, final String methodName) {
        this.post(requestDataDic, methodName, true);
    }

    public void post(ContentValues requestDataDic, final String methodName, final Boolean isMainUrl) {
        try {
            StringBuilder urlString = new StringBuilder();
            if (isMainUrl) {
                urlString.append(requestDataDic.get("MainUrl"));
                urlString.append(requestDataDic.get("ScriptPath"));
            } else {
                urlString.append(requestDataDic.get("LessUrl"));
                urlString.append(requestDataDic.get("ScriptPath"));
            }

            String url = urlString.toString();

            // 加上版本号及APP类型
            if (!requestDataDic.containsKey("Version")) {
                requestDataDic.put("Version", String.format(Locale.getDefault(), "%.1f", MyFunction.version));
            }

            if (!requestDataDic.containsKey("AppKind")) {
                requestDataDic.put("AppKind", MyFunction.appKind);
            }

            requestDataDic.put("RequestUrl", url);
            requestDataDic.put("IsMainUrl", isMainUrl ? 1 : 0);

            FormBody.Builder formBuilder = new FormBody.Builder();

            // 添加POST数据
            Set<Entry<String, Object>> requestValueSet = requestDataDic.valueSet();
            for (Entry<String, Object> rowRequest : requestValueSet) {
                if (rowRequest.getValue() != null) {
                    String key = rowRequest.getKey();
                    String value = rowRequest.getValue().toString();

                    if (!key.equals("MainUrl") && !key.equals("LessUrl") && !key.equals("Delegate")
                            && !key.equals("ScriptPath") && !key.equals("RequestUrl") && !key.equals("IsOpenCache")) {
                        formBuilder.add(key, value);
                    }
                }
            }

            Request request = new Request.Builder().url(url).post(formBuilder.build()).tag(requestDataDic).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                // 失败
                @Override
                public void onFailure(Call call, IOException e) {
                    try {
                        ContentValues requestDataDic = (ContentValues) call.request().tag();

                        if (requestDataDic.getAsInteger("IsMainUrl") == 1 && requestDataDic.containsKey("LessUrl")) {
                            post(requestDataDic, methodName, false);
                        } else {// 返回通知，失败与否还得判断
                            returnNotify(requestDataDic, methodName, "");
                        }
                    } catch (Exception ignored) {
                    }
                }

                // 成功
                @Override
                public void onResponse(Call call, final Response response) {
                    try {
                        assert response.body() != null;
                        final String returnText = response.body().string();
                        ContentValues requestDataDic = (ContentValues) call.request().tag();

                        if ((returnText == null || !returnText.contains("Result"))
                                && requestDataDic.getAsInteger("IsMainUrl") == 1
                                && requestDataDic.containsKey("LessUrl")) {
                            post(requestDataDic, methodName, false);
                        } else {// 返回通知，失败与否还得判断
                            returnNotify(requestDataDic, methodName, returnText);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("post Exception:" + e);
        }
    }

    // 返回通知
    public void returnNotify(ContentValues requestDataDic, String methodName, String returnText) {
        try {
            if (methodName != null) {
                requestDataDic.put("IsFromCache", 0);// 返回标志 是否来源于缓存

                // 缓存设置
                if (requestDataDic.containsKey("IsOpenCache") && requestDataDic.getAsInteger("IsOpenCache") == 1) {
                    String requestData = requestDataDic.getAsString("RequestUrl")
                            .replace(requestDataDic.getAsString("MainUrl"), "")
                            .replace(requestDataDic.getAsString("LessUrl"), "");
                    String cacheKey = "APIRequest_" + myFunc.md5(requestDataDic.getAsString("MainUrl")
                            + requestDataDic.getAsString("LessUrl") + requestData);

                    // 写入缓存
                    if (returnText.length() != 0) {
                        dbCache.set(cacheKey, returnText);
                    } else {
                        // 读取缓存
                        String cacheValue = dbCache.get(cacheKey);
                        if (cacheValue != null) {
                            requestDataDic.put("IsFromCache", 1);// 设置来源于缓存
                            returnText = cacheValue;
                        }
                    }
                }

                // 请求回调
                Method sel = callObject.getClass().getMethod(methodName, ContentValues.class, String.class);
                sel.invoke(callObject, requestDataDic, returnText);
            }

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (IllegalArgumentException e) {

        } catch (InvocationTargetException e) {

        } catch (Exception e) {
            System.out.println("APIRequeset returnNotify:" + e);
        }
    }
}
