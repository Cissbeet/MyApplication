package com.swufe.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TempTran extends AppCompatActivity {
    TextView out;
    EditText inp;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        out = findViewById(R.id.showText);
        inp = findViewById(R.id.inpText);
        btn = findViewById(R.id.btn);
//       btn.setOnClickListener(this);
//        btn.setOnClickListener(new View.OnClickListener() {
//           @Override
//            public void onClick(View v) {
//               Log.i("click","clicking....");
//            }
//       });
//        }


//   @Override
//   public void onClick(View v) {
//        Log.i("click","clicking......");
//        String str = inp.getText().toString();
//        out.setText("hello" + str);
   }
        public void btnclick(View btn){
        Log.i("click", "btnclick call.....");

        String num = inp.getText().toString();
        String sign = num.substring(num.length() - 1);
        if (sign.equals("C")) {
            float temp = Float.parseFloat(num.substring(0, num.length() - 1));
            float temp_change = (temp * 9) / 5 + 32;
            out.setText("结果：  " + temp_change + "F");
        }
        else if (sign.equals("F")) {
            float temp = Float.parseFloat(num.substring(0, num.length() - 1));
            float temp_change = (temp - 32) * 5 / 9;
            out.setText("结果：  " + temp_change + "C");
        }
        else {
            out.setText("请输入正确的温度值");
            }
        }

    }


