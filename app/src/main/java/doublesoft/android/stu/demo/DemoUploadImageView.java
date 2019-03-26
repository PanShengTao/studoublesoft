package doublesoft.android.stu.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import doublesoft.android.stu.myview.UploadImageView;
import doublesoft.android.stu.myview.UploadImageViewListener;

public class DemoUploadImageView extends MyFragment {
	private UploadImageView uploadImageView = null;

	// 创建视图
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.demo_uploadimageview, container, false);
	}

	// Activity创建完成
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (isFirstStart) {
		}

		// 控件及页面设置
		setNavigationTitle("图片上传UploadImageView");
		setNavigationBackButton();

		// 绑定控件
		uploadImageView = (UploadImageView) findViewById(R.id.UploadImageView);

		// 设置引用对象 必须
		uploadImageView.fragment = this;

		// 设置未上传 用于后续判断
		uploadImageView.setTag(0);

		// 设置网络图片
		uploadImageView.setBackgroudWithUrl("http://a.hiphotos.baidu.com/super/pic/item/f703738da9773912fb8504e7f4198618377ae291.jpg");

		// 设置监听器
		uploadImageView.setUploadImageViewListener(new UploadImageViewListener() {

			@Override
			public void imageChoosed(Bitmap image) {
				// 得到图片Base64编码
				System.out.println("得到图片:" + myFunc.bitmapToBase64(image));

				// 图片显示到控件上
				uploadImageView.setBackground(new BitmapDrawable(getResources(), image));

				// 设置已上传 用于后续判断
				uploadImageView.setTag(1);
			}

			@Override
			public void imageBtnClick() {
				// 图片按钮点击
				System.out.println("图片按钮点击");
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 回调传入
		uploadImageView.onActivityResult(requestCode, resultCode, data);

		// 有多个上传对象，都需要在此调用,对象内部会解决冲突问题
		// uploadImageView1.onActivityResult(requestCode, resultCode, data);
		// uploadImageView2.onActivityResult(requestCode, resultCode, data);
		// uploadImageView3.onActivityResult(requestCode, resultCode, data);
		// uploadImageView4.onActivityResult(requestCode, resultCode, data);
	}

}
