package com.gearback.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.gearback.methods.Methods;

public class CustomProgressBar extends View {

    private int max = 100;
    private int progress = 0;
    private Path path = new Path();
    int color = 0xff44C8E5, backgroundColor = 0x80000000, textColor = 0xff000000;
    private Paint paint;
    private Paint mPaintProgress;
    private RectF mRectF;
    private Paint textPaint;
    private String text = "", fontName = "iranyekanregular.ttf";
    private final Rect textBounds = new Rect();
    private int centerY;
    private int centerX;
    private int swipeAngle = 0;
    private int type = 0, sizeBase = 0, margin = 0, progressThickness = 10, backgroundThickness = 10; // 0 = number, 1 = percentage, 2 = time
    private Methods methods = new Methods();

    private int numeralSpacing = 0;
    private int handTruncation, hourHandTruncation = 0;
    private int radius = 0, hour = 0;
    private boolean isInit;
    private int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12};
    private int height, handWidth = 0;

    public CustomProgressBar(Context context) {
        super(context);
        initUI();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        type = a.getInt(R.styleable.CircleProgress_textType, 0);
        sizeBase = a.getInt(R.styleable.CircleProgress_sizeBase, 0);
        color = a.getColor(R.styleable.CircleProgress_progressColor, 0xff44C8E5);
        backgroundColor = a.getColor(R.styleable.CircleProgress_bgColor, 0x80000000);
        textColor = a.getColor(R.styleable.CircleProgress_textColor, 0xff000000);
        margin = a.getDimensionPixelSize(R.styleable.CircleProgress_margin, 0);
        progressThickness = a.getDimensionPixelSize(R.styleable.CircleProgress_progressThickness, 10);
        backgroundThickness = a.getDimensionPixelSize(R.styleable.CircleProgress_backgroundThickness, 10);
        fontName = a.getString(R.styleable.CircleProgress_fontName);

        a.recycle();
        initUI();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI();
    }

    private void initUI() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(backgroundThickness);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(backgroundColor);

        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(progressThickness);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        mPaintProgress.setColor(color);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = Math.min(widthMeasureSpec, heightMeasureSpec);
        if (sizeBase == 0) {
            width = widthMeasureSpec;
        }
        super.onMeasure(width, width);

        int viewWidth = MeasureSpec.getSize(width);
        int viewHeight = MeasureSpec.getSize(width);

        int radius = (viewWidth - margin) / 2;

        this.radius = radius;

        path.reset();
        centerX = viewWidth / 2;
        centerY = viewHeight / 2;
        path.addCircle(centerX, centerY, radius, Path.Direction.CW);

        int smallCircleRadius = radius - backgroundThickness;
        path.addCircle(centerX, centerY, smallCircleRadius, Path.Direction.CW);
        smallCircleRadius += methods.DPtoPX(4);
        handWidth = smallCircleRadius;

        mRectF = new RectF(centerX - smallCircleRadius, centerY - smallCircleRadius, centerX + smallCircleRadius, centerY + smallCircleRadius);
        textPaint.setTextSize(radius * 0.5f);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, 270, 360, false, paint);
        if (type < 3) {
            canvas.drawArc(mRectF, 270, swipeAngle, false, mPaintProgress);
            drawTextCentred(canvas);
        }
        else if (type == 3) {
            drawCenter(canvas);
            drawHands(canvas);
        }
        else {
            drawTextCentred(canvas);
        }
    }

    private void drawHands(Canvas canvas) {
        hour = hour > 12 ? hour - 12 : hour;

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(progressThickness + methods.DPtoPX(1));
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setColor(textColor);

        int angle = (hour * 360)/12;
        if (angle == 360) { angle = 0; }
        int l = handWidth - methods.DPtoPX(16);
        double x = l*Math.sin(Math.toRadians(angle)), y = l*Math.cos(Math.toRadians(angle));
        canvas.drawLine(centerX, methods.DPtoPX(8), centerX, centerY, mPaintProgress);
        canvas.drawLine(centerX, centerY, centerX + (float)x, centerY - (float)y, textPaint);
    }

    private void drawCenter(Canvas canvas) {
        Paint centerPaint = new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setStrokeWidth(backgroundThickness);
        centerPaint.setColor(textColor);
        centerPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, 12, centerPaint);
    }

    public void drawTextCentred(Canvas canvas) {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        textPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), fontName));
        canvas.drawText(text, centerX - textBounds.exactCenterX(), centerY - textBounds.exactCenterY(), textPaint);
    }

    public void setMax(int max) {
        this.max = max;
        if (max == 0) {
            max = 1;
        }
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (max == 0) {
            max = 1;
        }
        swipeAngle = progress * 360 / max;
        if (type == 0) {
            text = methods.ReplaceNumber(String.valueOf(progress));
        }
        else if (type == 1) {
            int percentage = progress * 100 / max;
            text = methods.ReplaceNumber(String.valueOf(percentage)) + "%";
        }
        else if (type == 2) {
            text = methods.ReplaceNumber(methods.milliSecondsToTimer(progress));
        }
        else if (type == 3) {
            hour = progress;
        }
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextSize(float multiplier) {
        textPaint.setTextSize(radius * multiplier);
        invalidate();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        initUI();
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        initUI();
        invalidate();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        initUI();
        invalidate();
    }

    public void setMargin(int margin) {
        this.margin = margin;
        initUI();
        invalidate();
    }

    public void setBackgroundThickness(int backgroundThickness) {
        this.backgroundThickness = backgroundThickness;
        initUI();
        invalidate();
    }

    public void setProgressThickness(int progressThickness) {
        this.progressThickness = progressThickness;
        initUI();
        invalidate();
    }

    public void setType(int type) {
        this.type = type;
        invalidate();
    }

    public void setSizeBase(int sizeBase) {
        this.sizeBase = sizeBase;
        requestLayout();
    }

    public int getProgress() {
        return progress;
    }
}
