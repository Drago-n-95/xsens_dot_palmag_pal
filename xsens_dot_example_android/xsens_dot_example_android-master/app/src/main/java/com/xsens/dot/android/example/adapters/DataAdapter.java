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

package com.xsens.dot.android.example.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.viewmodels.SensorViewModel;
import com.xsens.dot.android.example.views.BeddingFragment;
import com.xsens.dot.android.example.views.CoreInfoClass;
import com.xsens.dot.android.example.views.DataBaseHelper;
import com.xsens.dot.android.example.views.DataFragment;
import com.xsens.dot.android.example.views.MainActivity;
import com.xsens.dot.android.example.views.TableFragment;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotMeasurementCallback;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.utils.XsensDotParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Object;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * A view adapter for item view to present data.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> implements XsensDotDeviceCallback, XsensDotMeasurementCallback {

    private static final String TAG = DataAdapter.class.getSimpleName();

    // The keys of HashMap
    public static final String KEY_ADDRESS = "address", KEY_TAG = "tag", KEY_DATA = "data";

    // The application context
    public Context mContext;

    private BluetoothDevice mBluetoothDevice;

    private SensorViewModel mSensorViewModel;

    // Put all data from sensors into one list
    private ArrayList<HashMap<String, Object>> mDataList;

    private FusedLocationProviderClient fusedLocationClient;

    List<CoreInfoClass> CoreList;
    public static final int MODE_PRIVATE = 0;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevice;
    private XsensDotDeviceCallback xsensDotDeviceCallback;

    public DataAdapter(Context context, ArrayList<HashMap<String, Object>> dataList) {

        mContext = context;
        mDataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);


        return new DataViewHolder(itemView);
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {

        //String tag = (String) mDataList.get(position).get(KEY_TAG);
        XsensDotData xsData = (XsensDotData) mDataList.get(position).get(KEY_DATA);

        //holder.sensorName.setText(tag);

        double[] eulerAngles = xsData.getEuler();

        String eulerAnglesStr_zero =
                String.format("%.1f", (eulerAngles[2]-180)*(-1)-180);
        String eulerAnglesStr_one =
                String.format("%.1f", eulerAngles[1]+90.0);
        String eulerAnglesStr_two =
                String.format("%.1f", eulerAngles[0]*(-1));
        holder.northData.setText(eulerAnglesStr_zero);
        holder.eastData.setText(eulerAnglesStr_one);
        holder.zData.setText(eulerAnglesStr_two);

    }

    @Override
    public int getItemCount() {

        return mDataList == null ? 0 : mDataList.size();
    }


    public static double[] enuToAer(double x, double y, double z, double[] aer){
        double east = StrictMath.toRadians(x);
        double north = StrictMath.toRadians(y);
        double up = StrictMath.toRadians(z);
        double r = StrictMath.hypot(east, north);
        double slantRange = StrictMath.hypot(r, up);
        double elev = StrictMath.atan2(up, r);
        double az = StrictMath.atan2(east, north);// % (2.0 * 3.14);
        double felev  = StrictMath.toDegrees(elev);
        double faz = StrictMath.toDegrees(az);
        aer[0] = faz;
        aer[1] = felev;
        aer[2] = StrictMath.toDegrees(slantRange);
        return aer;
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
    public void onXsensDotHeadingChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotRotLocalRead(String s, float[] floats) {

    }


    public class DataViewHolder extends RecyclerView.ViewHolder implements com.xsens.dot.android.example.adapters.DataViewHolder {

        View rootView;
        TextView northData, eastData, zData;
        TextView tvLatitude, tvLongitude, tvAltitude;
        EditText CoreData, SubcoreData, ProjectNameData, NoteData, SunReadingData;
        Button BtnAdd, BtnLoc, BtnNext;
        Double valLatitude, valLongitude, valAltitude;

        public DataViewHolder(View v) {

            super(v);

            rootView = v;
            //sensorName = v.findViewById(R.id.sensor_name);

            //These are the three orientation angles
            northData = v.findViewById(R.id.data_north);
            eastData = v.findViewById(R.id.data_east);
            zData = v.findViewById(R.id.data_z);

            //These are the coordinates of the phone
            tvLatitude = v.findViewById(R.id.txt_lat);
            tvLongitude = v.findViewById(R.id.txt_lon);
            tvAltitude = v.findViewById(R.id.txt_alt);

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {
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


            BtnNext = v.findViewById(R.id.ButtonNext);
            BtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("D4:22:CD:00:3A:21");

                    XsensDotDevice xsDevice = new XsensDotDevice(mContext.getApplicationContext(), device, DataAdapter.this);
                    xsDevice.setXsensDotMeasurementCallback((XsensDotMeasurementCallback) mContext);
                    /*
                    //Toast.makeText(mContext, "Success = " + xsDevice.getName(), Toast.LENGTH_SHORT).show();
                    xsDevice.startMeasuring();
                    xsDevice.resetHeading();

                    int result = 0;
                    onXsensDotHeadingChanged("D4:22:CD:00:3A:21", xsDevice.getHeadingStatus(), result);
                    Toast.makeText(mContext, "Success = " +  xsDevice.getHeadingStatus(), Toast.LENGTH_SHORT).show();

                    //XsensDotDevice device = SensorViewModel.getSensor("D4:22:CD:00:3A:21");
                    xsDevice.setMeasurementMode(16); // 4 refers Complete euler payload mode
                    xsDevice.setPlotState(1);
                    //xsDevice.setXsensDotDeviceCallback(true);
                    xsDevice.resetHeading();
                    Toast.makeText(mContext, "Success = " +  xsDevice.getHeadingStatus(), Toast.LENGTH_SHORT).show();
*/
                }
            });

            //The other fields which the user fills in
            CoreData = v.findViewById(R.id.core_value);
            SubcoreData = v.findViewById(R.id.subcore_value);
            ProjectNameData = v.findViewById(R.id.project_name);
            NoteData = v.findViewById(R.id.note_text);
            SunReadingData = v.findViewById(R.id.sun_reading_value);
            BtnAdd = v.findViewById(R.id.ButtonAdd);
/*
            Intent intent = Intent.getIntentOld(mContext, );
            id = intent.getIntExtra("id", -1);
            CoreInfoClass coreInfoClass = null;

            if (id >= 0){
                for(CoreInfoClass core: CoreList){
                    if (core.getId() == id){
                        coreInfoClass = core;
                    }
                }

            }
            else{

            }

 */
            BtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    TableFragment tableFragment = new TableFragment();
                    BeddingFragment beddingFragment = new BeddingFragment();
                    //Declaring the input variables from the user which are being stored in the global list
                    String DataNorth, DataEast, DataZ, DataLat, DataLon, Altitude;
                    String Core, Subcore, ProjectName, Note, SunReading;
                    DataNorth = northData.getText().toString();
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

/*
                    //This part is used to send data to table fragment but after I created the global list where I store the data I don`t need it.
                    Bundle args = new Bundle();
                    args.putString("DataNorthKey", DataNorth);
                    args.putString("DataEastKey", DataEast);
                    args.putString("DataZKey", DataZ);
                    args.putString("DataLatKey", DataLat);
                    args.putString("DataLonKey", DataLon);

                    args.putString("DataCoreKey", Core);
                    args.putString("DataSubcoreKey", Subcore);
                    args.putString("DataProjectNameKey", ProjectName);
                    args.putString("DataNoteKey", Note);
                    args.putString("DataSunReadingKey", SunReading);

                    tableFragment.setArguments(args);

*/
                    CoreList = XsensDotApplication.getCoreList();

                    //Create a list with the input data
                    int nextId = XsensDotApplication.getIntId();
                    //CoreInfoClass newCore = new CoreInfoClass(nextId + 1, DataNorth, DataEast, DataZ, DataLat, DataLon, Core, Subcore, ProjectName, Note, SunReading, "", "", "","");
                    CoreInfoClass newCore = new CoreInfoClass(nextId + 1, DataNorth, DataEast, DataZ,
                            DataLat, DataLon, Core, Subcore, Note, ProjectName, SunReading, Altitude, "", "", "");

                    //Add the data to the global list
                    CoreList.add(newCore);

                    //The ID inside the list is not changing so look into it
                    XsensDotApplication.setIntId(nextId);

                    //DataBase generation
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext.getApplicationContext());

                    boolean success = dataBaseHelper.addOne(newCore);
                    Toast.makeText(mContext, "Success = " + success, Toast.LENGTH_SHORT).show();

                    File source = new File("/data/data/com.xsens.dot.android.example/databases/tt3.db");
                    File destination = new File("/sdcard/Download/tt3.db");

                    try {
                        copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //This opens TableFragment
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, tableFragment).addToBackStack(null).commit();

                }
            });
        }
    }
/*
    public void resetHeadings(){
        final ArrayList<XsensDotDevice> devices = mSensorViewModel.getAllSensors();
        mSensorViewModel.setMeasurementMode(4); // 16 refers Complete euler payload mode
        mSensorViewModel.setMeasurement(true);
        for(XsensDotDevice device: devices){
            device.resetHeading();
        }
    }
*/
    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(String.valueOf(src));
        FileOutputStream outStream = new FileOutputStream(String.valueOf(dst));
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
