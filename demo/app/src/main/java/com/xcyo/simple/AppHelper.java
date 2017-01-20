package com.xcyo.simple;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by NoName on 2016/10/17.
 */
public class AppHelper {

    static String _login = "http://ready.xcyo.com/test/app-login";
    static String _exchange = "http://ready.xcyo.com/test/app-exchange";
    static String _addAmount = "http://ready.xcyo.com/test/app-add-amount";

    private static Handler mHandler = new Handler();

    static class User{
        String openId;
        String name;
        String pwd;
        String token;
        String amount;
    }

    static User user = new User();

    static class Exchange{
        String transactionId;
        String amount;
    }

    static Exchange ex = new Exchange();

    static Collection<Ok> mOk = new HashSet<>();

    public static void setOk(Ok ok) {
        mOk.add(ok);
    }

    public static void removeOK(Ok ok){
        mOk.remove(ok);
    }

    public static ExecutorService execut = Executors.newSingleThreadExecutor();

    private static void handMainThread(final int tag){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for(Ok ok : mOk){
                    if(ok != null){
                        ok.isOk(tag);
                    }
                }
            }
        });
    }

    static void doLogin(final String username, final String password){
        execut.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put("name", username);
                params.put("pwd", password);
                byte[] data = Net.sendReqUrl(_login, "POST", params, null, null);
                if(data == null) {
                    handMainThread(4);
                    return ;
                }
                try {
                    JSONObject jsonData = new JSONObject(new String(data));
                    if("10000".equals(jsonData.getString("s"))){
                        jsonData = jsonData.getJSONObject("d").getJSONObject("user");
                        user.openId = jsonData.getString("openId");
                        user.name = jsonData.getString("name");
                        user.pwd = jsonData.getString("pwd");
                        user.token = jsonData.getString("token");
                        user.amount = jsonData.getString("amount");
                        handMainThread(1);
                        return ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handMainThread(4);
            }
        });
    }

    static void doLogout(){
        user.openId = "";
        user.name = "";
        user.pwd = "";
        user.token = "";
        user.amount = "";
    }

    static void exchange(final String count){
        execut.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put("name", user.name);
                params.put("amount", count);
                byte[] data = Net.sendReqUrl(_exchange, "POST", params, null, null);
                if(data == null) {
                    handMainThread(5);
                    return ;
                }
                try {
                    JSONObject jsonData = new JSONObject(new String(data));
                    if("10000".equals(jsonData.getString("s"))){
                        jsonData = jsonData.getJSONObject("d");
                        ex.transactionId = jsonData.getString("transactionId");
                        ex.amount = jsonData.getString("amount");
                        handMainThread(2);
                        return ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handMainThread(5);
            }
        });
    }


    static void addMount(final String amount){

        execut.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, String> params = new HashMap<>();
                params.put("name", user.name);
                params.put("amount", amount);
                byte[] data = Net.sendReqUrl(_addAmount, "POST", params, null, null);
                if(data == null) {
                    handMainThread(6);
                    return ;
                }
                try {
                    JSONObject jsonData = new JSONObject(new String(data));
                    if("10000".equals(jsonData.getString("s"))){
                        jsonData = jsonData.getJSONObject("d").getJSONObject("user");
                        user.openId = jsonData.getString("openId");
                        user.name = jsonData.getString("name");
                        user.pwd = jsonData.getString("pwd");
                        user.token = jsonData.getString("token");
                        user.amount = jsonData.getString("amount");
                        handMainThread(3);
                        return ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handMainThread(6);
            }
        });
    }

    public interface Ok{
        public void isOk(int tag);
    }
}
