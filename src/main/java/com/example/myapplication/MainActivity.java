package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent; // подключаем класс Intent
import android.view.View; // подключаем класс View для обработки нажатия кнопки
import android.os.SystemClock;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    Object[]  drawView;

    Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout02 =  findViewById(R.id.layort02);
        LinearLayout linearLayout03 =  findViewById(R.id.layort03);

        // Инициализация DrawViews
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, 1);


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

        this.drawView = new Object[5];
        DrawView tmp_drawView;

        tmp_drawView = new DrawView(this);
        tmp_drawView.generateRandomOffset();
        tmp_drawView.setOnClickListener(oclBtnOk);
        tmp_drawView.setLayoutParams(lp);
        tmp_drawView.capture = "кухня";
        linearLayout02.addView(tmp_drawView);
        this.drawView[0] = tmp_drawView;

        tmp_drawView = new DrawView(this);
        tmp_drawView.generateRandomOffset();
        tmp_drawView.setOnClickListener(oclBtnOk);
        tmp_drawView.setLayoutParams(lp);
        tmp_drawView.capture = "каб.301.";
        linearLayout02.addView(tmp_drawView);
        this.drawView[1] = tmp_drawView;

        tmp_drawView = new DrawView(this);
        tmp_drawView.generateRandomOffset();
        tmp_drawView.setOnClickListener(oclBtnOk);
        tmp_drawView.setLayoutParams(lp);
        tmp_drawView.capture = "каб.302.";
        linearLayout02.addView(tmp_drawView);
        this.drawView[2] = tmp_drawView;

        tmp_drawView = new DrawView(this);
        tmp_drawView.generateRandomOffset();
        tmp_drawView.setOnClickListener(oclBtnOk);
        tmp_drawView.setLayoutParams(lp);
        tmp_drawView.capture = "конферец зал";
        linearLayout03.addView(tmp_drawView);
        this.drawView[3] = tmp_drawView;

        tmp_drawView = new DrawView(this);
        tmp_drawView.generateRandomOffset();
        tmp_drawView.setOnClickListener(oclBtnOk);
        tmp_drawView.setLayoutParams(lp);
        tmp_drawView.capture = "коридор";
        linearLayout03.addView(tmp_drawView);
        this.drawView[4] = tmp_drawView;
        //------------------------------

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

    protected void writePreferences(DrawView _drawView){
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putInt("offset", _drawView.offset);
        myEditor.putFloat("lowTemperature", (float)_drawView.lowTemperature);
        myEditor.putFloat("hightTemperature", (float)_drawView.hightTemperature);
        myEditor.putInt("offset", _drawView.offset);
        myEditor.putString("capture", _drawView.capture);
        myEditor.commit();
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

    Runnable MyThreadRun = new Runnable() {
        public void run() {
            for (int i = 0; i < drawView.length; i++){
                if (drawView[i] == null) break;
                DrawView tmpDrawView = (DrawView)drawView[i];
                tmpDrawView.addOffset();
                tmpDrawView.invalidate();
            }
        }
    };

    public void sendMessage_butDraw_SingleClick(View view) {
        DrawView tmpDW = (DrawView)view;
        tmpDW.invertDrawMode();
        tmpDW.invalidate();
        Log.i("Click","SingleClick");
    }

    public void sendMessage_butDraw_DoubleClick(View view) {
        this.sendMessage_butDraw_SingleClick(view); //Вызываю потому что было первое нажатиє а на самом деле DblClick
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        this.writePreferences((DrawView)view);
        Log.i("Click","DoubleClick");
        startActivity(intent);
    }
}
