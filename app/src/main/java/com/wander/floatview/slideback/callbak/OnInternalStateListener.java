package com.wander.floatview.slideback.callbak;

import android.support.annotation.FloatRange;
import com.wander.floatview.slideback.widget.SlideBackLayout;


/**
 * Created by Oubowu on 2016/9/22 0022 18:22.
 */
public interface OnInternalStateListener {

    void onSlide(@FloatRange(from = 0.0,
            to = 1.0) float percent);

    void onOpen();

    void onClose(boolean finishActivity);

    void onCheckPreActivity(SlideBackLayout slideBackLayout);

}
