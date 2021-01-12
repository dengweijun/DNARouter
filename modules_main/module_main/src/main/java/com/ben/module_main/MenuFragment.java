package com.ben.module_main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ben.lib_common.utils.UIHelper;

import androidx.fragment.app.Fragment;


public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        rootView.findViewById(R.id.ll_to_skin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.startActivity(getActivity(), SkinActivity.class);
            }
        });
    }

}