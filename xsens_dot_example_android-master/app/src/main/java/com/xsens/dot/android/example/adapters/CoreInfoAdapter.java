package com.xsens.dot.android.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.views.BeddingFragment;
import com.xsens.dot.android.example.views.CoreInfoClass;
import com.xsens.dot.android.example.views.DataFragment;
import com.xsens.dot.android.example.views.MainActivity;
import com.xsens.dot.android.example.views.ScanFragment;
import com.xsens.dot.android.example.views.TableFragment;
import com.xsens.dot.android.example.views.TableStratFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.MissingFormatArgumentException;

public class CoreInfoAdapter extends RecyclerView.Adapter<CoreInfoAdapter.MyViewHolder> {

    List<CoreInfoClass> coreInfoList;
    Context context;
    private TableFragment tableFragment;
    private TabLayout RowOneTabs;
    private static final String TAG = DataFragment.class.getSimpleName();
    private long buttonPressTime;

    private AdapterDataListener mDataListener;

    public interface OnTabSelectedListener {
        void onTabSelected(int position);
    }

    public CoreInfoAdapter(List<CoreInfoClass> coreInfoList, Context context, TableFragment tableFragment) {
        this.coreInfoList = coreInfoList;
        this.context = context;
        this.tableFragment = tableFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String coreAndSubcore = coreInfoList.get(position).getCore() + coreInfoList.get(position).getSubcore();

        holder.id.setText(coreInfoList.get(position).toString());
        holder.core.setText(coreAndSubcore);
        holder.az.setText(coreInfoList.get(position).getNorth());
        holder.av.setText(coreInfoList.get(position).getEast());
        holder.sun.setText(coreInfoList.get(position).getSunReading());
        holder.zrot.setText(coreInfoList.get(position).getZdir());
        holder.strat.setText(coreInfoList.get(position).getStrat());
    }

    @Override
    public int getItemCount() {
        return coreInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, core, az, av, sun, zrot, strat;
        ConstraintLayout parentLayout;
        TabLayout RowOneTabs;


        public MyViewHolder(@NonNull View view) {
            super(view);

            id = view.findViewById(R.id.tv_id_space);
            parentLayout = view.findViewById(R.id.oneLineLayout);

            core = view.findViewById(R.id.tv_core_space);
            az = view.findViewById(R.id.tv_az_space);
            av = view.findViewById(R.id.tv_av_space);
            sun = view.findViewById(R.id.tv_sun_space);
            zrot = view.findViewById(R.id.tv_zrot_space);
            strat = view.findViewById(R.id.tv_strat_space);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            RowOneTabs = activity.findViewById(R.id.row1_tabLayout);

        }
    }


}
