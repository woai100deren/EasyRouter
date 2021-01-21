package com.dj.easyrouter.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.dj.easyrouter.EasyRouter;
import com.dj.easyrouter.annotation.EasyRoute;
import com.dj.easyrouter.export.HelloService;

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
        findViewById(R.id.startService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/app/myService").navigation();
            }
        });
        findViewById(R.id.stopService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/app/myService").stopNavigation();
            }
        });
        findViewById(R.id.startBm1Service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/module1/myService").navigation();
            }
        });
        findViewById(R.id.stopBm1Service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyRouter.getInstance().build("/module1/myService").stopNavigation();
            }
        });
        findViewById(R.id.getBm1Fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,FragmentActivity.class));
            }
        });
        findViewById(R.id.postInfoToBm2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object object = EasyRouter.getInstance().build("/module2/helloService").navigation();
                Log.e("123",object.toString());
                HelloService helloService = (HelloService)object;
                helloService.sayHello("a xi ba");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}