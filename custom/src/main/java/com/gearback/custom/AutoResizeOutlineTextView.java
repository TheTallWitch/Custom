package com.gearback.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.widget.TextView;

import java.lang.reflect.Field;

public class AutoResizeOutlineTextView extends android.support.v7.widget.AppCompatTextView {

    public interface OnTextResizeListener {
        public void onTextResize(TextView textView, float oldSize, float newSize);
    }

    public static final float MIN_TEXT_SIZE = 20;
    private static final String mEllipsis = "...";
    private OnTextResizeListener mTextResizeListener;
    private boolean mNeedsResize = false;
    private float mTextSize;
    private float mMaxTextSize = 0;
    private float mMinTextSize = MIN_TEXT_SIZE;
    private float mSpacingMult = 1.0f;
    private float mSpacingAdd = 0.0f;
    private boolean mAddEllipsis = true;

    private Field colorField;
    private int textColor;
    private int outlineColor;
    private String fontName = "";

    public AutoResizeOutlineTextView(Context context) {
        this(context, null);
    }

    public AutoResizeOutlineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        applyBorder(context, attrs);
    }

    public AutoResizeOutlineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyBorder(context, attrs);
        mTextSize = getTextSize();
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        mNeedsResize = true;
        resetTextSize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mNeedsResize = true;
        }
    }

    public void setOnResizeListener(OnTextResizeListener listener) {
        mTextResizeListener = listener;
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        mTextSize = getTextSize();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        mTextSize = getTextSize();
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        mSpacingMult = mult;
        mSpacingAdd = add;
    }

    public void setMaxTextSize(float maxTextSize) {
        mMaxTextSize = maxTextSize;
        requestLayout();
        invalidate();
    }

    public float getMaxTextSize() {
        return mMaxTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        mMinTextSize = minTextSize;
        requestLayout();
        invalidate();
    }

    public float getMinTextSize() {
        return mMinTextSize;
    }

    public void setAddEllipsis(boolean addEllipsis) {
        mAddEllipsis = addEllipsis;
    }

    public boolean getAddEllipsis() {
        return mAddEllipsis;
    }

    public void resetTextSize() {
        if (mTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mMaxTextSize = mTextSize;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed || mNeedsResize) {
            int widthLimit = (right - left) - getCompoundPaddingLeft() - getCompoundPaddingRight();
            int heightLimit = (bottom - top) - getCompoundPaddingBottom() - getCompoundPaddingTop();
            resizeText(widthLimit, heightLimit);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void resizeText() {
        int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
        int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
        resizeText(widthLimit, heightLimit);
    }

    public void resizeText(int width, int height) {
        CharSequence text = getText();
        if (text == null || text.length() == 0 || height <= 0 || width <= 0 || mTextSize == 0) {
            return;
        }

        if (getTransformationMethod() != null) {
            text = getTransformationMethod().getTransformation(text, this);
        }

        TextPaint textPaint = getPaint();
        float oldTextSize = textPaint.getTextSize();
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize) : mTextSize;

        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        }

        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            TextPaint paint = new TextPaint(textPaint);
            StaticLayout layout = new StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            if (layout.getLineCount() > 0) {
                int lastLine = layout.getLineForVertical(height) - 1;
                if (lastLine < 0) {
                    setText("");
                }
                else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = textPaint.measureText(mEllipsis);

                    while (width < lineWidth + ellipseWidth) {
                        lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        mNeedsResize = false;
    }

    private int getTextHeight(CharSequence source, TextPaint paint, int width, float textSize) {
        TextPaint paintCopy = new TextPaint(paint);
        paintCopy.setTextSize(textSize);
        StaticLayout layout = new StaticLayout(source, paintCopy, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
        return layout.getHeight();
    }
    
    //////////////OUTLINE//////////////
    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(fontName, context);
        setTypeface(customFont);
    }

    private void applyBorder(Context context, AttributeSet attrs) {
        try {
            colorField = TextView.class.getDeclaredField("mCurTextColor");
            colorField.setAccessible(true);
            textColor = getTextColors().getDefaultColor();
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OutlineAutoTextView);
            outlineColor = a.getColor(R.styleable.OutlineAutoTextView_outlineAutoColor, Color.TRANSPARENT);
            setOutlineStrokeWidth(a.getDimensionPixelSize(R.styleable.OutlineAutoTextView_outlineAutoWidth, 0));
            fontName = a.getString(R.styleable.OutlineAutoTextView_outlineAutoFont);
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
