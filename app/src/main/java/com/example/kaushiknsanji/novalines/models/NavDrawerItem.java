/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kaushiknsanji.novalines.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;

/**
 * Model class for storing the Navigation Drawer Item details
 * used by {@link com.example.kaushiknsanji.novalines.NewsActivity}
 * to setup the Navigation Drawer content.
 *
 * @author Kaushik N Sanji
 */
public class NavDrawerItem {

    //Stores a reference to the Layout resource
    private int mDrawerLayout;
    //Stores a reference to the Drawable resource
    private int mDrawerIcon;
    //Stores the title of the Drawer Item
    private String mItemTitle;

    /**
     * Constructor to initialize the {@link NavDrawerItem}
     *
     * @param drawerLayout is an integer reference to the Layout resource
     *                     to be used for the Drawer Item
     * @param drawerIcon   is an integer reference to the Drawable resource
     *                     to be used for the Drawer Item's icon
     * @param itemTitle    is a String containing the title to be used for the Drawer Item
     */
    public NavDrawerItem(@LayoutRes int drawerLayout, @DrawableRes int drawerIcon, String itemTitle) {
        mDrawerLayout = drawerLayout;
        mDrawerIcon = drawerIcon;
        mItemTitle = itemTitle;
    }

    /**
     * Method that returns the Drawable resource of the icon
     *
     * @return Integer for the Drawable resource of the icon
     */
    public int getDrawerIcon() {
        return mDrawerIcon;
    }

    /**
     * Setter Method for the Drawable resource of the icon to be used
     * for the Drawer Item
     *
     * @param drawerIcon is an integer reference to the Drawable resource
     *                   to be used for the Drawer Item's icon
     */
    public void setDrawerIcon(@DrawableRes int drawerIcon) {
        mDrawerIcon = drawerIcon;
    }

    /**
     * Method that returns the Title to be used for the Drawer item
     *
     * @return String for the Title to be used for the Drawer item
     */
    public String getItemTitle() {
        return mItemTitle;
    }

    /**
     * Setter Method for the Title to be used for the Drawer item
     *
     * @param itemTitle is a String containing the title to be used for the Drawer Item
     */
    public void setItemTitle(String itemTitle) {
        mItemTitle = itemTitle;
    }

    /**
     * Method that returns the Layout resource of the Drawer item
     *
     * @return Integer for the Layout resource of the Drawer item
     */
    public int getDrawerLayout() {
        return mDrawerLayout;
    }

    /**
     * Setter Method for the Layout resource of the Drawer item to be used
     *
     * @param drawerLayout is an integer reference to the Layout resource
     *                     to be used for the Drawer Item
     */
    public void setDrawerLayout(@LayoutRes int drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

}