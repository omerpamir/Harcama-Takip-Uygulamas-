package com.fm.egecuzdan.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.fm.egecuzdan.R;

import java.util.ArrayList;

public class CustomPieChart extends View {

    private Paint paint;
    private ArrayList<Float> pieValues;
    private RectF rectF;
    private Context context;
    private final int diameter;


    public CustomPieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        diameter = 320;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF(0, 0, diameter, diameter);
        pieValues = new ArrayList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (pieValues != null) {
            //draw regular arc
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorDüzenli));
            canvas.drawArc(rectF, 0, pieValues.get(0), true, paint);
            //draw non-regular arc
            paint.setColor(ContextCompat.getColor(getContext(), R.color.colorDüzensiz));
            canvas.drawArc(rectF, pieValues.get(0), pieValues.get(1), true, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(diameter, widthSize), Math.min(diameter, heightSize));
    }

    public void drawChart(Context context, ArrayList<Float> pieValues) {
        this.context = context;
        this.pieValues = convertToAngle(pieValues);
        invalidate();
    }

    //returns angle values to plot the pie graph
    private ArrayList<Float> convertToAngle(ArrayList<Float> values) {
        float total = 0;
        //calculate the total value
        for (int i = 0; i < values.size(); i++) {
            total += values.get(i);
        }
        //calculate angle for each slice and return in same array
        for (int i = 0; i < values.size(); i++) {
            values.set(i, ((values.get(i) / total) * 360));
        }
        return values;
    }
}