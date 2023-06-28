package com.xsens.dot.android.example.interfaces;

import com.xsens.dot.android.sdk.events.XsensDotData;

public interface XsensDataCallBack {
    void onDataReceived(XsensDotData xsensDotData);
}
