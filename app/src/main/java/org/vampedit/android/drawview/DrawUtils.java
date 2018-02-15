package org.vampedit.android.drawview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawUtils {
    public static void drawLine(Canvas canvas, Paint paint, PointF point0, PointF point1,
                                PointF point2) {
        draw(canvas, paint, midPointF(point1, point0), point1, midPointF(point2, point1));
    }

    public static PointF getPointF(PointF p1, PointF p2, float percent) {
        return new PointF(p1.x + ((p2.x - p1.x) * percent), p1.y + ((p2.y - p1.y) * percent));
    }

    public static void draw(Canvas canvas, Paint paint, PointF point0, PointF point1,
                            PointF point2) {
        for (float i = 0f; i < 1f; i += 0.0085f) {
            PointF p = getPointF(getPointF(point0, point1, i), getPointF(point1, point2, i), i);

            canvas.drawPoint(p.x, p.y, paint);
        }
    }

    public static PointF midPointF(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0F);
    }

    public static float getDistance(PointF p1, PointF p2) {
        float x = p1.x - p2.x;
        float y = p1.y - p2.y;

        return (float)Math.sqrt(x * x + y * y);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
