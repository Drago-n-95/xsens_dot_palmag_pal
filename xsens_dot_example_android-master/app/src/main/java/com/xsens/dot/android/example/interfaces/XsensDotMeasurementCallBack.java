package com.xsens.dot.android.example.interfaces;

import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.models.XsensDotDevice;

public interface XsensDotMeasurementCallBack {
    void onMeasurementReceived(XsensDotDevice device, XsensDotData data);
}
