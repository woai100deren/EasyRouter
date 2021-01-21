package com.dj.easyrouter.businessmodule2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/module2/main")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.businessmodule2_activity_main);

        Log.e("1111","业务2主页获得传递过来的数据是："+getIntent().getStringExtra("value2"));
    }

    @Override
    public void onBackPressed() {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", "hhhhhhhhhhhhhhhhhhhh");
        //设置返回数据
        setResult(RESULT_OK, intent);
        finish();
    }
}