package doublesoft.android.stu.common;

import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import doublesoft.android.stu.R;
import doublesoft.android.stu.myui.MyFragment;
import okhttp3.Call;
import okhttp3.Response;

public class TbsFileFragment extends MyFragment implements TbsReaderView.ReaderCallback {
    private RelativeLayout mRelativeLayout;
    private TbsReaderView mTbsReaderView;
    private String downloadDir = Environment.getExternalStorageDirectory() + "/downloadDir/test/document/";
    private String fileUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tbs_layout, container, false);
    }

    // Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 控件及页面设置
        setNavigationTitle("文件浏览");
        setNavigationBackButton();

        Bundle bundle = getArguments();
        fileUrl = bundle.getString("FileUrl");

        mTbsReaderView = new TbsReaderView(getContext(), this);
        mRelativeLayout = (RelativeLayout)findViewById(R.id.tbsView);
        mRelativeLayout.addView(mTbsReaderView,new RelativeLayout.LayoutParams(-1,-1));
        initDoc();
    }

    private void initDoc() {
        int i = fileUrl.lastIndexOf("/");
        String fileName = fileUrl.substring(i, fileUrl.length());
        //Log.d("print", "---substring---" + docName);

        String[] split = fileUrl.split("\\/");
        String s = split[split.length - 4] + split[split.length - 3] + split[split.length - 2] + split[split.length - 1];
        //Log.d("print", "截取带时间---" + s);

        //判断是否在本地/[下载/直接打开]
        File docFile = new File(downloadDir, fileName);
        if (docFile.exists()) {
            //存在本地
            //Log.d("print", "本地存在");
            displayFile(docFile.toString(),  fileName);
        } else {
            //远程下载
            OkGo.get(fileUrl)
                    .tag(this)
                    .execute(new FileCallback(downloadDir, fileName) {  //文件下载时，可以指定下载的文件目录和文件名
                        @Override
                        public void onSuccess(File file, Call call, Response response) {
                            // file 即为文件数据，文件保存在指定目录
                            //Log.d("print", "下载文件成功");
                            displayFile(downloadDir +file.getName(), file.getName());
                            //Log.d("print", "" + file.getName());
                        }

                        @Override
                        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                            //这里回调下载进度(该回调在主线程,可以直接更新ui)
                            //Log.d("print", "总大小---" + totalSize + "---文件下载进度---" + progress);
                        }
                    });
        }

    }

    private String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/TbsReaderTemp";
    private void displayFile(String filePath, String fileName) {
        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = tbsReaderTemp;
        File bsReaderTempFile =new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            //Log.d("print","准备创建/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if(!mkdir){
                //Log.d("print","创建/TbsReaderTemp失败！！！！！");
            }
        }
        Bundle bundle = new Bundle();
        //Log.d("print","filePath"+filePath);//可能是路径错误
        //Log.d("print","tempPath"+tbsReaderTemp);
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", tbsReaderTemp);
        boolean result = mTbsReaderView.preOpen(getFileType(fileName), false);
        //Log.d("print","查看文档---"+result);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            //Log.d("print", "paramString---->null");
            return str;
        }
        //Log.d("print", "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            //Log.d("print", "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        //Log.d("print", "paramString.substring(i + 1)------>" + str);
        return str;
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTbsReaderView.onStop();
    }
}
