package com.ch.doudemo.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.ch.doudemo.R;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

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
    TXCloudVideoView txvVideo;
    @BindView(R.id.rl_back_right)
    RelativeLayout rlBackRight;
    @BindView(R.id.dl_back_play)
    DrawerLayout dlBackPlay;
    @BindView(R.id.iv_play_thun)
    ImageView ivPlayThun;
    private TXVodPlayer mVodPlayer;
    private String url;
    public static final String URL = "URL";

    @Override
    protected int getLayoutId() {
        return R.layout.fm_video;
    }

    @Override
    protected void initView() {

        url = getArguments().getString(URL);
        //创建player对象
        mVodPlayer = new TXVodPlayer(context);
//关键player对象与界面view
        mVodPlayer.setPlayerView(txvVideo);
//        url = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
        mVodPlayer.setLoop(true);

        Glide.with(context)
                .load(url)
                .into(ivPlayThun);
//        dlBackPlay.addDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
//
//            }
//
//            @Override
//            public void onDrawerOpened(@NonNull View drawerView) {
//                if (mVodPlayer != null) {
//                    mVodPlayer.pause();
//                }
//            }
//
//            @Override
//            public void onDrawerClosed(@NonNull View drawerView) {
//                if (mVodPlayer != null) {
//                    mVodPlayer.resume();
//                }
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
//
//
//        //设置菜单内容之外其他区域的背景色
//        dlBackPlay.setScrimColor(Color.TRANSPARENT);
//        //设置 全屏滑动
//        setDrawerRightEdgeSize(getActivity(), dlBackPlay, 1);

    }

    @Override
    protected void loadData() {
        mVodPlayer.startPlay(url);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (mVodPlayer == null) {
            return;
        }
        if (isVisibleToUser) {
            mVodPlayer.resume();
        } else {
            mVodPlayer.pause();
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if (mVodPlayer != null) {
            mVodPlayer.resume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVodPlayer != null) {
            mVodPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVodPlayer != null) {
            // true代表清除最后一帧画面
            mVodPlayer.stopPlay(true);
        }
        if (txvVideo != null) {
            txvVideo.onDestroy();
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
