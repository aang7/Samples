package io.radika.bitmaptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    // created and reused in onDraw
    private Bitmap linesBitmap;
    private Bitmap mDstBitmap;
    private Canvas linesCanvas;

    private final Paint paint;
    private Paint mBitmapPaint;
    private Paint linePaint;
    private Paint lineUpPaint;

    private final PorterDuffXfermode mXfermode;

    private int mWidth;
    private int mHeight;
    private int arcColor;

    private float mPitch = 0; // Degrees
    private float mRoll = 0; // Degrees, left roll is positive

    private Paint textPaint;


    private TypedArray a;

    public CustomView(Context context) {
        this(context,null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        a = context.obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0); //Resources Access
        arcColor = a.getColor(R.styleable.CustomView_arcColor, ContextCompat.getColor(context, android.R.color.white));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(arcColor);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setFakeBoldText(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        lineUpPaint = new Paint();
        lineUpPaint.setColor(Color.BLACK);
        lineUpPaint.setStyle(Paint.Style.STROKE);
        lineUpPaint.setAntiAlias(true);

        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        //textHeight = (int) textPaint.measureText("YYY"); //ESTO NO JALA
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20.0f);
        textPaint.setFakeBoldText(true);
        textPaint.setSubpixelText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    //Crea el ovalo (La forma del view)
    private Bitmap getCircle(int radio, int Width, int Height) {

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

    public Bitmap Lines(int radio, int Width, int Height) {

        if (linesBitmap == null){
            linesBitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            linesCanvas = new Canvas(linesBitmap);
        }

        Canvas canvas = linesCanvas;

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //It's like repaint

        float centerX = Width / 2;
        float centerY = Height / 2;

        canvas.save();
        canvas.rotate(mRoll, centerX, centerY);
        canvas.translate(0, (mPitch / TOTAL_VISIBLE_PITCH_DEGREES) * Height);

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

        float centerX = viewWidthHalf; ///
        float centerY = viewWidthHalf;///

        int radius = 0;
        if(viewWidthHalf>viewHeightHalf)
            radius=(viewHeightHalf/2); // +69, -28 , -10
        else
            radius=(viewWidthHalf/2);

        //Green Arc
        canvas.drawArc(new RectF(viewWidthHalf-radius,viewHeightHalf-radius,viewWidthHalf+radius,viewHeightHalf+radius),mRoll+mPitch*radius/90,180-2*mPitch*radius/90,false,paint);

        ////
        //canvas.restore();
        canvas.save();

        canvas.rotate(180, centerX, centerY);
        for (int i = -180; i < 180; i += 10)
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
                        centerX, radius+ 5,
                        linePaint);
            }

            canvas.rotate(10, centerX, centerY);
        }

        //canvas.save();
        canvas.restore();


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

    }

    public void setAttitude(float pitch, float roll) {
        mPitch = pitch;
        mRoll = roll;
        invalidate();
    }
}
