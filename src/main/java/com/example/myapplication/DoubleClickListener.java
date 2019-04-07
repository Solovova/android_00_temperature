package com.example.myapplication;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class DoubleClickListener implements OnClickListener {

    private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    long lastClickTime = 0;
    View lastView;

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA && lastView == v){
            onDoubleClick(v);
            lastClickTime = 0;
            lastView = null;
        } else {
            onSingleClick(v);
        }
        lastClickTime = clickTime;
        lastView = v;
    }

    public abstract void onSingleClick(View v);
    public abstract void onDoubleClick(View v);
}
