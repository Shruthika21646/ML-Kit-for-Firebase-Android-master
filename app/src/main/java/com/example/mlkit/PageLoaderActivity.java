package com.example.mlkit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import com.example.mlkit.rest.api.EntityResolutionAPI;
import com.google.gson.JsonObject;
import com.thefinestartist.finestwebview.FinestWebView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import kotlin.jvm.internal.Intrinsics;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PageLoaderActivity extends AppCompatActivity {

    private static final String TAG = "PageLoaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_loader);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String srchText = extras.getString("KEY");
            annotateText(srchText);
        }

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
                    createFineWebView(annoatedHtmlText);
                }

            }
        }));
    }

    private void createFineWebView(String annotatedHtmlText) {
        new FinestWebView.Builder(this).theme(R.style.FinestWebViewTheme)
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
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                .load(annotatedHtmlText);

    }

}
