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
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
//import com.xsens.dot.android.example.BuildConfig;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.adapters.SendMessage;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.databinding.FragmentDataBinding;
import com.xsens.dot.android.example.interfaces.DataChangeInterface;
import com.xsens.dot.android.example.interfaces.StreamingClickInterface;
import com.xsens.dot.android.example.interfaces.XsensDataCallBack;
import com.xsens.dot.android.example.viewmodels.SensorViewModel;
import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotMeasurementCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotSyncCallback;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.models.XsensDotSyncManager;
import com.xsens.dot.android.sdk.recording.XsensDotRecordingManager;
import com.xsens.dot.android.sdk.utils.XsensDotLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.xsens.dot.android.example.views.MainActivity.FRAGMENT_TAG_DATA;
import static com.xsens.dot.android.sdk.models.XsensDotDevice.LOG_STATE_ON;
import static com.xsens.dot.android.sdk.models.XsensDotDevice.PLOT_STATE_ON;
import static com.xsens.dot.android.sdk.models.XsensDotPayload.PAYLOAD_TYPE_COMPLETE_EULER;
import static com.xsens.dot.android.sdk.models.XsensDotPayload.PAYLOAD_TYPE_COMPLETE_QUATERNION;
import static com.xsens.dot.android.sdk.models.XsensDotPayload.PAYLOAD_TYPE_CUSTOM_MODE_2;

/**
 * A fragment for presenting the data and storing to file.
 */
