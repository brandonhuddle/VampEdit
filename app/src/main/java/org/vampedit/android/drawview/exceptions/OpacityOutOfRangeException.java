package org.vampedit.android.drawview.exceptions;

public class OpacityOutOfRangeException extends Exception {
    private float wrong;
    private float lo;
    private float hi;

    public OpacityOutOfRangeException(float wrong, float lo, float hi) {
        super("Opacity '" + String.valueOf(wrong) +
                "' is out of the range range '" + String.valueOf(lo) + "'-'" + String.valueOf(hi) +
                "'!");
        this.wrong = wrong;
        this.lo = lo;
        this.hi = hi;
    }

    public float getWrong() {
        return wrong;
    }

    public float getLo() {
        return lo;
    }

    public float getHi() {
        return hi;
    }
}
