package com.gearback.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

public class CustomTextInputLayout extends TextInputLayout {

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);
    }

    @Override
    public void setErrorEnabled(boolean enabled) {
        super.setErrorEnabled(enabled);

        if (!enabled) {
            return;
        }

        try {
            Typeface customFont = FontCache.getTypeface("iranyekanregular.ttf", getContext());
            Field errorViewField = TextInputLayout.class.getDeclaredField("mErrorView");
            errorViewField.setAccessible(true);
            TextView errorView = (TextView) errorViewField.get(this);
            errorView.setTypeface(customFont);
            if (errorView != null) {
                errorView.setGravity(Gravity.RIGHT);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                errorView.setLayoutParams(params);
            }
        }
        catch (Exception e) {
            // At least log what went wrong
            e.printStackTrace();
        }
    }

    private void initFont(Context context) {
        Typeface customFont = FontCache.getTypeface("iranyekanregular.ttf", context);
        EditText editText = getEditText();
        if (editText != null) {
            editText.setTypeface(customFont);
        }
    }
}
