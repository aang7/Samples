package com.aang.compassviewstudy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class CompassView extends View {

    private float bearing;

    //tools for use the resources
    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private String northString;
    private String southString;
    private String eastString;
    private String westString;
    private int textHeight;

    //Constructors
    public CompassView(Context context) {
        super(context);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompassView();
    }


    protected void initCompassView(){
        setFocusable(true);

        Resources r = this.getResources(); //objeto que nos servira para obtener los recursos

        /* Setting Up */
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);  //Paint flag that enables antialiasing when drawing
        circlePaint.setColor(r.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        northString = r.getString(R.string.cardinal_north);
        southString = r.getString(R.string.cardinal_south);
        westString = r.getString(R.string.cardinal_west);
        eastString = r.getString(R.string.cardinal_east);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));
        textPaint.setTextSize(20.0f);

        textHeight = (int) textPaint.measureText("YYY");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_color));

    }


    //Indica la magnitud del espacio donde se podra dibujar
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight); //Retorna el numero mas pequeño

        //Siempre tiene que llamar a esta funcion en este metodo
        setMeasuredDimension(d, d); //Siempre se llamara en onMeasure para alamacenar las medidas
    }

    private int measure(int measureSpec){
        int result = 0;

        //Decodifica las especificaciones de la magnitud
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        if (specMode == MeasureSpec.UNSPECIFIED){
            //retorna un valor por default, si los limites no se han especificado
            result = 200;
        }
        else {
            return result = specSize;
        }


        return result;
    }



    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }


    //Aqui ocurre la magia del dibujo
    @Override
    public void onDraw(Canvas canvas){
        int mMeasureWidth = getMeasuredWidth();
        int mMeasureHeight = getMeasuredHeight();

        //Nos ubicamps en el centro
        int px = mMeasureWidth / 2;
        int py = mMeasureHeight / 2;



        int radius = Math.min(px, py);

        //Draw the background
        canvas.drawCircle(px, py, radius, circlePaint);

        canvas.save();
        canvas.rotate(-bearing, px, py);

        int textWidth = (int) textPaint.measureText("W");
        int cardinalX = px-textWidth/2;
        int cardinalY = py-radius+textHeight;

        //Draw the marker every 15 degrees and text every 45
        for (int i = 0;i < 24; i++){
            //Dibuja marcador
            canvas.drawLine(px, py-radius, px, py-radius+10, markerPaint);
            canvas.save();

            //Dibuja los puntos cardinales
            if (i % 6 == 0){
                String dirString = "";
                switch (i) {
                    case 0:{
                        dirString = northString;
                        int arrowY = 2*textHeight;
                        canvas.drawLine(px, arrowY, px-5, 3*textHeight, markerPaint);
                        canvas.drawLine(px, arrowY, px+5, 3*textHeight, markerPaint);
                        break;
                    }
                    case 6 : dirString = eastString; break;
                    case 12 : dirString = southString; break;
                    case 18 : dirString = westString; break;
                }//End switch
                canvas.drawText(dirString, cardinalX, cardinalY, textPaint);
            }

            else if (i % 3 == 0){
                //Dibuja el texto alternativo cada 45 grados
                String angle = String.valueOf(i*15);
                float angleTextWidth = textPaint.measureText(angle);

                int angleTextX = (int)(px-angleTextWidth/2);
                int angleTextY = py-radius+textHeight;
                canvas.drawText(angle, angleTextX, angleTextY, textPaint);


            }
            canvas.restore();
            canvas.rotate(15,px,py);
        }
        canvas.restore();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent event) {
        super.dispatchPopulateAccessibilityEvent(event);
        if (isShown()) {
            String bearingStr = String.valueOf(bearing);
            if (bearingStr.length() > AccessibilityEvent.MAX_TEXT_LENGTH)
                bearingStr = bearingStr.substring(0, AccessibilityEvent.MAX_TEXT_LENGTH);
            event.getText().add(bearingStr);
            return true;
        }
        else
            return false;
    }
}
