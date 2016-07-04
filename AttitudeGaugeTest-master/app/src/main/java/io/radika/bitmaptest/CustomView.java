package io.radika.bitmaptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

    private static final float TOTAL_VISIBLE_PITCH_DEGREES = 45 * 2; // � 45�


    // These are created once and reused in subsequent onDraw calls.
    private Bitmap linesBitmap;
    private Canvas linesCanvas;
    private Bitmap mDstBitmap;
    //private Canvas mSrcCanvas;
    //private Bitmap mDstBitmap;

    private final PorterDuffXfermode mXfermode;

    private Paint linePaint;
    private Paint lineUpPaint;
    private int mWidth;
    private int mHeight;

    private Paint mBitmapPaint;

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
        //linesBitmap = new Bitmap();

        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w ;
        mHeight = h;
    }


    //Crea el ovalo (La forma del view)
    private Bitmap getCircle(int radio, int Width, int Height) {
        if (mDstBitmap == null) {
            mDstBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mDstBitmap);
            c.drawColor(Color.TRANSPARENT);

            float Width2 = c.getWidth()/2; //Obtengo medidas del bitmap
            float Height2 = c.getHeight()/2;
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.RED);
            c.drawOval(new RectF(Width2-radio,Height2-radio,Width2+radio,Height2+radio), p);
            //c.drawCircle();
            //c.drawOval(new RectF(0, 0, mWidth, mHeight), p);
        }
        return mDstBitmap;
    }

    public Bitmap Lines(int radio, int Width, int Height) {

        if (linesBitmap == null){
            linesBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            linesCanvas = new Canvas(linesBitmap);
        }

        Canvas canvas = linesCanvas;

        //canvas.drawColor(Color.TRANSPARENT);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        float centerX = Width / 2;
        float centerY = Height / 2;

        canvas.save();
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch / TOTAL_VISIBLE_PITCH_DEGREES) * Height);


       // canvas.drawArc(new RectF(Width-radio,Height-radio,Width+radio,Height+radio),mRoll+mPitch*radio/90,180-2*mPitch*radio/90,false,paint);//wArc(wingsCircleBounds, 0, 180, false, mMinPlanePaint);

        //Triangle
        float bottomLadderStepX = radio/3;
        float bottomLadderStepY = radio/3;
        canvas.drawLine(centerX, centerY, centerX - bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);
        canvas.drawLine(centerX, centerY, centerX + bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);

        //draw bottom lines
        for (int i = 1; i <= 6; i++) {
            float y = centerY + bottomLadderStepY * i/3;
            canvas.drawLine(centerX - radio * i/9f, y, centerX + radio * i/9f , y,
                    linePaint); //en vez de radio estaba bottomLadderStepX

        }

        //Draw up lines
        float ladderStepY = radio/4;
        for (int i = 1; i <= 4; i++) {
            float width = Width / (10.2f*i + 2);
            float y = centerY - ladderStepY * i;
            canvas.drawLine(centerX - width / 2, y, centerX + width / 2, y, lineUpPaint);
        }


        canvas.restore();


        return linesBitmap;
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


        Bitmap line = Lines(radius, viewWidthHalf, viewHeightHalf);
        Bitmap circle = getCircle(radius, viewWidthHalf, viewHeightHalf);

        int sc = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.drawBitmap(circle, viewWidthHalf-radius,viewHeightHalf-radius, mBitmapPaint);
        mBitmapPaint.setXfermode(mXfermode);
        canvas.drawBitmap(line, viewWidthHalf-radius, viewHeightHalf-radius, mBitmapPaint);
        mBitmapPaint.setXfermode(null);



        canvas.restoreToCount(sc);

        //canvas.drawArc(new RectF(viewWidthHalf-radius,viewHeightHalf-radius,viewWidthHalf+radius,viewHeightHalf+radius),mRoll+mPitch*radius/90,180-2*mPitch*radius/90,false,paint);//wArc(wingsCircleBounds, 0, 180, false, mMinPlanePaint);

    }

    public void setAttitude(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;
        invalidate();
    }
}
