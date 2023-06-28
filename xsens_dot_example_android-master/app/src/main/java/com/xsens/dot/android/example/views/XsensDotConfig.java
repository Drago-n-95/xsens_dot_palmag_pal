package com.xsens.dot.android.example.views;

public class XsensDotConfig {

    private int sampleFrequency;
    private boolean enableOrientationData;

    public XsensDotConfig(int sampleFrequency, boolean enableOrientationData) {
        this.sampleFrequency = sampleFrequency;
        this.enableOrientationData = enableOrientationData;
    }

    public void setSampleFrequency(int sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    public void setEnableOrientationData(boolean enableOrientationData) {
        this.enableOrientationData = enableOrientationData;
    }

    public int getSampleFrequency() {
        return sampleFrequency;
    }

    public boolean isEnableOrientationData() {
        return enableOrientationData;
    }
}
