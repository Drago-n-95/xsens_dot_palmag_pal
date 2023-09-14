package com.xsens.dot.android.example.views;

import static com.xsens.dot.android.example.views.MainActivity.FRAGMENT_TAG_DATA;

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
import android.util.TypedValue;
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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
    TextView tvLatitude, tvLongitude, tvAltitude, siteName, ProjectNameData;
    EditText dipDir, dip, strat;
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
                            tvLatitude.setText(new DecimalFormat("00.0000").format(latit));
                            tvLongitude.setText(new DecimalFormat("00.0000").format(longit));
                            tvAltitude.setText(new DecimalFormat("000.00").format(altit));

                            valLatitude = location.getLatitude();
                            valLongitude = location.getLongitude();
                            valAltitude = location.getAltitude();
                        }
                    }
                });

        coreId = view.findViewById(R.id.id_core);
        ProjectNameData = view.findViewById(R.id.project_name1);


        //These are the coordinates of the phone
        tvLatitude = view.findViewById(R.id.txt_lat);
        tvLongitude = view.findViewById(R.id.txt_lon);
        tvAltitude = view.findViewById(R.id.txt_alt);

        dipDir = view.findViewById(R.id.txt_dip_dir);
        dip = view.findViewById(R.id.txt_dip);
        strat = view.findViewById(R.id.txt_strat);

        siteName = view.findViewById(R.id.site_name_label);

        RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);

        BtnAdd = view.findViewById(R.id.final_store);
        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declaring the input variables from the user which are being stored in the global list
                String DataNorth, DataEast, DataZ, DataLat, DataLon, Altitude;
                String Core, Subcore, ProjectName, Site, Note, SunReading;
                String DipDirection, Dip, Stratig;

                //DataBase generation
                DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext().getApplicationContext());
                //int lastId = XsensDotApplication.getIntId();

                String project = dataBaseHelper.LastProjectNameEntry();


                int lID = dataBaseHelper.getLastId()-1;

                DataNorth = dataBaseHelper.getEverything().get(lID).getNorth();
                DataEast = dataBaseHelper.getEverything().get(lID).getEast();
                DataZ = dataBaseHelper.getEverything().get(lID).getZdir();
                DataLat = dataBaseHelper.getEverything().get(lID).getLatitude();
                DataLon = dataBaseHelper.getEverything().get(lID).getLongitude();
                Altitude = tvAltitude.getText().toString();//dataBaseHelper.getEverything().get(lID).getAltitude();
                Core = dataBaseHelper.getEverything().get(lID).getCore();
                Subcore = dataBaseHelper.getEverything().get(lID).getSubcore();
                //ProjectName = dataBaseHelper.getEverything().get(lID).getProject();
                Note = dataBaseHelper.getEverything().get(lID).getNote();
                SunReading = dataBaseHelper.getEverything().get(lID).getSunReading();
                Site = dataBaseHelper.getEverything().get(lID).getSite();

                DipDirection = dipDir.getText().toString();
                Dip = dip.getText().toString();
                Stratig = strat.getText().toString();
                Log.d("Look here Drago: ", String.valueOf(dataBaseHelper.getLastId()));

                //Creating a variable for the date and time
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = simpleDateFormat.format(date);
                String dbDate = formattedDateTime.substring(0, 9);
                String dbTime = formattedDateTime.substring(11, 18);

                //Create a list with the input data
                CoreInfoClass newCore = new CoreInfoClass(lID+1, DataNorth, DataEast, DataZ,
                        DataLat, DataLon, Site, Core, Subcore, Note, project, SunReading, Altitude, DipDirection, Dip, Stratig, dbDate, dbTime);


                dataBaseHelper.updateOne(newCore);
                Log.d("Core: ", Core);
                Log.d("ID: ", Integer.toString(lID));

                File source = new File("/data/data/com.xsens.dot.android.example/databases/USA_table.db");
                File destination = new File("/storage/self/primary/Android/data/com.xsens.dot.android.example/files/DataBase/USA_table.db");

                try {
                    copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
/*
                String dbFilePath = "/storage/self/primary/Android/data/com.xsens.dot.android.example/files/DataBase/USA_table.db";
                String csvFilePath = "/storage/self/primary/Android/data/com.xsens.dot.android.example/files/DataBase/USA_table.csv";

                try {
                    // Connect to the SQLite database
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

                    // Create a SQL statement and execute query
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM CORE_TABLE");

                    // Create a CSV file for writing
                    FileWriter csvWriter = new FileWriter(csvFilePath);

                    // Write header row if needed
                    csvWriter.append("ID,PROJ,SITE,CORE,SUBCORE,NOTE,AZ,AV,Z,LAT,LON,ALT,SUN,DIP_DIR,DIP,STRAT,DATE,TIME\n");

                    // Iterate through the result set and write to CSV
                    while (resultSet.next()) {
                        String id_csv = resultSet.getString("ID");
                        String proj_csv = resultSet.getString("PROJ");
                        String site_csv = resultSet.getString("SITE");
                        String core_csv = resultSet.getString("CORE");
                        String subcore_csv = resultSet.getString("SUBCORE");
                        String note_csv = resultSet.getString("NOTE");
                        String az_csv = resultSet.getString("AZ");
                        String av_csv = resultSet.getString("AV");
                        String z_csv = resultSet.getString("Z");
                        String lat_csv = resultSet.getString("LAT");
                        String lon_csv = resultSet.getString("LON");
                        String alt_csv = resultSet.getString("ALT");
                        String sun_csv = resultSet.getString("SUN");
                        String dip_dir_csv = resultSet.getString("DIP_DIR");
                        String dip_csv = resultSet.getString("DIP");
                        String strat_csv = resultSet.getString("STRAT");
                        String date_csv = resultSet.getString("DATE");
                        String time_csv = resultSet.getString("TIME");

                        // Write data to CSV
                        csvWriter.append(id_csv)
                                .append(",")
                                .append(proj_csv)
                                .append(",")
                                .append(site_csv)
                                .append(",")
                                .append(core_csv)
                                .append(",")
                                .append(subcore_csv)
                                .append(",")
                                .append(note_csv)
                                .append(",")
                                .append(az_csv)
                                .append(",")
                                .append(av_csv)
                                .append(",")
                                .append(z_csv)
                                .append(",")
                                .append(lat_csv)
                                .append(",")
                                .append(lon_csv)
                                .append(",")
                                .append(alt_csv)
                                .append(",")
                                .append(sun_csv)
                                .append(",")
                                .append(dip_dir_csv)
                                .append(",")
                                .append(dip_csv)
                                .append(",")
                                .append(strat_csv)
                                .append(",")
                                .append(date_csv)
                                .append(",")
                                .append(time_csv)
                                .append("\n");
                    }

                    // Close resources
                    csvWriter.flush();
                    csvWriter.close();
                    resultSet.close();
                    statement.close();
                    connection.close();

                    System.out.println("Conversion completed successfully.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
                TableFragment tableFragment = new TableFragment();
                //getFragmentManager().beginTransaction().replace(R.id.container, tableFragment).addToBackStack(null).commit();

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
        // Notify main activity to refresh menu.
        MainActivity.sCurrentFragment = FRAGMENT_TAG_DATA;
        if (getActivity() != null) getActivity().invalidateOptionsMenu();
        DataBaseHelper databsHelp = new DataBaseHelper(getContext().getApplicationContext());

        String lastCoreString = databsHelp.LastCoreEntry();
        String lastSubCoreString = databsHelp.LastSubCoreEntry();
        String lastCoreAndSubcore = lastCoreString + lastSubCoreString;
        coreId.setText(lastCoreAndSubcore);
        coreId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);


        String lastSiteString = databsHelp.LastSiteEntry();
        siteName.setText(String.valueOf(lastSiteString));

        String projectName = databsHelp.LastProjectNameEntry();
        Log.i("MY CORE", projectName);
        ProjectNameData.setText(projectName);
        ProjectNameData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
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