package com.nan.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nan.customview.R;

/**
 * Created by nan on 18-3-8.
 */

public class ToggleView extends View {
    public static final String TAG = "ToggleView";
    private Paint mPaint;
    /**
     * 开关背景图
     */
    private Bitmap mToggleBackgroundBitmap;
    /**
     * 滑块图
     */
    private Bitmap mToggleSlideBitmap;
    /**
     * 开关状态
     */
    private Boolean mOpen;
    /**
     * 手指当前相对于本view的x坐标位置
     */
    private float mCurrentX;
    /**
     * 手指是否触摸开关
     */
    private boolean mTouchMode;
    /**
     * 开关除去滑块的剩余的宽度
     */
    private int mRemainingWidth;

    /**
     * 开关状态监听器
     */
    private OnToggleChangeListener mOnToggleChangeListener;

    /**
     * 只是用java代码创建,则调用该构造方法
     *
     * @param context
     */
    public ToggleView(Context context) {
        super(context);
        Log.i(TAG, "ToggleView: context");
        init();
    }

    /**
     * 使用布局文件创建,即使有自定义属性也调用该方法
     *
     * @param context
     * @param attrs
     */
    public ToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "ToggleView: context,attrs");
        //方式一 原始方法,通过该方法拿到的值就是xml直接配置的值(eg:22sp,#ff0),故无法直接使用,需要自己转化才可以使用,为了方便借助TypedArray则直接得到可以使用的值
        /*attrs.getAttributeValue(R.styleable.ToggleView_toggle_background);
        attrs.getAttributeValue(R.styleable.ToggleView_toggle_slide);
        attrs.getAttributeValue(R.styleable.ToggleView_toggle_state);*/

        //方式二
        /*String namespace = "http://schemas.android.com/apk/res-auto";
        setToggleBackgroundResource(attrs.getAttributeResourceValue(namespace, "toggle_background", -1));
        setToggleSlideResource(attrs.getAttributeResourceValue(namespace, "toggle_slide", -1));
        setToggle(attrs.getAttributeBooleanValue(namespace, "toggle_state", false));*/

        //方式三 通过TypedArray,自定义属性会在R文件中生成一个数组,和对应个变量,本例三个属性则三个变量来标识下标
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        setToggleBackgroundResource(ta.getResourceId(R.styleable.ToggleView_toggle_background, -1));
        setToggleSlideResource(ta.getResourceId(R.styleable.ToggleView_toggle_slide, -1));
        setToggle(ta.getBoolean(R.styleable.ToggleView_toggle_state, false));

        init();
    }

    public ToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i(TAG, "ToggleView: context,attrs,defStyleAttr");
        init();
    }

    private void init() {
        mPaint = new Paint();
    }


    //view绘制流程 measure->draw   该方法都在Activity onResume之后调用
    //viewgroup绘制流程 measure->layout->draw
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置view宽高
        setMeasuredDimension(mToggleBackgroundBitmap.getWidth(), mToggleBackgroundBitmap.getHeight());
        mRemainingWidth = mToggleBackgroundBitmap.getWidth() - mToggleSlideBitmap.getWidth();
    }

    /**
     * 绘制在Canvas上的内容就可以显示在View上
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //1. 绘制开关背景
        canvas.drawBitmap(mToggleBackgroundBitmap, 0, 0, mPaint);

        float currentLeft = mCurrentX - mToggleSlideBitmap.getWidth() / 2f;

        //手指触摸开关,则实时刷新滑块位置
        if (mTouchMode) {
            if (currentLeft <= 0) {
                currentLeft = 0;//滑块左侧临界
            }
            if (currentLeft >= mRemainingWidth) {
                currentLeft = mRemainingWidth;//滑块滑动右侧临界
            }
            canvas.drawBitmap(mToggleSlideBitmap, currentLeft, 0, mPaint);
        } else {
            //2. 绘制滑块
            int slideLeft = mOpen ? mRemainingWidth : 0;
            canvas.drawBitmap(mToggleSlideBitmap, slideLeft, 0, mPaint);
        }
        //super.onDraw(canvas); View没有实现该方法,故不调用也可以
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchMode = true;
                mCurrentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                mTouchMode = false;
                boolean state = mCurrentX > mToggleBackgroundBitmap.getWidth() / 2f;
                //状态发生改变则更新开关状态,并回调onToggleChange()方法,通知设置监听器方
                if (state != mOpen && mOnToggleChangeListener != null) {
                    mOpen = state;
                    mOnToggleChangeListener.onToggleChange(mOpen);
                }
                break;
            default:
                break;
        }

        invalidate();//每次触摸事件都使界面重绘
        return true;//消耗了该事件
    }

    /**
     * 设置开关背景图片
     *
     * @param backgroundResource
     */
    public void setToggleBackgroundResource(int backgroundResource) {
        mToggleBackgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundResource);
    }

    /**
     * 设置滑块图片
     *
     * @param slideResource
     */
    public void setToggleSlideResource(int slideResource) {
        mToggleSlideBitmap = BitmapFactory.decodeResource(getResources(), slideResource);
    }

    /**
     * 设置开关状态
     *
     * @param open
     */
    public void setToggle(boolean open) {
        mOpen = open;
    }

    /**
     * 设置状态改变监听器,适当时候调用mOnToggleChangeListener中的onToggleChange()方法即可
     *
     * @param onToggleChangeListener
     */
    public void setOnToggleChangeListener(OnToggleChangeListener onToggleChangeListener) {
        mOnToggleChangeListener = onToggleChangeListener;
    }

    /**
     * 状态改变接口
     */
    public interface OnToggleChangeListener {
        void onToggleChange(boolean open);
    }
}
