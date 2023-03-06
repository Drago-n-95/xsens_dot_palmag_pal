package com.xsens.dot.android.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.xsens.dot.android.example.R;
import com.xsens.dot.android.example.views.CoreInfoClass;

import java.util.List;

public class CoreInfoAdapter extends RecyclerView.Adapter<CoreInfoAdapter.MyViewHolder> {

    List<CoreInfoClass> coreInfoList;
    Context context;

    public CoreInfoAdapter(List<CoreInfoClass> coreInfoList, Context context) {
        this.coreInfoList = coreInfoList;
        this.context = context;
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

        holder.id.setText(coreInfoList.get(position).toString());

        holder.core.setText(coreInfoList.get(position).getCore());
        holder.az.setText(coreInfoList.get(position).getZdir());
        holder.av.setText(coreInfoList.get(position).getEast());
        holder.sun.setText(coreInfoList.get(position).getNorth());
        holder.zrot.setText(coreInfoList.get(position).getLatitude());
        holder.strat.setText(coreInfoList.get(position).getLongitude());
/*
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                DataAdapter dataFragment = new DataAdapter();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, dataFragment).addToBackStack(null).commit();

            }
        });
 */
    }

    @Override
    public int getItemCount() {
        return coreInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, core, az, av, sun, zrot, strat;
        ConstraintLayout parentLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tv_id_space);
            parentLayout = itemView.findViewById(R.id.oneLineLayout);

            core = itemView.findViewById(R.id.tv_core_space);
            az = itemView.findViewById(R.id.tv_az_space);
            av = itemView.findViewById(R.id.tv_av_space);
            sun = itemView.findViewById(R.id.tv_zrot_space);
            zrot = itemView.findViewById(R.id.tv_zrot_space);
            strat = itemView.findViewById(R.id.tv_strat_space);

        }
    }
}
