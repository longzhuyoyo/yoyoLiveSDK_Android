package com.xcyo.simple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoServerInterface;

/**
 * Created by NoName on 2016/10/17.
 */
public class ExchangeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        ((TextView)findViewById(R.id.coin)).setText(YoyoApi.getCoin() + "");
        setAction();
    }

    public final void exchange(View v){
        inputNumber = ((TextView) findViewById(R.id.input)).getText().toString().trim();
        AppHelper.exchange(inputNumber);
    }

    String inputNumber ;

    void goExchage(){
        YoyoApi.exchange(Integer.parseInt(inputNumber), AppHelper.ex.transactionId, AppHelper.user.token, new YoyoServerInterface<Boolean>() {
            @Override
            public boolean onOk(Boolean response) {
                ((TextView)findViewById(R.id.coin)).setText(YoyoApi.getCoin()+"");
                return false;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                Toast.makeText(ExchangeActivity.this, "exchange->" + msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void setAction(){
        AppHelper.setOk(new AppHelper.Ok() {
            @Override
            public void isOk(int tag) {
                if(tag == 2){
                    goExchage();
                }else if(tag == 5){
                    Toast.makeText(ExchangeActivity.this, "兑换失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
