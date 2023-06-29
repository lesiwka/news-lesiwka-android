package com.lesiwka.novyny;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private String hostname;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (hostname.equals(request.getUrl().getHost())) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            mySwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            view.loadUrl("about:blank");

            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setTitle("Error");
            alert.setPositiveButton("OK", (dialog, whichButton) -> view.loadUrl(hostname));
            alert.setCancelable(false);
            alert.show();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostname = getResources().getString(R.string.hostname);

        WebView myWebView = findViewById(R.id.webview);
        mySwipeRefreshLayout = findViewById(R.id.swipe);
        mySwipeRefreshLayout.setOnRefreshListener(myWebView::reload);

        myWebView.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl(hostname);
    }
}