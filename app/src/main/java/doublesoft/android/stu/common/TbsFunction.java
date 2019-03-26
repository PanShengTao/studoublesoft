package doublesoft.android.stu.common;

import android.content.Context;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsVideo;

public class TbsFunction {
    //播放视频
    public static void startPlay(Context context, String videoUrl) {
        //判断当前是否可用
        if (TbsVideo.canUseTbsPlayer(context)) {
            //播放视频
            TbsVideo.openVideo(context, videoUrl);
        } else {
            Toast.makeText(context, "未检测到视频播放器，可在本机安装微信后重试!", Toast.LENGTH_SHORT).show();
        }
    }
}
