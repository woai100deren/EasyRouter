package com.dj.easyrouter.businessmodule1;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module1/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessmodule1_activity_main);
    }
}