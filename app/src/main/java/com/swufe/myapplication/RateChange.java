package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RateChange extends AppCompatActivity implements Runnable{  //实现Runnable接口开启子线程
    EditText in;
    TextView out;
    Handler handler;
    private float doullarRate = 0.1415f;
    private float poundRate = 0.1141f;
    private float yenRate = 15.392f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_change);
        in = findViewById(R.id.Rate_in);
        out = findViewById(R.id.Rate_out);
        //获得sp里的数据
        SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        //也可以使用下面这个方式保存数据，但是只能有一个文件用于保存数据，上面的方法可以有多个文件
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        doullarRate = sp.getFloat("Rate_doullar",0.0f);
        poundRate = sp.getFloat("Rate_pound",0.0f);
        yenRate = sp.getFloat("Rate_yen",0.0f);

        Log.i("doullar", "onCreate:sp   doullarRate =" + doullarRate);
        Log.i("pound", "onCreate:sp  poundRate =" + poundRate);
        Log.i("yen", "onCreate:sp   yenRate =" + yenRate);

        //开启子线程
        Thread t = new Thread(this);   //注意有this
        t.start();

        handler = new Handler(){  //改写父类Handler的方法
            @Override
            public void handleMessage(@NonNull Message msg) {
                //获得子线程返回的信息，并处理信息
                if(msg.what == 5){  //判断信息
                    String info = (String) msg.obj;
                    Log.i("handleMessage","msg ="+info);
                }
                super.handleMessage(msg);

            }
        };
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
            return;
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

            //获得sp中的数据
            SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            //注意保存数据和获取数据的sp文件名字要一样，key也要一样
            SharedPreferences.Editor editor = sp.edit();
            editor.putFloat("Rate_doullar",doullarRate);
            editor.putFloat("Rate_pound",poundRate);
            editor.putFloat("Rate_yen",yenRate);
            editor.commit();  //注意需要保存
            Log.i("onCreate","数据以保存到SharedPreferences中");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    //加载菜单
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


    //加载子线程
    @Override
    public void run() {
        Log.i("run","running......");
        //休眠，有异常需要尝试
        for(int i = 1;i < 6;i++){
            Log.i("run","i="+i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //获得信息并发送给handler
        Message msg = handler.obtainMessage(2);
        msg.obj = "This is message";
        handler.sendMessage(msg);

        //获取网络数据,注意需要在main中添加权限internet
        URL url = null;
        try {
            url = new URL("http://www.usd.cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputSteamtoString(in);
            Log.i("run","html="+html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //定义方法，将网页中的数据转换为字符串
    private  String inputSteamtoString(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final  StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");  //注意编码规则
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz < 0){
                break;
            }
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
