package io.radika.bitmaptest;


/**
 * Created by abel on 15/06/16.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;


public class GoHomeGauge extends View {

    protected static final int DEFAULT_LONG_POINTER_SIZE = 1;

    protected Paint mPaint;
    protected float mStrokeWidth;
    protected int mStrokeColor;
    protected RectF mRect;
    protected String mStrokeCap;
    protected int mStartAngel;
    protected int mSweepAngel;
    protected int mStartValue;
    protected int mEndValue;
    protected int mValue;
    protected double mPointAngel;
    protected float mRectLeft;
    protected float mRectTop;
    protected float mRectRight;
    protected float mRectBottom;
    protected int mPoint;
    protected int mPointSize;
    protected int mPointStartColor;
    protected int mPointEndColor;
    protected int mDividerColor;
    protected int mDividerSize;
    protected int mDividerStepAngel;
    protected int mDividersCount;
    protected boolean mDividerDrawFirst;
    protected boolean mDividerDrawLast;


    protected TypedArray a;
    protected AlphaAnimation animation;
    protected boolean onAnimation = true;
    protected final Handler handler = new Handler();


    public GoHomeGauge(Context context) {
        super(context);
        init();
    }

    public GoHomeGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        a = context.obtainStyledAttributes(attrs, R.styleable.GoHomeGauge, 0, 0);

        //Setting up animation
        animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(10);
        animation.setFillAfter(true);

        // stroke style
        mStrokeWidth = a.getDimension(R.styleable.GoHomeGauge_gaugeStrokeWidth, 10);
        mStrokeColor = a.getColor(R.styleable.GoHomeGauge_gaugeStrokeColor, ContextCompat.getColor(context, android.R.color.darker_gray));
        mStrokeCap = a.getString(R.styleable.GoHomeGauge_gaugeStrokeCap);

        // angel start and sweep (opposite direction 0, 270, 180, 90)
        mStartAngel = a.getInt(R.styleable.GoHomeGauge_gaugeStartAngel, 0);
        mSweepAngel = a.getInt(R.styleable.GoHomeGauge_gaugeSweepAngel, 360);

        // scale (from mStartValue to mEndValue)
        mStartValue = a.getInt(R.styleable.GoHomeGauge_gaugeStartValue, 0);
        mEndValue = a.getInt(R.styleable.GoHomeGauge_gaugeEndValue, 1000);

        // pointer size and color
        mPointSize = a.getInt(R.styleable.GoHomeGauge_gaugePointSize, 0);
        mPointStartColor = a.getColor(R.styleable.GoHomeGauge_gaugePointStartColor, ContextCompat.getColor(context, android.R.color.white));
        mPointEndColor = a.getColor(R.styleable.GoHomeGauge_gaugePointEndColor, ContextCompat.getColor(context, android.R.color.white));

        // divider options
        int dividerSize = a.getInt(R.styleable.GoHomeGauge_gaugeDividerSize, 0);
        mDividerColor = a.getColor(R.styleable.GoHomeGauge_gaugeDividerColor, ContextCompat.getColor(context, android.R.color.white));
        int dividerStep = a.getInt(R.styleable.GoHomeGauge_gaugeDividerStep, 0);
        mDividerDrawFirst = a.getBoolean(R.styleable.GoHomeGauge_gaugeDividerDrawFirst, true);
        mDividerDrawLast = a.getBoolean(R.styleable.GoHomeGauge_gaugeDividerDrawLast, true);

        // calculating one point sweep
        mPointAngel = ((double) Math.abs(mSweepAngel) / (mEndValue - mStartValue));

        // calculating divider step
        if (dividerSize > 0) {
            mDividerSize = mSweepAngel / (Math.abs(mEndValue - mStartValue) / dividerSize);
            mDividersCount = 100 / dividerStep;
            mDividerStepAngel = mSweepAngel / mDividersCount;
        }
        goAnimation();
        a.recycle();
        init();
    }

    protected void init() {
        //main Paint
        mPaint = new Paint();
        mPaint.setColor(mStrokeColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        if (!TextUtils.isEmpty(mStrokeCap)) {
            if (mStrokeCap.equals("BUTT"))
                mPaint.setStrokeCap(Paint.Cap.BUTT);
            else if (mStrokeCap.equals("ROUND"))
                mPaint.setStrokeCap(Paint.Cap.ROUND);
        } else
            mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStyle(Paint.Style.STROKE);
        mRect = new RectF();

        mValue = mStartValue;
        mPoint = mStartAngel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float paddingLeft = getPaddingLeft();
        float paddingRight= getPaddingRight();
        float paddingTop = getPaddingTop();
        float paddingBottom = getPaddingBottom();
        float width = getWidth() - (paddingLeft+paddingRight);
        float height = getHeight() - (paddingTop+paddingBottom);
        float radius = (width > height ? width/2 : height/2);

        mRectLeft = width/2 - radius + paddingLeft;
        mRectTop = height/2 - radius + paddingTop;
        mRectRight = width/2 - radius + paddingLeft + width;
        mRectBottom = height/2 - radius + paddingTop + height;

        mRect.set(mRectLeft, mRectTop, mRectRight, mRectBottom);

        mPaint.setColor(mStrokeColor);
        mPaint.setShader(null);
        canvas.drawArc(mRect, mStartAngel, mSweepAngel, false, mPaint);
        mPaint.setColor(mPointStartColor);
        mPaint.setShader(new LinearGradient(getWidth(), getHeight(), 0, 0, mPointEndColor, mPointStartColor, Shader.TileMode.CLAMP));
        if (mPointSize>0) {//if size of pointer is defined
            if (mPoint > mStartAngel + mPointSize/2) {
                canvas.drawArc(mRect, mPoint - mPointSize/2, mPointSize, false, mPaint);
            }
            else { //to avoid excedding start/zero point
                canvas.drawArc(mRect, mPoint, mPointSize, false, mPaint);
            }
        }
        else { //draw from start point to value point (long pointer)
            if (mValue==mStartValue) //use non-zero default value for start point (to avoid lack of pointer for start/zero value)
                canvas.drawArc(mRect, mStartAngel, DEFAULT_LONG_POINTER_SIZE, false, mPaint);
            else
                canvas.drawArc(mRect, mStartAngel, mPoint - mStartAngel, false, mPaint);
        }

        if (mDividerSize > 0) {
            mPaint.setColor(mDividerColor);
            mPaint.setShader(null);
            int i = mDividerDrawFirst ? 0 : 1;
            int max = mDividerDrawLast ? mDividersCount + 1 : mDividersCount;
            for (; i < max; i++) {
                canvas.drawArc(mRect, mStartAngel + i*mDividerStepAngel, mDividerSize, false, mPaint);
            }
        }

    }

    public void setValue(int angle, int distance) {

        mValue = angle;
        mPoint = (int) (mStartAngel + (angle-mStartValue) * mPointAngel);

        if(distance < 5) {
                setmStrokeColor(0XFF259b24);
                setNewColor(0XFF259b24);
                onAnimation = true;

        }
        else {
            setNewColor(Color.RED);
            setmStrokeColor(Color.BLACK);
            onAnimation = false;
        }

        invalidate();
    }


    public int getValue() {
        return mValue;
    }

    /* New Functions */
    public void setNewColor(int color){
        mPointStartColor = color;
        mPointEndColor = color;
        invalidate();
    }

    public void setmStrokeColor(int color){
        mStrokeColor = color;
        invalidate();
    }

    public void goAnimation(){

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (onAnimation)
                    GoHomeGauge.this.startAnimation(animation); //start animation
                else if (!onAnimation)
                    GoHomeGauge.this.clearAnimation(); //stop animation

                handler.postDelayed(this, 2777);

            }
        });
    }

}
