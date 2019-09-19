package com.example.coffee_bt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class sec_page_activity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{
    private static final String TAG = "sec_page_Activity";
    private NumberPicker mNumberPicker;
    private TextView tvShowNumbers;
    Button sendspeed;


    private int value;
    private int mvalue;
    private String Svalue;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_page_activity);

        mNumberPicker =(NumberPicker)findViewById(R.id.picker);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(6);
        //int nowValue = mNumberPicker.getValue();

        tvShowNumbers = (TextView)findViewById(R.id.tvShowNumbers);
        sendspeed = (Button)findViewById(R.id.sendspeed);






        // to change formate of number in numberpicker
        mNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        mNumberPicker.setOnValueChangedListener(this);


        sendspeed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                    byte[] bytes = Svalue.getBytes(Charset.defaultCharset());
                    //mBluetoothConnection.write1(bytes);
                    tvShowNumbers.setText(""+Svalue);
                    /*try {
                }catch (Exception e){
                    Log.d(TAG,"請先連接再送出");
                    Toast.makeText(getApplicationContext(),"請先連接再送出",Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }




    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        //tvShowNumbers.setText(""+i1);
        //tvShowNumbers.setTextSize(80);
        value = Integer.valueOf(mNumberPicker.getValue()).intValue();
        mvalue = value*682;
        Svalue = String.valueOf(mvalue);
    }




    /**
     * 偵測是否退出兩秒內按兩下才會退出
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(getApplicationContext(),"再按一次退出程式",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else {
                finish();
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







}

