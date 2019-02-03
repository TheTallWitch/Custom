package com.gearback.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class SquareTextView extends TextView {

    private String fontName = "";

    public SquareTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public SquareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    public SquareTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("iranyekanregular.ttf", context);
        setTypeface(customFont);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareTextView);
        fontName = a.getString(R.styleable.SquareTextView_textFont);
        if (!fontName.equals("")) {
            Typeface customFont = FontCache.getTypeface(fontName, context);
            setTypeface(customFont);
        }
        else {
            Typeface customFont = FontCache.getTypeface("iranyekanregular.ttf", context);
            setTypeface(customFont);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (width < height) {
            setMeasuredDimension(width, width);
        }
        else {
            setMeasuredDimension(height, height);
        }
    }
}
