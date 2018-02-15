package org.vampedit.android.drawview.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class RasterLayer extends BaseLayer {
    private Bitmap bitmap;
    private Canvas canvas;

    public RasterLayer(Context context, Point location, int width, int height) {
        super(context, location, width, height);

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitmap);
    }

    @Override
    public Canvas getDisplayCanvas() {
        return canvas;
    }

    @Override
    public Bitmap getDisplayBitmap() {
        return bitmap;
    }
}
