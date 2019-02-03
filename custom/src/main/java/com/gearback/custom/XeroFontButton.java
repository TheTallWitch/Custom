package com.gearback.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class XeroFontButton extends Button {

    public XeroFontButton(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public XeroFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public XeroFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }
    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("xero.ttf", context);
        setTypeface(customFont);
    }
}
