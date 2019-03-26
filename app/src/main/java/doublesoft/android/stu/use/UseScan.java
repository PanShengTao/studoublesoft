package doublesoft.android.stu.use;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rbj.zxing.decode.QrcodeDecode;

import doublesoft.android.stu.R;
import doublesoft.android.stu.inc.QRCodeUtils;
import doublesoft.android.stu.myui.MyFragment;

public class UseScan extends MyFragment {
    private static final int IMAGE_REQUEST_CODE = 3001;
    private SurfaceView scanPreview;// 相机
    private RelativeLayout scanCropView;// 扫描框
    private ImageView scanLine;// 扫描框中间的线
    private QrcodeDecode qd;

    // 创建视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.use_scan, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setNavigationTitle("扫码验证");
        setNavigationBackButton();

        if (isFirstStart) {

        }

        Window window = this.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);

        qd = new QrcodeDecode(this.getActivity(), scanPreview, scanCropView) {

            @Override
            public void handleDecode(Bundle bundle) {
                // 扫描成功后调用
                doCode(bundle.getString(QrcodeDecode.BARCODE_RESULT));
                stopScan();
            }
        };

        //右侧按钮
        Button pageRightBtn = (Button) setNavigationRightButtonWithTextAndColor("相册", R.color.selector_btn_darkgray_lightgray);
        pageRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageRightButtonClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在此处开起扫描
        this.startScan();
    }

    @Override
    public void onPause() {
        // 在此处暂停扫描
        this.stopScan();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 释放资源
        qd.onDestroy();
        super.onDestroy();
    }

    // 开始扫描
    public void startScan() {
        qd.onResume();

        scanLine.clearAnimation();
        scanLine.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
    }

    // 停止扫描
    public void stopScan() {
        qd.onPause();

        scanLine.clearAnimation();
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(400);
        animation.setRepeatCount(0);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                scanLine.clearAnimation();
                scanLine.setVisibility(View.INVISIBLE);
            }
        });
        scanLine.startAnimation(animation);

    }

    // 从相册选取照片
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pageRightButtonClick() {
        // 从相册选取照片
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        if (Build.VERSION.SDK_INT < 19) {
            intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intentFromGallery.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Bitmap bitmap;
        try {
            // 照片上传
            if (resultCode != 0) {
                switch (requestCode) {
                    case IMAGE_REQUEST_CODE:// 相册选取
                        bitmap = myFunc.getBitmapFromUri(myContext, data.getData(),
                                true);
                        if (bitmap != null) {
                            try {

                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        final String code = QRCodeUtils
                                                .getStringFromQRCode(myFunc
                                                        .compress(bitmap, 100));
                                        myHandler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                if (code == null) {
                                                    stopScan();
                                                    dropHUD.showFailText("选取的照片中未能识别到二维码！");
                                                } else {
                                                    doCode(code);
                                                }
                                            }
                                        });
                                    }
                                }).start();

                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else {
                            dropHUD.showFailText("图片选取错误，请重试！");
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // 得到二维码 处理二维码
    public void doCode(String code) {
        System.out.println(code);

    }

    @Override
    public void onFragmentManagerBackStackChanged() {
        super.onFragmentManagerBackStackChanged();
        if (isTrueVisible()) {
            myHandler.postDelayed(new Runnable() {// 不要卡了返回动画

                @Override
                public void run() {
                    if (isFirstStart == false) {
                        startScan();
                    }
                }
            }, 500);
        }
    }
}
