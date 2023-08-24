package com.xsens.dot.android.example.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xsens.dot.android.example.R;

import java.util.ArrayList;
import java.util.List;

public class MfmAdapter extends BaseAdapter {

    private List<MfmData> mfmDataList;
    private LayoutInflater inflater;

    public MfmAdapter(Context context) {
        mfmDataList = new ArrayList<MfmData>();
        inflater = LayoutInflater.from(context);
    }

    public void updateData(List<MfmData> data) {
        mfmDataList.clear();
        mfmDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mfmDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mfmDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mfm, parent, false);
            holder = new ViewHolder();
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.percentageTextView = convertView.findViewById(R.id.percentageTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MfmData data = mfmDataList.get(position);
        holder.progressBar.setProgress(data.getProgressPercentage());
        holder.percentageTextView.setText(data.getProgressPercentage() + "%");

        return convertView;
    }

    public void updatePercentage(String address, int percentage) {
        for (MfmData data : mfmDataList) {
            if (data.getAddress().equals(address)) {
                data.setProgressPercentage(percentage);
                updateData(mfmDataList);
                break;
            }
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ProgressBar progressBar;
        TextView percentageTextView;
    }
}

