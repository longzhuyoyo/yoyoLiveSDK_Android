package com.xcyo.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.sdk.api.request.YoyoErrorCode;
import com.xcyo.sdk.api.request.YoyoServerInterface;
import com.xcyo.simple.simadapter.SimpleAdapter;
import com.xcyo.yoyo.record.server.YoyoSingerRoomListRecord;
import com.xcyo.yoyo.record.server.YoyoSingerRoomRecord;

import java.util.ArrayList;

/**
 * Created by bear on 2017/1/13.
 */

public class SingerListActivity extends FragmentActivity {
    private GridView listView;
    private SimpleAdapter simpleAdapter;
    private Button btnGetData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_list);
        listView = (GridView) findViewById(R.id.demo_list);
        btnGetData = (Button) findViewById(R.id.get_data);
        simpleAdapter = new SimpleAdapter(SingerListActivity.this, new ArrayList<YoyoSingerRoomRecord>());
        getList();
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                YoyoApi.enterRoom(simpleAdapter.getList().get(position).getUid());
            }
        });
        btnGetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });
        YoyoApi.reportForClickLiveTab();
    }

    private void getList(){
        YoyoApi.getHostList(new YoyoServerInterface<YoyoSingerRoomListRecord>() {
            @Override
            public boolean onOk(YoyoSingerRoomListRecord response) {
                if (response != null) {
                    simpleAdapter.setList(response.getList());
                }
                return true;
            }

            @Override
            public boolean onError(YoyoErrorCode err, String msg) {
                Toast.makeText(SingerListActivity.this, "list ->" + msg, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
