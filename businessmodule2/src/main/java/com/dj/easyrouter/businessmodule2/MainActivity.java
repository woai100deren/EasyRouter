package com.dj.easyrouter.businessmodule2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module2/main")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessmodule2_activity_main);
    }
}