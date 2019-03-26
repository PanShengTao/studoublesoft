package doublesoft.android.stu.demo;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class DemoAPIRequest extends MyFragment {
	private Button postBtn = null;
	private Button getBtn = null;
	private EditText resultEditText = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_apirequest, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("网络请求");
		setNavigationBackButton();

		// 获得前一个页面传递过来的数据
		// 此处get的变量类型 和 传递时 put的变量类型要完全一致
		Bundle bundle = getArguments();
		if (bundle.containsKey("TestInt")) {
			System.out.println("变量传递值 TestInt:" + bundle.getInt("TestInt"));
			// 如下错误的类型，是取不到值的
			System.out.println("变量传递值 TestInt（错误类型示范）:" + bundle.getString("TestInt"));
		}

		if (bundle.containsKey("TestString")) {
			System.out.println("变量传递值 TestString:" + bundle.getString("TestString"));
		}

		// 按钮绑定
		postBtn = (Button) findViewById(R.id.DemoPostBtn);
		postBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				postBtnClick();
			}
		});

		getBtn = (Button) findViewById(R.id.DemoGetBtn);
		getBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getBtnClick();
			}
		});

		resultEditText = (EditText) findViewById(R.id.DemoResultEditText);

		// 初始化数据
		if (isFirstStart) {

		}
	}

	// POST方式提交
	public void postBtnClick() {
		// 开起加载动画
		loadingView.startAnimation();

		// 提交请求
		ContentValues requestDataDic = new ContentValues();
		// 指定主Url
		requestDataDic.put("MainUrl", "http://192.168.50.4/");
		// 指定副Url(当主Url解析失败时候，请求副Url)
		requestDataDic.put("LessUrl", "http://192.168.50.4/");
		// 指定要请求的PHP路径
		requestDataDic.put("ScriptPath", "api/demo/APIRequest.php");

		// 其余键值对 用户自定义
		requestDataDic.put("Action", "List");
		requestDataDic.put("UserName", "双软科技");
		requestDataDic.put("PostKind", "POST");

		// post方式提交 数据少也可以使用get提交（请求是异步的）
		apiRequest.post(requestDataDic, "postCall");
	}

	public void postCall(final ContentValues requestDataDic, final String returnText) {
		// myHandler封装起来，是为了在主线程中跑。android中操作UI的改变，只能在主线程中
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					// 停止刷新动画
					loadingView.stopAnimation();

					// 打印请求参数
					System.out.println(requestDataDic);

					// 这个方法可以输出请求的得到的串，用于调试
					JSONObject returnText1 = new JSONObject(returnText);
					resultEditText.setText(returnText1.toString());

					// 请求结果
					if (returnText.length() != 0) {
						try {
							JSONObject resultDic = new JSONObject(returnText);
							if (resultDic.has("Result")) {
								if (resultDic.getInt("Result") == 1) {
									System.out.println(resultDic);// 输出结果
								} else if (resultDic.getInt("Result") == -1) {// -1 是指token验证失败，需要登录
									user.clearUserDic();
									user.checkLogin(fragmentManager);
								} else {// 0 直接显示错误提示
									dropHUD.showFailText(resultDic.getString("Message"));
								}
							}
						} catch (JSONException e) {

						}
					} else {// 如果请求得到结果长度为0，一般是网络连接错误
						dropHUD.showNetworkFail();
					}
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		});

	}

	// GET方式提交
	public void getBtnClick() {

		// 开起加载动画
		loadingView.startAnimation();

		// 提交请求
		ContentValues requestDataDic = new ContentValues();
		// 指定主Url
		requestDataDic.put("MainUrl", "http://stu.doublesoft.cn/");
		// 指定副Url(当主Url解析失败时候，请求副Url)
		requestDataDic.put("LessUrl", "http://stu.doublesoft.cn/");
		// 指定要请求的PHP路径
		requestDataDic.put("ScriptPath", "api/demo/APIRequest.php");

		// 其余键值对 用户自定义
		requestDataDic.put("Action", "List");
		requestDataDic.put("PostKind", "GET");

		// post方式提交 数据少也可以使用get提交（请求是异步的）
		apiRequest.get(requestDataDic, "getCall");
	}

	public void getCall(final ContentValues requestDataDic, final String returnText) {
		// myHandler封装起来，是为了在主线程中跑。android中操作UI的改变，只能在主线程中
		myHandler.post(new Runnable() {

			@Override
			public void run() {
				// 停止刷新动画
				loadingView.stopAnimation();

				// 打印请求参数
				System.out.println(requestDataDic);

				// 这个方法可以输出请求的得到的串，用于调试
				resultEditText.setText(returnText);

				// 请求结果
				if (returnText.length() != 0) {
					try {
						JSONObject resultDic = new JSONObject(returnText);
						if (resultDic.has("Result")) {
							if (resultDic.getInt("Result") == 1) {
								System.out.println(resultDic);// 输出结果
							} else if (resultDic.getInt("Result") == -1) {// -1 是指token验证失败，需要登录
								user.clearUserDic();
								user.checkLogin(fragmentManager);
							} else {// 0 直接显示错误提示
								dropHUD.showFailText(resultDic.getString("Message"));
							}
						}
					} catch (JSONException e) {

					}
				} else {// 如果请求得到结果长度为0，一般是网络连接错误
					dropHUD.showNetworkFail();
				}
			}
		});

	}
}
