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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
                    Bundle bdl = (Bundle) msg.obj;
                    doullarRate = bdl.getFloat("doullar-rate");
                    poundRate = bdl.getFloat("pound-rate");
                    yenRate = bdl.getFloat("yen-rate");

                    Log.i("run:","doullarRate" +doullarRate );
                    Log.i("run:","poundRate" +poundRate );
                    Log.i("run:","yenRate" +yenRate );

                    Toast.makeText(RateChange.this, "汇率已更新", Toast.LENGTH_SHORT).show();
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
        //用户信息保存
        Bundle bundle = new Bundle();

        //获得信息并发送给handler
        Message msg = handler.obtainMessage(2);
//        msg.obj = "This is message";
        msg.obj = bundle;
        handler.sendMessage(msg);




        //获取网络数据,注意需要在main中添加权限internet
//        URL url = null;
//        try {
//            url = new URL("http://www.usd.cny.com/icbc.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//
//            String html = inputSteamtoString(in);
//            Log.i("run","html="+html);
//            Document doc = Jsoup.parse(html); //从html文件直接转成document对象
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //直接从网络中获得document对象
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Log.i("run...",doc.title());
            //查找需要的数据在哪个table里
            Elements tables = doc.getElementsByTag("table");
            int i = 1;
//            for(Element table : tables){
//                Log.i("table","table["+i+"] = "+table);
//                i++;
//            }
            Element table2 = tables.get(1);
            Log.i("run...","table2="+table2);
            Elements tds = table2.getElementsByTag("td");
            for(int a=0;a<tds.size();a+=8){
                Element td1 = tds.get(a);
                Element td2 = tds.get(a+5);
                Log.i("run:",td1.text()+"==>"+td2.text());
                if("美元".equals(td1.text())){
                    bundle.putFloat("doullar-rate",100f/Float.parseFloat(td2.text()));
                }else if("英镑".equals(td1.text())){
                    bundle.putFloat("pound-rate",100f/Float.parseFloat(td2.text()));
                }else if("日元".equals(td1.text())){
                    bundle.putFloat("yen-rate",100f/Float.parseFloat(td2.text()));
                }
            }

//            for(Element td:tds){
//                Log.i("run:","td="+td);
//                Log.i("run:","text="+td.text());//注意两者可能有区别，也可能没有区别
//                Log.i("run:","html="+td.html());
//            }
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
