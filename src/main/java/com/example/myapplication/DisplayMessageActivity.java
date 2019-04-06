package com.example.myapplication;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.ToggleButton;

public class DisplayMessageActivity extends AppCompatActivity {
    DrawView drawView;
    Thread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        drawView = new DrawView(this);
        Intent intent = getIntent();
        drawView.loadFromIntent(intent,true,true);

        LinearLayout linearLayout00 =  findViewById(R.id.layort00);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);

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
        drawView.invertDrawMode();
        this.setMinTemperature();
        this.setMaxTemperature();
        this.setAccuracy();
        this.setAlarmIgnore();
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

    public void sendMessage_butDraw_SingleClick(View view) {
        DrawView tmpDW = (DrawView)view;
        tmpDW.invertDrawMode();
        tmpDW.invalidate();
    }

    public void sendMessage_butDraw_DoubleClick(View view) {
        this.exitFromIntent(0);
    }

    Runnable MyThreadRun = new Runnable() {
        public void run() {
            drawView.addOffset();
            drawView.invalidate();
        }
    };

    private void setMinTemperature(){
        TextView _editText = findViewById(R.id.textViewmin);
        _editText.setText(String.format("%.1f", drawView.lowTemperature));
    }

    public void sendMessage_button_minUp(View view) {
        if (this.drawView.lowTemperature >= (this.drawView.hightTemperature - 1)) return;
        this.drawView.lowTemperature += 0.1;
        this.setMinTemperature();
    }

    public void sendMessage_button_minDown(View view) {
        if (drawView.lowTemperature <= 13.0) return;
        this.drawView.lowTemperature -= 0.1;
        this.setMinTemperature();
    }

    private void setMaxTemperature(){
        TextView _editText = findViewById(R.id.textViewmax);
        _editText.setText(String.format("%.1f", drawView.hightTemperature));
    }

    public void sendMessage_button_maxUp(View view) {
        if (this.drawView.hightTemperature >= 32) return;
        this.drawView.hightTemperature += 0.1;
        this.setMaxTemperature();
    }

    public void sendMessage_button_maxDown(View view) {
        if (drawView.hightTemperature <= (this.drawView.lowTemperature + 1)) return;
        this.drawView.hightTemperature -= 0.1;
        this.setMaxTemperature();
    }

    private void setAccuracy(){
        boolean _Accuracy = (drawView.tempAccuracy.equals("%.1f"));
        Log.i("Accuracy", Boolean.toString(_Accuracy));
        Log.i("tempAccuracy", drawView.tempAccuracy);
        ToggleButton _editText = findViewById(R.id.checkBox_1);
        _editText.setChecked(_Accuracy);
    }

    public void sendMessage_button_Accuracy(View view) {
        if (drawView.tempAccuracy.equals("%.1f"))
            drawView.tempAccuracy = "%.0f";
        else
            drawView.tempAccuracy = "%.1f";
    }

    private void setAlarmIgnore(){
        ToggleButton _editText = findViewById(R.id.checkBox_2);
        _editText.setChecked(drawView.alarmIgnore);
    }

    public void sendMessage_button_AlarmIgnore(View view) {
        drawView.alarmIgnore = !drawView.alarmIgnore;
    }

    public void sendMessage_button_Save(View view) {
        this.exitFromIntent(1);
    }

    public void sendMessage_button_SaveAll(View view) {
        this.exitFromIntent(2);
    }

    public void sendMessage_button_Cancel(View view) {
        this.exitFromIntent(0);
    }

    public void exitFromIntent(int result) {
        Intent intent = getIntent();
        this.drawView.saveToIntent(intent);
        setResult(result, intent);
        finish();
    }


}
