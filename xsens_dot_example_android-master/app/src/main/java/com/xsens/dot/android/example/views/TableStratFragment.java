package com.xsens.dot.android.example.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.adapters.CoreInfoAdapter;
import com.xsens.dot.android.example.adapters.StratInfoAdapter;
import com.xsens.dot.android.example.apps.XsensDotApplication;
import com.xsens.dot.android.example.databinding.FragmentDataBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableStratFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableStratFragment extends Fragment implements CoreInfoAdapter.OnTabSelectedListener {

    private static final int MODE_PRIVATE = 0;
    private FragmentDataBinding mBinding;
    private ArrayList<CoreInfoClass> CoreList;
    private TextView northText, eastText, zText, latText, lonText, CoreText, SubcoreText, ProjectNameText, NoteText, SunReadingText;
    public String northValue, eastValue, zValue, latValue, lonValue, coreValue, subcoreValue, projectValue, noteValue, sunValue;
    public List<CoreInfoClass> coreInfoList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TableStratFragment tableStratFragment;
    DataBaseHelper dataBaseHelper;
    Button BtnGoBack;
    List<CoreInfoClass> cores;
    TabLayout RowOneTabs;

    public TableStratFragment() {
        // Required empty public constructor
    }


    public static TableStratFragment newInstance() {
        TableStratFragment fragment = new TableStratFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View thisView = inflater.inflate(R.layout.fragment_table_strat, container, false);

        recyclerView = thisView.findViewById(R.id.lv_dataList2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        //coreInfoList = XsensDotApplication.getCoreList();
        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        //XsensDotApplication.setCoreList(cores);
        //Log.i("Check strat", cores.get());

        //TableStratFragment tableStratFragment = new TableStratFragment();

        mAdapter = new StratInfoAdapter(cores, getContext(), tableStratFragment);
        recyclerView.setAdapter(mAdapter);

        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        XsensDotApplication.setCoreList(cores);

        //parentLayout = thisView.findViewById(R.id.oneLineLayout);
        RowOneTabs = getActivity().findViewById(R.id.row1_tabLayout);

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