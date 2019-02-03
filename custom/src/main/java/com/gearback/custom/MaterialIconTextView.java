package com.gearback.custom;

import android.content.Context;
        import android.graphics.Typeface;
        import android.util.AttributeSet;
        import android.widget.TextView;

public class MaterialIconTextView extends TextView {

    public MaterialIconTextView(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MaterialIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MaterialIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("materialicons.ttf", context);
        setTypeface(customFont);
    }
}

