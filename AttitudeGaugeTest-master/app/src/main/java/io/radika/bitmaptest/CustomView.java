package io.radika.bitmaptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by greenapsis on 24/06/16.
 */
public class CustomView extends View {

    private static final float TOTAL_VISIBLE_PITCH_DEGREES = 45 * 2; // � 45�
    private static final int EARTH_COLOR = Color.parseColor("#865B4B");

    // created and reused in onDraw
    private Bitmap linesBitmap;
    private Bitmap mDstBitmap;
    private Canvas linesCanvas;

    private final Paint circlePaint;
    private Paint mBitmapPaint;
    private Paint linePaint;
    private Paint lineUpPaint;

    private final PorterDuffXfermode mXfermode;

    private int mWidth;
    private int mHeight;

    private int arcColor;
    private int arrowColor;
    private int markColor;
    private int textColor;
    private int textSize;
    private int lineColor;
    private int SKY_COLOR;

    private float mPitch = 0; // Degrees
    private float mRoll = 0; // Degrees, left roll is positive

    private Paint textPaint;
    private Paint mEarthPaint;
    private Paint arrowPaint;


    private TypedArray a;

    public CustomView(Context context) {
        this(context,null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0); //Resources Access
        arcColor = a.getColor(R.styleable.CustomView_arcColor, ContextCompat.getColor(context, android.R.color.black));
        arrowColor = a.getColor(R.styleable.CustomView_arrowColor, ContextCompat.getColor(context, android.R.color.holo_red_dark));
        markColor = a.getColor(R.styleable.CustomView_markColor, ContextCompat.getColor(context, android.R.color.black));
        textColor = a.getColor(R.styleable.CustomView_textColor, ContextCompat.getColor(context, android.R.color.black));
        textSize = a.getInt(R.styleable.CustomView_textSize, 20);
        lineColor = a.getColor(R.styleable.CustomView_lineColor, ContextCompat.getColor(context, android.R.color.black));
        SKY_COLOR = a.getColor(R.styleable.CustomView_skyColor, ContextCompat.getColor(context, android.R.color.holo_blue_dark));

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(arcColor);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setFakeBoldText(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        lineUpPaint = new Paint();
        lineUpPaint.setColor(lineColor);
        lineUpPaint.setStyle(Paint.Style.STROKE);
        lineUpPaint.setAntiAlias(true);

        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        arrowPaint = new Paint();
        arrowPaint.setColor(arrowColor);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setStrokeWidth(5);
        arrowPaint.setAntiAlias(true);
        arrowPaint.setStrokeCap(Paint.Cap.ROUND);

        mEarthPaint = new Paint();
        mEarthPaint.setAntiAlias(true);
        mEarthPaint.setColor(EARTH_COLOR);


        a.recycle();

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mDstBitmap != null)
            mDstBitmap.recycle();

        if (linesBitmap != null)
            linesBitmap.recycle();

        mWidth = w;
        mHeight = h;
    }


