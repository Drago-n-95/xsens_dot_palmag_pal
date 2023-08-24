package com.xsens.dot.android.example.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.views.BeddingFragment;
import com.xsens.dot.android.example.views.CoreInfoClass;
import com.xsens.dot.android.example.views.DataFragment;
import com.xsens.dot.android.example.views.MainActivity;
import com.xsens.dot.android.example.views.TableFragment;
import com.xsens.dot.android.example.views.TableStratFragment;

import java.text.BreakIterator;
import java.util.List;

public class StratInfoAdapter extends RecyclerView.Adapter<StratInfoAdapter.MyViewHolder> {

    List<CoreInfoClass> coreInfoList;
    Context context;
    private TableStratFragment tableStratFragment;

    public StratInfoAdapter(List<CoreInfoClass> coreInfoList, Context context, TableStratFragment tableStratFragment) {
        this.coreInfoList = coreInfoList;
        this.context = context;
        this.tableStratFragment = tableStratFragment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_line_layout_strat, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.id.setText(coreInfoList.get(position).toString());

        holder.alt.setText(coreInfoList.get(position).getAltitude());
        holder.dip_dir.setText(coreInfoList.get(position).getDip_Dir());
        holder.dip.setText(coreInfoList.get(position).getDip());
        holder.lat.setText(coreInfoList.get(position).getLatitude());
        holder.lon.setText(coreInfoList.get(position).getLongitude());
        holder.bed.setText(coreInfoList.get(position).getStrat());

    }

    @Override
    public int getItemCount() {
        return coreInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id, core, az, av, sun, zrot, strat;
        TextView bed, dip_dir, dip, lat, lon, alt;
        ConstraintLayout parentLayout;
        TabLayout RowOneTabs;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_id_space);
            parentLayout = itemView.findViewById(R.id.one_line_layout_strat);

            bed = itemView.findViewById(R.id.tv_bed);
            dip_dir = itemView.findViewById(R.id.tv_dip_dir);
            dip = itemView.findViewById(R.id.tv_dip);
            lat = itemView.findViewById(R.id.tv_lat);
            lon = itemView.findViewById(R.id.tv_lon);
            alt = itemView.findViewById(R.id.tv_alt);
            AppCompatActivity activity = (AppCompatActivity) itemView.getContext();


            RowOneTabs = activity.findViewById(R.id.row1_tabLayout);



        }
    }
}
