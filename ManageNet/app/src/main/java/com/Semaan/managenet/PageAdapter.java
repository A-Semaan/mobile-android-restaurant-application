package com.Semaan.managenet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import DataClasses.MenuObject;

public class PageAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    HashMap<String,ArrayList<MenuObject>> menu;
    Context mContext;
    public PageAdapter(Context context, FragmentManager fm, int NumOfTabs, HashMap<String, ArrayList<MenuObject>> menu) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.menu=menu;
        mContext=context;
    }




    @Override
    public Fragment getItem(int position) {

        /*switch (position) {
            case 0:
                AllTasksTabFragment tab1 = new AllTasksTabFragment();
                return tab1;
            case 1:
                WaitingTasksTabFragment tab2 = new WaitingTasksTabFragment();
                return tab2;
            default:
                return null;
        }*/

        Iterator<String> it= menu.keySet().iterator();;
        int counter=0;
        while(counter++<position){
            it.next();
        }
        String positionTitle=it.next();
        MenuArrayAdapter adapter = new MenuArrayAdapter(mContext,R.layout.menu_object_list_row,menu.get(positionTitle));
        Fragment fragment = new CustomFragement();
        Bundle b = new Bundle();
        b.putSerializable("adapter",adapter);

        fragment.setArguments(b);
        return fragment;

    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
