package com.ch.doudemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ch.doudemo.R;
import com.ch.doudemo.widget.VerticalViewPager;
import com.ch.doudemo.adapter.VerticalViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 翻页
 */
public class PageActivity extends AppCompatActivity {

    @BindView(R.id.vvp_back_play)
    VerticalViewPager vvpBackPlay;
    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        ButterKnife.bind(this);
        initView();
        addListener();
    }

    private void addListener() {

    }

    private void initView() {
        makeData();
        VerticalViewPagerAdapter pagerAdapter = new VerticalViewPagerAdapter(getSupportFragmentManager());
        vvpBackPlay.setVertical(true);
        vvpBackPlay.setOffscreenPageLimit(10);
        pagerAdapter.setUrlList(urlList);
        vvpBackPlay.setAdapter(pagerAdapter);


    }

    private void makeData() {
        urlList = new ArrayList<>();
        urlList.add("http://image.38.hn/public/attachment/201805/100651/201805181532123423.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803151735198462.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150923220770.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150922255785.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150920130302.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803141625005241.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803141624378522.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803131546119319.mp4");
    }
}
