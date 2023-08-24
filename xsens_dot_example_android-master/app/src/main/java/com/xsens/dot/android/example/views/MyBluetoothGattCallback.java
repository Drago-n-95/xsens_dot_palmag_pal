package com.xsens.dot.android.example.views;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;

public class MyBluetoothGattCallback extends BluetoothGattCallback {

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        // This method will be called when the connection state changes (e.g., connected, disconnected)
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // Device connected, perform required actions
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // Device disconnected, perform required actions
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        // This method will be called when services are discovered on the remote device
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Services discovered successfully, perform required actions
        } else {
            // Services discovery failed, handle error if needed
        }
    }
}
