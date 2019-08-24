package com.example.mlkit;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.mlkit.rest.api.EntityResolutionAPI;
import com.google.gson.JsonObject;
import com.thefinestartist.finestwebview.FinestWebView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class DetectTextActivity extends AppCompatActivity {

    private static final String TAG = "DetectTextActivity";
//    private static final String LOADING_PAGE_URL = "https://wikify-service.herokuapp.com/static/index_loading.html";
    private TextView mTextView;
    private ACProgressFlower dialog;
//    private FinestWebView.Builder webViewBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_text);
        // To retrieve object in second Activity

        mTextView = findViewById(R.id.text_view);
        final String detectedText = getIntent().getExtras().getString("detectedText");
        mTextView.setText(detectedText);

//        webViewBuilder = createFinestWebViewBuilder();

        final FloatingTextButton annotateTextBtn = findViewById(R.id.annotate_button);
        annotateTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDialog();
                annotateText(mTextView.getText().toString());
            }

        });

    }

    private void createDialog() {
        dialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Please wait...")
                .fadeColor(Color.DKGRAY).build();
        dialog.show();
    }

    private void annotateText(final String srchText) {
        final JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("text", srchText);
        EntityResolutionAPI.Companion.getService().getEntitiesByText(jsonObj).enqueue((Callback) (new Callback() {
            public void onFailure(@NotNull Call call, @NotNull Throwable t) {
                Intrinsics.checkParameterIsNotNull(call, "call");
                Intrinsics.checkParameterIsNotNull(t, "t");
                String errMsg = "POST Throwable EXCEPTION:: " + t.getMessage();
                Log.e(TAG, errMsg);
                dialog.dismiss();
            }

            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Intrinsics.checkParameterIsNotNull(call, "call");
                Intrinsics.checkParameterIsNotNull(response, "response");
                if (response.isSuccessful()) {
                    ResponseBody responseBody = (ResponseBody) response.body();
                    String annoatedHtmlText = null;
                    try {
                        annoatedHtmlText = responseBody != null ? responseBody.string().toString() : null;
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    String responseMsg = "POST msg from server :: " + annoatedHtmlText;
                    Log.d(TAG, responseMsg);
                    FinestWebView.Builder webViewBuilder = createFinestWebViewBuilder();
                    dialog.dismiss();
                    webViewBuilder.load(annoatedHtmlText);
                }else{
                    Log.d(TAG,"Response nto successfull:"+response.message());
                }

            }
        }));
    }


    private FinestWebView.Builder createFinestWebViewBuilder() {
        FinestWebView.Builder webViewBuilder = new FinestWebView.Builder(this).theme(R.style.FinestWebViewTheme_Fullscreen)
                .titleDefault("Annotated Text")
                .showUrl(false)
                .statusBarColorRes(R.color.colorPrimary)
                .toolbarColorRes(R.color.colorPrimary)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.colorPrimary)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .showSwipeRefreshLayout(true)
                .swipeRefreshColorRes(R.color.colorPrimaryDark)
                .menuSelector(R.drawable.selector_light_theme)
                .menuTextGravity(Gravity.CENTER)
                .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down);
        return webViewBuilder;
    }


}
