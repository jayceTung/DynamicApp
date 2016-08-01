package com.asuper.dynamicapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    /** 组件 **/
    private TextView mTextView;
    private ImageView mImageView;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private static int version = -1;

    /** 类加载器 **/
    protected DexClassLoader classLoader = null;
    protected File fileRelease = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mTextView = (TextView)findViewById(R.id.text);
        mImageView = (ImageView)findViewById(R.id.imageview);
        mLinearLayout = (LinearLayout)findViewById(R.id.layout);

        fileRelease = getDir("dex", 0);

        moveToData();

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_text, null, false);
        mLinearLayout.addView(view);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String filesDir = getCacheDir().getAbsolutePath();
                String filePath = filesDir + File.separator +"PluginApp.apk";
                Log.i("Loader", "filePath:"+filePath);
                Log.i("Loader", "isExist:"+new File(filePath).exists());

                classLoader = new DexClassLoader(filePath, fileRelease.getAbsolutePath(), null, getClassLoader());
                loadResources(filePath);
                setContent();
            }
        });

        Volley.newRequestQueue(this).add(new StringRequest("http://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.toString());
            }
        }));
    }

    private void moveToData() {
        if (Utils.isSDExist()) {
            String inPath = Environment.getExternalStorageDirectory().getAbsolutePath() +  File.separator
                    + "PluginApp.apk";
            String outPath = getCacheDir().getAbsolutePath() + File.separator + "PluginApp.apk";
            File inFile = new File(inPath);
            File outFile = new File(outPath);
            Log.i(TAG, "local Version = " + version + "apk Version = " + Utils.getApkVersion(this, inPath));

            if (outFile.exists() && version >= Utils.getApkVersion(this, inPath)) {
                return;
            } else {
                version = Utils.getApkVersion(this, inPath);
                FileOutputStream fos;
                FileInputStream fis;
                if (inFile.exists()) {
                    try {
                        fos = new FileOutputStream(outFile);
                        fis = new FileInputStream(inFile);

                        Log.i(TAG, "fosPath = " + getCacheDir().getAbsolutePath());
                        Log.i(TAG, "fisPath = " + inPath);
                        byte[] bt = new byte[1024];
                        int len;
                        while((len = fis.read(bt)) != -1) {
                            fos.write(bt, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * 动态加载主题包中的资源，然后替换每个控件
     */
    @SuppressLint("NewApi")
    private void setContent(){
        try{
            Class clazz = classLoader.loadClass("com.asuper.pluginapp.UIUtil");
            Method method = clazz.getMethod("getTextString", Context.class);
            String str = (String)method.invoke(null, this);
            mTextView.setText(str);
            method = clazz.getMethod("getImageDrawable", Context.class);
            Drawable drawable = (Drawable)method.invoke(null, this);
            mImageView.setBackground(drawable);
            method = clazz.getMethod("getLayout", Context.class);
            View view = (View)method.invoke(null, this);
            mLinearLayout.addView(view);
        }catch(Exception e){
            Log.i("Loader", "error:"+ Log.getStackTraceString(e));
        }
    }

}
