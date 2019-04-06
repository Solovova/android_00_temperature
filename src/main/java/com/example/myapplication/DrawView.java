package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Rect;

public class DrawView extends View {
    Paint paint = new Paint();
    public int offset;

    double[] grx;
    double[] gry;
    public double lowTemperature;
    public double hightTemperature;
    public boolean alarmIgnore;
    public boolean dontWriteGreen;
    String capture;

    // прорисовка
    int border;
    int drawMode;
    String tempAccuracy; //количество цифр после точки

    private void init() {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        this.offset = 0;
        this.drawMode = 0;
        this.lowTemperature = 18.0;
        this.hightTemperature = 24.0;
        this.border = 20;
        this.tempAccuracy = "%.0f";
        this.alarmIgnore = false;
        this.dontWriteGreen = false;
        this.capture = "test";

        this.generateTestData();
    }

    public void saveToIntent(Intent data) {
        data.putExtra("offset", this.offset);
        data.putExtra("lowTemperature", (float)this.lowTemperature);
        data.putExtra("hightTemperature", (float)this.hightTemperature);
        data.putExtra("capture", this.capture);
        data.putExtra("tempAccuracy", this.tempAccuracy);
        data.putExtra("alarmIgnore", this.alarmIgnore);
    }

    public void loadFromIntent(Intent data, boolean loadCapture, boolean loadOffset) {
        this.lowTemperature = data.getFloatExtra("lowTemperature", 0);
        this.hightTemperature = data.getFloatExtra("hightTemperature", 0);
        this.tempAccuracy = data.getStringExtra("tempAccuracy");
        this.alarmIgnore = data.getBooleanExtra("alarmIgnore", false);
        if (loadCapture) this.capture = data.getStringExtra("capture");
        if (loadOffset) this.setOffset(data.getIntExtra("offset", 0));
    }


    private void generateTestData() {
        int td_len = 63;
        double td_min_temp = 12.0;
        double td_max_temp = 33.0;
        this.grx = new double[td_len];
        this.gry = new double[td_len];
        for (int i = 0; i < td_len; i++) {
            this.grx[i] = i;
            this.gry[i] = Math.sin((double) i / 5.0) * 30.0 + Math.sin((double) i / 10.0) * 20.0;
        }
        double td_min_temp_is = 1000.0;
        double td_max_temp_is = 0.0;
        for (int i = 0; i < td_len; i++) {
            if (this.gry[i] > td_max_temp_is) td_max_temp_is = this.gry[i];
            if (this.gry[i] < td_min_temp_is) td_min_temp_is = this.gry[i];
        }
        double rescale = (td_max_temp_is - td_min_temp_is) / (td_max_temp - td_min_temp);
        for (int i = 0; i < td_len; i++) {
            this.gry[i] = (this.gry[i] - td_min_temp_is) / rescale + td_min_temp;
        }
    }

    public void generateRandomOffset() {
        int offset_random = (int)Math.round(Math.random()* this.grx.length);
        for (int j = 0; j < offset_random; j++) {
            this.addOffset();
        }
    }

