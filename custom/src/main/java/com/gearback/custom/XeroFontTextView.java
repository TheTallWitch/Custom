package com.gearback.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class XeroFontTextView extends TextView {

    public XeroFontTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public XeroFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public XeroFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("xero.ttf", context);
        setTypeface(customFont);
    }
}
