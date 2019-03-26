package doublesoft.android.stu.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageLoadListener {
    public void loadMemoryFinish(ImageView imageView, String url, Bitmap bitmap);

    public void loadUrlFinish(ImageView imageView, String url, Bitmap bitmap);

    public void setImage(ImageView imageView, String url, Bitmap bitmap,
                         int displayType);
}
