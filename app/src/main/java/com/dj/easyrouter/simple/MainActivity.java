package com.dj.easyrouter.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
                EasyRouter.getInstance().build("/module2/main").withString("value2","123456").navigation();
            }
        });
    }
}