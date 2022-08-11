package org.dhis2.usescases.qrCodes;

import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;

import org.dhis2.App;
import org.dhis2.R;
import org.dhis2.databinding.ActivityQrCodesBinding;
import org.dhis2.usescases.general.ActivityGlobalAbstract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static org.dhis2.data.qr.QRjson.ATTR_JSON;
import static org.dhis2.data.qr.QRjson.DATA_JSON;
import static org.dhis2.data.qr.QRjson.DATA_JSON_WO_REGISTRATION;
import static org.dhis2.data.qr.QRjson.ENROLLMENT_JSON;
import static org.dhis2.data.qr.QRjson.EVENTS_JSON;
import static org.dhis2.data.qr.QRjson.EVENT_JSON;
import static org.dhis2.data.qr.QRjson.RELATIONSHIP_JSON;
import static org.dhis2.data.qr.QRjson.TEI_JSON;

/**
 * QUADRAM. Created by ppajuelo on 21/06/2018.
 */

public class QrActivity extends ActivityGlobalAbstract implements QrContracts.View {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://roco1.netlify.app/");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void showQR(@NonNull List<QrViewModel> bitmaps) {

    }

    @Override
    public void onBackClick() {

    }

    @Override
    public void onPrevQr() {

    }

    @Override
    public void onNextQr() {

    }

    @Override
    public void showQRBitmap(Bitmap bitmap) {

    }
}
