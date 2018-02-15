package org.vampedit.android.drawview.tools.brush;

import android.graphics.Canvas;
import android.graphics.PointF;

import org.vampedit.android.drawview.DrawUtils;

public class BasicBrush extends BaseBrush {
    protected PointF previousPoint;
    protected PointF currentPoint;
    protected PointF nextPoint;

    @Override
    public void onTouchDown(Canvas canvas, float x, float y) {
        previousPoint = currentPoint = nextPoint = new PointF(x, y);
    }

    @Override
    public void onTouchMove(Canvas canvas, float x, float y) {
        if (currentPoint == null) {
            return;
        }

        previousPoint = currentPoint;
        currentPoint = nextPoint;
        nextPoint = new PointF(x, y);

        DrawUtils.drawLine(canvas, paintForeground, previousPoint, currentPoint, nextPoint);
    }

    @Override
    public void onTouchUp(Canvas canvas, float x, float y) {
        if (currentPoint == null) {
            return;
        }

        previousPoint = currentPoint;
        currentPoint = nextPoint;
        nextPoint = new PointF(x, y);

        DrawUtils.drawLine(canvas, paintForeground, previousPoint, currentPoint, nextPoint);

        previousPoint = currentPoint = nextPoint = null;
    }
}
