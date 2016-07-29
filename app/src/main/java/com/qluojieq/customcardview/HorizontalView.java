package com.qluojieq.customcardview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by shiliushuo-1 on 16/7/29.
 */

public class HorizontalView extends ViewGroup {

    public HorizontalView(Context context) {
        super(context);
        init();
    }

    public HorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }


    public void init() {
        scroller = new Scroller(getContext());
        tracker = VelocityTracker.obtain();
    }

    int lastInterceptX;
    int lastInterceptY;

//todo intercept的拦截逻辑

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        //处理滑动冲突,也就是什么时候返回true的问题
        //规则:开始滑动时水平距离超过垂直距离的时候
        boolean intercept = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //DOWN返回false,导致onTouchEvent中无法获取到DOWN
                intercept = false;
                Log.d("HV", "Intercept.DOWN");
                if (!scroller.isFinished()) { //如果动画还没有执行完成,则打断,这种情况肯定还是由父组件处理触摸事件所以返回true
                    scroller.abortAnimation();
                    intercept = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("HV", "Intercept.MOVE");
                int deltaX = x - lastInterceptX;
                int deltaY = y - lastInterceptY;
                if (Math.abs(deltaX) - Math.abs(deltaY) > 0) { //水平方向距离长  MOVE中返回true一次,后续的MOVE和UP都不会收到此请求
                    intercept = true;
                    Log.d("HV", "intercepted");
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("HV", "Intercept.UP");
                intercept = false;
                break;
        }

        //因为DOWN返回true,所以onTouchEvent中无法获取DOWN事件,所以这里要负责设置lastX,lastY
        lastX = x;
        lastY = y;
        lastInterceptX = x; //因为先经过的DOWN,所以在MOVE的时候,这两个值已经有了
        lastInterceptY = y;

        return intercept;
    }

    int lastX;
    int lastY;
    int currentIndex = 0; //当前子元素
    int childWidth = 0;
    private Scroller scroller;
    private VelocityTracker tracker;    //增加速度检测,如果速度比较快的话,就算没有滑动超过一半的屏幕也可以

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        tracker.addMovement(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("HV", "TouchEvent.DOWN");
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("HV", "TouchEvent.MOVE");
                int deltaX = x - lastX; //跟随手指滑动
                Log.d("HV", "move:" + -deltaX);
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP: //释放手指以后开始自动滑动到目标位置
                Log.d("HV", "TouchEvent.UP");
                int distance = getScrollX() - currentIndex * childWidth; //相对于当前View滑动的距离,正为向左,负为向右
                if (Math.abs(distance) > childWidth / 2) {//必须滑动的距离要大于1/2个宽度,否则不会切换到其他页面
                    if (distance > 0) {
                        currentIndex++;
                    } else {
                        currentIndex--;
                    }
                } else {
                    tracker.computeCurrentVelocity(1000);
                    float xV = tracker.getXVelocity();
                    if (Math.abs(xV) > 50) {
                        if (xV > 0) {
                            currentIndex--;
                        } else {
                            currentIndex++;
                        }
                    }
                }
                currentIndex = currentIndex < 0 ? 0 : currentIndex > getChildCount() - 1 ? getChildCount() - 1 : currentIndex;
                smoothScrollTo(currentIndex * childWidth, 0);
                tracker.clear();
                break;
        }
        lastX = x;
        lastY = y;
        return true;
    }
//
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        //测量所有子元素
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//        //处理wrap_content的情况
//        if (getChildCount() == 0) {
//            setMeasuredDimension(0, 0);
//        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
//            View childOne = getChildAt(0);
//            int childWidth = childOne.getMeasuredWidth();
//            int childHeight = childOne.getMeasuredHeight();
//            setMeasuredDimension(childWidth * getChildCount(), childHeight);
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            View childOne = getChildAt(0);
//            int childWidth = childOne.getMeasuredWidth();
//            setMeasuredDimension(childWidth * getChildCount(), heightSize);
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            int childHeight = getChildAt(0).getMeasuredHeight();
//            setMeasuredDimension(widthSize, childHeight);
//        }
//    }

    //scroller的标准用法步骤
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    //scroller的标准用法步骤
    public void smoothScrollTo(int destX, int destY) {
        scroller.startScroll(getScrollX(), getScrollY(), destX - getScrollX(), destY - getScrollY(), 1000);
        invalidate();
    }

    //不要问我当前View参数中的四个位置是哪儿来的
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int left = 0; //左边的距离
        View child;
        //遍历布局子元素
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            int width = child.getMeasuredWidth();
            childWidth = width; //赋值给子元素宽度变量
            child.layout(left, 0, left + width, child.getMeasuredHeight());
            left += width;
        }
    }


}
