package com.ben.module_main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;


import com.allure.lbanners.LMBanners;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MusicHomeFragment extends Fragment {

    private LMBanners bannerView;
    private RecyclerView recyclerView;
    private HomeSheetAdapter sheetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        Log.i("TAG", "MusicHomeFragment onCreateView...");
        View rootView = inflater.inflate(R.layout.fragment_music_home, container, false);
        initViews(rootView);
        initBanner();
        initList();
        return rootView;
    }

    private void initList() {
        if (sheetAdapter == null) {
            sheetAdapter = new HomeSheetAdapter();
            recyclerView.setFocusable(false);
            recyclerView.setAdapter(sheetAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayout.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        }
        ArrayList<Integer> list = new ArrayList<Integer>() {
            {
                for (int i = 0; i < 2; i++) {
                    add(R.mipmap.img_music1);
                    add(R.mipmap.img_music2);
                    add(R.mipmap.img_music3);
                    add(R.mipmap.img_music4);
                    add(R.mipmap.img_music5);
                    add(R.mipmap.img_music6);
                }
            }
        };
        sheetAdapter.setNewData(list);
    }

    private void initViews(View rootView) {
        bannerView = rootView.findViewById(R.id.bannerView);
        recyclerView = rootView.findViewById(R.id.recycler_view_sheet);
    }

    private void initBanner() {
        ArrayList<Integer> imgs = new ArrayList<Integer>() {
            {
                add(R.mipmap.img_banner1);
                add(R.mipmap.img_banner2);
                add(R.mipmap.img_banner3);
            }
        };
        bannerView.setAdapter(new BannerImgAdapter(), imgs);
    }

}