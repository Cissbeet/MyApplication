package com.swufe.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MylistActivity extends ListActivity {
    Handler handler;
    private ArrayList<HashMap<String,String>> listItem;
    private SimpleAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list_item);
        initListView();
        this.setListAdapter(listItemAdapter);
    }
    private void initListView(){
        listItem = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<10;i++){
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("ItemTitle","title" + i);//标题
            map.put("ItemDetail","url" + i);//网址
            listItem.add(map);
        }
        listItemAdapter = new SimpleAdapter(this,listItem,
                R.layout.list_item,
                new String[] {"ItemTitle","ItemDetail"},
                new  int[] {R.id.itemTitle,R.id.itemDetail}
                );
    }
}
