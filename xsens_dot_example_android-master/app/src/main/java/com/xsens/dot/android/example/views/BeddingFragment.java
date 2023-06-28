package com.xsens.dot.android.example.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.databinding.FragmentDataBinding;
import com.xsens.dot.android.example.R;

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
import java.util.List;


public class BeddingFragment extends Fragment {


    // The adapter for data item
    //private StratAdapter mStratAdapter;
    // A list contains tag and data from each sensor
    private ArrayList<HashMap<String, Object>> mDataList = new ArrayList<>();

    private Context mContext;
    private FragmentDataBinding mBinding;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    TextView Latitude, Longitude, Altitude, CoreId;
    private FusedLocationProviderClient fusedLocationClient;
    public List<CoreInfoClass> coreInfoList;
    DataBaseHelper dataBaseHelper;
    List<CoreInfoClass> cores;
    List<CoreInfoClass> CoreList;

    TextView northData, eastData, zData, coreId;
    TextView tvLatitude, tvLongitude, tvAltitude;
    EditText dipDir, dip, strat, projectName;
    Button BtnAdd, BtnLoc, BtnNext;
    Double valLatitude, valLongitude, valAltitude;
    private TabLayout RowOneTabs;

    public BeddingFragment() {
        // Required empty public constructor
    }

    public static BeddingFragment newInstance(String param1, String param2) {

        return new BeddingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    public static Fragment newInstance() {
        return new BeddingFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bedding, container, false);


        return view;
    }

    protected void displayReceivedData(String message){
        coreId.setText(message);
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

        coreId = view.findViewById(R.id.id_core);
        projectName = view.findViewById(R.id.project_name1);
        DataBaseHelper dbLast = new DataBaseHelper(getContext().getApplicationContext());
        projectName.setText(dbLast.LastProjectNameEntry());
/*
        CoreInfoClass firstCore = new CoreInfoClass(0, "", "", "", "", "",
                "", "", "", "", "", "", "", "",
                "", "", "");


        dbLast.updateOne(firstCore);

 */
        dbLast.close();

        //These are the coordinates of the phone
        tvLatitude = view.findViewById(R.id.txt_lat);
        tvLongitude = view.findViewById(R.id.txt_lon);
        tvAltitude = view.findViewById(R.id.txt_alt);

        dipDir = view.findViewById(R.id.txt_dip_dir);
        dip = view.findViewById(R.id.txt_dip);
        strat = view.findViewById(R.id.txt_strat);

        RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);

        //Log.d("Look here Drago: ", String.valueOf(lastId));
        // coreD.getNorth().toString());
        //Toast.makeText(mContext, "Look here: " + newCore.getDip(), Toast.LENGTH_LONG).show();

        BtnNext = view.findViewById(R.id.ButtonNext);
        BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataBaseHelper db = new DataBaseHelper(getContext().getApplicationContext());
                int lID = XsensDotApplication.getIntId();
                int someId = db.getEverything().size();
                String cored = db.getEverything().get(lID).getCore();
                Log.d("Core: ", String.valueOf(someId));

                coreId.setText(cored);
            }
        });

        BtnAdd = view.findViewById(R.id.final_store);
        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declaring the input variables from the user which are being stored in the global list
                String DataNorth, DataEast, DataZ, DataLat, DataLon, Altitude;
                String Core, Subcore, ProjectName, Note, SunReading;
                String DipDirection, Dip, Stratig;

                //DataBase generation
                DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext().getApplicationContext());
                //int lastId = XsensDotApplication.getIntId();
                int lastId = 1; //dataBaseHelper.getLastId();
                String project = dataBaseHelper.LastProjectNameEntry();

/*
                coreInfoList = XsensDotApplication.getCoreList();
                DataNorth = coreInfoList.get(lastId).getNorth();
                DataEast = coreInfoList.get(lastId).getEast();
                DataZ = coreInfoList.get(lastId).getZdir();
                DataLat = coreInfoList.get(lastId).getLatitude();
                DataLon = coreInfoList.get(lastId).getLongitude();
                Altitude = coreInfoList.get(lastId).getAltitude();
                Core = coreInfoList.get(lastId).getCore();
                Subcore = coreInfoList.get(lastId).getSubcore();
                ProjectName = coreInfoList.get(lastId).getProject();
                Note = coreInfoList.get(lastId).getNote();
                SunReading = coreInfoList.get(lastId).getSunReading();
*/
                DataNorth = dataBaseHelper.getEverything().get(lastId).getNorth();
                DataEast = dataBaseHelper.getEverything().get(lastId).getEast();
                DataZ = dataBaseHelper.getEverything().get(lastId).getZdir();
                DataLat = dataBaseHelper.getEverything().get(lastId).getLatitude();
                DataLon = dataBaseHelper.getEverything().get(lastId).getLongitude();
                Altitude = tvAltitude.getText().toString();//dataBaseHelper.getEverything().get(lastId).getAltitude();
                Core = dataBaseHelper.getEverything().get(lastId).getCore();
                Subcore = dataBaseHelper.getEverything().get(lastId).getSubcore();
                //ProjectName = dataBaseHelper.getEverything().get(lastId).getProject();
                Note = dataBaseHelper.getEverything().get(lastId).getNote();
                SunReading = dataBaseHelper.getEverything().get(lastId).getSunReading();


                DipDirection = dipDir.getText().toString();
                Dip = dip.getText().toString();
                Stratig = strat.getText().toString();
                Log.d("Look here Drago: ", project + "and last Core: ");

                //Creating a variable for the date and time
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = simpleDateFormat.format(date);
                String dbDate = formattedDateTime.substring(0, 9);
                String dbTime = formattedDateTime.substring(11, 18);

                //Create a list with the input data
                CoreInfoClass newCore = new CoreInfoClass(lastId+1, DataNorth, DataEast, DataZ,
                        DataLat, DataLon, Core, Subcore, Note, project, SunReading, Altitude, DipDirection, Dip, Stratig, dbDate, dbTime);


                dataBaseHelper.updateOne(newCore);
                Log.d("Core: ", Core);
                Log.d("ID: ", Integer.toString(lastId));

                File source = new File("/data/data/com.xsens.dot.android.example/databases/tt7.db");
                File destination = new File("/storage/self/primary/Download/tt7.db");

                try {
                    copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TableFragment tableFragment = new TableFragment();
                //getFragmentManager().beginTransaction().replace(R.id.container, tableFragment).addToBackStack(null).commit();
/* */
                TabLayout.Tab coreTabMeasure = RowOneTabs.getTabAt(3);
                coreTabMeasure.select();


            }
        });

    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
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



}