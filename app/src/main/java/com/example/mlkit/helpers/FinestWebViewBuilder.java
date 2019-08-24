package com.example.mlkit.helpers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;

public class FinestWebViewBuilder extends com.thefinestartist.finestwebview.FinestWebView.Builder {

    public FinestWebViewBuilder(@NonNull Activity activity) {
        super(activity);
    }

    public FinestWebViewBuilder(@NonNull Context context) {
        super(context);
    }

    public FinestWebViewBuilder setCustomAnimations(@AnimRes int animationOpenEnter,
                                                    @AnimRes int animationOpenExit, @AnimRes int animationCloseEnter,
                                                    @AnimRes int animationCloseExit) {
        this.animationOpenEnter = animationOpenEnter;
        this.animationOpenExit = animationOpenExit;
        this.animationCloseEnter = animationCloseEnter;
        this.animationCloseExit = animationCloseExit;
        return this;
    }

    @Override
    public void show(String url, String data) {
        super.show(url, data);
    }

}
