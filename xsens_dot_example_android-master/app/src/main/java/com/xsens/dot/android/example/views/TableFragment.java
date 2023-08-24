package com.xsens.dot.android.example.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    //private FragmentDataBinding mBinding;
    private ArrayList<CoreInfoClass> CoreList;
    private TextView northText, eastText, zText, latText, lonText, CoreText, SubcoreText, ProjectNameText, NoteText, SunReadingText;
    public String northValue, eastValue, zValue, latValue, lonValue, coreValue, subcoreValue, projectValue, noteValue, sunValue;
    public List<CoreInfoClass> coreInfoList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DataBaseHelper dataBaseHelper;
    Button BtnGoBack;
    List<CoreInfoClass> cores, Cores;
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

        dataBaseHelper = new DataBaseHelper(getContext());
        cores = dataBaseHelper.getEverything();
        int lastPosition = dataBaseHelper.getEverything().size();
        dataBaseHelper.close();
        Log.i("POSITION RECYCLER", String.valueOf(lastPosition-1));
        mAdapter = new CoreInfoAdapter(cores, getContext(), tableFragment);
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(lastPosition);
        parentLayout = thisView.findViewById(R.id.oneLineLayout);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        /**/
        DataBaseHelper dBaseHelper = new DataBaseHelper(getContext());
        Cores = dBaseHelper.getEverything();
        dBaseHelper.close();
        CoreInfoAdapter bAdapter = new CoreInfoAdapter(Cores, getContext(), tableFragment);

        recyclerView.setAdapter(bAdapter);
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