package com.example.saber.okhttptest.interfaze;

/**
 * Created by saber on 2017/7/12.
 */

public interface IOkHttpDownloadcallback extends IOkHttpUpdate {

    /**
     * 显示进度
     * @param bytesRead 当前读取进度
     * @param contentLength 总进度
     * @param done 是否完成
     */
    void onUpdate(long bytesRead, long contentLength, boolean done);

    void onDownloadSuccessed();
    void onDownloadError();
    void onDownloadFileExists();




}
