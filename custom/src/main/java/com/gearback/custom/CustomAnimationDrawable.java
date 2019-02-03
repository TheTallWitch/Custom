package com.gearback.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;

public abstract class CustomAnimationDrawable extends AnimationDrawable {
    Handler mAnimationHandler;

    public CustomAnimationDrawable(Context context, Bitmap[] bitmaps, int frameDuration, int frameCount) {
        for (int i = 0; i < frameCount; i++) {
            this.addFrame(new BitmapDrawable(context.getResources(), bitmaps[i]), frameDuration);
        }
    }

    @Override
    public void start() {
        super.start();
        mAnimationHandler = new Handler();
        mAnimationHandler.post(new Runnable() {
            @Override
            public void run() {
                onAnimationStart();
            }
        });
        mAnimationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onAnimationFinish();
            }
        }, getTotalDuration());

    }

    public int getTotalDuration() {

        int iDuration = 0;

        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            iDuration += this.getDuration(i);
        }

        return iDuration;
    }

    public abstract void onAnimationFinish();
    public abstract void onAnimationStart();
}
