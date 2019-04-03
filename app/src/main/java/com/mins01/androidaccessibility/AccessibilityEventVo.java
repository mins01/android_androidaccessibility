package com.mins01.androidaccessibility;

import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import java.io.Serializable;

public class AccessibilityEventVo implements Serializable {
    public String packagename = "";
    public String text = "";
    public String contentDescription = "";
    public String sourceToString = "";

    //    public String source = null;
    public  AccessibilityEventVo(String packagename,String text, String contentDescription,String sourceToString){
        this.setValue(packagename,text,contentDescription,sourceToString);
    }

    public void setValue(@Nullable String packagename,@Nullable  String text,@Nullable  String contentDescription, @Nullable String sourceToString){
        this.packagename = packagename;
        this.text = text;
        this.contentDescription = contentDescription;
        this.sourceToString = sourceToString;
    }
    public  AccessibilityEventVo(AccessibilityEvent evt){
        String packagename = evt.getPackageName()==null?"": evt.getPackageName().toString();
        String text = evt.getText()==null?"": evt.getText().toString();
        String contentDescription = evt.getContentDescription()==null?"": evt.getContentDescription().toString();
        String sourceToString = evt.getSource()==null?"": evt.getSource().toString();
        this.setValue(packagename,text,contentDescription,sourceToString);
    }
}
