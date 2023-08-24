//  Copyright (c) 2003-2020 Xsens Technologies B.V. or subsidiaries worldwide.
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification,
//  are permitted provided that the following conditions are met:
//
//  1.      Redistributions of source code must retain the above copyright notice,
//           this list of conditions, and the following disclaimer.
//
//  2.      Redistributions in binary form must reproduce the above copyright notice,
//           this list of conditions, and the following disclaimer in the documentation
//           and/or other materials provided with the distribution.
//
//  3.      Neither the names of the copyright holders nor the names of their contributors
//           may be used to endorse or promote products derived from this software without
//           specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
//  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
//  THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
//  OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
//  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY OR
//  TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.THE LAWS OF THE NETHERLANDS
//  SHALL BE EXCLUSIVELY APPLICABLE AND ANY DISPUTES SHALL BE FINALLY SETTLED UNDER THE RULES
//  OF ARBITRATION OF THE INTERNATIONAL CHAMBER OF COMMERCE IN THE HAGUE BY ONE OR MORE
//  ARBITRATORS APPOINTED IN ACCORDANCE WITH SAID RULES.
//

package com.xsens.dot.android.example.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.adapters.MfmAdapter;
import com.xsens.dot.android.example.adapters.MyViewPagerAdapter;
import com.xsens.dot.android.example.adapters.SendMessage;
import com.xsens.dot.android.example.databinding.ActivityMainBinding;
import com.xsens.dot.android.example.interfaces.ScanClickInterface;
import com.xsens.dot.android.example.interfaces.StreamingClickInterface;
import com.xsens.dot.android.example.utils.Utils;
import com.xsens.dot.android.example.viewmodels.BluetoothViewModel;
import com.xsens.dot.android.example.viewmodels.SensorViewModel;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotMeasurementCallback;
import com.xsens.dot.android.sdk.mfm.XsensDotMfmManager;
import com.xsens.dot.android.sdk.mfm.interfaces.XsensDotMfmCallback;
import com.xsens.dot.android.sdk.mfm.models.XsensDotMfmResult;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.utils.XsensDotDebugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The main activity.
 */
public class MainActivity extends AppCompatActivity implements XsensDotDeviceCallback, XsensDotMeasurementCallback, SendMessage {

    private static final String TAG = MainActivity.class.getSimpleName();

    // The code of request
    private static final int REQUEST_ENABLE_BLUETOOTH = 1001, REQUEST_PERMISSION_LOCATION = 1002;

    // The tag of fragments
    public static final String FRAGMENT_TAG_SCAN = "scan", FRAGMENT_TAG_DATA = "data";

    // The view binder of MainActivity
    private ActivityMainBinding mBinding;

    // The Bluetooth view model instance
    private BluetoothViewModel mBluetoothViewModel;

    // The sensor view model instance
    private SensorViewModel mSensorViewModel;

    // A variable for scanning flag
    private boolean mIsScanning = false;

    // Send the start/stop scan click event to fragment
    private ScanClickInterface mScanListener;

    // Send the start/stop streaming click event to fragment
    private StreamingClickInterface mStreamingListener;

    // A variable to keep the current fragment id
    public static String sCurrentFragment = FRAGMENT_TAG_SCAN;

    public ArrayList<String> SensorDataNorth;
    public ArrayList<String> SensorDataEast;
    public ArrayList<String> SensorDataZ;
    public ListView ListDataSensor;

    private TabItem coreTab, stratTab, angleTableTab, stratTableTab, noteTab, photoTab, testTab;
    TabLayout.Tab coreTabMeasure;
    public PagerAdapter pagerAdapter;
    public FrameLayout fragmentContainer;
    private DataFragment dataFragment;

    //we`ll see
    private TabLayout RowOneTabs, RowTwoTabs;

    private ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private XsensDotDebugger xsensDotDebugger;
    private XsensDotMfmManager mXsensDotMfmManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        mBinding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(mBinding.getRoot());
         */

        setupFragmentContainer();
        bindViewModel();
        checkBluetoothAndPermission();