    public void setOffset(int _offset) {
        while (this.offset % this.grx.length != _offset % this.grx.length) {
            this.addOffset();
        }
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void addOffset() {
        this.offset++;
        double tmp = this.gry[0];
        for (int i = 0; i < this.gry.length - 1; i++ )
            this.gry[i] = this.gry[i+1];
        this.gry[this.gry.length - 1] = tmp;
    }

    public void invertDrawMode(){
        if (this.drawMode == 0)
            this.drawMode = 1;
        else
            this.drawMode = 0;
    }

    private void draw_border (Canvas canvas) {
        paint.setColor(Color.BLACK);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int border = 10;
        canvas.drawLine(border, border, width - border, border, paint);
        canvas.drawLine(width - border, border, width - border, height - border, paint);
        canvas.drawLine(width - border, height - border, border, height - border, paint);
        canvas.drawLine(border, height - border, border,border, paint);
    }

    private void draw1(Canvas canvas) {
        this.draw_border (canvas);
        paint.setColor(Color.BLACK);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //normalize
        double minx = 1000.0;
        double maxx = 0.0;
        double miny = 1000.0;
        double maxy = 0.0;
        for (int i = 0; i < this.grx.length; i++ ){
            if (this.grx[i] < minx) minx = this.grx[i];
            if (this.grx[i] > maxx) maxx = this.grx[i];
            if (this.gry[i] < miny) miny = this.gry[i];
            if (this.gry[i] > maxy) maxy = this.gry[i];
        }

        double[] dgrx = new double[this.grx.length];
        double[] dgry = new double[this.grx.length];

        double scalex = (double)(width - this.border * 2) / (maxx - minx);
        double scaley = (double)(height - this.border * 2) / (maxy - miny) * 0.9;

        double ylowTemperature = (double)(this.lowTemperature - miny) * scaley + this.border;
        double yhightTemperature = (double)(this.hightTemperature - miny) * scaley + this.border;
        paint.setColor(Color.BLUE);
        canvas.drawLine(this.border, height - Math.round(ylowTemperature), width - this.border, height - Math.round(ylowTemperature), paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(30);
        canvas.drawText(String.format("%.1f", this.lowTemperature), this.border , height - Math.round(ylowTemperature) + 25 , paint);

        paint.setColor(Color.RED);
        canvas.drawLine(this.border, height - Math.round(yhightTemperature), width - this.border, height - Math.round(yhightTemperature), paint);

        canvas.drawText(String.format("%.1f", this.hightTemperature), this.border , height - Math.round(yhightTemperature) - 5 , paint);


        for (int i = 0; i < this.grx.length; i++ ){
            dgrx[i] = (this.grx[i] - minx) * scalex + this.border;
            dgry[i] = (this.gry[i] - miny) * scaley + this.border;
        }

        for (int i = 0; i < this.grx.length - 1; i++ ) {
            double temp = this.gry[i+1];
            if (temp < this.lowTemperature)
                paint.setColor(Color.BLUE);
            else if (temp > this.hightTemperature)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.rgb(20,180,20));
            canvas.drawLine(Math.round(dgrx[i]), height - Math.round(dgry[i]), Math.round(dgrx[i+1]), height - Math.round(dgry[i+1]), paint);
        }

        float textsize = (float)50.0;
        paint.setColor(Color.BLACK);
        paint.setTextSize(textsize);
        double _temp = this.gry[this.gry.length - 1];
        String _tempstr = String.format(this.tempAccuracy, _temp);
        if (_temp < this.lowTemperature)
            paint.setColor(Color.BLUE);
        else if (_temp > this.hightTemperature)
            paint.setColor(Color.RED);
        else
            paint.setColor(Color.GREEN);

        paint.setTextAlign(Paint.Align.LEFT);
        Rect r = new Rect();
        paint.getTextBounds(_tempstr, 0, _tempstr.length(), r);
        float x = width  - r.width()  - r.left - this.border;
        float y = height  - r.height()  - r.bottom;
        canvas.drawText(_tempstr, x, y, paint);

        this.draw_capture(canvas);

    }

    private void draw_capture(Canvas canvas) {
        if (this.capture == "") return;
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        paint.setTextAlign(Paint.Align.LEFT);
        Rect r = new Rect();
        paint.getTextBounds(this.capture, 0, this.capture.length(), r);
        float x = width  - r.width()  - r.left - this.border;
        float y = this.border + r.height();
        canvas.drawText(this.capture, x, y, paint);

    }

    private void draw0(Canvas canvas) {
        this.draw_border (canvas);
        this.draw_capture(canvas);
        if (this.alarmIgnore) return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int textsize = Math.min((height - this.border),(width - this.border)) / 2;
        paint.setColor(Color.BLACK);
        paint.setTextSize(textsize);
        double _temp = this.gry[this.gry.length - 1];
        String _tempstr = String.format(this.tempAccuracy, _temp);
        if (_temp < this.lowTemperature)
            paint.setColor(Color.BLUE);
        else if (_temp > this.hightTemperature)
            paint.setColor(Color.RED);
        else {
            paint.setColor(Color.rgb(20,180,20)); //0x55005500
            if (this.dontWriteGreen) return;
        }




        paint.setTextAlign(Paint.Align.LEFT);
        Rect r = new Rect();
        paint.getTextBounds(_tempstr, 0, _tempstr.length(), r);
        float x = width / 2f - r.width() / 2f - r.left;
        float y = height / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(_tempstr, x, y, paint);

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (this.drawMode == 0) this.draw0(canvas);
        if (this.drawMode == 1) this.draw1(canvas);
    }

}