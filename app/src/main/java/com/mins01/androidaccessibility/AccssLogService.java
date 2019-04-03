package com.mins01.androidaccessibility;


import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class AccssLogService extends AccessibilityService {
    public Deque<AccessibilityEventVo> dqEvt = new ArrayDeque<AccessibilityEventVo>();

//    public void onCreate() {
//        Log.v("@onCreate", "onCreate");
//    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.e("@onServiceConnected", "onServiceConnected");

    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.e("@onDestroy", "onDestroy");
//    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(accessibilityEvent.getPackageName().equals(this.getPackageName())){
            return;
        }
        Log.e("@evt", "Catch Event Package Name : " + accessibilityEvent.getPackageName());
        Log.e("@evt", "Catch Event TEXT : " + accessibilityEvent.getText());
        Log.e("@evt", "Catch Event ContentDescription : " + accessibilityEvent.getContentDescription());
        Log.e("@evt", "Catch Event getSource : " + accessibilityEvent.getSource());
        Log.e("@evt", "=========================================================================");
        if(accessibilityEvent.getText().size()>0){
            if(dqEvt.size()>3){
                dqEvt.poll();
            }
            dqEvt.add(new AccessibilityEventVo(accessibilityEvent));
        }
    }

    @Override
    public void onInterrupt() {
        Log.e("@onInterrupt", "OnInterrupt");

    }

    Context context;
    //    private Button fv;
    View fv;
    View inflateView;
    private WindowManager wm;
    private WindowManager.LayoutParams params;

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
        Button btnShowResults = (Button) inflateView.findViewById(R.id.btnShowResults);
        btnShowResults.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResults();
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

    public void showResults(){
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("dqEvt", (Serializable) dqEvt);
        context.startActivity(intent);
    }



}
