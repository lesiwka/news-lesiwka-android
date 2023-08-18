package com.lesiwka.novyny;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private String hostName;
    private String url;

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (hostName.equals(request.getUrl().getHost())) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
                view.loadUrl("about:blank");

                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Error");
                alert.setPositiveButton("OK", (dialog, whichButton) -> view.loadUrl(url));
                alert.setCancelable(false);
                alert.show();
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostName = getString(R.string.host_name);
        url = "https://" + hostName;

        WebView mWebView = findViewById(R.id.webview);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(mWebView::reload);

        mWebView.setOnLongClickListener(v -> true);
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        String userAgentTemplate = getString(R.string.user_agent_template);
        int versionCode = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                versionCode = (int) getPackageManager().getPackageInfo(getPackageName(), PackageManager.PackageInfoFlags.of(0)).getLongVersionCode();
            } else {
                versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            }
        } catch (PackageManager.NameNotFoundException ignored) {}

        String appName = getString(R.string.app_name);
        String userAgent = Base64.encodeToString(webSettings.getUserAgentString().getBytes(), Base64.NO_PADDING | Base64.NO_WRAP);
        webSettings.setUserAgentString(String.format(userAgentTemplate, versionCode, appName, hostName, userAgent));

        mWebView.loadUrl(url);
    }

    @SuppressWarnings("unused")
    public boolean zoomIn() {
        return true;
    }

    @SuppressWarnings("unused")
    public boolean zoomOut() {
        return true;
    }
}