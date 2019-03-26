package doublesoft.android.stu.mode;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myui.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApp {
	private Context myContext;
	private APIRequest apiRequest;
	private MyHandler myHandler;
	private long preCheckVersionTime = 0;

	public UpdateApp(Context context) {
		myContext = context;
		apiRequest = new APIRequest(myContext, this);
		myHandler = new MyHandler();
	}

	// 自动检测 用于首页自动检测，1分钟检测一次
	public void autoCheckUpdate() {
		long nowTime = System.currentTimeMillis() / 1000;
		if ((nowTime - preCheckVersionTime) > 60) {
			checkUpdate(false);
		}
	}

	// 检测版本 用于会员中心用户点击更新时调用，checkUpdate(true);
	public void checkUpdate(boolean isShowAlert) {// 为true时，只有当检测到新版本才弹出提示
		try {
			preCheckVersionTime = System.currentTimeMillis() / 1000;
			ContentValues requestDataDic = new ContentValues();
			requestDataDic.put("MainUrl", MyFunction.mainApiUrl);
			requestDataDic.put("LessUrl", MyFunction.lessApiUrl);
			requestDataDic.put("IsShowAlert", isShowAlert);
			requestDataDic.put("ScriptPath", "api/VersionCheck.php");
			apiRequest.post(requestDataDic, "checkUpdateCall");
		} catch (Exception e) {

		}
	}

	public void checkUpdateCall(final ContentValues requestDataDic, final String returnText) {
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				if (returnText.length() != 0) {
					try {
						final JSONObject jsonObject = new JSONObject(returnText);
						if (jsonObject.has("Result")) {
							if (jsonObject.getInt("Result") == 1) {
								if ((float) jsonObject.getDouble("AndroidVersion") > MyFunction.version) {
									if (jsonObject.getInt("AndroidForce") == 1) {// 强制升级无需提示
										preCheckVersionTime = 0;
										try {
											downLoadAPK(jsonObject.getString("AndroidUrl"));
										} catch (JSONException e) {
											e.printStackTrace();
										}
									} else {// 非强制升级，提示用户选择
										new MyDialog.Builder(myContext).setTitle("版本升级提示")
												.setMessage(jsonObject.getString("AndroidMessage"))
												.setNegativeButton("取消升级", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														dialog.dismiss();
													}
												}).setPositiveButton("开始升级", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {

														dialog.dismiss();
														try {
															downLoadAPK(jsonObject.getString("AndroidUrl"));
														} catch (JSONException e) {
															e.printStackTrace();
														}

													}
												}).create().show();
									}

								} else {// 未发现新版本
									if (requestDataDic.getAsBoolean("IsShowAlert")) {
										new MyDialog.Builder(myContext).setTitle("操作提示").setMessage("当前已是最新版本，无需更新！")
												.setPositiveButton("确定", new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {

														dialog.dismiss();
													}
												}).create().show();
									}
								}
							}
						}
					} catch (JSONException e) {

					}
				} else {
					if (requestDataDic.getAsBoolean("IsShowAlert")) {
						new MyDialog.Builder(myContext).setTitle("操作提示").setMessage("网络连接错误，请检查！")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).create().show();
					}
				}
			}
		});

	}

	// 从服务器中下载APK
	protected void downLoadAPK(final String fromUrl) {
		final ProgressDialog pd = new ProgressDialog(myContext);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在升级 ...");
		pd.show();
		pd.setCancelable(false);

		new Thread() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(fromUrl, "update.apk", pd);
					sleep(3000);
					installAPK(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					new MyDialog.Builder(myContext).setTitle("下载出错").setMessage(e.toString())
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).create().show();
				}
			}
		}.start();
	}

	// 下载文件
	public static File getFileFromServer(String fromUrl, String saveFileName, ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(fromUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), saveFileName);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		}

		return null;
	}

	// 安装apk
	protected void installAPK(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		myContext.startActivity(intent);
	}
}