public class DataFragment extends Fragment implements StreamingClickInterface, DataChangeInterface, XsensDotSyncCallback, XsensDotMeasurementCallback,
        XsensDotDeviceCallback, XsensDataCallBack, SensorEventListener, SendMessage {

    private static final String TAG = DataFragment.class.getSimpleName();

    // The code of request
    private static final int SYNCING_REQUEST_CODE = 1001;

    // The keys of HashMap
    public static final String KEY_LOGGER = "logger";

    // The view binder of DataFragment
    private FragmentDataBinding mBinding;

    // The devices view model instance
    private SensorViewModel mSensorViewModel;

    // The application context
    public Context bContext;

    private BluetoothDevice mBluetoothDevice;

    //TextView test
    TextView TestingText;

    // A list contains tag and data from each sensor
    private ArrayList<HashMap<String, Object>> mDataList = new ArrayList<>();

    // A list contains mac address and XsensDotLogger object.
    private List<HashMap<String, Object>> mLoggerList = new ArrayList<>();

    // A variable for data logging flag
    private boolean mIsLogging = false;

    // A dialog during the synchronization
    private AlertDialog mSyncingDialog;

    private XsensDotSdk xsensDotSdk;

    private ArrayList<HashMap<String, Object>> dataList;

    private FusedLocationProviderClient fusedLocationClient;

    private XsensDataCallBack mXsensDataCallBack = new XsensDataCallBack() {
        @Override
        public void onDataReceived(XsensDotData xsensDotData) {
            TestingText.setText(xsensDotData.toString());
        }
    };

    private XsensDotRecordingManager xsensDotRecordingManager;
    private SensorManager sensorManager;
    private SensorViewModel sensorViewModel;
    private StreamingClickInterface mStreamingListener;

    View rootView;
    TextView northData, eastData, zData;
    TextView tvLatitude, tvLongitude, tvAltitude;
    TextView correctedAzimuth;
    EditText CoreData, SubcoreData, ProjectNameData, NoteData, SunReadingData, TestCore;

    Button BtnAdd, BtnLoc, BtnNext;
    Double valLatitude, valLongitude, valAltitude;
    private TabLayout RowOneTabs;
    Spinner spinner;
    List<CoreInfoClass> CoreList;
    List<CoreInfoClass> dbLastEntry;
    SendMessage SM;

    AutoCompleteTextView autoCompleteTextView;

    /**
     * Get the instance of DataFragment
     *
     * @return The instance of DataFragment
     */
    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bindViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        String idValue, coreValue, azValue, avValue, sunValue, zValue, stratValue;

        //These are the three orientation angles
        northData = view.findViewById(R.id.data_north);
        eastData = view.findViewById(R.id.data_east);
        zData = view.findViewById(R.id.data_z);

        //These are the coordinates of the phone
        tvLatitude = view.findViewById(R.id.txt_lat);
        tvLongitude = view.findViewById(R.id.txt_lon);
        tvAltitude = view.findViewById(R.id.txt_alt);

        //The other fields which the user fills in
        CoreData = view.findViewById(R.id.core_value);
        SubcoreData = view.findViewById(R.id.subcore_value);
        ProjectNameData = view.findViewById(R.id.project_name1);
        NoteData = view.findViewById(R.id.note_text);
        SunReadingData = view.findViewById(R.id.txt_dip_dir);
        BtnAdd = view.findViewById(R.id.final_store);
        //spinner = view.findViewById(R.id.spinner_site);

        correctedAzimuth = view.findViewById(R.id.corrected_az);

        //Autocomplete field with the sites is initialized in the next few lines
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView1);
        autoCompleteTextView.clearComposingText();

        List<String> newSuggestions = new ArrayList<>(); // Initialize with initial suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, newSuggestions);
        autoCompleteTextView.setAdapter(adapter);

        DataBaseHelper dbaseHelper = new DataBaseHelper(getContext().getApplicationContext());
        int listLength = dbaseHelper.getEverything().size();
        for (int i=0; i<listLength; i++){
            newSuggestions.add(dbaseHelper.getEverything().get(i).getSite());
        }
        dbaseHelper.close();

        // Create a Set to hold unique elements
        Set<String> uniqueSet = new HashSet<>(newSuggestions);

        // Convert the Set back to a List if needed
        List<String> uniqueList = new ArrayList<>(uniqueSet);

        Log.i("SUGGESTIONS", uniqueList.toString());
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentInput = s.toString().trim();
                if (!currentInput.isEmpty()) {
                    // Filter newSuggestions to match the current input
                    List<String> filteredSuggestions = new ArrayList<>();
                    for (String suggestion : uniqueList) {
                        if (suggestion.toLowerCase().contains(currentInput.toLowerCase())) {
                            filteredSuggestions.add(suggestion);
                        }
                    }

                    // Update the adapter with filtered suggestions
                    adapter.clear();
                    adapter.addAll(filteredSuggestions);
                    adapter.notifyDataSetChanged();
                }
            }
        });


        RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);


        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This part will stop the streaming of data and it will put a values for azimuth (0-360) in the textview correctedAzimuth
                mSensorViewModel.setMeasurement(false);
                mSensorViewModel.updateStreamingStatus(false);

                double dataNorth = Double.parseDouble(northData.getText().toString());
                double correctAzimuth = 270.0 - dataNorth;//*(-1)+360;

                if (correctAzimuth > 360.0) {
                    correctAzimuth = correctAzimuth - 360.0;
                }

                String correction360Azimuth_string =
                        String.format("%.1f", correctAzimuth);
                correctedAzimuth.setText(correction360Azimuth_string);
                correctedAzimuth.setTextSize(20.0f);

                /*
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                TableFragment tableFragment = new TableFragment();
                BeddingFragment beddingFragment = new BeddingFragment();
                 */

                //Declaring the input variables from the user which are being stored in the global list
                String DataNorth, DataEast, DataZ, DataLat, DataLon, Altitude, Site;
                String Core, Subcore, ProjectName, Note, SunReading;
                DataNorth = correctedAzimuth.getText().toString();
                DataEast = eastData.getText().toString();
                DataZ = zData.getText().toString();
                DataLat = tvLatitude.getText().toString();
                DataLon = tvLongitude.getText().toString();
                Altitude = tvAltitude.getText().toString();
                Core = CoreData.getText().toString();
                Subcore = SubcoreData.getText().toString();
                ProjectName = ProjectNameData.getText().toString();
                Note = NoteData.getText().toString();
                SunReading = SunReadingData.getText().toString();
                Site = autoCompleteTextView.getText().toString();

                //DataBase generation
                DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext().getApplicationContext());

                CoreList = XsensDotApplication.getCoreList();
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String formattedDateTime = simpleDateFormat.format(date);
                String dbDate = formattedDateTime.substring(0, 9);
                String dbTime = formattedDateTime.substring(11, 18);
                //Create a list with the input data
                //int nextId = XsensDotApplication.getIntId();
                //int nextId = dataBaseHelper.getEverything().size();
                int nextId = XsensDotApplication.getIntId();
                //CoreInfoClass newCore = new CoreInfoClass(nextId + 1, DataNorth, DataEast, DataZ, DataLat, DataLon, Core, Subcore, ProjectName, Note, SunReading, "", "", "","");
                CoreInfoClass newCore = new CoreInfoClass(nextId, DataNorth, DataEast, DataZ,
                        DataLat, DataLon, Site, Core, Subcore, Note, ProjectName, SunReading, Altitude, "0.0", "0.0", "0", dbDate, dbTime);

                //Add the data to the global list
                CoreList.add(newCore);

                //The ID inside the list is not changing so look into it
                XsensDotApplication.setIntId(nextId);

                dataBaseHelper.addOne(newCore);

                dataBaseHelper.close();
                File source = new File("/data/data/com.xsens.dot.android.example/databases/USA_table.db");
                File destination = new File("/storage/self/primary/Android/data/com.xsens.dot.android.example/files/DataBase/USA_table.db");

                try {
                    copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                TabLayout.Tab coreTabMeasure = RowOneTabs.getTabAt(2);
                coreTabMeasure.select();

            }
        });

        return view;
    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(String.valueOf(src));
        FileOutputStream outStream = new FileOutputStream(String.valueOf(dst));
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latit = location.getLatitude();
                            double longit = location.getLongitude();
                            double altit = location.getAltitude();
                            tvLatitude.setText(new DecimalFormat("00.00").format(latit));
                            tvLongitude.setText(new DecimalFormat("00.00").format(longit));
                            tvAltitude.setText(new DecimalFormat("000.00").format(altit));

                            valLatitude = location.getLatitude();
                            valLongitude = location.getLongitude();
                            valAltitude = location.getAltitude();
                        }
                    }
                });

        DataBaseHelper dbLast = new DataBaseHelper(getContext().getApplicationContext());
        ProjectNameData.setText(dbLast.LastProjectNameEntry());
        dbLast.close();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorViewModel.setStates(PLOT_STATE_ON, LOG_STATE_ON);

        // Set the StreamingClickInterface instance to main activity.
        if (getActivity() != null) ((MainActivity) getActivity()).setStreamingTriggerListener(this);
    }




    @Override
    public void onDetach() {
        super.onDetach();
        // Stop measurement for each sensor when exiting this page.
        //mSensorViewModel.setMeasurement(false);
        // It's necessary to update this status, because user may enter this page again.
        //mSensorViewModel.updateStreamingStatus(false);

        closeFiles();
    }

    @Override
    public void onStreamingTriggered() {

        if (mSensorViewModel.isStreaming().getValue()) {
            // To stop.
            mSensorViewModel.setMeasurement(false);
            mSensorViewModel.updateStreamingStatus(false);

            double dataNorth = Double.parseDouble(northData.getText().toString());
            double correctAzimuth = 270.0 - dataNorth;//*(-1)+360;

            if (correctAzimuth > 360.0) {
                correctAzimuth = correctAzimuth - 360.0;
            }

            String correction360Azimuth_string =
                    String.format("%.1f", correctAzimuth);
            correctedAzimuth.setText(correction360Azimuth_string);


            XsensDotSyncManager.getInstance(this).stopSyncing();

            closeFiles();
        }

        else {
            // To start.
            resetPage();

            if (!mSensorViewModel.checkConnection()) {

                Toast.makeText(getContext(), getString(R.string.hint_check_connection), Toast.LENGTH_LONG).show();
                return;
            }

            // Syncing precess is success, choose one measurement mode to start measuring.
            //mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_COMPLETE_EULER);
            mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_CUSTOM_MODE_2);
            createFiles();

            mSensorViewModel.setMeasurement(true);

            final XsensDotDevice device = mSensorViewModel.getSensor("D4:22:CD:00:3A:21");

            mSensorViewModel.updateStreamingStatus(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Notify main activity to refresh menu.
        MainActivity.sCurrentFragment = FRAGMENT_TAG_DATA;
        if (getActivity() != null) getActivity().invalidateOptionsMenu();

        DataBaseHelper databsHelp = new DataBaseHelper(getContext().getApplicationContext());

        String lastCoreString = databsHelp.LastCoreEntry();
        Integer nextCoreInt = Integer.parseInt(lastCoreString) + 1;
        CoreData.setText(String.valueOf(nextCoreInt));

        String lastSiteString = databsHelp.LastSiteEntry();
        autoCompleteTextView.setText(String.valueOf(lastSiteString));

        String projectName = databsHelp.LastProjectNameEntry();
        Log.i("MY CORE", projectName);

        ProjectNameData.setText(projectName);
    }
    /**
     * Initialize and observe view models.
     */
    private void bindViewModel() {

        if (getActivity() != null) {

            mSensorViewModel = SensorViewModel.getInstance(getActivity());
            // Implement DataChangeInterface and override onDataChanged() function to receive data.
            mSensorViewModel.setDataChangeCallback(this);
        }
    }

    public void setStreamingTriggerListener(StreamingClickInterface listener) {

        mStreamingListener = listener;
    }

    /**
     * Reset page UI to default.
     */
    private void resetPage() {
        //mBinding.syncResult.setText("-");
        mDataList.clear();
        //mDataAdapter.notifyDataSetChanged();
    }

    /**
     * Get the filter profile name.
     *
     * @param device The XsensDotDevice object
     * @return The filter profile name, "General" by default
     */
    private String getFilterProfileName(XsensDotDevice device) {

        int index = device.getCurrentFilterProfileIndex();
        ArrayList<FilterProfileInfo> list = device.getFilterProfileInfoList();
        Log.i("Filter for magField", String.valueOf(index));

        for (FilterProfileInfo info : list) {

            if (info.getIndex() == index) return info.getName();
        }

        return "General";
    }

    /**
     * Create data logger for each sensor.
     */
    private void createFiles() {

        // Remove XsensDotLogger objects from list before start data logging.
        mLoggerList.clear();

        ArrayList<XsensDotDevice> devices = mSensorViewModel.getAllSensors();

        for (XsensDotDevice device : devices) {

            String appVersion = "";
            String fwVersion = device.getFirmwareVersion();
            String address = device.getAddress();
            String tag = device.getTag().isEmpty() ? device.getName() : device.getTag();
            String filename = "";
            if (getContext() != null) {

                // Store log file in app internal folder.
                // Don't need user to granted the storage permission.
                File dir = getContext().getExternalFilesDir(null);

                if (dir != null) {

                    // This filename contains full file path.
                    filename = dir.getAbsolutePath() +
                            File.separator +
                            tag + "_" +
                            new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(new Date()) +
                            ".csv";
                }
            }

            Log.d(TAG, "createFiles() - " + filename);

            XsensDotLogger logger = new XsensDotLogger(
                    getContext(),
                    XsensDotLogger.TYPE_CSV,
                    PAYLOAD_TYPE_CUSTOM_MODE_2,
                    filename,
                    tag,
                    fwVersion,
                    device.isSynced(),
                    device.getCurrentOutputRate(),
                    getFilterProfileName(device),
                    appVersion);

            // Use mac address as a key to find logger object.
            HashMap<String, Object> map = new HashMap<>();

            map.put(KEY_LOGGER, logger);
            mLoggerList.add(map);
        }

        mIsLogging = true;
    }

    /**
     * Update data to specific file.
     *
     * @param address The mac address of device
     * @param data    The XsensDotData packet
     */
    private void updateFiles(String address, XsensDotData data) {
/*
        for (HashMap<String, Object> map : mLoggerList) {

            String _address = (String) map.get(KEY_ADDRESS);
            if (_address != null) {

                if (_address.equals(address)) {

                    XsensDotLogger logger = (XsensDotLogger) map.get(KEY_LOGGER);
                    if (logger != null && mIsLogging) logger.update(data);
                }
            }
        }

 */
    }

    /**
     * Close the data output stream.
     */
    private void closeFiles() {

        mIsLogging = false;

        for (HashMap<String, Object> map : mLoggerList) {
            // Call stop() function to flush and close the output stream.
            // Data is kept in the stream buffer and write to file when the buffer is full.
            // Call this function to write data to file whether the buffer is full or not.
            XsensDotLogger logger = (XsensDotLogger) map.get(KEY_LOGGER);
            if (logger != null) logger.stop();
        }
    }

    @Override
    public void onSyncingStarted(String address, boolean isSuccess, int requestCode) {

        Log.i(TAG, "onSyncingStarted() - address = " + address + ", isSuccess = " + isSuccess + ", requestCode = " + requestCode);
    }

    @Override
    public void onSyncingProgress(final int progress, final int requestCode) {

        Log.i(TAG, "onSyncingProgress() - progress = " + progress + ", requestCode = " + requestCode);


        if (requestCode == SYNCING_REQUEST_CODE) {

            if (mSyncingDialog.isShowing()) {

                if (getActivity() != null) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Find the view of progress bar in dialog layout and update.
                            ProgressBar bar = mSyncingDialog.findViewById(R.id.syncing_progress);
                            if (bar != null) bar.setProgress(progress);
                        }
                    });
                }
            }
        }

    }

    @Override
    public void onSyncingResult(String address, boolean isSuccess, int requestCode) {

        //Log.i(TAG, "onSyncingResult() - address = " + address + ", isSuccess = " + isSuccess + ", requestCode = " + requestCode);
    }

    @Override
    public void onSyncingDone(final HashMap<String, Boolean> syncingResultMap, final boolean isSuccess, final int requestCode) {

        Log.i(TAG, "onSyncingDone() - isSuccess = " + isSuccess + ", requestCode = " + requestCode);

        if (requestCode == SYNCING_REQUEST_CODE) {

            if (getActivity() != null) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (mSyncingDialog.isShowing()) mSyncingDialog.dismiss();

                        mSensorViewModel.setRootDevice(false);
                        // Syncing precess is success, choose one measurement mode to start measuring.
                        mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_CUSTOM_MODE_2);
                        //mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_COMPLETE_QUATERNION);

                        createFiles();

                        mSensorViewModel.setMeasurement(true);
                        // Notify the current streaming status to MainActivity to refresh the menu.
                        mSensorViewModel.updateStreamingStatus(true);

                        if (isSuccess) {

                            //mBinding.syncResult.setText(R.string.sync_result_success);

                            // Syncing precess is success, choose one measurement mode to start measuring.
                            mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_CUSTOM_MODE_2);
                            //mSensorViewModel.setMeasurementMode(PAYLOAD_TYPE_COMPLETE_QUATERNION);


                            createFiles();

                            mSensorViewModel.setMeasurement(true);
                            // Notify the current streaming status to MainActivity to refresh the menu.
                            mSensorViewModel.updateStreamingStatus(true);

                        } else {

                            //mBinding.syncResult.setText(R.string.sync_result_fail);

                            // If the syncing result is fail, show a message to user
                            Toast.makeText(getContext(), getString(R.string.hint_syncing_failed), Toast.LENGTH_LONG).show();

                            for (Map.Entry<String, Boolean> result : syncingResultMap.entrySet()) {

                                if (!result.getValue()) {
                                    // Get the key of this failed device.
                                    String address = result.getKey();
                                    // It's preferred to stop measurement of all sensors.
                                    mSensorViewModel.setMeasurement(false);
                                    // Notify the current streaming status to MainActivity to refresh the menu.
                                    mSensorViewModel.updateStreamingStatus(false);
                                }
                            }
                        }

                    }
                });
            }
        }
    }

    public void onSyncingStopped(String address, boolean isSuccess, int requestCode) {

        Log.i(TAG, "onSyncingStopped() - address = " + address + ", isSuccess = " + isSuccess + ", requestCode = " + requestCode);
    }

    @Override
    public void onDataChanged(String address, XsensDotData data) {

        Log.i(TAG, "onDataChanged() - address = " + address);
/*
        boolean isExist = false;

        for (HashMap<String, Object> map : mDataList) {

            String _address = (String) map.get(KEY_ADDRESS);
            if (_address.equals(address)) {
                // If the data is exist, try to update it.
                map.put(KEY_DATA, data);
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            // It's the first data of this sensor, create a new set and add it.
            HashMap<String, Object> map = new HashMap<>();
            map.put(KEY_ADDRESS, address);
            map.put(KEY_TAG, mSensorViewModel.getTag(address));
            map.put(KEY_DATA, data);
            mDataList.add(map);
        }
*/
        updateFiles(address, data);
/*
        if (getActivity() != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // The data is coming from background thread, change to UI thread for updating.
                    mDataAdapter.notifyDataSetChanged();
                }
            });
        }
        final float[] quater = data.getQuat();
        String quaterAngle =
                String.format("%.5f", quater[0]);
        SunReadingData.setText(quaterAngle);
*/
        //XsensDotData xsData = (XsensDotData) dataList.get(0).get("data");
       // final double[] euler1 = xsData.getEuler();
        //String eulerAnglesStr =
        //        String.format("%.1f", euler1[2]);
        //SunReadingData.setText(eulerAnglesStr);

        final double[] euler = data.getEuler();
        final double[] magnetic = data.getMag();

        //Permutation 6:
        final double z = euler[0];
        final double y = euler[1];
        final double x = euler[2];

        final double magX = magnetic[0];
        final double magY = magnetic[1];
        final double magZ = magnetic[2];

        String magneticNorm = String.format("%.1f", Math.sqrt(magX*magX + magY*magY + magZ*magZ));
        double azimuthAngle = Math.toDegrees(Math.atan2(z, Math.sqrt(x*x + y*y)));
        //SunReadingData.setText(String.valueOf(azimuthAngle));
        String eulerAnglesStr_zero =
                String.format("%.1f", euler[2]);//((euler[2]+90)%360)
        String eulerAnglesStr_one =
                String.format("%.1f", euler[1]+90.0);
        String eulerAnglesStr_two =
                String.format("%.1f", euler[0]);
        northData.setText(eulerAnglesStr_zero);
        eastData.setText(eulerAnglesStr_one);
        zData.setText(eulerAnglesStr_two);
        //SunReadingData.setText(magneticNorm);
    }

    @Override
    public void onXsensDotHeadingChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotRotLocalRead(String s, float[] floats) {

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

    @Override
    public void onDataReceived(XsensDotData xsensDotData) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d(TAG, "onSensorChanged: X: " + sensorEvent.values[0] + "Y: " + sensorEvent.values[1] + "Z: " + sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void sendData(String message) {

    }
}
