package com.test.caifu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by zhengcf on 2017/12/14.
 */

public class SlideMenuViewGroup extends ViewGroup implements Sliding {

    /**
     * 记录touch down X位置
     */
    private int mMoveDownX;
    /**
     * 记录touch down Y位置
     */
    private int mMoveDownY;

    /**
     * 菜单界面
     */
    View menuView;
    /**
     * 主界面
     */
    View mainView;

    private Scroller mScroller;

    private boolean showMenu;

    /**
     * 向左滑动菜单隐藏的一定距离
     */
    private int mTouchSlop;

    public SlideMenuViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureView(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        menuView.layout(-menuView.getMeasuredWidth(), t, 0, b);
        mainView.layout(l, t, r, b);
    }

    /**
     * 事件分发机制，处理菜单向左滑动时，时间消费事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveDownX = (int) ev.getX();
                mMoveDownY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int diffX = moveX - mMoveDownX;
                int moveY = (int) ev.getY();
                int diffY = moveY - mMoveDownY;
                if (Math.abs(diffX) > mTouchSlop && Math.abs(diffX) > Math.abs(diffY))
                    return true; // 认为是横向移动，消耗掉此事件
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                int diff = mMoveDownX - currentX;
                int scrollX = getScrollX() + diff;
                if (scrollX < -menuView.getWidth()) {
                    // 超出左边界
                    scrollTo(-menuView.getWidth(), 0);
                } else if (scrollX > 0) {
                    // 超出右边界
                    scrollTo(0, 0);
                } else {
                    scrollBy(diff, 0);
                }
                mMoveDownX = currentX;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 菜单的中心点
                int menuCenter = -menuView.getWidth() / 2;
                // 当前移动到的X轴坐标
                int moveX = getScrollX();
                Log.d("test", "moveX:" + moveX + ",menuView width:" + menuView.getWidth());
                showMenu = moveX < menuCenter;
                switchView();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        // 更新当前的X轴偏移量
        if (mScroller.computeScrollOffset()) { // 返回true代表正在模拟数据，false 已经停止模拟数据
            scrollTo(mScroller.getCurrX(), 0); // 更新X轴的偏移量
            invalidate();
        }
    }

    /**
     * 菜单和主界面切换
     */
    private void switchView() {
        int startX = getScrollX();
        int dx = showMenu ? -menuView.getWidth() - startX : 0 - startX;
        // 开始模拟数据
        mScroller.startScroll(startX, 0, dx, 0, Math.abs(dx));
        invalidate(); // 刷新界面,会触发ViewGroup下drawChild-->child.draw-->computeScrol
    }

    /**
     * 测量菜单和主界面的宽和高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void measureView(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取菜单并且测量宽高
        menuView = this.getChildAt(0);
        // menuView.getLayoutParams().width拿到布局参数 heightMeasureSpec屏幕高度
        menuView.measure(MeasureSpec.makeMeasureSpec(menuView.getLayoutParams().width, MeasureSpec.EXACTLY), heightMeasureSpec);
        // 获取主界面并且测量宽高
        mainView = this.getChildAt(1);
        mainView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void toggleMenu() {
        showMenu = !showMenu;
        switchView();
    }

    /**
     * 滚动发生时调用
     *
     * @param l    getScrollX()
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = (menuView.getWidth() + l * 1.0f) / menuView.getWidth();
        Log.d("test", "l:" + l + ",mMenuWidth:" + menuView.getWidth() + ",scale" + scale);
        float leftScale = 1 - 0.3f * scale;
        float rightScale = 0.8f + scale * 0.2f;
        menuView.setScaleX(leftScale);
        menuView.setScaleY(leftScale);
        menuView.setAlpha(0.6f + 0.4f * (1 - scale));
        menuView.setTranslationX(menuView.getWidth() * scale);
        mainView.setPivotX(0);
        mainView.setPivotY(mainView.getHeight() / 2);
        mainView.setScaleX(rightScale);
        mainView.setScaleY(rightScale);
    }
}
