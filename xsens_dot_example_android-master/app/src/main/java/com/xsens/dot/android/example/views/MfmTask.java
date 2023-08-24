package com.xsens.dot.android.example.views;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xsens.dot.android.example.adapters.MfmAdapter;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.mfm.XsensDotMfmManager;
import com.xsens.dot.android.sdk.mfm.XsensDotMfmProcessor;
import com.xsens.dot.android.sdk.mfm.interfaces.XsensDotMfmCallback;
import com.xsens.dot.android.sdk.mfm.models.XsensDotMfmResult;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;

import java.util.ArrayList;

public class MfmTask extends AsyncTask<Void, Void, Void> implements XsensDotDeviceCallback {

    private XsensDotMfmManager mMfmManager;
    private MfmAdapter mfmAdapter;
    private ProgressBar progressBar;
    private XsensDotDevice xsDevice;
    private Context context;
    private XsensDotMfmProcessor mMfmProcessor;

    public MfmTask(Context context, XsensDotDevice xsDevice) {
        this.context = context;
        this.xsDevice = xsDevice;
        mfmAdapter = new MfmAdapter(context);
        mMfmProcessor = new XsensDotMfmProcessor(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Initialize and show the ProgressBar
        progressBar = new ProgressBar(context);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Step 1: Implement the XsensDotMfmCallback interface
        class EasyMfmFragment implements XsensDotMfmCallback {
            public void onXsensDotMfmProgressChanged(String address, int percentage) {
                // Update the UI with the percentage progress
                // Assuming mfmAdapter.updatePercentage() method is defined in the MfmAdapter class "D4:22:CD:00:3A:21"
                mfmAdapter.updatePercentage(address, percentage);
                if (percentage == 100) {
                    // Stop MFM first
                    mMfmManager.stopMfm(false);

                    // Processing collected data.
                    String path = mMfmManager.getMtbFilePath();
                    Log.i("MFM PATH", path);
                    if (!path.isEmpty()) {
                        mMfmProcessor.addMtbFile(address, path);
                        mMfmProcessor.startProcess();
                    }
                }
            }

            public void onXsensDotMfmCompleted(String address, int result, byte[] mtbData) {
                if (result == XsensDotMfmResult.ACCEPTABLE || result == XsensDotMfmResult.GOOD) {
                    boolean isSuccess = mMfmManager.writeMfmResultToDevice(mtbData);
                    // Check write status, re-write, or update the UI
                    if (isSuccess) {
                        // Handle success
                        Toast.makeText(context, "MFM result written to device successfully", Toast.LENGTH_SHORT).show();
                        //Log.i("MFM Check", String.valueOf(true));
                        // Perform any additional actions or UI updates for success
                    } else {
                        // Handle failure
                        Toast.makeText(context, "Failed to write MFM result to device", Toast.LENGTH_SHORT).show();
                        // Perform any additional actions or UI updates for failure
                    }
                }
            }
        }

        // Step 2: Instantiate an XsensDotMfmManager
        mMfmManager = new XsensDotMfmManager(context, xsDevice, new EasyMfmFragment());

        // Step 3: Start MFM
        mMfmManager.startMfm();

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Hide or dismiss the ProgressBar
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        // Update UI with progress if needed
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        // Handle cancellation if needed
    }

    @Override
    public void onXsensDotConnectionChanged(String s, int i) {

    }

    @Override
    public void onXsensDotServicesDiscovered(String s, int i) {

    }

    @Override
    public void onXsensDotFirmwareVersionRead(String s, String s1) {

    }

    @Override
    public void onXsensDotTagChanged(String s, String s1) {

    }

    @Override
    public void onXsensDotBatteryChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotDataChanged(String s, XsensDotData xsensDotData) {

    }

    @Override
    public void onXsensDotInitDone(String s) {

    }

    @Override
    public void onXsensDotButtonClicked(String s, long l) {

    }

    @Override
    public void onXsensDotPowerSavingTriggered(String s) {

    }

    @Override
    public void onReadRemoteRssi(String s, int i) {

    }

    @Override
    public void onXsensDotOutputRateUpdate(String s, int i) {

    }

    @Override
    public void onXsensDotFilterProfileUpdate(String s, int i) {

    }

    @Override
    public void onXsensDotGetFilterProfileInfo(String s, ArrayList<FilterProfileInfo> arrayList) {

    }

    @Override
    public void onSyncStatusUpdate(String s, boolean b) {

    }
}
