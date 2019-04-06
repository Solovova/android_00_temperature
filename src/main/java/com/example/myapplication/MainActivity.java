package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent; // подключаем класс Intent
import android.view.View; // подключаем класс View для обработки нажатия кнопки
import android.widget.EditText; // подключаем класс EditText
import android.os.SystemClock;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    DrawView drawView1;
    DrawView drawView2;
    DrawView drawView3;
    Thread myThread;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.drawView1 = (DrawView) findViewById(R.id.drawview1);
        this.drawView2 = (DrawView) findViewById(R.id.drawview2);
        this.drawView3 = (DrawView) findViewById(R.id.drawview3);
        this.editText = (EditText) findViewById(R.id.edit_message);

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

    Runnable MyThreadRun = new Runnable() {
        public void run() {
            drawView1.setOffset();
            drawView1.invalidate();
            drawView2.setOffset();
            drawView2.invalidate();
            drawView3.setOffset();
            drawView3.invalidate();
            editText.setText(Integer.toString(drawView1.getOffset()));
        }
    };

    // Метод обработки нажатия на кнопку
    public void sendMessage(View view) {
        // действия, совершаемые после нажатия на кнопку
        // Создаем объект Intent для вызова новой Activity
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        // Получаем текстовое поле в текущей Activity
        EditText editText = (EditText) findViewById(R.id.edit_message);
        // Получае текст данного текстового поля
        String message = editText.getText().toString();
        // Добавляем с помощью свойства putExtra объект - первый параметр - ключ,
        // второй параметр - значение этого объекта
        intent.putExtra(EXTRA_MESSAGE, message);
        // запуск activity
        startActivity(intent);
    }

    // Метод обработки нажатия на кнопку
    public void sendMessage_butDraw(View view) {
        DrawView tmpDW = (DrawView)view;
        tmpDW.invertDrawMode();
        tmpDW.invalidate();
    }
}
