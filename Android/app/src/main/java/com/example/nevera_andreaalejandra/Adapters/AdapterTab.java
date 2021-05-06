package com.example.nevera_andreaalejandra.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.nevera_andreaalejandra.Fragments.Listas.ListaCongelador_Fragment;
import com.example.nevera_andreaalejandra.Fragments.Listas.ListaNevera_Fragment;

public class AdapterTab extends FragmentStatePagerAdapter {

    private int numberOfTabs;
    private ListaNevera_Fragment nevera = new ListaNevera_Fragment();
    private ListaCongelador_Fragment congelador = new ListaCongelador_Fragment();

    public AdapterTab(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return nevera;
            case 1:
                return congelador;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}