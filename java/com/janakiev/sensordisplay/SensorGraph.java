package com.janakiev.sensordisplay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

public class SensorGraph {
    private static final String TAG = "SensorGraph";
    private Paint paint[];
    private Paint axisPaint;
    private Paint textPaint;
    private float[][] values;
    private final int valueSize;
    private final int dims;
    private int valueIdx;
    private float minValue;
    private float maxValue;

    private String description = "";

    static public final float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

    public SensorGraph(int valueSize, int dims) {
        this(valueSize, dims, Float.MAX_VALUE, Float.MIN_VALUE, "");
    }

    public SensorGraph(int valueSize, int dims, float minValue, float maxValue, String description) {
        this.description = description;
        this.valueSize = valueSize;
        this.dims = dims;
        values = new float[dims][valueSize];
        valueIdx = 0;

        this.minValue = minValue;
        this.maxValue = maxValue;

        axisPaint = new Paint();
        axisPaint.setStrokeWidth(1.0f);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setColor(Color.BLACK);


        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        paint = new Paint[dims];
        for (int i=0; i < dims; i++) {
            paint[i] = new Paint();
            paint[i].setStrokeWidth(3.0f);
            paint[i].setStyle(Paint.Style.STROKE);
            float hsv[] = { (float) i * 360.0f / (float) dims, 1.0f, 1.0f };
            paint[i].setColor(Color.HSVToColor(hsv));
        }
        Log.d(TAG, "Constructor with valueSize : " + valueSize + ", dims : " + dims);
    }

    public void setColors(int[] colors) {
        for (int i = 0; i < dims; i++) {
            paint[i].setColor(colors[i]);
        }
    }

    public void addValues(float[] newValues){
        for (int i = 0; i < dims; i++) {
            values[i][valueIdx] = newValues[i];
            if(values[i][valueIdx] < minValue)
                minValue = values[i][valueIdx];
            if(values[i][valueIdx] > maxValue)
                maxValue = values[i][valueIdx];
        }
        if(++valueIdx >= valueSize) valueIdx = 0;
    }

    public void draw(Canvas canvas, Rect rect) {

        // Draw for each dimension a single path
        Path path[] = new Path[dims];
        for (int i = 0; i < dims; i++) {
            path[i] = new Path();
            path[i].moveTo(rect.left, map(values[i][0], minValue, maxValue, rect.top, rect.bottom));
            for (int elem = 1; elem < valueSize; elem++) {
                path[i].lineTo(
                        map(elem, 0.0f, valueSize,
                                rect.left, rect.right),
                        map(values[i][elem], minValue, maxValue,
                                rect.top, rect.bottom));
            }
            canvas.drawPath(path[i], paint[i]);
        }

        // Draw horizontal axis
        Path axisPath = new Path();
        axisPath.moveTo(rect.left, map(0.0f, minValue, maxValue, rect.top, rect.bottom));
        axisPath.lineTo(rect.right, map(0.0f, minValue, maxValue, rect.top, rect.bottom));

        // Draw cursor
        axisPath.moveTo(map(valueIdx, 0.0f, valueSize, rect.left, rect.right), rect.top);
        axisPath.lineTo(map(valueIdx, 0.0f, valueSize, rect.left, rect.right), rect.bottom);

        // Draw top border
        canvas.drawRect(rect, axisPaint);

        canvas.drawPath(axisPath, axisPaint);

        canvas.drawText(description, rect.left + 5, rect.top + 30, textPaint);
    }

    public float getMaxValue(){
        return maxValue;
    }

    public float getMinValue(){
        return minValue;
    }
}
