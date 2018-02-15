package org.vampedit.android.drawview.tools.brush;

import android.graphics.Paint;

import org.vampedit.android.drawview.tools.BaseTool;

public abstract class BaseBrush extends BaseTool {
    protected float brushWidth = 20.0F;
    protected Paint paintForeground;
    protected Paint paintBackground;

    public BaseBrush() {
        paintForeground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintForeground.setColor(colorForeground);
        paintForeground.setAntiAlias(true);
        paintForeground.setStyle(Paint.Style.FILL);
        paintForeground.setStrokeJoin(Paint.Join.ROUND);
        paintForeground.setStrokeCap(Paint.Cap.ROUND);
        paintForeground.setStrokeWidth(brushWidth);

        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(colorForeground);
        paintBackground.setAntiAlias(true);
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setStrokeJoin(Paint.Join.ROUND);
        paintBackground.setStrokeCap(Paint.Cap.ROUND);
        paintBackground.setStrokeWidth(brushWidth);
    }

    //region Color Setters Overrides
    @Override
    public void setColorForeground(int colorForeground) {
        super.setColorForeground(colorForeground);
        paintForeground.setColor(colorForeground);
    }

    @Override
    public void setColorBackground(int colorBackground) {
        super.setColorBackground(colorBackground);
        paintBackground.setColor(colorBackground);
    }
    //endregion

    //region Getters & Setters for 'brushWidth'
    public float getBrushWidth() {
        return brushWidth;
    }

    public void setBrushWidth(float brushWidth) {
        this.brushWidth = brushWidth;
        paintForeground.setStrokeWidth(brushWidth);
        paintBackground.setStrokeWidth(brushWidth);
    }
    //endregion
}
