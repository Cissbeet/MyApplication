package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class RateListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener {
    String data[] = {"wait....."};//数组，长度固定
    Handler handler;
    List<HashMap<String,String>> ListItem;
    private SimpleAdapter listItemAdapter;
    String TAG = "RateList";
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获得当前SP中的时间
        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        Log.i("List","lastRateDateStr=" + logDate);
        List<String> list1 = new ArrayList<String>();   //创建list对象用于存放数据，长度可变，里面的string表示数据的类型
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
                    ListItem = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(RateListActivity.this,ListItem, //准备数据
                            R.layout.list_item,//准备布局
                            new String[] {"ItemTitle","ItemDetail"},//数据安排在布局当中
                            new  int[] {R.id.itemTitle,R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void run() {
        Log.i("List","run...");
        //这里的relist用于保存获得
        List<String> retList = new ArrayList<String>();
        //获取时间比对
        Message msg = handler.obtainMessage();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run","curDateStr:" + curDateStr + " logDate:" + logDate);
        if(curDateStr.equals(logDate)){
            //如果相等，则不从网络中获取数据
            Log.i("run","日期相等，从数据库中获取数据");
            DBManager dbManager = new DBManager(RateListActivity.this);
            for(RateItem rateItem : dbManager.listAll()){
                retList.add(rateItem.getCurName() + "=>" + rateItem.getCurRate());
            }
        }else{
            Log.i("run","日期不相等，从网络中获取在线数据");
            //获取网络数据
            Document doc = null;
            try {
                //这里的relist用于向数据库中传递信息
                List<RateItem> rateList = new ArrayList<RateItem>();
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                Log.i("run...",doc.title());

                //Log.i("WWW","retStr:" + retStr);
                //需要对获得的html字串进行解析，提取相应的汇率数据...


                Elements tables  = doc.getElementsByTag("table");

                Element retTable = tables.get(5);
                Elements tds = retTable.getElementsByTag("td");
                int tdSize = tds.size();
                for(int i=0;i<tdSize;i+=8){
                    Element td1 = tds.get(i);
                    Element td2 = tds.get(i+5);
                    //Log.i("www","td:" + td1.text() + "->" + td2.text());
                    float val = Float.parseFloat(td2.text());
                    val = 100/val;
                    retList.add(td1.text() + "->" + val);

                    RateItem rateItem = new RateItem(td1.text(),td2.text());
                    rateList.add(rateItem);
                }

                //删除数据库中原有的值，重新增添值
                DBManager dbManager = new DBManager(RateListActivity.this);
                dbManager.deleteAll();
                Log.i("db","删除所有记录");
                dbManager.addAll(rateList);
                Log.i("db","添加新记录集");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //更新记录日期
            SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(DATE_SP_KEY, curDateStr);
            edit.commit();
            Log.i("run","更新日期结束：" + curDateStr);
        }

        msg.obj = retList;
        msg.what = 7;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String TAG = "onItemClick";
        Log.i(TAG, "onItemClick: parent=" + parent);
        Log.i(TAG, "onItemClick: view=" + view);
        Log.i(TAG, "onItemClick: position=" + position);
        Log.i(TAG, "onItemClick: id=" + id);

        //从ListView中获取选中数据
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr=" + titleStr);
        Log.i(TAG, "onItemClick: detailStr=" + detailStr);

        //从View中获取选中数据
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2=" + title2);
        Log.i(TAG, "onItemClick: detail2=" + detail2);

        //打开新的页面，传入参数
        Intent rateCalc = new Intent(this,RateCalc.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG, "onItemLongClick: ");

//        ListItem.remove(position);
//        listItemAdapter.notifyDataSetChanged();
        //创建提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("是否删除信息：").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListItem.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("否",null);

        return false;//选择false则是可以执行点击操作，选择true则是只执行长按操作
    }
}
