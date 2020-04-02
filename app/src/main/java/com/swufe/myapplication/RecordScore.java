package com.swufe.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecordScore extends AppCompatActivity {
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_score);
        score = findViewById(R.id.R_tv3);    //需要找到View，用于显示或者获取
    }
    public void btnAdd3(View btn){
        showScore(3);
    }
    public void btnAdd2(View btn){
        showScore(2);
    }
    public void btnAdd1(View btn){
        showScore(1);
    }
    public void btnReset(View btn){
        score.setText("0");
    }
    private void showScore(int add){    //自定义私有方法，用于计算加减
        String oldScore = (String) score.getText();
        int newScore = Integer.parseInt(oldScore) + add;
       score.setText("" + newScore);       //注意setText中必须是字符串类型
    }
}
