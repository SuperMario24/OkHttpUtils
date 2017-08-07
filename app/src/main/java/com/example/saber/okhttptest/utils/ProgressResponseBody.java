package com.example.saber.okhttptest.utils;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by saber on 2017/8/7.
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private ProgressListener progressListener;

    public ProgressResponseBody(ResponseBody responseBody,ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return Okio.buffer(source(responseBody.source()));
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            //总进度
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //总的下载字节数，-1表示下载完毕
                totalBytesRead += bytesRead != -1? bytesRead:0;
                //回调接口
                progressListener.update(totalBytesRead,responseBody.contentLength(),bytesRead == -1);
                return bytesRead;
            }
        };
    }


    //回调接口
    interface ProgressListener{
        /**
         * @param bytesRead 已经读取的字节数
         * @param contentLength 响应总长度
         * @param done 是否读取完毕
         */
        void update(long bytesRead,long contentLength,boolean done);
    }
}
