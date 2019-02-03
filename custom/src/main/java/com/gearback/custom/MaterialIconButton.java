package com.gearback.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MaterialIconButton extends Button {

    public MaterialIconButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public MaterialIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MaterialIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }
    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("materialicons.ttf", context);
        setTypeface(customFont);
    }
}