    //Crea el ovalo (La forma del view)
    private Bitmap getCircle(float radio, int Width, int Height) {

        if (mDstBitmap == null) {
            mDstBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mDstBitmap);
            c.drawColor(Color.TRANSPARENT);

            float Width2 = c.getWidth()/2; //obtengo medidas del bitmap
            float Height2 = c.getHeight()/2;
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.RED);
            c.drawOval(new RectF(Width2-radio,Height2-radio,Width2+radio,Height2+radio), p);

        }


        return mDstBitmap;
    }

    public Bitmap Lines(float radio, int Width, int Height) {

        if (linesBitmap == null){
            linesBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            linesCanvas = new Canvas(linesBitmap);
        }


        Canvas canvas = linesCanvas;

        //Background
        ////canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //It's like repaint
        canvas.drawColor(SKY_COLOR);

        float centerX = Width / 2;
        float centerY = Height / 2;

        canvas.save();

        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch / TOTAL_VISIBLE_PITCH_DEGREES) * Height);

        //The earth paint
        canvas.drawRect(-mWidth, centerY, mWidth * 2, mHeight * 2, mEarthPaint);


        //Triangle
        float bottomLadderStepX = radio/3;
        float bottomLadderStepY = radio/3;
        canvas.drawLine(centerX, centerY, centerX - bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);
        canvas.drawLine(centerX, centerY, centerX + bottomLadderStepX * 2f, centerY
                + bottomLadderStepY * 2f, linePaint);

        //draw bottom lines
        for (int i = 1; i < 8; i++) {
            float y = centerY + bottomLadderStepY * i/3;
            if (i%2==0)
                canvas.drawLine(centerX - radio * i/9f, y, centerX + radio * i/9f , y,
                        linePaint); //en vez de radio estaba bottomLadderStepX

        }

        //Draw up lines
        float ladderStepY = radio/3;
        for (int i = 1; i <= 8; i++) {
            float width = Width / 8;
            float y = centerY - ladderStepY * i/3;
            if(i % 2 == 0)
                canvas.drawLine(centerX - width / 2, y, centerX + width / 2, y, lineUpPaint);
        }


        //canvas.save();
        canvas.restore();


        //LO que no se mueve

        //canvas.drawPoint(centerX, centerY, linePaint);

        canvas.drawCircle(centerX, centerY, radio/20, arrowPaint);
        canvas.drawLine(centerX-(radio/20), centerY, centerX - (radio/4), centerY, arrowPaint);
        canvas.drawLine(centerX + (radio/20), centerY, centerX + (radio/4), centerY, arrowPaint);
        canvas.drawLine(centerX, centerY-radio/15, centerX-10, centerY+radio/20, arrowPaint);
        canvas.drawLine(centerX, centerY-radio/15, centerX+10, centerY+radio/20, arrowPaint);

        Path triangle = new Path();
        triangle.moveTo(centerX, centerY-radio/15);
        triangle.lineTo(centerX-10, centerY-radio);


//        canvas.drawPath(triangle, arrowPaint);

        return linesBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        int viewWidthHalf = getWidth()/2;
        int viewHeightHalf = getHeight()/2;

        float centerX = viewWidthHalf; /// cambiar todos los centerX y centerY por su respectivo viewHalf
        float centerY = viewWidthHalf;///

        float radius = 0.0f;
        if(viewWidthHalf>viewHeightHalf)
            radius=(viewHeightHalf/2); // +69, -28 , -10
        else
            radius=(viewWidthHalf/2);


        //Green Arc
        //canvas.drawArc(new RectF(viewWidthHalf-radius,viewHeightHalf-radius,viewWidthHalf+radius,viewHeightHalf+radius),mRoll+mPitch*radius/90,180-2*mPitch*radius/90,false,circlePaint);

        //// Lines and degrees marks
//        canvas.save();



        //canvas.restore();

        ////

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

        canvas.save();



        canvas.rotate(-mRoll, centerX, centerY ); //Arrow movement

        //arrow
        Path rollArrow = new Path();
        rollArrow.moveTo(centerX, viewHeightHalf-radius+textSize);
        rollArrow.lineTo(centerX-12, viewHeightHalf-radius+50);
        rollArrow.lineTo(centerX+12, viewHeightHalf-radius+50);
        rollArrow.lineTo(centerX, viewHeightHalf-radius+textSize);
        canvas.drawPath(rollArrow, arrowPaint);

        canvas.restore();

        canvas.rotate(-80, centerX, centerY);
        for (int i = -80; i < 90; i += 10)
        {
            // Show a numeric value every 30 degrees
            if (i % 30 == 0) {
                String rollString = String.valueOf(i*-1);
                float rollStringWidth = textPaint.measureText(rollString);
                PointF rollStringCenter = new PointF(centerX-rollStringWidth/2,
                        radius+20);
                canvas.drawText(rollString, rollStringCenter.x, rollStringCenter.y, textPaint);
            }
            // Otherwise draw a marker line
            else {
                canvas.drawLine(centerX, (int)radius,
                        centerX, radius+ 7,
                        linePaint);
            }

            canvas.rotate(10, centerX, centerY);
        }

        canvas.restore();
    }

    public void setAttitude(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;
        invalidate();
    }
}
