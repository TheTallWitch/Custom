package com.gearback.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

public class OutlineTextview extends android.support.v7.widget.AppCompatTextView {

    private Field colorField;
    private int textColor;
    private int outlineColor;
    private String fontName = "";

    public OutlineTextview(Context context) {
        super(context);
    }

    public OutlineTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyBorder(context, attrs);
    }

    public OutlineTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyBorder(context, attrs);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(fontName, context);
        setTypeface(customFont);
    }

    private void applyBorder(Context context, AttributeSet attrs) {
        try {
            colorField = TextView.class.getDeclaredField("mCurTextColor");
            colorField.setAccessible(true);
            textColor = getTextColors().getDefaultColor();
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OutlineTextView);
            outlineColor = a.getColor(R.styleable.OutlineTextView_outlineColor, Color.TRANSPARENT);
            setOutlineStrokeWidth(a.getDimensionPixelSize(R.styleable.OutlineTextView_outlineWidth, 0));
            fontName = a.getString(R.styleable.OutlineTextView_outlineFont);
            if (!fontName.equals("")) {
                applyCustomFont(context);
            }
            a.recycle();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            colorField = null;
        }
    }

    @Override
    public void setTextColor(int color) {
        textColor = color;
        super.setTextColor(color);
    }

    public void setOutlineColor(int color) {
        outlineColor = color;
        invalidate();
    }

    public void setOutlineWidth(float width) {
        setOutlineStrokeWidth(width);
        invalidate();
    }

    private void setOutlineStrokeWidth(float width) {
        getPaint().setStrokeWidth(2 * width + 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (colorField != null) {
            setColorField(outlineColor);
            getPaint().setStyle(Paint.Style.STROKE);
            super.onDraw(canvas);

            setColorField(textColor);
            getPaint().setStyle(Paint.Style.FILL);
        }

        super.onDraw(canvas);
    }

    private void setColorField(int color) {
        try {
            colorField.setInt(this, color);
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.textColor = textColor;
        ss.outlineColor = outlineColor;
        ss.outlineWidth = getPaint().getStrokeWidth();
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        textColor = ss.textColor;
        outlineColor = ss.outlineColor;
        getPaint().setStrokeWidth(ss.outlineWidth);
    }

    private static class SavedState extends BaseSavedState {
        int textColor;
        int outlineColor;
        float outlineWidth;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            textColor = in.readInt();
            outlineColor = in.readInt();
            outlineWidth = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(textColor);
            out.writeInt(outlineColor);
            out.writeFloat(outlineWidth);
        }

        public static final Parcelable.Creator<SavedState>
                CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
