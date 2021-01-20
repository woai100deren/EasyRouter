package com.dj.easyrouter.simple;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dj.easyrouter.EasyRouter;
import com.dj.easyrouter.annotation.EasyRoute;

@EasyRoute(path = "/app/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/app/second").withString("value","123456").navigation();
            }
        });
        findViewById(R.id.jumpBm1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/module1/main").withString("value1","123456").navigation();
            }
        });
        findViewById(R.id.jumpBm2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/module2/main").withString("value2","123456").navigationForResult(MainActivity.this,333);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 333) {
                Log.e("1111111111", "返回结果：" + data.getStringExtra("result"));
            }
        }
    }
}