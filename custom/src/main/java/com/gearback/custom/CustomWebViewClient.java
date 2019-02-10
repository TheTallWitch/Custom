package com.gearback.custom;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CustomWebViewClient extends WebChromeClient {

    private ProgressListener mListener;

    public CustomWebViewClient(ProgressListener listener) {
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    public interface ProgressListener {
        public void onUpdateProgress(int progressValue);
    }
}