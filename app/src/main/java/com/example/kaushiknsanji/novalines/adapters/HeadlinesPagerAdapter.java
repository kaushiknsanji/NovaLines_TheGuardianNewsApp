package com.example.kaushiknsanji.novalines.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Kaushik N Sanji on 10-Jan-18.
 */

public class HeadlinesPagerAdapter extends FragmentStatePagerAdapter {

    //Sparse Array to keep track of the registered fragments in memory
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    //Sparse Array to save the list of ViewPager Fragments supplied
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    //Sparse Array to save the title of the Fragments supplied
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();

    //Stores a reference to the FragmentManager used
    private FragmentManager mFragmentManager;

    /**
     * Constructor of the FragmentStatePagerAdapter {@link HeadlinesPagerAdapter}
     *
     * @param fm is the FragmentManager to be used for managing the Fragments
     */
    public HeadlinesPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    /**
     * Method that adds/loads Fragments to the ViewPager's Adapter
     *
     * @param fragment is the Fragment to be added at the position specified
     * @param tabTitle is the Title to be displayed on the Tab of the Fragment
     * @param position is the Integer value of the Fragment View's position in the Adapter
     */
    public void addFragment(Fragment fragment, String tabTitle, int position) {
        mFragmentList.add(position, fragment);
        mFragmentTitleList.add(position, tabTitle);
    }

    /**
     * Method that returns the position of the Fragment based on its Title
     *
     * @param tabTitle is the Title displayed on the Tab of the Fragment
     * @return Integer value of the Fragment View's position in the Adapter.
     * <br/>-1 when the title is not present in the list
     */
    public int getItemPositionByTitle(String tabTitle) {
        return mFragmentTitleList.indexOf(tabTitle);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position is the Integer value of the Fragment View's position in the Adapter
     */
    @Override
    public Fragment getItem(int position) {
        //Returning the Fragment at the position
        return mFragmentList.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    //Registers the Fragment when the item is instantiated (for the first time) using #getItem
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    //Unregisters the Fragment when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.delete(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Returns the registered fragment at the position
     *
     * @param position is the Integer value of the Fragment View's position in the Adapter
     * @return Instance of the Active Fragment at the position if present; else Null
     */
    @Nullable
    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    /**
     * Overriding to restore the state of Registered Fragments array
     *
     * @param state  is the Parcelable state
     * @param loader is the ClassLoader required for restoring the state
     */
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        if (state != null) {
            //When the state is present
            Bundle bundle = (Bundle) state;
            //Setting the ClassLoader passed, onto the Bundle
            bundle.setClassLoader(loader);

            //Retrieving the keys used in Bundle
            Set<String> keyStringSet = bundle.keySet();
            //Iterating over the Keys to find the Fragments
            for (String keyString : keyStringSet) {
                if (keyString.startsWith("f")) {
                    //Fragment keys starts with 'f' followed by its position index
                    int position = Integer.parseInt(keyString.substring(1));
                    //Getting the Fragment from the Bundle using the Key through the FragmentManager
                    Fragment fragment = mFragmentManager.getFragment(bundle, keyString);
                    if (fragment != null) {
                        //If Fragment is valid, then update the Sparse Array of Registered Fragments
                        mRegisteredFragments.put(position, fragment);
                    }
                }
            }
        }
    }

}