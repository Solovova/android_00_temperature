//ToDo  1. точность отображения
//      2. размещение по карте дома или по рядам (быстро кнопки внизу)
//      3. показывать только проблемные зоны
//      4. 2-й ап - увеличить на весь экран
//      5. тестовый режим

package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.util.AttributeSet;
import android.graphics.Rect;

public class DrawView extends View {
    Paint paint = new Paint();
    int x;

    double[] grx;
    double[] gry;
    double lowTemperature;
    double hightTemperature;

    // прорисовка
    int border;
    int drawMode;
    String tempAccuracy; //количество цифр после точки

    private void init() {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        this.x = 0;
        this.drawMode = 0;
        this.lowTemperature = 18.0;
        this.hightTemperature = 24.0;
        this.border = 20;
        this.tempAccuracy = "%.0f";

        this.generateTestData();
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

        int offset_random = (int)Math.round(Math.random()* td_len);
        for (int j = 0; j < offset_random; j++) {
            double tmp = this.gry[0];
            for (int i = 0; i < this.gry.length - 1; i++ )
                this.gry[i] = this.gry[i+1];
            this.gry[this.gry.length - 1] = tmp;
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

    public void setOffset() {
        this.x++;
        double tmp = this.gry[0];
        for (int i = 0; i < this.gry.length - 1; i++ )
            this.gry[i] = this.gry[i+1];
        this.gry[this.gry.length - 1] = tmp;
    }

    public int getOffset() {
        return this.x;
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
        double scaley = (double)(height - this.border * 2) / (maxy - miny);

        double ylowTemperature = (double)(this.lowTemperature - miny) * scaley + this.border;
        double yhightTemperature = (double)(this.hightTemperature - miny) * scaley + this.border;
        paint.setColor(Color.BLUE);
        canvas.drawLine(this.border, height - Math.round(ylowTemperature), width - this.border, height - Math.round(ylowTemperature), paint);

        paint.setColor(Color.RED);
        canvas.drawLine(this.border, height - Math.round(yhightTemperature), width - this.border, height - Math.round(yhightTemperature), paint);


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

    }

    private void draw0(Canvas canvas) {
        this.draw_border (canvas);
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
        else
            paint.setColor(Color.rgb(20,180,20)); //0x55005500



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