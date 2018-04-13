package com.example.kaushiknsanji.novalines.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Set;

/**
 * Custom {@link FragmentStatePagerAdapter} that provides the appropriate Fragment
 * for the ViewPager in {@link com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment}
 *
 * @author Kaushik N Sanji
 */
public class HeadlinesPagerAdapter extends FragmentStatePagerAdapter {

    //Constant used for logs
    private static final String LOG_TAG = HeadlinesPagerAdapter.class.getSimpleName();

    //Sparse Array to keep track of the registered fragments in memory
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();

    //List to store the ViewPager Fragments supplied
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    //List to save the title of the Fragments supplied
    private ArrayList<String> mFragmentTitleList = new ArrayList<>();

    //Stores a reference to the FragmentManager used
    private FragmentManager mFragmentManager;

    //Stores the Fragment currently set as Primary item to be shown
    private Fragment mPrimaryFragment;
    //Stores the position of the Fragment currently set as Primary item
    private int mPrimaryFragmentPos;

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
     * Called when the host view is attempting to determine if an item's position
     * has changed. Returns {@link #POSITION_UNCHANGED} if the position of the given
     * item has not changed or {@link #POSITION_NONE} if the item is no longer present
     * in the adapter.
     *
     * @param object Object representing an item, previously returned by a call to
     *               {@link #instantiateItem(ViewGroup, int)}.
     * @return object's new position index from [0, {@link #getCount()}),
     * {@link #POSITION_UNCHANGED} if the object's position has not changed,
     * or {@link #POSITION_NONE} if the item is no longer present.
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        //Casting to a Fragment (assuming that it is always a Fragment)
        Fragment fragment = (Fragment) object;

        if (fragment.equals(mPrimaryFragment)) {
            //When the Primary Item Fragment is same as the Fragment being checked for position change
            if (mPrimaryFragmentPos == mFragmentList.indexOf(fragment)) {
                //Returning POSITION_UNCHANGED when the positions are same
                return POSITION_UNCHANGED;
            } else {
                //Returning POSITION_NONE to reload the Fragment when the positions are different
                return POSITION_NONE;
            }
        } else if (mRegisteredFragments.indexOfValue(fragment) > -1) {
            //Returning POSITION_UNCHANGED when not a Fragment of Primary Item
            //and is present in the saved fragments list
            return POSITION_UNCHANGED;
        }
        //On all else, Returning POSITION_NONE to reload the Fragment
        return POSITION_NONE;
    }

    /**
     * Called to inform the adapter of which item is currently considered to
     * be the "primary", that is the one to show the user as the current page.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position that is now the primary.
     * @param object    The same object that was returned by {@link #instantiateItem(ViewGroup, int)}.
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        //Saving the Primary Item Fragment shown
        mPrimaryFragment = (Fragment) object;
        //Saving the position of Primary Item Fragment
        mPrimaryFragmentPos = position;
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
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
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