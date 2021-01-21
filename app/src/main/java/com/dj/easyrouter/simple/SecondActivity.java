package com.dj.easyrouter.simple;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/app/second")
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Log.e("123","获取到传递过来的值是:"+getIntent().getStringExtra("value"));
    }
}