        // Register this action to monitor Bluetooth status.
        registerReceiver(mBluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        SensorDataNorth = new ArrayList<>();
        SensorDataEast = new ArrayList<>();
        SensorDataZ = new ArrayList<>();

        RowOneTabs = findViewById(R.id.row1_tabLayout);
        viewPager2 = findViewById(R.id.view_pager);
        myViewPagerAdapter = new MyViewPagerAdapter(this);
        viewPager2.setAdapter(myViewPagerAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.container);
        //RowTwoTabs = findViewById(R.id.row2_tabLayout);
        coreTab = (TabItem) findViewById(R.id.core_tabtabtab);
        stratTab = (TabItem) findViewById(R.id.strat_tabtabtab);
        angleTableTab = (TabItem) findViewById(R.id.table_angles_tabtabtab);
        stratTableTab = (TabItem) findViewById(R.id.strat_angles_tabtabtab);


        RowOneTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*
                Fragment fragment = null;
                switch (tab.getPosition()) {

                    case 0:
                        fragment = new DataFragment();
                        break;
                    case 1:
                        fragment = new BeddingFragment();
                        break;
                    case 2:
                        fragment = new TableFragment();
                        break;
                    case 3:
                        fragment = new TableStratFragment();
                        break;


                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();

                 */
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                RowOneTabs.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
/*
        //File sourceDir = new File("/storage/self/primary/Android/data/com.xsens.dot.android/files/mfm");
        //File destinationDir = new File("/storage/self/primary/Android/data/com.xsens.dot.android.example/files/mfm");
        String sourceDir = "/storage/self/primary/Android/data/com.xsens.dot.android/files/mfm";
        String destinationDir = "/storage/self/primary/Android/data/com.xsens.dot.android.example/files/mfm";
        try {
            copyDirectory(sourceDir, destinationDir);
            System.out.println("Directory tree copied successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to copy directory tree.");
        }

 */

    }
/*
    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString().substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

 */

        @Override
    public void sendData(String message) {
        String tag = "android.switcher:" + R.id.view_pager + ":" + 1;
        BeddingFragment f = (BeddingFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.displayReceivedData(message);
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();

        bindViewModel();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver(mBluetoothStateReceiver);
    }

    @Override
    public void onBackPressed() {

        FragmentManager manager = getSupportFragmentManager();

        // If the fragment count > 0 in the stack, try to resume the previous page.
        if (manager.getBackStackEntryCount() > 0) manager.popBackStack();
        else super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() - requestCode = " + requestCode + ", resultCode = " + resultCode);

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            if (resultCode == RESULT_OK) checkBluetoothAndPermission();
            else Toast.makeText(this, getString(R.string.hint_turn_on_bluetooth), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult() - requestCode = " + requestCode);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {

            for (int i = 0; i < grantResults.length; i++) {

                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) checkBluetoothAndPermission();
                    else Toast.makeText(this, getString(R.string.hint_allow_location), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem scanItem = menu.findItem(R.id.action_scan);
        MenuItem streamingItem = menu.findItem(R.id.action_streaming);
        MenuItem measureItem = menu.findItem(R.id.action_measure);
        MenuItem recordItem = menu.findItem(R.id.action_record);
        MenuItem mfmItem = menu.findItem(R.id.action_mfm);

        if (mIsScanning) scanItem.setTitle(getString(R.string.menu_stop_scan));
        else scanItem.setTitle(getString(R.string.menu_start_scan));

        final boolean isStreaming = mSensorViewModel.isStreaming().getValue();
        if (isStreaming) streamingItem.setTitle(getString(R.string.menu_stop_streaming));
        else streamingItem.setTitle(getString(R.string.menu_start_streaming));

        if (sCurrentFragment.equals(FRAGMENT_TAG_SCAN)) {

            scanItem.setVisible(true);
            streamingItem.setVisible(false);
            measureItem.setVisible(true);
            recordItem.setVisible(true);
            mfmItem.setVisible(true);

        } else if (sCurrentFragment.equals(FRAGMENT_TAG_DATA)) {

            scanItem.setVisible(false);
            streamingItem.setVisible(true);
            measureItem.setVisible(true);
            recordItem.setVisible(true);
            mfmItem.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.action_scan:

                if (mScanListener != null && checkBluetoothAndPermission()) {
                    // Make sure th location permission is granted then start/stop scanning.
                    if (mIsScanning) mScanListener.onScanTriggered(false);
                    else mScanListener.onScanTriggered(true);
                }
                break;

            case R.id.action_streaming:
                // When the streaming button is clicked, notify to DataFragment and wait for the syncing result.
                mStreamingListener.onStreamingTriggered();
                /*
                XsensDotData xsData = new XsensDotData();
                onDataReceived(xsData);

                DataFragment dataFragment = (DataFragment) getSupportFragmentManager().findFragmentById(R.id.data_fragment);
                if (dataFragment != null) {
                    dataFragment.startStreaming();
                }
*/
                break;

            case R.id.action_measure:
                // Change to DataFragment and put ScanFragment to the back stack.
                /*
                Fragment mDataFragment = DataFragment.newInstance();
                addFragment(mDataFragment, FRAGMENT_TAG_DATA);
                 */
                TabLayout.Tab coreTabMeasure = RowOneTabs.getTabAt(1);
                coreTabMeasure.select();
                break;

            case R.id.action_record:
                /*
                final XsensDotDevice device = mSensorViewModel.getSensor("D4:22:CD:00:3A:21");
                device.resetHeading();
                */
                break;


            case R.id.action_mfm:
                //new MfmTask().execute();
                /*
                final BluetoothManager bluetoothManager = (BluetoothManager) MainActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                // Initialize BluetoothAdapter (you should request Bluetooth permissions if needed)
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Log.i("Bluetooth Device: ", "Bluetooth device does not exist");
                }

                // Get the BluetoothDevice object by address (replace "deviceAddress" with the actual address of your target device)
                bluetoothDevice = bluetoothAdapter.getRemoteDevice("D4:22:CD:00:3A:21");

                // Connect to the device and get BluetoothGatt object using MyBluetoothGattCallback
                bluetoothGatt = bluetoothDevice.connectGatt(this, false, new MyBluetoothGattCallback());

                //bluetoothGatt.connect();
                List<BluetoothGattService> services = bluetoothGatt.getServices();

                Log.i("UUIDs", "UUIDs: " + Arrays.toString(bluetoothDevice.getUuids()) + ", Address: " + bluetoothDevice.getAddress());

                UUID requiredServiceUuid = UUID.fromString("15172004-4947-11e9-8646-d663bd873d93");
                //UUID requiredServiceUuid = UUID.fromString("ccb18b5e-c662-4ab9-8e7d-cf928152c95d");
                //UUID requiredServiceUuid = UUID.fromString("4aadb872-3be2-44d4-8b9d-3de0518c75d7");

                for (BluetoothGattService service : services) {
                    UUID serviceUuid = service.getUuid();
                    Log.i("services UUID", serviceUuid.toString());
                    // Check if the service UUID matches the required MFM service UUID
                    if (serviceUuid.equals(requiredServiceUuid)) {
                        // MFM service is supported, perform further checks if needed
                        // You can also check for specific characteristics within the service if required
                        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                        break;
                    }
                }

                //BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice("D4:22:CD:00:3A:21");

                //XsensDotDevice xsDevice = new XsensDotDevice(MainActivity.this, bluetoothDevice, MainActivity.this);
                final XsensDotDevice device = mSensorViewModel.getSensor("D4:22:CD:00:3A:21");

                MfmTask mfmTask = new MfmTask(MainActivity.this, device);
                mfmTask.execute();

                 */
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Use ScanFragment as default page.
     */
    private void setupFragmentContainer() {

        if (null != getIntent()) {

            ScanFragment fragment = ScanFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, FRAGMENT_TAG_SCAN).commit();
        }
    }

    public void addFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    /**
     * Check the state of Bluetooth adapter and location permission.
     */
    private boolean checkBluetoothAndPermission() {

        boolean isBluetoothEnabled = Utils.isBluetoothAdapterEnabled(this);
        boolean isPermissionGranted = Utils.isLocationPermissionGranted(this);

        if (isBluetoothEnabled) {
            if (!isPermissionGranted) Utils.requestLocationPermission(this, REQUEST_PERMISSION_LOCATION);
        } else {
            Utils.requestEnableBluetooth(this, REQUEST_ENABLE_BLUETOOTH);
        }

        boolean status = isBluetoothEnabled && isPermissionGranted;
        Log.i(TAG, "checkBluetoothAndPermission() - " + status);

        mBluetoothViewModel.updateBluetoothEnableState(status);
        return status;
    }

    /**
     * Initialize and observe view models.
     */
    private void bindViewModel() {

        mBluetoothViewModel = BluetoothViewModel.getInstance(this);

        mBluetoothViewModel.isScanning().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(Boolean scanning) {
                // If the status of scanning is changed, try to refresh the menu.
                mIsScanning = scanning;
                invalidateOptionsMenu();
            }
        });

        mSensorViewModel = SensorViewModel.getInstance(this);

        mSensorViewModel.isStreaming().observe(this, new Observer<Boolean>() {

            @Override
            public void onChanged(Boolean status) {
                // If the status of streaming is changed, try to refresh the menu.
                invalidateOptionsMenu();
            }
        });
    }

    public void setScanTriggerListener(ScanClickInterface listener) {

        mScanListener = listener;
    }

    public void setStreamingTriggerListener(StreamingClickInterface listener) {

        mStreamingListener = listener;
    }

    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (action != null) {

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    // Notify the Bluetooth status to ScanFragment.
                    switch (state) {

                        case BluetoothAdapter.STATE_OFF:

                            mBluetoothViewModel.updateBluetoothEnableState(false);
                            break;

                        case BluetoothAdapter.STATE_ON:

                            mBluetoothViewModel.updateBluetoothEnableState(true);
                            break;
                    }
                }
            }
        }
    };


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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onXsensDotHeadingChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotRotLocalRead(String s, float[] floats) {

    }

}
