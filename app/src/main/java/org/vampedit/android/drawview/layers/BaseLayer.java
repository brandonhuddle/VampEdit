package org.vampedit.android.drawview.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.vampedit.android.drawview.exceptions.OpacityOutOfRangeException;

public abstract class BaseLayer {
    protected boolean isReadOnly = false;
    protected Context context;
    protected boolean hasFocus = false;
    protected boolean isVisible = true;
    protected Point location = new Point(0, 0);
    protected int width;
    protected int height;
    protected float opacity = 100.0F;

    protected Paint displayPaint;

    public BaseLayer(Context context, Point location, int width, int height) {
        this.context = context;
        this.location = location;
        this.width = width;
        this.height = height;

        this.displayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.displayPaint.setColor(Color.BLACK);
        this.displayPaint.setStyle(Paint.Style.STROKE);
        this.displayPaint.setAntiAlias(true);
        this.displayPaint.setStrokeJoin(Paint.Join.ROUND);
        this.displayPaint.setStrokeCap(Paint.Cap.ROUND);
        this.displayPaint.setAlpha(Math.round(opacity / 100.0F * 255.0F));
    }

    //region Getters & Setters for 'location', 'width', and 'height'
    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        setLocation(new Point(x, y));
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    //endregion

    //region Getter & Setter for 'isVisible' and 'opacity'
    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) throws OpacityOutOfRangeException {
        if (opacity > 100 || opacity < 0) {
            throw new OpacityOutOfRangeException(opacity, 0F, 100F);
        }

        this.opacity = opacity;
        // Set the paint alpha
        this.displayPaint.setAlpha(Math.round(opacity / 100.0F * 255.0F));
    }
    //endregion

    //region Getter for 'isReadOnly'
    public boolean getIsReadOnly() {
        return isReadOnly;
    }
    //endregion

    //region Focus Handling
    public void gainFocus() {
        this.hasFocus = true;
    }

    public void loseFocus() {
        this.hasFocus = false;
    }

    public boolean getHasFocus() {
        return hasFocus;
    }
    //endregion

    //region Image Generation
    public abstract Canvas getDisplayCanvas();
    public abstract Bitmap getDisplayBitmap();

    public Paint getDisplayPaint() {
        return new Paint(displayPaint);
    }
    //endregion
}
