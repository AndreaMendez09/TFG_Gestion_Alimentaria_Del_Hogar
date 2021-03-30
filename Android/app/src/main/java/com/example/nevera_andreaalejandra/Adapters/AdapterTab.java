package com.example.nevera_andreaalejandra.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.nevera_andreaalejandra.Fragments.Listas.ListaCongelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Listas.ListaNevera_Fragment;

public class AdapterTab extends FragmentStatePagerAdapter {

    private int numberOfTabs;


    public AdapterTab(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ListaNevera_Fragment();
            case 1:
                return new ListaCongelador_Fragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}