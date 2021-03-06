package com.wander.floatview

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * author wander
 * date 2018/10/15
 * 仅适配竖向屏幕向下滑动，不支持屏幕旋转
 */
class DragLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val tag = "DragLayout"
    /**
     * 用户意图滑动的距离
     */
    private var hoverSlidDistance = resources.displayMetrics.heightPixels / 20
    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var orgContentY = 0

    var lock = false
    private lateinit var contentView: View
    private var viewHeight = 0
        set(value) {
            field = value
            slideOutRange = field * slideOutRangePercent
        }
    var slideOutVelocity = 2000f
    var slideOutRangePercent = 0.2f
    private var slideOutRange = 200f
    private val mDragHelper = ViewDragHelper.create(this, 1.0f, MyDragCallback())
    var dragStateListener: DragStateListener? = null

    init {

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0) {
            contentView = getChildAt(0)
            contentView.top = 0
        } else {
            throw IllegalArgumentException("need one direct child")
        }
    }

    fun dismiss() {
        dragStateListener?.onClose()
        contentView.top = 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(tag, "height:$height")
        viewHeight = height
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(tag, "orgY:${contentView.top}")
        orgContentY = contentView.top
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!lock) {
            mDragHelper.processTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                //当滑动大于hoverSlidDistance时开始相应drag
                if (Math.abs(event.y - mDownY) < hoverSlidDistance) {
                    return false
                }
                if (Math.abs(event.x - mDownX) > hoverSlidDistance) {
                    return false
                }
            }
            else -> {
            }
        }
        if (lock) {
            return false
        }

        return mDragHelper.shouldInterceptTouchEvent(event)
    }


    inner class MyDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == contentView
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.d(tag, "clampViewPositionVertical dy$dy   top:$top")
            return Math.max(Math.min(top, viewHeight), 0)
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return viewHeight
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild == contentView) {
                if (Math.abs(yvel) > slideOutVelocity) {
                    mDragHelper.settleCapturedViewAt(0, viewHeight)
                    invalidate()
                }

                if (contentView.top - orgContentY < slideOutRange) {
                    mDragHelper.settleCapturedViewAt(0, 0)
                } else {
                    mDragHelper.settleCapturedViewAt(0, viewHeight)
                }
                invalidate()
            }
        }

        override fun onViewDragStateChanged(state: Int) {
            when (state) {
                ViewDragHelper.STATE_IDLE -> {
                    if (contentView.top == viewHeight) {
                        dragStateListener?.onClose()
                        Log.d(tag, "close")
                    } else if (contentView.top == 0) {
                        Log.d(tag, "open")
                        dragStateListener?.onOpen()

                    }

                }
                else -> {
                }
            }
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val percent = top * 1.0f / viewHeight
            Log.d(tag,"percent$percent")
            dragStateListener?.onDrag(percent)
        }


    }


    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        checkChildCount()
        super.addView(child, params)
    }

    private fun checkChildCount() {
        if (childCount > 0) {
            throw IllegalArgumentException("DragLayout can host only one direct child")
        }
    }
}