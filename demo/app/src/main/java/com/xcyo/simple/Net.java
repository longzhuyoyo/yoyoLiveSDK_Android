package com.xcyo.simple;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by TDJ on 2016/8/16.
 */

public class Net {

    private static final String NET_COOKIE = "Cookie";
    private static final String NET_SET_COOKIE = "Set-Cookie";

    public static byte[] sendReqUrl(@NonNull String url, String method, Map<String, String> params, Map<String, String> header, String cookie){
        String osParams = readParams(params);

        OutputStream ostream = null;
        ByteArrayOutputStream byteOps = null;
        BufferedInputStream reader = null;
        HttpURLConnection conn = null;
        try {
            conn = getConnect(osParams.length() > 0 && !"POST".equals(method) ? url+"?"+osParams : url, method, header);
            if(conn == null){
                return null;
            }
            if(!TextUtils.isEmpty(cookie)){
                conn.addRequestProperty("Cookie", cookie);
            }
            conn.connect();
            if(params != null && "POST".equals(method)){
                ostream = conn.getOutputStream();
                ostream.write(readParams(params).getBytes("UTF-8"));
                ostream.flush();
                ostream.close();
            }
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                Map<String, List<String>> fields = conn.getHeaderFields();//获取所有的头部信息, 暂时没用

                byteOps = new ByteArrayOutputStream();
                reader = new BufferedInputStream(conn.getInputStream());
                byte[] buffer = new byte[1024];
                while(reader.read(buffer, 0, buffer.length) != -1){
                    byteOps.write(buffer);
                }
                return byteOps.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null){
                    conn.disconnect();
                }
                if(byteOps != null){
                    byteOps.close();
                }
                if(ostream != null){
                    ostream.close();
                }
                if(reader != null){
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static HttpURLConnection getConnect(@NonNull String url, @NonNull String method, Map<String, String> header) throws IOException {
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        if(conn != null){
            conn.setReadTimeout(8000);
            conn.setConnectTimeout(8000);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST".equals(method) ? "POST" : "GET");
            if(header != null && !header.isEmpty()){
                Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry<String, String> entry = it.next();
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            return conn;
        }
        return null;
    }

    private static String readParams(Map<String, String> params){
        StringBuffer mBuffer = new StringBuffer();
        if(params != null){
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = it.next();
                if(mBuffer.length() > 0){
                    mBuffer.append("&");
                }
                mBuffer.append(entry.getKey() + "="+entry.getValue());
            }
        }
        return mBuffer.toString();
    }
}
