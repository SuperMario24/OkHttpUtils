package com.example.saber.okhttptest.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.saber.okhttptest.interfaze.IOkHttpDownloadcallback;
import com.example.saber.okhttptest.interfaze.IOkHttpOnLoadcallback;
import com.example.saber.okhttptest.interfaze.IOkHttpUpdate;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by saber on 2017/7/12.
 */

public class OkHttpUtils {

    private static final String TAG = "OkHttpUtils";

    private static OkHttpUtils mInstance;
    public static final boolean DEPENDENCY_OKHTTP;

    static {
        boolean hasDependency;
        try {
            Class.forName("okhttp3.OkHttpClient");
            hasDependency = true;
        } catch (ClassNotFoundException e) {
            hasDependency = false;
        }
        DEPENDENCY_OKHTTP = hasDependency;
    }


    private OkHttpUtils(){
        handler = new Handler(Looper.getMainLooper());
    };

    private Handler handler ;

    public static OkHttpUtils getInstance() {
        if(mInstance == null){
            if (!DEPENDENCY_OKHTTP) { //必须依赖 Okhttp
                throw new IllegalStateException("Must be dependency Okhttp");
            }
            synchronized (OkHttpUtils.class){
                if(mInstance == null){
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * Get请求，下载
     */
    public void downLoad(final String url,final String destDir,final IOkHttpDownloadcallback callback) {

        //创建Request对象，参数最起码有个url，当然你可以通过Request.Builder设置更多的参数比如：header、method等。
        final Request request = new Request.Builder()
                .url(url)
                .build();

        //构建进度监听
        final ProgressResponseBody.ProgressListener progressListener = createListener(callback);

        //创建OkHttpClient对象并拦截ResponseBody
        OkHttpClient okHttpClient = makeNewProgressResponseBody(progressListener);

        //通过request的对象去构造得到一个Call对象，类似于将你的请求封装成了任务，既然是任务，就会有execute()和cancel()等方法。
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onDownloadError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = null;

                byte[] buf = new byte[1024*8];
                int len = 0;
                FileOutputStream fos = null;

                try {
                    //通过response.body().byteStream()获取返回数据的字节流
                    in = response.body().byteStream();
                    //若文件夹不存在则创建
                    File dir = new File(destDir);
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = new File(destDir,getFileName(url));
                    //文件已经存在时，退出下载
                    if(file.exists()){
                        callback.onDownloadFileExists();
                        return;
                    }
                    fos = new FileOutputStream(file);
                    //写入文件中
                    while((len = in.read(buf)) != -1){
                        fos.write(buf,0,len);
                    }
                    fos.flush();
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                    //关闭流
                    NormalUtils.close(in);
                    NormalUtils.close(fos);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDownloadSuccessed();
                    }
                });

            }
        });
    }


    /**
     * 返回一个实体类
     * @param clazz
     * @param url
     * @param callback
     */
    public void onLoad(final Class<?> clazz, String url, final IOkHttpOnLoadcallback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();

        //构建进度监听
        ProgressResponseBody.ProgressListener progressListener = createListener(callback);

        //创建OkHttpClient对象并拦截ResponseBody
        OkHttpClient okHttpClient = makeNewProgressResponseBody(progressListener);

        //执行请求
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.loadError();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //检查是否依赖Gson
                boolean hasDependencyGson;
                try {
                    Class.forName("com.google.gson.Gson");
                    hasDependencyGson = true;
                } catch (ClassNotFoundException e) {
                    hasDependencyGson = false;
                }
                if(!hasDependencyGson){
                    throw new IllegalStateException("Must be dependency Gson");
                }
                //封装成实体类
                Gson gson = new Gson();
                final Object object = gson.fromJson(response.body().string(),clazz);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoadSuccess(object);
                    }
                });

            }
        });

    }






    /**
     * 创建OkHttpClient对象并拦截ResponseBody
     * @param progressListener
     * @return
     */
    private OkHttpClient makeNewProgressResponseBody(final ProgressResponseBody.ProgressListener progressListener) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        //这里将ResponseBody包装成我们的ProgressResponseBody
                        return response.newBuilder()
                                .body(new ProgressResponseBody(response.body(),progressListener))
                                .build();
                    }
                }).build();
    }

    /**
     * 构建进度监听
     * @param callback
     */
    private ProgressResponseBody.ProgressListener createListener(final IOkHttpUpdate callback) {
        final ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
            @Override
            public void update(final long bytesRead, final long contentLength, final boolean done) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"bytesRead:"+bytesRead);
                        Log.d(TAG,"contentLength:"+contentLength);
                        callback.onUpdate(bytesRead,contentLength,done);
                    }
                });
            }
        };
        return progressListener;
    }

    //获取文件名
    private String getFileName(String path)
    {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


}
