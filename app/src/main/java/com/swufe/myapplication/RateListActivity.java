package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"wait....."};//数组，长度固定
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> list1 = new ArrayList<String>();
        for(int i=0;i<100;i++){
            list1.add("item"+i);
        }
//        setContentView(R.layout.activity_rate_list);
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);//需要当前类继承LIstActivity才能使用这个方法

        //调用子线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what==7){
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
            }
        };
    }

    @Override
    public void run() {
        //获取数据
        List<String> reList = new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(3000);
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
                reList.add(td1.text()+"==>"+td2.text());
            }

//            for(Element td:tds){
//                Log.i("run:","td="+td);
//                Log.i("run:","text="+td.text());//注意两者可能有区别，也可能没有区别
//                Log.i("run:","html="+td.html());
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = reList;
        handler.sendMessage(msg);
    }
}
