package com.wander.floatview.slideback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.wander.floatview.R;
import com.wander.floatview.slideback.callbak.OnInternalStateListener;
import com.wander.floatview.slideback.callbak.OnSlideListener;
import com.wander.floatview.slideback.widget.SlideBackLayout;


/**
 * Created by Oubowu on 2016/9/22 0022 14:31.
 */
// TODO: 2016/9/24 添加了上个页面的布局，如果页面有Toolbar的话其不随屏幕选择而大小变化，永远维持进入时的宽高比
public class SlideBackHelper {

    private static ViewGroup getDecorView(Activity activity) {
        if (activity == null){
            return   null;
        }
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    private static Drawable getDecorViewDrawable(Activity activity) {
        return getDecorView(activity).getBackground();
    }

    private static View getContentView(Activity activity) {
        return getDecorView(activity).getChildAt(0);
    }

    /**
     * 附着Activity，实现侧滑
     *
     * @param curActivity 当前Activity
     * @param helper      Activity栈管理类
     * @param config      参数配置
     * @param listener    滑动的监听
     * @return 处理侧滑的布局，提高方法动态设置滑动相关参数
     */
    public static SlideBackLayout attach(@NonNull final Activity curActivity, @NonNull final ActivityHelper helper, @Nullable final SlideConfig config, @Nullable final OnSlideListener listener) {
        Activity helperPreActivity = helper.getPreActivity();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || helperPreActivity == null) {
            return new SlideBackLayout(curActivity);
        }

        final ViewGroup decorView = getDecorView(curActivity);
        final View contentView = decorView.getChildAt(0);
        if (decorView.getChildCount() > 0) {
            decorView.removeViewAt(0);
        }

        if (contentView == null) {
            return null;
        }
        View content = contentView.findViewById(android.R.id.content);
        if (content == null) {
            return null;
        }
        if (content.getBackground() == null) {
            content.setBackground(decorView.getBackground());
        }

        final Activity[] preActivity = {helperPreActivity};
        final View[] preContentView = {getContentView(preActivity[0])};
        Drawable preDecorViewDrawable = getDecorViewDrawable(preActivity[0]);
        content = preContentView[0].findViewById(android.R.id.content);
        if (content.getBackground() == null) {
            content.setBackground(preDecorViewDrawable);
        }

        final SlideBackLayout slideBackLayout;
        slideBackLayout = new SlideBackLayout(curActivity, contentView, preContentView[0], preDecorViewDrawable, config, new OnInternalStateListener() {

            @Override
            public void onSlide(float percent) {
                if (listener != null) {
                    listener.onSlide(percent);
                }
            }

            @Override
            public void onOpen() {
                if (listener != null) {
                    listener.onOpen();
                }
            }

            @Override
            public void onClose(boolean finishActivity) {

                // finishActivity为true时关闭页面，为false时不关闭页面，为null时为其他地方关闭页面时调用SlideBackLayout.isComingToFinish的回调

                if (listener != null) {
                    listener.onClose();
                }

                if (config != null && config.isRotateScreen()) {

                    if (finishActivity) {
                        // remove了preContentView后布局会重新调整，这时候contentView回到原处，所以要设不可见
                        contentView.setVisibility(View.INVISIBLE);
                    }

                    if (preActivity[0] != null && preContentView[0].getParent() != getDecorView(preActivity[0])) {
                        // Log.e("TAG", ((SlideBackLayout) preContentView[0].getParent()).getTestName() + "这里把欠人的布局放回到上个Activity");
                        preContentView[0].setX(0);
                        ((ViewGroup) preContentView[0].getParent()).removeView(preContentView[0]);
                        getDecorView(preActivity[0]).addView(preContentView[0], 0);
                    }
                }

                if (finishActivity) {
                    curActivity.finish();
                    curActivity.overridePendingTransition(0, R.anim.anim);
                    helper.postRemoveActivity(curActivity);
                } else {
                    helper.postRemoveActivity(curActivity);
                }
            }

            @Override
            public void onCheckPreActivity(SlideBackLayout slideBackLayout) {
                // Log.e("TAG", "--------------------------------------------------");
                // helper.printAllActivity();
                // Log.e("TAG", "--------------------------------------------------");
                Activity activity = helper.getPreActivity();
                // Log.e("TAG", "SlideBackHelper-120行-onFocus(): " + preActivity[0] + ";" + activity);
                if (activity != preActivity[0]) {
                    // Log.e("TAG", "SlideBackHelper-122行-onFocus(): 上个Activity变了");
                    preActivity[0] = activity;
                    preContentView[0] = getContentView(preActivity[0]);
                    slideBackLayout.updatePreContentView(preContentView[0]);
                }
            }

        });

        decorView.addView(slideBackLayout);

        return slideBackLayout;
    }


}
