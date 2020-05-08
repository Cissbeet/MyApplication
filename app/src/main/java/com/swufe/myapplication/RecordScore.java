package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecordScore extends AppCompatActivity {
    TextView score;
    TextView score2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_score);
        score = findViewById(R.id.R_tv3);//需要找到View，用于显示或者获取
        score2 = findViewById(R.id.R_tv3b);
    }
    public void btnAdd3(View btn){
        if(btn.getId()==R.id.R_btn1){  //使用条件判断来区别同一方法不同对象调用
            showScore(3);
        }else {
            showScore2(3);
        }

    }
    public void btnAdd2(View btn){

        if(btn.getId()==R.id.R_btn2){
            showScore(2);
        }else {
            showScore2(2);
        }
    }
    public void btnAdd1(View btn){

        if(btn.getId()==R.id.R_btn3){
            showScore(1);
        }else {
            showScore2(1);
        }
    }
    public void btnReset(View btn){

        score.setText("0");
        score2.setText("0");
    }
    private void showScore(int add){    //自定义私有方法，用于计算加减
        String oldScore = (String) score.getText();
        int newScore = Integer.parseInt(oldScore) + add;
       score.setText("" + newScore);       //注意setText中必须是字符串类型
    }
    private void showScore2(int add){    //自定义私有方法，用于计算加减
        String oldScore = (String) score2.getText();
        int newScore = Integer.parseInt(oldScore) + add;
        score2.setText("" + newScore);       //注意setText中必须是字符串类型
    }

    //保存旋转时的数据
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String score = ((TextView)findViewById(R.id.R_tv3)).getText().toString();
        String score2 = ((TextView)findViewById(R.id.R_tv3b)).getText().toString();
        Log.i("run:","onSaveInstanceState");
        outState.putString("score",score);
        outState.putString("score2",score2);
    }

    //旋转之后，恢复旋转前的数据
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String score = savedInstanceState.getString("score");
        String score2 = savedInstanceState.getString("score2");
        Log.i("run:","onRestoreInstanceState");
        ((TextView)findViewById(R.id.R_tv3)).setText(score);
        ((TextView)findViewById(R.id.R_tv3b)).setText(score2);
    }
}
