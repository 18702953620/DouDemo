package com.ch.doudemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.btn_page)
    Button btnPage;
    @BindView(R.id.btn_list)
    Button btnList;
    @BindView(R.id.btn_list2)
    Button btnList2;

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


    @OnClick({R.id.btn_page, R.id.btn_list, R.id.btn_list2})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_page:
                intent.setClass(MainActivity.this, PageActivity.class);
                break;
            case R.id.btn_list:
                intent.setClass(MainActivity.this, ListActivity.class);
                break;
            case R.id.btn_list2:
                intent.setClass(MainActivity.this, List2Activity.class);
                break;
        }

        startActivity(intent);
    }
}
