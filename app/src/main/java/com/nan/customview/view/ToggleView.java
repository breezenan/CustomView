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
    public Paint mPaint;
    private Bitmap mToggleBackgroundBitmap;
    private Bitmap mToggleSlideBitmap;
    private Boolean mOpen;
    private float currentX;
    private boolean moving;
    private int remainingWidth;

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
        //获取属性值方式
        //方式一
        /*String namespace = "http://schemas.android.com/apk/res-auto";
        setToggleBackgroundResource(attrs.getAttributeResourceValue(namespace, "toggle_background", -1));
        setToggleSlideResource(attrs.getAttributeResourceValue(namespace, "toggle_slide", -1));
        setToggle(attrs.getAttributeBooleanValue(namespace, "toogle_state", false));*/
        //方式二
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleView);
        setToggleBackgroundResource(typedArray.getResourceId(R.styleable.ToggleView_toggle_background, -1));
        setToggleSlideResource(typedArray.getResourceId(R.styleable.ToggleView_toggle_slide, -1));
        setToggle(typedArray.getBoolean(R.styleable.ToggleView_toogle_state, false));

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
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置view宽高
        setMeasuredDimension(mToggleBackgroundBitmap.getWidth(), mToggleBackgroundBitmap.getHeight());
        remainingWidth = mToggleBackgroundBitmap.getWidth() - mToggleSlideBitmap.getWidth();
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

        float currentLeft = currentX - mToggleSlideBitmap.getWidth() / 2f;

        if (moving) {
            //实时刷新滑块为当前拖动位置
            if (currentLeft <= 0) {
                currentLeft = 0;
            }
            if (currentLeft >= remainingWidth) {
                currentLeft = remainingWidth;
            }
            canvas.drawBitmap(mToggleSlideBitmap, currentLeft, 0, mPaint);
        } else {
            //2. 绘制滑块
            int slideLeft = mOpen ? remainingWidth : 0;
            canvas.drawBitmap(mToggleSlideBitmap, slideLeft, 0, mPaint);
        }
        //super.onDraw(canvas); View没有实现该方法,故不调用也可以
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moving = true;
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                moving = false;
                boolean state = currentX > mToggleBackgroundBitmap.getWidth() / 2f;
                //状态发生改变则个更新状态,并回调onToggleChange()方法,来通知设置监听器方
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
