package com.asuper.dynamicapp;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Joker on 2016/7/28.
 */
public class BaseActivity extends Activity {
    protected AssetManager mAssentManager;
    protected Resources mResource;
    protected Theme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void loadResources(String desPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, desPath);
            mAssentManager = assetManager;
        } catch (Exception e) {
            Log.i("BaseActivity", "error = " + Log.getStackTraceString(e));
        }

        Resources superRes = super.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
        mResource = new Resources(mAssentManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mTheme = mResource.newTheme();
        mTheme.setTo(super.getTheme());

    }

    @Override
    public AssetManager getAssets() {
        return mAssentManager == null ? super.getAssets() : mAssentManager;
    }

    @Override
    public Resources getResources() {
        return mResource == null ? super.getResources() : mResource;
    }

    @Override
    public Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
