package com.test.caifu.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.test.caifu.R;
import com.test.caifu.view.SlidingMenu;

/**
 * Created by zhengcf on 2017/12/13.
 */

public class MainActivity extends BaseActivity {
    SlidingMenu menu;
    View contentView;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = findViewById(R.id.menu_layout);
        contentView = findViewById(R.id.content_layout);
        contentView.setOnClickListener(v -> {
            menu.toggleMenu();
        });
    }
}
