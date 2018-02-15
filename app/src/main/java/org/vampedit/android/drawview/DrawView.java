package org.vampedit.android.drawview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.vampedit.android.R;
import org.vampedit.android.drawview.layers.BaseLayer;
import org.vampedit.android.drawview.layers.RasterLayer;
import org.vampedit.android.drawview.tools.BaseTool;
import org.vampedit.android.drawview.tools.brush.BasicBrush;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    // How long to wait before locking into single or double finger
    protected static final long TOUCH_LOCK_TIME = 50L;

    protected List<BaseLayer> layers = new ArrayList<>();
    protected int currentLayerIndex = -1;
    protected Paint transparentPaint;

    protected BaseLayer cachedCombinedLayer;
    protected Paint bitmapPaint;

    protected BaseTool currentTool = new BasicBrush();

    protected float scaleFactor = 1.0F;
    protected PointF drawLocation = new PointF(0, 0);
    protected PointF drawLocationMod = new PointF(0,0);

    protected long firstTouchTimestamp = 0L;
    protected List<PointF> firstTouchPoints = new ArrayList<>();
    protected boolean oneFingerLock = false;
    protected boolean twoFingerLock = false;

    protected int imageWidth = 0;
    protected int imageHeight = 0;

    protected Paint backgroundDashedPaint;
    protected Paint foregroundDashedPaint;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setColor(Color.BLACK);
        bitmapPaint.setStyle(Paint.Style.STROKE);
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        bitmapPaint.setStrokeCap(Paint.Cap.ROUND);

        currentTool.setColorForeground(0xFF000000);
        currentTool.setColorBackground(0xFF000000);
        // TODO: Make this themeable
        setBackgroundColor(0xFF808080);

        // Transparent background paint
        Bitmap transparentTile = DrawUtils.drawableToBitmap(
                ContextCompat.getDrawable(context, R.drawable.ic_tile));

        BitmapShader shader = new BitmapShader(transparentTile, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);

        transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        transparentPaint.setColor(Color.BLACK);
        transparentPaint.setShader(shader);
        transparentPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        transparentPaint.setAntiAlias(true);

        // Background dashed outline
        backgroundDashedPaint = new Paint();
        backgroundDashedPaint.setStyle(Paint.Style.STROKE);
        backgroundDashedPaint.setPathEffect(new DashPathEffect(new float[]{12, 4}, 0));
        backgroundDashedPaint.setColor(Color.BLACK);
        backgroundDashedPaint.setStrokeWidth(4);

        // Foreground dash outline
        foregroundDashedPaint = new Paint();
        foregroundDashedPaint.setStyle(Paint.Style.STROKE);
        foregroundDashedPaint.setPathEffect(new DashPathEffect(new float[]{10, 6}, 0));
        foregroundDashedPaint.setColor(Color.WHITE);
        foregroundDashedPaint.setStrokeWidth(2);
    }

    public BaseTool getTool() {
        return currentTool;
    }

    public void setTool(BaseTool tool) {
        this.currentTool = tool;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // TODO: Improve, make dynamic?
        if (cachedCombinedLayer == null) {
            cachedCombinedLayer = new RasterLayer(getContext(), new Point(0, 0),
                    right - left, bottom - top);

            layers.add(new RasterLayer(getContext(), new Point(0, 0),
                    right - left, bottom - top));

            currentLayerIndex = 0;

            imageWidth = right - left;
            imageHeight = bottom - top;

            postInvalidateLayers();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (cachedCombinedLayer != null) {
            int pad = (int)(backgroundDashedPaint.getStrokeWidth() / 2.0f);
            float left = drawLocation.x * scaleFactor;
            float top = drawLocation.y * scaleFactor;
            float right = (drawLocation.x + imageWidth) * scaleFactor;
            float bottom = (drawLocation.y + imageHeight) * scaleFactor;

            if (top < 0) {
                top = 0;
            }

            if (left < 0) {
                left = 0;
            }

            if (right > getWidth()) {
                right = getWidth();
            }

            if (bottom > getHeight()) {
                bottom = getHeight();
            }

            canvas.drawRect(left, top, right, bottom, transparentPaint);

            canvas.save();
            canvas.scale(scaleFactor, scaleFactor);

            canvas.drawBitmap(cachedCombinedLayer.getDisplayBitmap(),
                    drawLocation.x, drawLocation.y, bitmapPaint);

            canvas.restore();

            // Draw dashed lines
            Path androidSucksBackgroundDashedLine = new Path();
            androidSucksBackgroundDashedLine.moveTo(left - pad, top - pad);
            androidSucksBackgroundDashedLine.lineTo(right + pad, top - pad);
            androidSucksBackgroundDashedLine.moveTo(left - pad, top - pad);
            androidSucksBackgroundDashedLine.lineTo(left - pad, bottom + pad);

            androidSucksBackgroundDashedLine.moveTo(right + pad, bottom + pad);
            androidSucksBackgroundDashedLine.lineTo(right + pad, top - pad);
            androidSucksBackgroundDashedLine.moveTo(right + pad, bottom + pad);
            androidSucksBackgroundDashedLine.lineTo(left - pad, bottom + pad);

            canvas.drawPath(androidSucksBackgroundDashedLine, backgroundDashedPaint);
            canvas.drawPath(androidSucksBackgroundDashedLine, foregroundDashedPaint);

        }
    }

    private float oldDistance = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getPointerCount() > 1 && !oneFingerLock) || twoFingerLock) {
            twoFingerLock = true;

            if (event.getPointerCount() > 1) {
                PointF point1 = new PointF(event.getX(0), event.getY(0));
                PointF point2 = new PointF(event.getX(1), event.getY(1));
                float newDistance = DrawUtils.getDistance(point1, point2);

                if (oldDistance > -1) {
                    float scale = 1 - ((oldDistance - newDistance) / oldDistance);

                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor * scale, 5.0f));

                    this.drawLocation = DrawUtils.midPointF(
                            new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    this.drawLocation = new PointF((drawLocation.x / scaleFactor) - (drawLocationMod.x),
                            (drawLocation.y / scaleFactor) - (drawLocationMod.y));

                    //this.drawLocation.x -= this.cachedCombinedLayer.getSize().getWidth() / 2;
                    //this.drawLocation.y -= this.cachedCombinedLayer.getSize().getHeight() / 2;

                    Log.i("asdasdasd", "[" + oldDistance + ", " + newDistance + "] " + scale);
                } else {
                    // Get the drawLocation and scale (start is [0, 0] and [1.0])
                    // Find the coordinate within the layer
                    // Take that coordinate and find what it is in the parent coordinates
                    PointF newDrawLocationMod = DrawUtils.midPointF(point1, point2);
                    drawLocationMod = new PointF((newDrawLocationMod.x / scaleFactor) - (drawLocation.x),
                            (newDrawLocationMod.y / scaleFactor) - (drawLocation.y));
                }

                oldDistance = newDistance;

                postInvalidate();
            }

            // Down will never be called here.
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    twoFingerLock = false;
                    oldDistance = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                default:
                    break;
            }

            return true;
        } else if ((currentLayerIndex > -1 && !layers.get(currentLayerIndex).getIsReadOnly()
                && currentTool != null) && !twoFingerLock) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // We don't add any of the 'firstTouchPoint's here because Android has a bug
                    // where if you move too quickly it won't detect you're still touching and
                    // send multiple 'DOWN->UP' events. Reproducible in all other drawing apps-
                    // (Because most other drawing apps draw the dots, which we won't do)
                    // If anyone is interested they can try to implement it where if events come in
                    // too quickly you just take the first down and last up in a certain time frame
                    currentTool.onTouchUp(layers.get(currentLayerIndex).getDisplayCanvas(),
                            (event.getX() / scaleFactor) - drawLocation.x, (event.getY() / scaleFactor) - drawLocation.y);

                    oneFingerLock = false;
                    firstTouchPoints.clear();
                    break;
                case MotionEvent.ACTION_DOWN:
                    //currentTool.onTouchDown(layers.get(currentLayerIndex).getDisplayCanvas(),
                    //        event.getX(), event.getY());

                    firstTouchTimestamp = System.currentTimeMillis();
                    firstTouchPoints.clear();
                    firstTouchPoints.add(new PointF((event.getX() / scaleFactor) - drawLocation.x, (event.getY() / scaleFactor) - drawLocation.y));
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (System.currentTimeMillis() - firstTouchTimestamp > TOUCH_LOCK_TIME) {
                        if (!oneFingerLock && firstTouchPoints.size() > 0) {
                            currentTool.onTouchDown(layers.get(currentLayerIndex).getDisplayCanvas(),
                                    firstTouchPoints.get(0).x, firstTouchPoints.get(0).y);

                            for (int i = 1; i < firstTouchPoints.size(); ++i) {
                                currentTool.onTouchMove(layers.get(currentLayerIndex).getDisplayCanvas(),
                                        firstTouchPoints.get(i).x, firstTouchPoints.get(i).y);
                            }

                            firstTouchPoints.clear();
                        }

                        oneFingerLock = true;

                        currentTool.onTouchMove(layers.get(currentLayerIndex).getDisplayCanvas(),
                                (event.getX() / scaleFactor) - drawLocation.x, (event.getY() / scaleFactor) - drawLocation.y);
                    } else {
                        firstTouchPoints.add(new PointF((event.getX() / scaleFactor) - drawLocation.x, (event.getY() / scaleFactor) - drawLocation.y));
                    }

                    break;
                default:
                    break;
            }

            postInvalidateLayers();

            return true;
        }

        return false;
    }

    protected void postInvalidateLayers() {
        Canvas cachedCanvas = cachedCombinedLayer.getDisplayCanvas();

        cachedCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        for (BaseLayer layer : layers) {
            cachedCanvas.drawBitmap(layer.getDisplayBitmap(), layer.getLocation().x, layer.getLocation().y, layer.getDisplayPaint());
        }

        postInvalidate();
    }
}
