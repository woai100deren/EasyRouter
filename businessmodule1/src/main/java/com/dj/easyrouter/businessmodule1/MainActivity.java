package com.dj.easyrouter.businessmodule1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module1/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessmodule1_activity_main);
    }
}