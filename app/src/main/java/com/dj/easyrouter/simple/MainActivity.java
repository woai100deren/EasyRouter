package com.dj.easyrouter.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute("/aaa/bbb")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}