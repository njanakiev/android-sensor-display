package com.janakiev.sensordisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SensorView extends View implements SensorEventListener{

    private static final String TAG = "SensorView";

    private List<SensorGraph> sensorGraphList;

    public SensorView(Context context) {
        super(context);

        sensorGraphList = new ArrayList<>();
        sensorGraphList.add(new SensorGraph(2000, 3,
                -40.0f, 40.0f, "Accelerometer"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                -30.0f, 30.f, "Gyroscope"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                -70.0f, 70.0f, "Magnetometer"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                -40.0f, 40.0f, "Linear Acceleration"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                -40.0f, 40.0f, "Gravity"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                (float) -Math.PI, (float) Math.PI, "Rotation Vector"));
        sensorGraphList.add(new SensorGraph(2000, 3,
                (float) -Math.PI, (float) Math.PI, "Geomagnetic Rotation Vector"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int size = sensorGraphList.size();

            for (int i = 0; i < size; i++){
                sensorGraphList.get(i).draw(canvas,
                        new Rect(0, i * height / size,
                                width, (i + 1) * height / size));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        final float[] values = event.values;

        switch (sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                sensorGraphList.get(0).addValues(values);
                break;

            case Sensor.TYPE_GYROSCOPE:
                sensorGraphList.get(1).addValues(values);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorGraphList.get(2).addValues(values);
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                sensorGraphList.get(3).addValues(values);
                break;

            case Sensor.TYPE_GRAVITY:
                sensorGraphList.get(4).addValues(values);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
                //sensorGraphList.get(0).addValues(values);
                float[] rotationMatrix = new float[16];
                float[] orientation = new float[3];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
                SensorManager.getOrientation(rotationMatrix, orientation);
                sensorGraphList.get(5).addValues(orientation);
                break;

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                //sensorGraphList.get(1).addValues(values);
                float[] geomagneticRotationMatrix = new float[16];
                float[] geomagneticOrientation = new float[3];
                SensorManager.getRotationMatrixFromVector(geomagneticRotationMatrix, values);
                SensorManager.getOrientation(geomagneticRotationMatrix, geomagneticOrientation);
                sensorGraphList.get(6).addValues(geomagneticOrientation);
                break;

            default:
                break;
        }
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
}
