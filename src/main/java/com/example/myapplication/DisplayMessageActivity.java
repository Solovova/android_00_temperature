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

    protected void initDrawView(){
        drawView = new DrawView(this);
        Intent intent = getIntent();
        drawView.setOffset(intent.getIntExtra("offset", 0));
        drawView.lowTemperature = intent.getFloatExtra("lowTemperature", 0);
        drawView.hightTemperature = intent.getFloatExtra("hightTemperature", 0);
        drawView.capture = intent.getStringExtra("capture");
        drawView.tempAccuracy = intent.getStringExtra("tempAccuracy");
        drawView.alarmIgnore = intent.getBooleanExtra("alarmIgnore", false);

        Log.i("READ lowTemperature",Double.toString(drawView.lowTemperature));
        Log.i("READ hightTemperature",Double.toString(drawView.hightTemperature));
        Log.i("READ offset",Double.toString(drawView.offset));
        Log.i("READ tempAccuracy",drawView.tempAccuracy);
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

    private void setMinTemperature(){
        String _mintemptext = "Мін. темп.: " + String.format("%.1f", drawView.lowTemperature);
        TextView _editText = findViewById(R.id.textViewmin);
        _editText.setText(_mintemptext);
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
        String _maxtemptext = "Макс. темп.: " + String.format("%.1f", drawView.hightTemperature);
        TextView _editText = findViewById(R.id.textViewmax);
        _editText.setText(_maxtemptext);
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
        CheckBox _editText = findViewById(R.id.checkBox_1);
        _editText.setChecked(_Accuracy);
    }

    public void sendMessage_button_Accuracy(View view) {
        if (drawView.tempAccuracy.equals("%.1f"))
            drawView.tempAccuracy = "%.0f";
        else
            drawView.tempAccuracy = "%.1f";
    }

    private void setAlarmIgnore(){
        CheckBox _editText = findViewById(R.id.checkBox_2);
        _editText.setChecked(drawView.alarmIgnore);
    }

    public void sendMessage_button_AlarmIgnore(View view) {
        drawView.alarmIgnore = !drawView.alarmIgnore;
    }

    public void sendMessage_button_Save(View view) {
        Intent intent = getIntent();

        intent.putExtra("offset", this.drawView.offset);
        intent.putExtra("lowTemperature", (float)this.drawView.lowTemperature);
        intent.putExtra("hightTemperature", (float)this.drawView.hightTemperature);
        intent.putExtra("capture", this.drawView.capture);
        intent.putExtra("tempAccuracy", this.drawView.tempAccuracy);
        intent.putExtra("alarmIgnore", this.drawView.alarmIgnore);

        setResult(1, intent);
        finish();
    }

    public void sendMessage_button_SaveAll(View view) {
        Intent intent = getIntent();

        intent.putExtra("offset", this.drawView.offset);
        intent.putExtra("lowTemperature", (float)this.drawView.lowTemperature);
        intent.putExtra("hightTemperature", (float)this.drawView.hightTemperature);
        intent.putExtra("capture", this.drawView.capture);
        intent.putExtra("tempAccuracy", this.drawView.tempAccuracy);
        intent.putExtra("alarmIgnore", this.drawView.alarmIgnore);

        setResult(2, intent);
        finish();
    }

    public void sendMessage_button_Cancel(View view) {
        Intent intent = getIntent();

        setResult(0, intent);
        finish();
    }


}
