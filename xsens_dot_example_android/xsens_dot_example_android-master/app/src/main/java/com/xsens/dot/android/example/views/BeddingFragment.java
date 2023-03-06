package com.xsens.dot.android.example.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.databinding.FragmentDataBinding;

import java.util.List;
import java.util.concurrent.Executor;


public class BeddingFragment extends Fragment {
    private Context bContext;
    private FragmentDataBinding mBinding;
    TextView Latitude, Longitude, Altitude;
    private FusedLocationProviderClient fusedLocationClient;
    public List<CoreInfoClass> coreInfoList;
    DataBaseHelper dataBaseHelper;
    List<CoreInfoClass> cores;

    public BeddingFragment() {
        // Required empty public constructor
    }

    public static BeddingFragment newInstance(String param1, String param2) {
        BeddingFragment fragment = new BeddingFragment();
        return fragment;
    }

    public static Fragment newInstance() {
        return new BeddingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //mBinding = FragmentDataBinding.inflate(LayoutInflater.from(getContext()));
        View view = inflater.inflate(R.layout.fragment_bedding, container, false);



        Latitude = view.findViewById(R.id.txt_lat);
        Longitude = view.findViewById(R.id.txt_lon);
        Altitude = view.findViewById(R.id.txt_alt);

        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        XsensDotApplication.setCoreList(cores);

        Latitude.setText(cores.get(0).getLatitude());
        Longitude.setText(cores.get(0).getLongitude());
        Altitude.setText(cores.get(0).getZdir());

        Toast.makeText(getContext(), "Count = " + cores.get(0).getZdir(), Toast.LENGTH_LONG).show();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}