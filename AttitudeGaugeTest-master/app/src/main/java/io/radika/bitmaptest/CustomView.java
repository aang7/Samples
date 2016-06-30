package io.radika.bitmaptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by greenapsis on 24/06/16.
 */
public class CustomView extends View {

    private final Paint paint;
    private float mPitch = 0; // Degrees
    private float mRoll = 0; // Degrees, left roll is positive

    // These are created once and reused in subsequent onDraw calls.
    private Bitmap mSrcBitmap;
    private Canvas mSrcCanvas;
    private Bitmap mDstBitmap;

    private Paint linePaint;
    private Paint lineUpPaint;
    private int mWidth;
    private int mHeight;

    private int arcColor;

    private TypedArray a;

    public CustomView(Context context) {
        //super(context);
        this(context,null);

    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        linePaint = new Paint();
        paint.setAntiAlias(true);
        //paint.setColor(R.color.md_green_600);
        a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
        arcColor = a.getColor(R.styleable.CustomView_arcColor, ContextCompat.getColor(context, android.R.color.white));

        paint.setColor(arcColor);
        linePaint.setColor(Color.BLACK);
        linePaint.setFakeBoldText(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        lineUpPaint = new Paint();
        lineUpPaint.setColor(Color.BLACK);
        lineUpPaint.setStyle(Paint.Style.STROKE);
        lineUpPaint.setAntiAlias(true);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w ;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        int viewWidthHalf = getWidth()/2;
        int viewHeightHalf = getHeight()/2;

        int radius = 0;
        if(viewWidthHalf>viewHeightHalf)
            radius=(viewHeightHalf/2); // +69, -28 , -10
        else
            radius=(viewWidthHalf/2);

        canvas.drawArc(new RectF(viewWidthHalf-radius,viewHeightHalf-radius,viewWidthHalf+radius,viewHeightHalf+radius),mRoll+mPitch*radius/90,180-2*mPitch*radius/90,false,paint);//wArc(wingsCircleBounds, 0, 180, false, mMinPlanePaint);

        //Aqui empeze
        //canvas.save();

        //canvas.restore();

        float minPlaneCircleRadiusX = mWidth / 6;
        float minPlaneCircleRadiusY = mHeight / 6;
        float centerX = mWidth / 2;
        float centerY = mHeight / 2;

        // Wings of miniature plane
        float wingLength = mWidth / 6;
        //canvas.drawLine(viewWidthHalf+69 - minPlaneCircleRadiusX - wingLength, centerY, centerX
          //      - minPlaneCircleRadiusX+69, centerY, linePaint);


        //canvas.drawLine(centerX-10, centerY, centerX- minPlaneCircleRadiusX+69, centerY, linePaint);

        //Triangle
        float bottomLadderStepX = radius/3;
        float bottomLadderStepY = radius/3;
        canvas.drawLine(centerX, centerY, centerX - bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);
        canvas.drawLine(centerX, centerY, centerX + bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);

        //draw bottom lines
        for (int i = 1; i <= 6; i++) {
            float y = centerY + bottomLadderStepY * i/3;
            canvas.drawLine(centerX - radius * i/9f, y, centerX + radius * i/9f , y,
                    linePaint); //en vez de radius estaba bottomLadderStepX

        }

        //Draw up lines
        float ladderStepY = radius/4;
        for (int i = 1; i <= 4; i++) {
            float width = mWidth / (10.2f*i + 2);
            float y = centerY - ladderStepY * i;
            canvas.drawLine(centerX - width / 2, y, centerX + width / 2, y, lineUpPaint);
        }


        //Log.d("START :", Float.toString(mRoll));
        //Log.d("SWEEP :",Float.toString(180+mRoll));

    }

    public void setAttitude(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;
        invalidate();
    }
}
