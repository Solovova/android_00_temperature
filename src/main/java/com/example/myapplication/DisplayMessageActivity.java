package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.util.Log;

public class DisplayMessageActivity extends AppCompatActivity {
    DrawView drawView;
    Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_display_message);

        LinearLayout linearLayout00 =  findViewById(R.id.layort00);
        this.initDrawView();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        DoubleClickListener oclBtnOk = new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                sendMessage_butDraw_SingleClick(v);
            }

            @Override
            public void onDoubleClick(View v) {
                sendMessage_butDraw_DoubleClick(v);
            }
        };
        drawView.setLayoutParams(lp);
        drawView.setOnClickListener(oclBtnOk);
        linearLayout00.addView(drawView);

        this.myThread = new Thread( // создаём новый поток
                new Runnable() { // описываем объект Runnable в конструкторе
                    public void run() {
                        while (true) {
                            runOnUiThread(MyThreadRun);
                            SystemClock.sleep(1000);
                        }
                    }
                }
        );
        this.myThread.start();
    }

    @Override
    protected void onDestroy() {
        if (this.myThread != null) {
            Thread dummy = this.myThread;
            this.myThread = null;
            dummy.interrupt();
        }
        super.onDestroy();
    }

    protected void initDrawView(){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int offset = myPreferences.getInt("offset", 0);
        drawView = new DrawView(this);
        drawView.lowTemperature = (double) myPreferences.getFloat("lowTemperature", 0);
        drawView.hightTemperature = (double) myPreferences.getFloat("hightTemperature", 0);

        drawView.setOffset(offset);
        drawView.capture = myPreferences.getString("capture", "");
        Log.i("READ lowTemperature",Double.toString(drawView.lowTemperature));
        Log.i("READ hightTemperature",Double.toString(drawView.hightTemperature));
        Log.i("READ offset",Double.toString(drawView.offset));
    }

    // Метод обработки нажатия на кнопку
    public void sendMessage_butDraw_SingleClick(View view) {
        DrawView tmpDW = (DrawView)view;
        tmpDW.invertDrawMode();
        tmpDW.invalidate();
    }

    public void sendMessage_butDraw_DoubleClick(View view) {

    }

    Runnable MyThreadRun = new Runnable() {
        public void run() {
            drawView.addOffset();
            drawView.invalidate();
        }
    };
}
