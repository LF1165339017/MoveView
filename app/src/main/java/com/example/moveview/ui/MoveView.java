package com.example.moveview.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.moveview.utils.ScreenUtil;

/**
 * @author liufang
 * @date 2019.03.15
 * 可以随着手指移动的View
 **/
public class MoveView extends RelativeLayout {

    private Context mContext;
    /**
     * 记录触发ACTION_DOWN时候的坐标x,y
     */
    private int lastX, lastY;
    /**
     * 获取系统状态栏的高度
     */
    private int navBarHeight;
    /**
     * 屏幕宽度和高度
     */
    private int screenWidth, screenHeight;
    /**
     * 手指距离此控件的距离
     */
    private int inX, inY;
    /**
     * 控件移动的距离
     */
    private int destX, destY;

    private static final int TOUCH_SLOP = 4;
    /**
     * 默认是否能够移动
     */
    private boolean isCanMove = true;

    private String Brand;

    /**
     * 华为虚拟导航栏
     */
    private int NavigationBarHeight = 0;


    public MoveView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MoveView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * ******************************
     * 内部View的逻辑
     * ******************************
     */
    public void init(Context context) {
        mContext = context;
        navBarHeight = ScreenUtil.getNavigationBarHeight(mContext);
        screenWidth = ScreenUtil.getScreenWidth(mContext);
        screenHeight = ScreenUtil.getScreenHeight(mContext);
        Brand = ScreenUtil.getDeviceInfo();
        if (Brand.equals("navigationbar_is_min")) {
            NavigationBarHeight = ScreenUtil.getNavigationBarHeight(mContext);
        }
    }

    /**
     * 可移动的逻辑
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**
         * 是否能够移动
         */
        if (isCanMove) {
            /**
             * 每一次走到ACTION_DOWN的方法时候，就获取当前绝对坐标值x,y
             */
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    inX = (int) event.getX();
                    inY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    /**
                     * 获取当前手指移动的距离x，y的坐标值
                     * 并返回他们的绝对值大小，比较x,y中哪个数值比较大，
                     * 并判断当前最大的数值是否我们定义的点击事件容差距离TOUCH_SLOP大，
                     * 如果小，就是点击事件，如果大，就是移动事件。
                     */
                    final int diff = Math.max(Math.abs(lastX - x), Math.abs(lastY - y));
                    if (diff < TOUCH_SLOP) {
                        return false;
                    }
                    //如果此时控件的横向移动距离desrX小于0的话，就默认左边left为0；
                    if (x - inX <= 0) {
                        destX = 0;
                        //如果此时控件的横向移动距离大于整个屏幕的宽度的话，就让其左边left距离为ScreenUtil.screenWidth - view.getWidth()
                    } else if (x - inX + getWidth() >= screenWidth) {
                        destX = screenWidth - getWidth();
                        //如果都不是，则横向移动距离等于left.
                    } else {
                        destX = x - inX;
                    }

                    //如果此时控件的纵向移动距离destY小于0的话，就默认顶部top为20
                    if (y - inY <= 0) {
                        destY = 0;
                        //如果此时控件的纵向移动距离大于整个屏幕的高度的话，就让其顶部top距离为ScreenUtil.screenHeight - view.getHeight()
                    } else if (y - inY + getHeight() >= screenHeight - navBarHeight) {
                        destY = screenHeight - getHeight() - navBarHeight;
                    } else {
                        //如果都不是，则纵向移动距离等于left.
                        destY = y - inY;
                    }

                    /**
                     * 这里需要注意是和父容器的布局想对应的
                     */
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
                    params.gravity = Gravity.NO_GRAVITY;
                    params.leftMargin = destX;
                    params.topMargin = destY;
                    setLayoutParams(params);
                    break;
                case MotionEvent.ACTION_UP:
                    //这里做动画贴边效果
                    FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) getLayoutParams();
                    float centerX = getX() + getWidth() / 2;
                    if (centerX > screenWidth / 2) {
                        params1.leftMargin = screenWidth - getWidth();
                        params1.topMargin = getTop();
                        setLayoutParams(params1);
                    } else {
                        params1.leftMargin = 0;
                        params1.topMargin = getTop();
                        setLayoutParams(params1);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 提供方法，设置控件当前是否可以移动
     *
     * @param canMove
     */
    public void setCanMove(boolean canMove) {
        isCanMove = canMove;
    }


}
