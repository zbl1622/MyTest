package com.example.zbl.mytest;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by zbl on 2017/2/27.
 */

public class ViewTestActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_viewtest);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new MyListAdapter());
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 220;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 10 ? 1 : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (getItemViewType(position) == 0) {
                    TextView textView = new TextView(context);
                    textView.setTextSize(40);
                    textView.setText("" + position);
                    textView.setPadding(20,20,20,20);
                    convertView = textView;
                } else {
                    MyView myView = new MyView(context);
                    myView.setMinimumHeight(50);
                    convertView = myView;
                }
            }
            return convertView;
        }
    }
}
