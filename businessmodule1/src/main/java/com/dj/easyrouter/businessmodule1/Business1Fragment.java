package com.dj.easyrouter.businessmodule1;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module1/business1Fragment")
public class Business1Fragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String value = getArguments().getString("value");  //获取参数
        Log.e("111", "Fragment接收传递过来的参数：" + value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.businessmodule1_fragment,container,false);
    }
}