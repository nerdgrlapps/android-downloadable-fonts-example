package org.nerdgrl.apps.googlefontsexample;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tvText;
    EditText etFontName;
    Button btnLoad;

    Handler mHandler;
    HandlerThread mHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandlerThread = new HandlerThread("fonts");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        tvText = findViewById(R.id.text);
        etFontName = findViewById(R.id.et_font_name);
        btnLoad = findViewById(R.id.btn_load);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fontName = etFontName.getText().toString().trim();
                loadFont(fontName);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mHandlerThread != null) {
            mHandlerThread.quit();
        }
        super.onDestroy();
    }

    private void loadFont(String fontName) {

        if(!TextUtils.isEmpty(fontName)) {

            FontRequest request = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "name="+fontName,
                    R.array.com_google_android_gms_fonts_certs);

            FontsContractCompat.FontRequestCallback callback = new FontsContractCompat.FontRequestCallback() {

                @Override
                public void onTypefaceRetrieved(Typeface typeface) {
                    if(typeface != null) {
                        tvText.setTypeface(typeface);
                    }
                }

                @Override
                public void onTypefaceRequestFailed(int reason) {
                    Toast.makeText(
                            MainActivity.this,
                            getUserFriendlyFontError(reason),
                            Toast.LENGTH_LONG
                    ).show();
                }
            };

            FontsContractCompat.requestFont(this, request, callback, mHandler);

        }
    }

    public static String getUserFriendlyFontError(int reason) {
        String userFriendlyReason;
        switch (reason) {
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR:
                userFriendlyReason = "Font was not loaded properly";
                break;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND:
                userFriendlyReason = "Font not found";
                break;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_UNAVAILABLE:
                userFriendlyReason = "Font unavailable";
                break;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_MALFORMED_QUERY:
                userFriendlyReason = "Incorrect query";
                break;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_PROVIDER_NOT_FOUND:
                userFriendlyReason = "Font provider not found";
                break;
            case FontsContractCompat.FontRequestCallback.FAIL_REASON_WRONG_CERTIFICATES:
                userFriendlyReason = "Incorrect font provider certificates";
                break;
            default:
                userFriendlyReason = "Unknown error";
                break;
        }
        return userFriendlyReason;
    }
}
