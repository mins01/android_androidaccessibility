package com.mins01.androidaccessibility;


import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private AccessibilityEventVo lastEvt;
    private AccessibilityNodeInfo lastAn;
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
        if(accessibilityEvent.getEventType()==AccessibilityEvent.TYPE_VIEW_SCROLLED){
            hideRect();
        }
        if(accessibilityEvent.getText().size()>0){

            if(accessibilityEvent.getSource() != null){

                lastEvt = new AccessibilityEventVo(accessibilityEvent);
                lastAn = accessibilityEvent.getSource();
                tvLastPackagename.setText(accessibilityEvent.getPackageName());
                Rect bound = new Rect();
                lastAn.getBoundsInScreen(bound);
                showRect(bound.left,bound.top,bound.width(),bound.height());
            }
            if(dqEvt.size()>3){
                dqEvt.poll();
            }
            dqEvt.add(new AccessibilityEventVo(accessibilityEvent));
        }
        if(accessibilityEvent.getSource() != null){
            Log.v("@@",accessibilityEvent.getSource().toString());
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
    private WindowManager.LayoutParams params2;
    private TextView tvLastPackagename;
    View selected_rect;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        Log.v("@onCreate","onCreate");

        super.onCreate();
        context = this;
        inflateView = View.inflate(this, R.layout.floatmenu_main, null);
        View fv = inflateView;

        tvLastPackagename = (TextView) inflateView.findViewById(R.id.tvLastPackagename);

        fv.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

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
                openSetting();
            }
        });
        Button btnShowResults = (Button) inflateView.findViewById(R.id.btnShowResults);
        btnShowResults.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResults();
            }
        });



        //클릭 위치 표시용
        selected_rect = View.inflate(this, R.layout.selected_rect, null);
        params2 = new WindowManager.LayoutParams(
                200,
                600,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //터치 인식
                PixelFormat.TRANSLUCENT); //투명

        params2.gravity = Gravity.LEFT | Gravity.TOP;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(selected_rect, params2);
        hideRect();

        TextView tvMagnifier = (TextView) selected_rect.findViewById(R.id.tvMagnifier);
        tvMagnifier.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResults();
            }
        });

    }
    public void hideRect(){
        selected_rect.setVisibility(View.GONE);

    }
    public void showRect(int left, int top, int width, int height){
        width = 120;
        height = 120;
        selected_rect.setVisibility(View.VISIBLE);
        params2.x = left;
        params2.y = top;
        params2.width = width;
        params2.height = height;
        wm.updateViewLayout(selected_rect,params2);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(inflateView != null){
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(inflateView);
        }
        stopSelf();
    }



    public void showResults(){
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("dqEvt", (Serializable) dqEvt);
        intent.putExtra("lastEvt", (Serializable) lastEvt);
        intent.putExtra("lastAn", lastAn);


        context.startActivity(intent);
    }

    public void openSetting(){
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }


}
