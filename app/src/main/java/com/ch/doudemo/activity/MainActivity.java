package com.ch.doudemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ch.doudemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_page)
    Button btnPage;
    @BindView(R.id.btn_list)
    Button btnList;
    @BindView(R.id.btn_record)
    Button btnList2;
    @BindView(R.id.btn_record2)
    Button btnRecord2;
    @BindView(R.id.btn_page2)
    Button btnPage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        addListener();
    }

    private void addListener() {

    }

    private void initView() {


    }


    @OnClick({R.id.btn_page, R.id.btn_list, R.id.btn_record, R.id.btn_record2, R.id.btn_page2})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_page:
                intent.setClass(MainActivity.this, PageActivity.class);
                break;
            case R.id.btn_list:
                intent.setClass(MainActivity.this, ListActivity.class);
                break;
            case R.id.btn_record:
                intent.setClass(MainActivity.this, RecordActivity.class);
                break;

            case R.id.btn_record2:
                intent.setClass(MainActivity.this, Record2Activity.class);
                break;

            case R.id.btn_page2:
                intent.setClass(MainActivity.this, Page2Activity.class);

                break;
        }

        startActivity(intent);
    }
}
