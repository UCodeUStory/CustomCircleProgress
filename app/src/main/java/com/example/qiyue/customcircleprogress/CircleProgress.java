package com.example.qiyue.customcircleprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by qiyue on 2016/8/25 0025.
 *
 * 通过pathMeasure测量path 路径长度，根据动画变化值，
 * 来改变路径长度，通过getSegment截取路径进行绘制
 *
 * 通过0.5 控制可前后的增加和减少
 */
public class CircleProgress extends View {

    private int mViewWidth;
    private int mViewHeight;
    private Paint mPaint;
    private Path path_circle;
    private PathMeasure mMeasure;
    private int defaultDuration = 2000;
    private ValueAnimator mSearchingAnimator;
    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;
    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;

    public CircleProgress(Context context) {
        super(context);
        init(context);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(15);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        path_circle = new Path();
        mMeasure = new PathMeasure();
        RectF oval2 = new RectF(-100, -100, 100, 100);
        path_circle.addArc(oval2, 45, -359.9f);

        float[] pos = new float[2];
        mMeasure.setPath(path_circle, false);
        
        initAnimation(context);
    }

    private void initAnimation(Context context) {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                Log.i("qiyue","mAnimatorValue="+mAnimatorValue);
                invalidate();
            }
        };

        mSearchingAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);
        mSearchingAnimator.addUpdateListener(mUpdateListener);
        mSearchingAnimator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.WHITE);
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        canvas.drawColor(Color.parseColor("#467500"));

        mMeasure.setPath(path_circle, false);
        Path dst = new Path();
        Log.i("qiyue","onDraw="+mAnimatorValue);
        float stop = mMeasure.getLength() * mAnimatorValue;
        Log.i("qiyue","Math.abs(mAnimatorValue - 0.5))="+Math.abs(mAnimatorValue - 0.5));
        /**
         * ((0.5 - Math.abs(mAnimatorValue - 0.5)) 保证重0到0.5 再到 0
         * stop 是终点距离起点的距离
         *
         * 因此起点距离可以设置一个范围300
         */
        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * 300f));
        /**
         * 截取片段
         */
        mMeasure.getSegment(start, stop, dst, true);
        canvas.drawPath(dst,mPaint);
        if (mAnimatorValue==1){
            mSearchingAnimator.start();
        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }
}
