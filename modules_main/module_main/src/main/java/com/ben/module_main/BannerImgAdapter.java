package com.ben.module_main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;

public class BannerImgAdapter implements LBaseAdapter<Integer> {

    @Override
    public View getView(LMBanners lBanners, final Context context, final int position, final Integer res) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_banner, null);
        ImageView bannerIV = view.findViewById(R.id.iv_banner);
        bannerIV.setImageResource(res);
        return view;
    }

}