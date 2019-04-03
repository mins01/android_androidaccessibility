package com.mins01.androidaccessibility;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatMenu extends Service {
    Context context;
//    private Button fv;
    View fv;
    View inflateView;
    private WindowManager wm;
    private WindowManager.LayoutParams params;

    public void FloatWindow(){
        Log.v("@FloatWindow","FloatWindow");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("@onBind","onBind");

        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        Log.v("@onCreate","onCreate");

        super.onCreate();
        context = this;
        inflateView = View.inflate(this, R.layout.floatmenu_main, null);
//        fv = new FloatView(this);
//        fv = new Button(this);
//        fv.setText("Test");
        View fv = inflateView;


        fv.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

//        fv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.v("@onTouch","ACTION_DOWN");
//                        break;
//                }
//                return false;
//            }
//        });
        int LAYOUT_FLAG;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                200,
                600,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //터치 인식
                PixelFormat.TRANSLUCENT); //투명

        params.gravity = Gravity.LEFT | Gravity.TOP;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(fv, params);

        Button btnHide = (Button) inflateView.findViewById(R.id.btnHide);
        btnHide.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(fv != null){
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(fv);
        }
        if(inflateView != null){
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(inflateView);
        }
        stopSelf();
    }
}
