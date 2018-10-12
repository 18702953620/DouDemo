package com.ch.doudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ch.doudemo.base.BaseRecAdapter;
import com.ch.doudemo.base.BaseRecViewHolder;
import com.ch.doudemo.widget.MyVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayerStandard;

public class List2Activity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;
    private ArrayList<String> urlList;
    private ListVideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        ButterKnife.bind(this);


        urlList = new ArrayList<>();
        urlList.add("http://image.38.hn/public/attachment/201805/100651/201805181532123423.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803151735198462.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150923220770.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150922255785.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803150920130302.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803141625005241.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803141624378522.mp4");
        urlList.add("http://image.38.hn/public/attachment/201803/100651/201803131546119319.mp4");

        videoAdapter = new ListVideoAdapter(urlList);
        rvList.setLayoutManager(new LinearLayoutManager(List2Activity.this));
        rvList.setAdapter(videoAdapter);

        addListener();
    }

    private void addListener() {
    }


    class ListVideoAdapter extends BaseRecAdapter<String, VideoViewHolder> {


        public ListVideoAdapter(List<String> list) {
            super(list);
        }

        @Override
        public void onHolder(VideoViewHolder holder, String bean, int position) {
            holder.mp_video.setUp(bean, JZVideoPlayerStandard.CURRENT_STATE_NORMAL);
            if (position == 0) {
                holder.mp_video.startVideo();
            }
            Glide.with(context).load(bean).into(holder.mp_video.thumbImageView);
            holder.tv_title.setText("第" + position + "个视频");
        }

        @Override
        public VideoViewHolder onCreateHolder() {
            return new VideoViewHolder(getViewByRes(R.layout.item_video));

        }


    }

    public class VideoViewHolder extends BaseRecViewHolder {
        public View rootView;
        public MyVideoPlayer mp_video;
        public TextView tv_title;

        public VideoViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.mp_video = rootView.findViewById(R.id.mp_video);
            this.tv_title = rootView.findViewById(R.id.tv_title);
        }

    }
}
