package com.xsens.dot.android.example.adapters;

import android.inputmethodservice.Keyboard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.xsens.dot.android.example.views.BeddingFragment;
import com.xsens.dot.android.example.views.DataFragment;
import com.xsens.dot.android.example.views.ScanFragment;
import com.xsens.dot.android.example.views.TableFragment;
import com.xsens.dot.android.example.views.TableStratFragment;

public class MyViewPagerAdapter  extends FragmentStateAdapter {

    private DataFragment dataFragment;
    private BeddingFragment beddingFragment;
    private TableFragment tableFragment;
    private TableStratFragment tableStratFragment;
    TabLayout RowOneTabs;

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new ScanFragment();
            case 1:
                return new DataFragment();
            case 2:
                return new BeddingFragment();
            case 3:
                return new TableFragment();
            case 4:
                return new TableStratFragment();
            default:
                return null;

        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
