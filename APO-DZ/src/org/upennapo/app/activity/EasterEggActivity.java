package org.upennapo.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import org.upennapo.app.R;

/**
 * An easter egg activity that contains the APO-DZ Edition of 2048.
 *
 * @author Ronald Martin
 */
public class EasterEggActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(savedInstanceState);

        setContentView(mWebView);
    }

    private void init(Bundle savedInstanceState) {
        mWebView = new WebView(this);

        if (savedInstanceState == null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(getString(R.string.apo_2048_url));
        } else {
            mWebView.restoreState(savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }
}
