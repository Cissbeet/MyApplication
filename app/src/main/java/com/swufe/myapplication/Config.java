package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class Config extends AppCompatActivity {
    public final String TAG = "Config";
    EditText doullar;
    EditText pound;
    EditText yen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent = getIntent();  //先要获取intent对象
        float doullar2 = intent.getFloatExtra("Rate_doullar",0.0f); //获得参数
        float pound2 = intent.getFloatExtra("Rate_pound",0.0f);
        float yen2 = intent.getFloatExtra("Rate_yen",0.0f);

        Log.i(TAG,"onCreate:doullarRate ="+doullar2);
        Log.i(TAG,"onCreate:poundRate ="+pound2);
        Log.i(TAG,"onCreate:yenRate ="+yen2);

        doullar = findViewById(R.id.figDou);
        pound = findViewById(R.id.figpon);
        yen = findViewById(R.id.figYen);

        doullar.setText(String.valueOf(doullar2));
        pound.setText(String.valueOf(pound2));
        yen.setText(String.valueOf(yen2));

    }
    public void save(View btn){
        Log.i("save","Saving");
        //获取新的值
        float newdoullar = Float.parseFloat(doullar.getText().toString());
        float newpound = Float.parseFloat(pound.getText().toString());
        float newyen = Float.parseFloat(yen.getText().toString());
        //保存新的值
        Intent intent = getIntent();   //可以使用bundle对象来组合数据
        Bundle bdl = new Bundle();
        bdl.putFloat("doullar",newdoullar);
        bdl.putFloat("pound",newpound);
        bdl.putFloat("yen",newyen);

        Log.i(TAG,"save:newdoullar ="+newdoullar);
        Log.i(TAG,"save:newpound ="+newpound);
        Log.i(TAG,"save:newyen ="+newyen);
        intent.putExtras(bdl);
        setResult(2,intent);
        //返回调用页面
        finish();

    }
}
