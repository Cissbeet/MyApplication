package com.swufe.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateChange extends AppCompatActivity {
    EditText in;
    TextView out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_change);
        in = findViewById(R.id.Rate_in);
        out = findViewById(R.id.Rate_out);
    }
    public void onclick(View btn){
        String rmb = in.getText().toString();//获取当前用户输
        float r = 0;
        double showout;
        if(rmb.length()>0){
            r = Float.parseFloat(rmb);
        }else{
            //提示用户输入信息
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        if(btn.getId()==R.id.Rate_dol){
            showout = r * 0.1415;
        }else if(btn.getId()==R.id.Rate_pou) {
            showout = r * 0.1141;
        }else {
            showout = r * 15.392;
        }
        out.setText("结果："+ showout);
    }

}
