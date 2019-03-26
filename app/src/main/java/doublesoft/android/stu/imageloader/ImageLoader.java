package doublesoft.android.stu.imageloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import doublesoft.android.stu.R;
import doublesoft.android.stu.inc.MyFunction;
import doublesoft.android.stu.myui.MyFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {

    private MemoryCache memoryCache = new MemoryCache();
    private FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private ImageLoadListener imageLoadListener = null;
    private Context myContext;

    public ImageLoader(Context context) {
        myContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    public ImageLoadListener getImageLoadListener() {
        return imageLoadListener;
    }

    public void setImageLoadListener(ImageLoadListener imageLoadListener) {
        this.imageLoadListener = imageLoadListener;
    }

    // displayType 说明见 setImage 方法的case
    public void displayImage(ImageView imageView, String url) {
        displayImage(imageView, url, R.drawable.public_placeholder);
    }

    public void displayImage(ImageView imageView, String url, int placeHolder) {
        displayImage(imageView, url, placeHolder, 0);
    }

    public void displayImage(ImageView imageView, String url, int placeHolder, int displayType) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            setImage(imageView, url, bitmap, displayType);
            if (imageLoadListener != null) {
                imageLoadListener.loadMemoryFinish(imageView, url, bitmap);
            }
        } else {
            queuePhoto(url, imageView, placeHolder, displayType);
            imageView.setBackgroundResource(placeHolder);
        }
    }

    private void queuePhoto(String url, ImageView imageView, int placeHolder, int displayType) {
        PhotoToLoad p = new PhotoToLoad(url, imageView, placeHolder, displayType);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        // from web
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 2000;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException ignored) {
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public int placeHolder;
        public int displayType;

        public PhotoToLoad(String u, ImageView i, int p, int dt) {
            url = u;
            imageView = i;
            placeHolder = p;
            displayType = dt;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    public long size() {
        return fileCache.size + memoryCache.size;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                setImage(photoToLoad.imageView, photoToLoad.url, bitmap, photoToLoad.displayType);
                if (imageLoadListener != null) {
                    imageLoadListener.loadUrlFinish(photoToLoad.imageView, photoToLoad.url, bitmap);
                }
            } else {
                photoToLoad.imageView.setBackgroundResource(photoToLoad.placeHolder);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    public Drawable loadImageFromNetwork(String address) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(address).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        return drawable;
    }

    // 设置图片
    public void setImage(ImageView imageView, String url, Bitmap bitmap, int displayType) {
        switch (displayType) {
            case 0:// 直接显示图片
                imageView.setImageBitmap(bitmap);
                break;
            case 1:// 是按钮图片设置个点击效果
                StateListDrawable drawable = new StateListDrawable();

                Drawable pressedDrawable = new BitmapDrawable(myContext.getResources(), bitmap);
                pressedDrawable.setAlpha(122);

                Drawable normalDrawable = new BitmapDrawable(myContext.getResources(), bitmap);

                drawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
                drawable.addState(new int[]{android.R.attr.state_selected}, pressedDrawable);
                drawable.addState(new int[]{}, normalDrawable);

                imageView.setBackground(drawable);
            default:
                break;
        }

        // 回调
        if (imageLoadListener != null) {
            imageLoadListener.setImage(imageView, url, bitmap, displayType);
        }
    }

    // 显示大图预览
    public void previewImage(final MyFragment fragment, final Drawable drawable) {
//        // 设置图片
//        try {
//            final PopView popView = (PopView) fragment.findViewById(R.id.ImagePreviewPopView);
//            if (popView != null) {
//                PhotoView photoView = (PhotoView) popView.findViewById(R.id.PhotoView);
//                photoView.setImageDrawable(drawable);
//                popView.showPopView();
//
//                // 点击外部关闭
//                photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
//
//                    @Override
//                    public void onOutsidePhotoTap(ImageView imageView) {
//                        popView.hiddenPopView();
//                    }
//                });
//
//                // 点击保存到相册按钮
//                Button photoSaveButton = (Button) popView.findViewById(R.id.PhotoSaveButton);
//                photoSaveButton.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            // drawable 转为 bitmap 并保存到相册
//                            // 取 drawable 的长宽
//                            int w = drawable.getIntrinsicWidth();
//                            int h = drawable.getIntrinsicHeight();
//
//                            // 取 drawable 的颜色格式
//                            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                    : Bitmap.Config.RGB_565;
//                            // 建立对应 bitmap
//                            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
//                            // 建立对应 bitmap 的画布
//                            Canvas canvas = new Canvas(bitmap);
//                            drawable.setBounds(0, 0, w, h);
//                            // 把 drawable 内容画到画布中
//                            drawable.draw(canvas);
//
//                            saveBmp2Gallery(fragment, bitmap, "");
//                        } catch (Exception e) {
//                            Toast toast = Toast.makeText(fragment.getActivity().getApplicationContext(),
//                                    "图片保存失败:" + e.toString(), Toast.LENGTH_LONG);
//                            toast.setGravity(Gravity.CENTER, 0, 0);
//                            TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
//                            textView.setTextSize(15);
//                            toast.show();
//                        }
//                    }
//                });
//            } else {
//                System.out.println("图片预览布局文件中没有加载");
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
    }

    // 保存到本地
    public static void saveBmp2Gallery(MyFragment fragment, Bitmap bmp, String picName) {
        try {
            String fileName = null;
            // 系统相册目录
            String galleryPath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM
                    + File.separator + "Camera" + File.separator;

            // 声明文件对象
            File file = null;
            // 声明输出流
            FileOutputStream outStream = null;

            try {
                // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
                file = new File(galleryPath, picName + ".jpg");

                // 获得文件相对路径
                fileName = file.toString();
                // 获得输出流，如果文件中有内容，追加内容
                outStream = new FileOutputStream(fileName);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);

            } catch (Exception e) {
                e.getStackTrace();
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 通知相册更新
            MediaStore.Images.Media.insertImage(fragment.getActivity().getContentResolver(), bmp, fileName, null);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            fragment.getActivity().sendBroadcast(intent);

            Toast toast = Toast.makeText(fragment.getActivity().getApplicationContext(), "图片保存成功", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextSize(15);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(fragment.getActivity().getApplicationContext(), "图片保存失败:" + e.toString(),
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextSize(15);
            toast.show();
        }

    }
}
