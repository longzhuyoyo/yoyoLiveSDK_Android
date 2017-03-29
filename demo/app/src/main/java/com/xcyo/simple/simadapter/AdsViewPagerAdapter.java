package com.xcyo.simple.simadapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcyo.sdk.api.YoyoApi;
import com.xcyo.simple.R;
import com.xcyo.yoyo.record.server.YoyoAdRecord;
import com.xutils.x;

import java.util.List;

/**
 * Created by caixiaoxiao on 29/3/17.
 */
public class AdsViewPagerAdapter extends PagerAdapter{
    private Context mCtx;
    private List<YoyoAdRecord> mListData;
    private View[] mViews;

    public AdsViewPagerAdapter(Context ctx,List<YoyoAdRecord> listData){
        this.mCtx = ctx;
        this.mListData = listData;
        mViews = new View[listData.size()];

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        YoyoAdRecord record;
        for (int i = 0; i < listData.size();i++){
            record = listData.get(i);
            View view = inflater.inflate(R.layout.item_ad,null);
            ImageView img = (ImageView) view.findViewById(R.id.item_img);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            TextView desc = (TextView) view.findViewById(R.id.item_desc);
            x.image().bind(img,record.getImg());
            title.setText(record.getTitle());
            desc.setText(record.getDesc());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoyoApi.enterRandomHotRoom(null);
                }
            });
            mViews[i] = view;
        }
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews[position];
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews[position]);
    }
}
