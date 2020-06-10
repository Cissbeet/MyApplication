package com.swufe.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mylist2Activity extends ListActivity {
    Handler handler;
    private ArrayList<HashMap<String,String>> listItem;
    private SimpleAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list_item);
        initListView();
        //自己定义的adapter
        MyAdapter myadapter = new MyAdapter(this,R.layout.list_item,listItem);
        setListAdapter(myadapter);
    }
    private void initListView(){
        listItem = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("ItemTitle","title" + i);//标题
            map.put("ItemDetail","url" + i);//网址
            listItem.add(map);
        }

        //准备系统已有的adapter,设置参数
        listItemAdapter = new SimpleAdapter(this,listItem, //准备数据
                R.layout.list_item,//准备布局
                new String[] {"ItemTitle","ItemDetail"},//数据安排在布局当中
                new  int[] {R.id.itemTitle,R.id.itemDetail}
                );
    }
}
