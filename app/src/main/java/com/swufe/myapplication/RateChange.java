package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RateChange extends AppCompatActivity {
    EditText in;
    TextView out;
    private float doullarRate = 0.1415f;
    private float poundRate = 0.1141f;
    private float yenRate = 15.392f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_change);
        in = findViewById(R.id.Rate_in);
        out = findViewById(R.id.Rate_out);
    }

    public void onclick(View btn) {
        String rmb = in.getText().toString();//获取当前用户输
        float r = 0;
        double showout;
        if (rmb.length() > 0) {
            r = Float.parseFloat(rmb);
        } else {
            //提示用户输入信息
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        if (btn.getId() == R.id.Rate_dol) {
            showout = r * doullarRate;
        } else if (btn.getId() == R.id.Rate_pou) {
            showout = r * poundRate;
        } else {
            showout = r * yenRate;
        }
        out.setText("结果：" + showout);
    }

    //    public void openOne(View btn){
//        Log.i("openOne","opening");
//        Intent hello = new Intent(this,RecordScore.class);
//        Intent url = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));
//        Intent number = new Intent(Intent.ACTION_VIEW,Uri.parse("tel:870984312"));
//        startActivity(hello);
//    }
    public void openOne(View btn) {
        openConfig();
    }

    private void openConfig() {
        Log.i("openOne", "opening");
        Intent config = new Intent(this, Config.class);   //跳转页面，使用intent对象
        config.putExtra("Rate_doullar", doullarRate);    //获得本页面的参数，并传递给下个页面
        config.putExtra("Rate_pound", poundRate);
        config.putExtra("Rate_yen", yenRate);

        Log.i("doullar", "openOne:doullarRate =" + doullarRate);
        Log.i("pound", "openOne:poundRate =" + poundRate);
        Log.i("yen", "openOne:yenRate =" + yenRate);
//        startActivity(config);    //跳转页面
        startActivityForResult(config, 1);
    }

    @Override  //传回数据使用的方法
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();  //获得bondle对象
            doullarRate = bundle.getFloat("newdoullar",0.0f);
            doullarRate = bundle.getFloat("newpound",0.0f);
            doullarRate = bundle.getFloat("newyen",0.0f);

            Log.i("doullar", "onA:doullarRate =" + doullarRate);
            Log.i("pound", "onA:poundRate =" + poundRate);
            Log.i("yen", "onA:yenRate =" + yenRate);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_settings){
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }
}
