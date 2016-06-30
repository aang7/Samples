package io.radika.bitmaptest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    CustomView customView;
    int pitch, roll;

    private GoHomeGauge gauge;
    private int valor = 0;
    private int distance = 0;

    private SensorManager mSensorManager;
    @Nullable
    private Sensor mRotationSensor;
    private WindowManager mWindowManager;

    private TextView showValor;
    private TextView showDistance;

    private TextView showPitch;
    private TextView showRoll;

    //private String _pitch = "Pitch: ";
    //private String _roll  = "Roll: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customView = (CustomView)findViewById(R.id.cVIEW);
        pitch = 0;
        roll = 0;

        mWindowManager = getWindow().getWindowManager();

        mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        gauge = (GoHomeGauge) findViewById(R.id.gaugeAtLocation);
        gauge.setValue(valor, distance);

        showValor = (TextView) findViewById(R.id.showValor);
        showDistance = (TextView) findViewById(R.id.showDistance);
        showPitch = (TextView) findViewById(R.id.showPitch);
        showRoll = (TextView) findViewById(R.id.showRoll);

        showValor.setText(Integer.toString(valor));
        showDistance.setText(Integer.toString(distance));



    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(this, mRotationSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Botones
    public void IncrementaD(View view){
        distance +=5;
        gauge.setValue(valor, distance);
        showDistance.setText(Integer.toString(distance));
    }

    public void DecrementaD(View view){
        distance -=5;
        gauge.setValue(valor, distance);
        showDistance.setText(Integer.toString(distance));

    }

    public void Incrementa(View view) {
        valor += 10;
        gauge.setValue(valor, distance);
        showValor.setText(Integer.toString(valor));
    }

    public void Decrementa(View view) {
        valor -= 10;
        gauge.setValue(valor, distance);
        showValor.setText(Integer.toString(valor));

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
            updateOrientation(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void updateOrientation(float[] rotationVector) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        final int worldAxisForDeviceAxisX;
        final int worldAxisForDeviceAxisY;

        // Remap the axes as if the device screen was the instrument panel,
        // and adjust the rotation matrix for the device orientation.
        switch (mWindowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
            default:
                worldAxisForDeviceAxisX = SensorManager.AXIS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
                break;
            case Surface.ROTATION_90:
                worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
                break;
            case Surface.ROTATION_180:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
                worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
                break;
            case Surface.ROTATION_270:
                worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
                worldAxisForDeviceAxisY = SensorManager.AXIS_X;
                break;
        }

        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
                worldAxisForDeviceAxisY, adjustedRotationMatrix);

        // Transform rotation matrix into azimuth/pitch/roll
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        // Convert radians to degrees
        float pitch = orientation[1] * -57;
        float roll = orientation[2] * -57;

        Log.d("MAIN Pitch",Float.toString(pitch));
        Log.d("MAIN Roll",Float.toString(roll));
        customView.setAttitude(pitch, roll);

        showPitch.setText(Float.toString(pitch));
        showRoll.setText(Float.toString(roll));
    }
}
