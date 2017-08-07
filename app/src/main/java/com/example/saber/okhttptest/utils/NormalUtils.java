package com.example.saber.okhttptest.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by saber on 2017/8/7.
 */

public class NormalUtils {

    public static void close(Closeable closeable){
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
