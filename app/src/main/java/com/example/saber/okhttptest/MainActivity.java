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
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.saber.okhttptest.interfaze.IOkHttpDownloadcallback;
import com.example.saber.okhttptest.utils.OkHttpUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();

    private Button btnDownloadImages;
    private ProgressBar pbUpload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbUpload = (ProgressBar) findViewById(R.id.pb_upload);
        btnDownloadImages = (Button) findViewById(R.id.btn_download_images);




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




    }

}
