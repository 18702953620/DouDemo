package com.ch.doudemo.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ch.doudemo.R;
import com.ch.doudemo.widget.MyVideoPlayer;

import java.lang.reflect.Field;

import butterknife.BindView;

/**
 * 作者： ch
 * 时间： 2018/7/30 0030-下午 2:55
 * 描述：
 * 来源：
 */


public class VideoFragment extends BaseFragment {
    @BindView(R.id.txv_video)
    MyVideoPlayer txvVideo;
    @BindView(R.id.rl_back_right)
    RelativeLayout rlBackRight;
    @BindView(R.id.dl_back_play)
    DrawerLayout dlBackPlay;
    private String url;
    public static final String URL = "URL";

    @Override
    protected int getLayoutId() {
        return R.layout.fm_video;
    }

    @Override
    protected void initView() {

        url = getArguments().getString(URL);
        Glide.with(context)
                .load(url)
                .into(txvVideo.thumbImageView);
        txvVideo.rl_touch_help.setVisibility(View.GONE);
        txvVideo.setUp(url, url);

    }

    @Override
    protected void loadData() {
        txvVideo.startVideo();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (txvVideo == null) {
            return;
        }
        if (isVisibleToUser) {
            txvVideo.goOnPlayOnResume();
        } else {
            txvVideo.goOnPlayOnPause();
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if (txvVideo != null) {
            txvVideo.goOnPlayOnResume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (txvVideo != null) {
            txvVideo.goOnPlayOnPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (txvVideo != null) {
            txvVideo.releaseAllVideos();
        }
    }


    /**
     * 设置 全屏滑动
     *
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage
     */
    private void setDrawerRightEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field mRightDragger =
                    drawerLayout.getClass().getDeclaredField("mRightDragger");//Right
            mRightDragger.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) mRightDragger.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 设置新的边缘大小
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x *
                    displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }
}
