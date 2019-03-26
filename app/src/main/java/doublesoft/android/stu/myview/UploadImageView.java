package doublesoft.android.stu.myview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import doublesoft.android.stu.imageloader.ImageLoadListener;
import doublesoft.android.stu.imageloader.ImageLoader;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.inc.MyHandler;
import doublesoft.android.stu.myui.MyDialog;
import doublesoft.android.stu.myui.MyFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

//
//贵州双软科技有限公司
//版本 1.1
//Updated by yy on 18-06-25.
//Copyright 2018 Guizhou DoubleSoft Technology Co.,Ltd. All rights reserved.
//

public class UploadImageView extends Button {

	public MyFunction myFunc;
	public MyHandler myHandler;
	public Context myContext = null;
	public UploadImageViewListener uploadImageViewListener = null;
	public MyFragment fragment = null;

	// 用于加载网络图片
	private ImageLoader imageLoader = null;
	private ImageView tempImageView = null;

	public String userTag;

	// 是否自由裁剪
	public Boolean freeCrop;

	// 随机数加数字标识操作
	private int OBJECT_RANDOW_NUM = 0;
	private int IMAGE_REQUEST_CODE = 1;
	private int CAMERA_REQUEST_CODE = 2;
	private int RESULT_REQUEST_CODE = 3;
	private static final String IMAGE_FILE_NAME = "uploadTemp.jpg";

	private Uri imageUri;

	public UploadImageView(Context context) {
		super(context);
		setDefaultVars(context);
	}

	public UploadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDefaultVars(context);
	}

	public UploadImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setDefaultVars(context);
	}

	public UploadImageViewListener getUploadImageViewListener() {
		return uploadImageViewListener;
	}

	public void setUploadImageViewListener(UploadImageViewListener uploadImageViewListener) {
		this.uploadImageViewListener = uploadImageViewListener;
	}

	// 初始化
	public void setDefaultVars(Context context) {
		myContext = context;
		myFunc = new MyFunction();
		myHandler = new MyHandler();
		imageLoader = new ImageLoader(myContext);
		tempImageView = new ImageView(myContext);

		freeCrop = true;

		// 产生操作码
		int min = 100;
		int max = 999;
		Random random = new Random();
		OBJECT_RANDOW_NUM = random.nextInt(max) % (max - min + 1) + min;
		IMAGE_REQUEST_CODE += OBJECT_RANDOW_NUM * 10;
		CAMERA_REQUEST_CODE += OBJECT_RANDOW_NUM * 10;
		RESULT_REQUEST_CODE += OBJECT_RANDOW_NUM * 10;

		// 绑定事件
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageBtnClick();
			}
		});
	}

	// 自身按钮点击
	public void imageBtnClick() {
		if (this.uploadImageViewListener != null) {
			this.uploadImageViewListener.imageBtnClick();
		}

		if (fragment == null) {
			return;
		}

		final String[] arr = new String[] { "从相册选择", "新拍摄一张" };

		new MyDialog.Builder(myContext).setTitle("请选择照片来源?").setItems(arr, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 判断SD卡存在
				if (!myFunc.hasSdcard()) {
					fragment.dropHUD.showFailText("未找到存储卡，无法存储照片！");
					return;
				}

				// 创建File对象，用于存储拍照后的图片
				// 将此图片存储于SD卡的根目录下
				File outputImage = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);

				try {
					if (outputImage.exists()) {
						outputImage.delete();
					}
					outputImage.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 将File对象转换成Uri对象
				// Uri表标识着图片的地址
				imageUri = Uri.fromFile(outputImage);

				if (which == 0) {
					// 从相册选取照片
					Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
					intentFromGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
					intentFromGallery.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					fragment.startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
				}

				if (which == 1) {
					// 从摄像头选取照片
					Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					fragment.startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
				}

				dialog.dismiss();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			// 判断请求码是不是自己的 因为可能有多个组件
			if (requestCode / 10 == OBJECT_RANDOW_NUM) {
				// 照片上传
				if (resultCode != 0) {
					if (requestCode == IMAGE_REQUEST_CODE) {
						startPhotoZoom(1, data);
					} else if (requestCode == CAMERA_REQUEST_CODE) {
						startPhotoZoom(2, data);
					} else if (requestCode == RESULT_REQUEST_CODE) {
						if (data != null) {
							try {
								try {
									// 将output_image.jpg对象解析成Bitmap对象，然后设置到ImageView中显示出来
									Bitmap photo = BitmapFactory
											.decodeStream(myContext.getContentResolver().openInputStream(imageUri));
									if (this.uploadImageViewListener != null) {
										this.uploadImageViewListener.imageChoosed(photo);
									}
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							} catch (Exception e) {

							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 裁剪图片方法实现
	public void startPhotoZoom(int kind, Intent data) {
		try {
			Intent intent = new Intent("com.android.camera.action.CROP");
			if (kind == 1) {
				intent.setDataAndType(data.getData(), "image/*");
			} else if (kind == 2) {
				intent.setDataAndType(imageUri, "image/*");
			}

			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			intent.putExtra("crop", "true");

			// aspectX aspectY 是宽高的比例
			if (!freeCrop) {
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
			}

			// outputX,outputY 是剪裁图片的宽高
			if (!freeCrop) {
				intent.putExtra("outputX", 300);
				intent.putExtra("outputY", 300);
			}

			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true);
			intent.putExtra("return-data", false);

			fragment.startActivityForResult(intent, RESULT_REQUEST_CODE);
		} catch (Exception e) {

		}
	}

	// 设置网络图片
	public void setBackgroudWithUrl(String url) {
		imageLoader.displayImage(tempImageView, url, doublesoft.android.stu.R.drawable.public_placeholder, 9999);
		imageLoader.setImageLoadListener(new ImageLoadListener() {

			@Override
			public void setImage(ImageView imageView, String url, Bitmap bitmap, int displayType) {
				setBackground(new BitmapDrawable(getResources(), bitmap));
			}

			@Override
			public void loadUrlFinish(ImageView imageView, String url, Bitmap bitmap) {
				// TODO Auto-generated method stub

			}

			@Override
			public void loadMemoryFinish(ImageView imageView, String url, Bitmap bitmap) {
				// TODO Auto-generated method stub

			}
		});
	}

}
