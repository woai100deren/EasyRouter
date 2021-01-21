package com.dj.easyrouter.simple;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.dj.easyrouter.EasyRouter;

public class FragmentActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment fragment = (Fragment) EasyRouter.getInstance().build("/module1/business1Fragment").withString("value","123456").navigation();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }
}
