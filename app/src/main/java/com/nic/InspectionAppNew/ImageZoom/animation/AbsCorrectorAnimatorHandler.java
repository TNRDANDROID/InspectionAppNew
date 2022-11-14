package com.nic.InspectionAppNew.ImageZoom.animation;

import android.animation.ValueAnimator;

import com.nic.InspectionAppNew.ImageZoom.ImageMatrixCorrector;


public abstract class AbsCorrectorAnimatorHandler implements ValueAnimator.AnimatorUpdateListener {

    private ImageMatrixCorrector corrector;
    private float[] values;

    public AbsCorrectorAnimatorHandler(ImageMatrixCorrector corrector) {
        this.corrector = corrector;
        this.values = new float[9];
    }

    public ImageMatrixCorrector getCorrector() {
        return corrector;
    }

    protected float[] getValues() {
        return values;
    }
}
