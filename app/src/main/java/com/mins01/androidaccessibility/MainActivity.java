package com.mins01.androidaccessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context context;
    Activity activity;

    public Deque<AccessibilityEventVo> dqEvt;

    @Override
    protected void onResume() {
        super.onResume();
        syncBundle(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        setContentView(R.layout.activity_main);
        startOverlayWindowService(context);

        if(!checkAccessibilityPermissions()){
            setAccessibilityPermissions();
        }
        ((Button)findViewById(R.id.btnOpenSetting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSetting();
            }
        });
        ((Button)findViewById(R.id.btnOpenFloatingMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOverlayWindowService(context);
//                openFloatingMenu();
            }
        });
        ((Button)findViewById(R.id.btnCheckAccessibility)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkAccessibilityPermissions()){
                    setAccessibilityPermissions();
                }
            }
        });
        ((TextView) findViewById(R.id.tvResults)).setMovementMethod(new ScrollingMovementMethod());
    }
    public boolean checkAccessibilityPermissions(){
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        // getEnabledAccessibilityServiceList는 현재 접근성 권한을 가진 리스트를 가져오게 된다
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.DEFAULT);
        for (int i = 0; i < list.size(); i++) {
            AccessibilityServiceInfo info = list.get(i);
            // 접근성 권한을 가진 앱의 패키지 네임과 패키지 네임이 같으면 현재앱이 접근성 권한을 가지고 있다고 판단함
            if (info.getResolveInfo().serviceInfo.packageName.equals(getApplication().getPackageName())) {
                return true;
            }
        }
        return false;
    }
    // 접근성 설정화면으로 넘겨주는 부분
    public void setAccessibilityPermissions() {
        AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
        gsDialog.setTitle("접근성 권한 설정");
        gsDialog.setMessage("접근성 권한을 필요로 합니다");
        gsDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 설정화면으로 보내는 부분
                openSetting();
                return;
            }
        }).create().show();
    }
    public void openSetting(){
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }
    public void openFloatingMenu(){
//        stopService(new Intent(this,FloatMenu.class));
//        startService(new Intent(this,FloatMenu.class));
    }

    public void startOverlayWindowService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(context)) {
            onObtainingPermissionOverlayWindow();

        } else {
//            openFloatingMenu();
        }
    }
    public void onObtainingPermissionOverlayWindow() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
//                    openFloatingMenu();
                }
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.v("@on","onNewIntent");
        super.onNewIntent(intent);
        if(intent != null){
            setIntent(intent);
        }

    }
    public void syncBundle(@Nullable Intent intent){
        if(intent == null){
            return;
        }
//        Intent intent = getIntent();
        if(intent.hasExtra("dqEvt")){
            dqEvt = (Deque<AccessibilityEventVo>) intent.getSerializableExtra("dqEvt");
        }
        showResult();
    }
    public void showResult(){
        StringBuilder sb = new StringBuilder();
        if(dqEvt != null){
            for(AccessibilityEventVo aev : dqEvt){
                sb.append("Catch Event Package Name : " + aev.packagename+"\n");
                sb.append("Catch Event TEXT : " + aev.text);
                sb.append("Catch Event ContentDescription : " + aev.contentDescription+"\n");
                sb.append("Catch Event Source : " + aev.sourceToString+"\n");
                sb.append("================================"+"\n");
            }
        }
        ((TextView)findViewById(R.id.tvResults)).setText(sb.toString());
    }
}
