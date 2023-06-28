package com.xsens.dot.android.example.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.adapters.CoreInfoAdapter;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.databinding.FragmentDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableFragment extends Fragment implements CoreInfoAdapter.OnTabSelectedListener {


    private static final int MODE_PRIVATE = 0;
    private FragmentDataBinding mBinding;
    private ArrayList<CoreInfoClass> CoreList;
    private TextView northText, eastText, zText, latText, lonText, CoreText, SubcoreText, ProjectNameText, NoteText, SunReadingText;
    public String northValue, eastValue, zValue, latValue, lonValue, coreValue, subcoreValue, projectValue, noteValue, sunValue;
    public List<CoreInfoClass> coreInfoList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DataBaseHelper dataBaseHelper;
    Button BtnGoBack;
    List<CoreInfoClass> cores;
    ConstraintLayout parentLayout;
    public TabLayout RowOneTabs;
    private TableFragment tableFragment;

    public TableFragment() {

    }

    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        return fragment;
    }
/*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        XsensDotApplication xsensDotApplication = (XsensDotApplication) getActivity().getApplication();

    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View thisView = inflater.inflate(R.layout.fragment_table, container, false);
        recyclerView = thisView.findViewById(R.id.lv_dataList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        coreInfoList = XsensDotApplication.getCoreList();

        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        XsensDotApplication.setCoreList(cores);

        mAdapter = new CoreInfoAdapter(cores, getContext(), tableFragment);
        recyclerView.setAdapter(mAdapter);

        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        XsensDotApplication.setCoreList(cores);

        parentLayout = thisView.findViewById(R.id.oneLineLayout);
        RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);

/*
        northValue = getArguments().getString("DataNorthKey");
        eastValue = getArguments().getString("DataEastKey");
        zValue = getArguments().getString("DataZKey");
        latValue = getArguments().getString("DataLatKey");
        lonValue = getArguments().getString("DataLonKey");

        coreValue = getArguments().getString("DataCoreKey");
        subcoreValue = getArguments().getString("DataSubcoreKey");
        projectValue = getArguments().getString("DataProjectNameKey");
        noteValue = getArguments().getString("DataNoteKey");
        sunValue = getArguments().getString("DataSunReadingKey");

 */
        //fillCoreInfoList();

        //Toast.makeText(getContext(), "Count = " + coreInfoList.size() + coreInfoList.get(0).getCore(), Toast.LENGTH_SHORT).show();

        BtnGoBack = thisView.findViewById(R.id.btn_GoBack);
        BtnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TabLayout.Tab coreTabMeasure = RowOneTabs.getTabAt(1);
                coreTabMeasure.select();

            }
        });

        return thisView;
    }

/*
    private void fillCoreInfoList() {
        CoreInfoClass core1 = new CoreInfoClass(0, northValue, eastValue, zValue, latValue, lonValue, coreValue, subcoreValue, noteValue, projectValue, sunValue);

        coreInfoList.addAll(Arrays.asList(new CoreInfoClass[] {core1}));
    }

 */

    @Override
    public void onResume() {
        super.onResume();

        /*
        northText = getView().findViewById(R.id.northData);
        northText.setText(northValue);
        eastText = getView().findViewById(R.id.eastData);
        eastText.setText(eastValue);
        zText = getView().findViewById(R.id.zData);
        zText.setText(zValue);
        latText = getView().findViewById(R.id.LatData);
        latText.setText(latValue);
        lonText = getView().findViewById(R.id.LonData);
        lonText.setText(lonValue);

        CoreText = getView().findViewById(R.id.core_text);
        CoreText.setText(coreValue);

        SubcoreText.setText(subcoreValue);
        ProjectNameText.setText(projectValue);
        NoteText.setText(noteValue);
        SunReadingText.setText(sunValue);
         */

    }

    @Override
    public void onTabSelected(int position){
        TabLayout RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);
        TabLayout.Tab taby = RowOneTabs.getTabAt(position);
        if (taby != null) {
            taby.select();
        }
    }

}