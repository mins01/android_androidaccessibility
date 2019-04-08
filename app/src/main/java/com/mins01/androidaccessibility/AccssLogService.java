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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Deque;


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

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if(source==null){
            return;
        }
        String packageName = accessibilityEvent.getPackageName().toString();
        String className = source==null?"":source.getClassName().toString();
        String text = accessibilityEvent.getText()==null?"":accessibilityEvent.getText().toString();
        String text2 = source.getText()==null?"":source.getText().toString();
        int eventType = accessibilityEvent.getEventType();


        className = accessibilityEvent.getSource().getClassName().toString();

        Log.v("@evt", "evtInfo: " + String.valueOf(eventType)+"/"+packageName+"/"+className+"/"+String.valueOf(accessibilityEvent.getSource().isVisibleToUser())+"/"+ text+"/"+text2);

//        Log.v("@evtType",String.valueOf(accessibilityEvent.getEventType()));


        switch (eventType){
//            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: ; //2048
//            break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED: ; //4096
            hideRect(true);
                Log.v("@evt", "hide -0");
            break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:; //8192
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:; //8
            case AccessibilityEvent.TYPE_VIEW_CLICKED:; //1
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:; //2
            case AccessibilityEvent.TYPE_VIEW_SELECTED:; //4
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:; //8388608




                if(accessibilityEvent.getSource() == null){

                }else if(!packageName.equals(this.getPackageName()) && text2.length()>0){
//                    if(className.equals("android.view.View")||className.equals("android.widget.TextView")||className.equals("android.widget.EditText")){
                        lastEvt = new AccessibilityEventVo(accessibilityEvent);
                        lastAn = accessibilityEvent.getSource();
                        tvLastPackagename.setText(accessibilityEvent.getPackageName());
                        tvLastText.setText(lastAn.getText());
                        if(lastAn!=null && lastAn.isVisibleToUser()){
                            Rect bound = new Rect();
                            lastAn.getBoundsInScreen(bound);
                            syncPos(bound);
                            hideRect(false);
                            Log.v("@evt", "show");

                        }
//                    }else{
//                        hideRect(true);
//                        Log.v("@evt", "hide -1");
//                    }
                }else{
                    hideRect(true);
                    Log.v("@evt", "hide -2");
                }
            break;
            default:
//                hideRect(true);
            break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.e("@onInterrupt", "OnInterrupt");

    }

    Context context;
    //    private Button fv;
    private View floatmenuMain;
    private WindowManager wm;
    private WindowManager.LayoutParams params;

    private TextView tvLastPackagename;
    private TextView tvLastText;
    private View layoutIconSelector;
    private WindowManager.LayoutParams paramsIconSelector;
    private View layoutIconMagnifier;
    private WindowManager.LayoutParams paramsIconMagnifier;
    private View layoutSelectedArea;
    private WindowManager.LayoutParams paramsSelectedArea;
    private int LAYOUT_FLAG=0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("@onCreate","onCreate");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);


        context = this;
        addViewForIconMagnifier();
        addViewForSelectedArea();
        addViewForFloatMenuMain();
        addViewForIconSelector();
        //-- 초기화
        hideRect(true);
    }
    private void addViewForIconSelector(){
        //돋보기용
        layoutIconSelector = View.inflate(this, R.layout.icon_selector, null);
        int margin50 = (int)dpToPx(context,30);

        paramsIconSelector = new WindowManager.LayoutParams(
                margin50,
                margin50,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE , //터치는 인식
                PixelFormat.TRANSLUCENT); //투명

        paramsIconSelector.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(layoutIconSelector, paramsIconSelector);

        layoutIconSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("@layoutIconSelector","click");
            }
        });
        layoutIconSelector.setOnTouchListener(new View.OnTouchListener() {
            private float marginPx = dpToPx(context,25);
            private boolean isMove = false;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));   // statisBar의 높이
                WindowManager.LayoutParams params;
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:

                        return true;
                    case MotionEvent.ACTION_MOVE:

                        params = (WindowManager.LayoutParams) view.getLayoutParams();
                        params.x = Math.round(motionEvent.getRawX()-(params.width/2));
                        params.y = Math.round(motionEvent.getRawY()-(params.height/2)-statusBarHeight);
                        wm.updateViewLayout(view,params);
                        isMove = true;
                        return true;
                    case MotionEvent.ACTION_UP:

                        if(isMove){
                            isMove = false;
                        }else{
                            params = (WindowManager.LayoutParams) view.getLayoutParams();
                            getAccessibilityNodeInfoByXY(Math.round(params.x+(params.width)),Math.round(params.y+(params.height)));
//                            float x = Math.round(motionEvent.getRawX()-marginPx);
//                            float y = Math.round(motionEvent.getRawY()-marginPx);
//                            getAccessibilityNodeInfoByXY(Math.round(x),Math.round(y));
                        }
                        return false;
                    case MotionEvent.ACTION_CANCEL:

                        return false;
                    case MotionEvent.ACTION_OUTSIDE:

                        return false;
                }
                return false;
            }
        });
    }
    private void addViewForIconMagnifier(){
        //돋보기용
        layoutIconMagnifier = View.inflate(this, R.layout.icon_magnifier, null);
        paramsIconMagnifier = new WindowManager.LayoutParams(
                120,
                120,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE , //터치는 인식
                PixelFormat.TRANSLUCENT); //투명

        paramsIconMagnifier.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(layoutIconMagnifier, paramsIconMagnifier);

        TextView tvMagnifier = (TextView) layoutIconMagnifier.findViewById(R.id.tvMagnifier);
        tvMagnifier.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResults();
            }
        });
        layoutIconMagnifier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
    }
    private void addViewForSelectedArea(){
        //위치표기용
        Log.v("@layoutSelectedArea","layoutSelectedArea");
        layoutSelectedArea = View.inflate(this, R.layout.selected_area, null);
        paramsSelectedArea = new WindowManager.LayoutParams(
                200,
                600,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //터치 인식이 안되며, 이벤트는 뒤로 흐름
                PixelFormat.TRANSLUCENT); //투명

        paramsSelectedArea.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(layoutSelectedArea, paramsSelectedArea);
        layoutSelectedArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

//        ((TextView) layoutSelectedArea.findViewById(R.id.tvSelectedArea)).setOnTouchListener(new TextView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });
    }
    private void addViewForFloatMenuMain(){
        //기본 메뉴
        floatmenuMain = View.inflate(this, R.layout.floatmenu_main, null);
        View fv = floatmenuMain;

        tvLastPackagename = (TextView) floatmenuMain.findViewById(R.id.tvLastPackagename);
        tvLastText = (TextView) floatmenuMain.findViewById(R.id.tvLastText);
        params = new WindowManager.LayoutParams(
                300,
                600,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //터치 인식
                PixelFormat.TRANSLUCENT); //투명
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        wm.addView(floatmenuMain, params);

        Button btnHide = (Button) floatmenuMain.findViewById(R.id.btnHide);
        btnHide.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSetting();
            }
        });
        Button btnShowResults = (Button) floatmenuMain.findViewById(R.id.btnShowResults);
        btnShowResults.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResults();
            }
        });
    }

    public void hideRect(boolean hide){
        layoutIconMagnifier.setVisibility(hide?View.GONE:View.VISIBLE);
        layoutSelectedArea.setVisibility(hide?View.GONE:View.VISIBLE);
    }
    public void syncPos(Rect bound) {
        int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));   // statisBar의 높이
        syncPos(bound.left,bound.top-statusBarHeight,bound.width(),bound.height());
    }
    public void syncPos(int left, int top, int width, int height){
        Log.v("@syncPos",String.valueOf(left)+","+String.valueOf(top)+","+String.valueOf(width)+","+String.valueOf(height));

        int dpGapPx = dpToPx(context,3)*2;

        paramsIconMagnifier.x = left + width - paramsIconMagnifier.width - dpGapPx;
        paramsIconMagnifier.y = top + height - paramsIconMagnifier.height;
//        paramsIconMagnifier.width = 120;
//        paramsIconMagnifier.height = 120;
        wm.updateViewLayout(layoutIconMagnifier,paramsIconMagnifier);

        paramsSelectedArea.x = left  - dpGapPx;;
        paramsSelectedArea.y = top  - dpGapPx;;
        paramsSelectedArea.width = width  + 2*dpGapPx;;
        paramsSelectedArea.height = height  + 2*dpGapPx;;
        wm.updateViewLayout(layoutSelectedArea,paramsSelectedArea);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatmenuMain != null){
            try{
                wm.removeView(floatmenuMain);
            }catch (Exception e){

            }

        }
        if(layoutIconMagnifier != null){
            try{
                wm.removeView(layoutIconMagnifier);
            }catch (Exception e){

            }
        }
        if(layoutSelectedArea != null){
            try{
                wm.removeView(layoutSelectedArea);
            }catch (Exception e){

            }
        }
        if(layoutIconSelector != null){
            try{
                wm.removeView(layoutIconSelector);
            }catch (Exception e){

            }
        }
        stopSelf();
    }



    public void showResults(){
        hideRect(true);
        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("dqEvt", (Serializable) dqEvt);
//        intent.putExtra("lastEvt", (Serializable) lastEvt);
//        intent.putExtra("lastAn", lastAn);

//        intent.putExtra("lastText", lastAn.getText()!=null ? lastAn.getText().toString():"");
        intent.putExtra("lastText", lastAn!=null && lastAn.getText()!=null ? lastAn.getText().toString():"");


        context.startActivity(intent);
    }

    public void openSetting(){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    public int dpToPx(Context context, float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }

    private View findViewAt(ViewGroup viewGroup, int x, int y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View foundView = findViewAt((ViewGroup) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains(x, y)) {
                    return child;
                }
            }
        }

        return null;
    }

    public AccessibilityNodeInfo findNodeInfoByPoint(AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo == null) {
            return null;
        }
        AccessibilityNodeInfo resultNodeInfo = nodeInfo;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childInfo = nodeInfo.getChild(i);
            if (childInfo != null && isPointInNodeInfo(childInfo, x, y)) {
                AccessibilityNodeInfo foundNodeInfo = findNodeInfoByPoint(childInfo, x, y);
                resultNodeInfo = getSmallerNodeInfo(resultNodeInfo, foundNodeInfo);
            }
        }
        return resultNodeInfo;
    }
    public static boolean isPointInNodeInfo(AccessibilityNodeInfo nodeInfo, int x, int y) {
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);

        // 좌표가 View에 포함되어있는가
        return rect.contains(x, y);
    }
    public static AccessibilityNodeInfo getSmallerNodeInfo(AccessibilityNodeInfo leftInfo, AccessibilityNodeInfo rightInfo) {
        Rect leftInfoRect = new Rect();
        leftInfo.getBoundsInScreen(leftInfoRect);

        Rect rightInfoRect = new Rect();
        rightInfo.getBoundsInScreen(rightInfoRect);

        // 사이즈가 작은 NodeInfo를 취득

        return leftInfoRect.width() * leftInfoRect.height() < rightInfoRect.width() * rightInfoRect.height() ? leftInfo : rightInfo;
    }

    public AccessibilityNodeInfo getAccessibilityNodeInfoByXY(int x,int y){
        Log.v("@getAccssNodeInfoByXY","X="+String.valueOf(x)+"/Y="+String.valueOf(y));


        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        Log.v("@getAccssNodeInfoByXY","root:"+rootNode.toString());

        AccessibilityNodeInfo nodeInfo = findNodeInfoByPoint(rootNode,x,y);
        if(nodeInfo==null){
            Log.v("@getAccssNodeInfoByXY","Result NULL");
        }else{
            Log.v("@getAccssNodeInfoByXY","node:"+nodeInfo.toString());
            lastAn = nodeInfo;
            tvLastPackagename.setText(nodeInfo.getPackageName());
            tvLastText.setText(lastAn.getText());
            Rect bound = new Rect();
            nodeInfo.getBoundsInScreen(bound);
            syncPos(bound);
            hideRect(false);

        }
        return null;
    }
}
