package com.example.saber.okhttptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.saber.okhttptest.interfaze.IOkHttpDownloadcallback;
import com.example.saber.okhttptest.interfaze.IOkHttpOnLoadcallback;
import com.example.saber.okhttptest.utils.DoubanContentResp;
import com.example.saber.okhttptest.utils.OkHttpUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();

    private Button btnDownloadImages;
    private ProgressBar pbUpload;

    private Button btnLoad;
    private ProgressBar pbOnload;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbUpload = (ProgressBar) findViewById(R.id.pb_upload);
        btnDownloadImages = (Button) findViewById(R.id.btn_download_images);
        pbOnload = (ProgressBar) findViewById(R.id.pb_onload);
        btnLoad = (Button) findViewById(R.id.btn_on_load);
        webView = (WebView) findViewById(R.id.web_view);
        webView.setScrollbarFadingEnabled(true);
        WebSettings settings = webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置缓存
        settings.setSupportZoom(true);// 支持放大网页功能
        settings.setBuiltInZoomControls(false);// 支持缩小网页功能
        settings.setJavaScriptEnabled(true);// 支持js
        //开启DOM storage API功能
        settings.setDomStorageEnabled(true);
        //开启application Cache功能
        settings.setAppCacheEnabled(false);

        //不打开系统浏览器
        webView.setWebViewClient(new WebViewClient());
        //webView加载网页的进度条
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    pbOnload.setVisibility(View.GONE);
                } else {
                    // 加载中
                    pbOnload.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });



        //下载请求
        btnDownloadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //运行时权限
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    //图片的url
                    String imageUrl = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2859174087,963187950&fm=23&gp=0.jpg";
                    //保存的目标文件夹
                    String destDir = "";
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        destDir = Environment.getExternalStorageDirectory() + "/OkHttpDownload/";
                    }


                    okHttpUtils.downLoad(imageUrl,destDir, new IOkHttpDownloadcallback() {
                        @Override
                        public void onUpdate(long bytesRead, long contentLength, boolean done) {
                            //计算百分比并更新ProgressBar
                            final int percent = (int) (100 * bytesRead / contentLength);
                            pbUpload.setProgress(percent);
                            Log.d(TAG,"下载进度："+(100*bytesRead)/contentLength+"%");
                        }

                        @Override
                        public void onDownloadSuccessed() {
                            Log.d(TAG,"download success");
                        }

                        @Override
                        public void onDownloadError() {
                            Log.e(TAG,"download failed");
                        }

                        @Override
                        public void onDownloadFileExists() {
                            Log.e(TAG,"the file is already exists");
                        }
                    });
                }

            }
        });


        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okHttpUtils.onLoad(DoubanContentResp.class, "https://moment.douban.com/api/post/178848", new IOkHttpOnLoadcallback() {
                    @Override
                    public void onUpdate(long bytesRead, long contentLength, boolean done) {
                        //计算百分比并更新ProgressBar
                        final int percent = (int) (100 * bytesRead / contentLength);
                        pbUpload.setProgress(percent);
                        Log.d(TAG,"下载进度："+(100*bytesRead)/contentLength+"%");
                    }

                    @Override
                    public void loadError() {
                        Log.e(TAG,"download failed");
                    }

                    @Override
                    public void onLoadSuccess(Object object) {
                        DoubanContentResp doubanContentResp = (DoubanContentResp) object;
                        Log.d(TAG,"Author Name:"+doubanContentResp.getAuthor().getName());
                        String shortUrl = doubanContentResp.getShort_url();
                        webView.loadUrl(shortUrl);
                    }
                });
            }
        });




    }

}
