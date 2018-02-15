package org.vampedit.android.drawview.tools;

import android.graphics.Canvas;

public abstract class BaseTool {
    protected int colorForeground;
    protected int colorBackground;

    public int getColorForeground() {
        return colorForeground;
    }

    public void setColorForeground(int colorForeground) {
        this.colorForeground = colorForeground;
    }

    public int getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
    }

    public abstract void onTouchDown(Canvas canvas, float x, float y);
    public abstract void onTouchMove(Canvas canvas, float x, float y);
    public abstract void onTouchUp(Canvas canvas, float x, float y);
}
