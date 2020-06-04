package com.swufe.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InformActivity extends ListActivity implements Runnable {
    EditText in;
    ListView out;
    String TAG = "run";
    String keyword;
    Handler handler;
    String updateDate = "";
    private ArrayList<HashMap<String, String>> listItem;
    private SimpleAdapter listItemAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        in = findViewById(R.id.inf_edit);
        out = findViewById(R.id.info_list);

        String data[] = {"wait......."};
        ListAdapter adapter = new ArrayAdapter<String>(InformActivity.this, android.R.layout.simple_list_item_1, data);
        out.setAdapter(adapter);

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        //判断时间
        if (!todayStr.equals(updateDate)) {
            Log.i("date", "需要更新");
            //开启子线程
            Thread t = new Thread(this);   //注意有this
            t.start();
        } else {
            Log.i("date", "不需要更新");
        }


        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 7) {
                    List<HashMap<String, String>> list2 = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(InformActivity.this, listItem,
                            R.layout.list_item,
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    Date today = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    editor.putString("update_date",todayStr);
                    editor.apply();
                    setListAdapter(listItemAdapter);
                }
            }
        };
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Parent" + parent);
                HashMap<String, String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
                String url = map.get("ItemDetail");
                String title = map.get("ItemTitle");
                Log.i("openOne", "opening");
                Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(web);
            }

            //获得用户输入
            public void onClick(View btn) {
                keyword = in.getText().toString();
                //提示用户输入数据
                if (keyword.length() > 0) {
                    Log.i(TAG, "run:正在获取数据");
                    //调用子线程
                    Thread t = new Thread(this);
                    t.start();
                } else Toast.makeText(this, "请输入关键字", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void run() {
                List<HashMap<String, String>> reList = new ArrayList<HashMap<String, String>>();
                Document doc = null;
                try {
                    Thread.sleep(3000);
                    doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
                    Log.i("run...", doc.title());
                    Elements divs = doc.getElementsByClass("article-list wow fadeInRight");
                    Log.i(TAG, "run:" + "div" + divs);
                    Element div = divs.get(0);
                    Elements spans = div.getElementsByTag("span");
                    Elements urls = div.getElementsByTag("href");

                    for (int i = 1; i <= 20; i++) {
                        //使用正则表达式匹配
                        String pattern = "(\\d+)?" + keyword + "(\\d+)?";
                        Element span = spans.get(i);
                        String line = span.outerHtml();
                        // 创建 Pattern 对象
                        Pattern r = Pattern.compile(pattern);
                        // 现在创建 matcher 对象
                        Matcher m = r.matcher(line);
                        if (m.lookingAt()) {
                            Element url = urls.get(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ItemTitle", "title" + span.text());//标题
                            map.put("ItemDetail", "url" + "https://it.swufe.edu.cn/" + url.text());//网址
                            reList.add(map);
                        }
                    }

                    Message msg = handler.obtainMessage(7);
                    msg.obj = reList;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override  //传回数据使用的方法
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                if (requestCode == 1 && resultCode == 2) {
                    Bundle bundle = data.getExtras();  //获得bondle对象

                    //获得sp中的数据
                    SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    //注意保存数据和获取数据的sp文件名字要一样，key也要一样
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("update_date",todayStr);
                    editor.commit();  //注意需要保存
                    Log.i("onCreate","数据以保存到SharedPreferences中");
                }
                super.onActivityResult(requestCode,resultCode,data);
            }
        }
    }
}