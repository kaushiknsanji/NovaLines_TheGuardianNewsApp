package com.example.kaushiknsanji.novalines;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.kaushiknsanji.novalines.adapters.NavRecyclerAdapter;
import com.example.kaushiknsanji.novalines.cache.BitmapImageCache;
import com.example.kaushiknsanji.novalines.drawerviews.BookmarksFragment;
import com.example.kaushiknsanji.novalines.drawerviews.FavoritesFragment;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.drawerviews.RandomNewsFragment;
import com.example.kaushiknsanji.novalines.models.NavDrawerItem;
import com.example.kaushiknsanji.novalines.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The Main Activity of the App that inflates the layout 'R.layout.activity_news'
 * containing a DrawerLayout that displays a Navigation Drawer with the Main content.
 * <p>
 * Responsible for initializing the custom Navigation Drawer implemented using
 * a RecyclerView, and handling the click actions on the Drawer items.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class NewsActivity extends AppCompatActivity
        implements NavRecyclerAdapter.OnNavAdapterItemClickListener {

    //Constant used for Logs
    private static final String LOG_TAG = NewsActivity.class.getSimpleName();
    //Bundle Key constants used for saving/restoring the state
    private static final String SELTD_NAV_ITEM_TITLE_STR_KEY = "NavDrawer.SelectedItemTitle";
    private static final String SELTD_NAV_FRAGMENT_ITEM_TITLE_STR_KEY = "NavDrawer.SelectedFragmentItemTitle";
    //For the RecyclerView of the Navigation Drawer
    private RecyclerView mNavRecyclerView;
    //For the Adapter of the Navigation Drawer RecyclerView
    private NavRecyclerAdapter mNavRecyclerAdapter;
    //For the Drawer layout
    private DrawerLayout mDrawerLayout;
    //For the ActionBarDrawerToggle
    private ActionBarDrawerToggle mDrawerToggle;
    //For storing the Navigation item currently selected
    private String mSelectedNavItemTitle;
    //For storing the Navigation Fragment item currently/previously selected
    private String mSelectedNavFragmentItemTitle;

    //Initializing FragmentManager's FragmentLifecycleCallbacks
    //for setting up the Drawer Toggle for each Fragment containing custom Toolbar
    private FragmentManager.FragmentLifecycleCallbacks
            mFragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {

        /**
         * Called after the fragment has returned from the FragmentManager's call to
         * {@link Fragment#onActivityCreated(Bundle)}. This will only happen once for any given
         * fragment instance, though the fragment may be attached and detached multiple times.
         *
         * @param fm                 Host FragmentManager
         * @param fragment           Fragment changing state
         * @param savedInstanceState Saved instance bundle from a previous instance
         */
        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment fragment, Bundle savedInstanceState) {
            super.onFragmentActivityCreated(fm, fragment, savedInstanceState);

            if (fragment instanceof HeadlinesFragment
                    || fragment instanceof RandomNewsFragment
                    || fragment instanceof BookmarksFragment
                    || fragment instanceof FavoritesFragment) {
                //When the Fragment is an instance of one of the Fragments used by the Navigation Drawer,
                //setup the Drawer Toggle with the Toolbar loaded by the Fragment
                setupDrawerToggle((Toolbar) findViewById(R.id.toolbar_id));
            }
        }
    };

    //Called when the activity is to be created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //Initializing the Preferences
        setupPreferences();

        //Finding the RecyclerView for the Navigation Drawer
        mNavRecyclerView = findViewById(R.id.nav_recycler_view_id);
        setupNavigationDrawer();

        //Finding the Drawer layout
        mDrawerLayout = findViewById(R.id.drawer_root);

        if (savedInstanceState == null) {
            //On Initial Launch of the App

            //Load the HeadlinesFragment by default
            openNavItemByTitle(getString(R.string.headlines_title_str));
        }
    }

    /**
     * Method that initializes the Preferences used by the app
     */
    private void setupPreferences() {
        //Retrieving the SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Manually defaulting the value of 'from-date' if not set: START
        String fromDateKeyStr = getString(R.string.pref_start_period_manual_key);
        if (sharedPreferences.getLong(fromDateKeyStr, 0) == 0) {
            //When not defaulted, setting the value to the current day date
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(fromDateKeyStr, Calendar.getInstance().getTimeInMillis());
            editor.apply(); //Applying the update
        }
        //Manually defaulting the value of 'from-date' if not set: END

        //Loading the default values for the Preferences on the first Initial launch after install
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /**
     * This method is called after {@link #onStart} when the activity is
     * being re-initialized from a previously saved state, given here in
     * <var>savedInstanceState</var>.
     * <p>This method is called between {@link #onStart} and
     * {@link #onPostCreate}.
     *
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     * @see #onCreate
     * @see #onPostCreate
     * @see #onResume
     * @see #onSaveInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Restoring the Item Title of the Drawer Fragment Item last shown
        mSelectedNavFragmentItemTitle = savedInstanceState.getString(SELTD_NAV_FRAGMENT_ITEM_TITLE_STR_KEY);

        //Retrieving the Title of the Item previously selected
        mSelectedNavItemTitle = savedInstanceState.getString(SELTD_NAV_ITEM_TITLE_STR_KEY);

        if (!mSelectedNavItemTitle.equals(getString(R.string.settings_title_str))
                && !mSelectedNavItemTitle.equals(getString(R.string.about_title_str))) {
            //Relaunching only when the Item previously selected was Not an Activity

            //Reload the Drawer Item Fragment last shown
            openNavItemByTitle(mSelectedNavItemTitle);
        }

    }

    /**
     * Called to retrieve per-instance state from an activity before being killed
     * so that the state can be restored in {@link #onCreate} or
     * {@link #onRestoreInstanceState} (the {@link Bundle} populated by this method
     * will be passed to both).
     * <p>
     * <p>This method is called before an activity may be killed so that when it
     * comes back some time in the future it can restore its state.
     * <p>
     * <p>If called, this method will occur before {@link #onStop}.  There are
     * no guarantees about whether it will occur before or after {@link #onPause}.
     *
     * @param outState Bundle in which to place your saved state.
     * @see #onCreate
     * @see #onRestoreInstanceState
     * @see #onPause
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //Saving the Item Title of the Drawer Item last shown
        outState.putString(SELTD_NAV_ITEM_TITLE_STR_KEY, mSelectedNavItemTitle);
        //Saving the Item Title of the Drawer Fragment Item last shown
        outState.putString(SELTD_NAV_FRAGMENT_ITEM_TITLE_STR_KEY, mSelectedNavFragmentItemTitle);

        super.onSaveInstanceState(outState);
    }

    /**
     * Called when activity start-up is complete (after {@link #onStart}
     * and {@link #onRestoreInstanceState} have been called).
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onCreate
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //Finding the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_id);
        if (toolbar != null) {
            //Setting up the Drawer Toggle Listener when Toolbar is present
            setupDrawerToggle(toolbar);
        }

    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Registering the FragmentLifecycleCallbacks
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, false);
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            //Clearing the Bitmap Cache when the activity is finishing
            BitmapImageCache.clearCache();
        }

        //UnRegistering the FragmentLifecycleCallbacks
        getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
    }

    /**
     * Method that initializes the {@link ActionBarDrawerToggle} with the {@link Toolbar} passed
     * to load the Hamburger icon, and to attach with the events of {@link DrawerLayout}
     *
     * @param toolbar is the {@link Toolbar} to which the Drawer Toggle needs to be setup with
     */
    private void setupDrawerToggle(Toolbar toolbar) {
        Log.d(LOG_TAG, "setupDrawerToggle: Started");
        if (mDrawerToggle != null) {
            //Removing any old Drawer Listener if present
            mDrawerLayout.removeDrawerListener(mDrawerToggle);
            mDrawerToggle = null;
        }

        //Initializing the ActionBarDrawerToggle
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        //Sync the toggle state of the Drawer
        mDrawerToggle.syncState();
        //Attaching DrawerLayout events to ActionBarDrawerToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    /**
     * Called by the system when the device configuration changes while your
     * activity is running.
     *
     * @param newConfig The new device configuration.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Passing any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            //Returning true when the Drawer Toggle handles the event
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Method that initializes the Layout Manager, Adapter and Listeners
     * for the RecyclerView of the Navigation Drawer
     */
    private void setupNavigationDrawer() {
        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting the LayoutManager on the RecyclerView
        mNavRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing the Adapter for NavDrawerItem Objects
        mNavRecyclerAdapter = new NavRecyclerAdapter(this, prepareNavDrawerItems());

        //Registering the OnNavAdapterItemClickListener on the Adapter
        mNavRecyclerAdapter.setOnNavAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mNavRecyclerView.setAdapter(mNavRecyclerAdapter);
    }

    /**
     * Method that prepares and returns a list of {@link NavDrawerItem} objects
     * which are the Drawer Items to be displayed through the Adapter of RecyclerView
     *
     * @return ArrayList of {@link NavDrawerItem} objects
     */
    private @NonNull
    ArrayList<NavDrawerItem> prepareNavDrawerItems() {
        //Initializing an ArrayList for NavDrawerItem
        ArrayList<NavDrawerItem> navDrawerItemsList = new ArrayList<>();

        //Item 0 is the Navigation Header
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_header, -1, ""));
        //Item 1 is the Headlines Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_newspaper_selector, getString(R.string.headlines_title_str)));
        //Item 2 is the Random News Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_search_selector, getString(R.string.random_news_title_str)));
        //Item 3 is the Bookmarked News Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_bookmark_selector, getString(R.string.bookmarked_news_title_str)));
        //Item 4 is the Favorited News Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_favorite_selector, getString(R.string.favorites_title_str)));
        //Item 5 is the Divider
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item_divider, -1, ""));
        //Item 6 is the Settings Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_settings_accent, getString(R.string.settings_title_str)));
        //Item 7 is the About Menu
        navDrawerItemsList.add(new NavDrawerItem(R.layout.nav_item, R.drawable.ic_about_accent, getString(R.string.about_title_str)));

        //Returning the prepared list of NavDrawerItem
        return navDrawerItemsList;
    }

    /**
     * Method invoked when an Item on the Adapter is clicked
     *
     * @param navDrawerItem is the corresponding {@link NavDrawerItem} object of the item view
     *                      clicked in the Adapter
     */
    @Override
    public void onNavItemClick(NavDrawerItem navDrawerItem) {
        //Saving the Title of the Item Selected
        mSelectedNavItemTitle = navDrawerItem.getItemTitle();
        //Launching the corresponding Navigation Drawer Item
        openNavItem();
    }

    /**
     * Method invoked by {@link #onNavItemClick(NavDrawerItem)}
     * to load the corresponding Navigation Drawer Item
     */
    private void openNavItem() {
        if (mSelectedNavItemTitle.equals(getString(R.string.headlines_title_str))) {
            //When Headlines Navigation Menu is clicked
            openHeadlines(); //Launching the Headlines Fragment
            //Saving the Title to another member to keep track of the last fragment launched
            mSelectedNavFragmentItemTitle = mSelectedNavItemTitle;

        } else if (mSelectedNavItemTitle.equals(getString(R.string.random_news_title_str))) {
            //When Random News Navigation Menu is clicked
            openRandomNews(); //Launching the Random News Fragment
            //Saving the Title to another member to keep track of the last fragment launched
            mSelectedNavFragmentItemTitle = mSelectedNavItemTitle;

        } else if (mSelectedNavItemTitle.equals(getString(R.string.bookmarked_news_title_str))) {
            //When Bookmarked News Navigation Menu is clicked
            openBookmarkedNews(); //Launching the Bookmarked News Fragment
            //Saving the Title to another member to keep track of the last fragment launched
            mSelectedNavFragmentItemTitle = mSelectedNavItemTitle;

        } else if (mSelectedNavItemTitle.equals(getString(R.string.favorites_title_str))) {
            //When Favorited News Navigation Menu is clicked
            openFavoritedNews(); //Launching the Favorited News Fragment
            //Saving the Title to another member to keep track of the last fragment launched
            mSelectedNavFragmentItemTitle = mSelectedNavItemTitle;

        } else if (mSelectedNavItemTitle.equals(getString(R.string.settings_title_str))) {
            //When Settings Navigation Menu is clicked
            openSettings();

        } else if (mSelectedNavItemTitle.equals(getString(R.string.about_title_str))) {
            //When "About" Navigation Menu is clicked
            openAbout();
        }

        //Closing the Drawer if opened
        mDrawerLayout.closeDrawers();
    }

    /**
     * Method that loads the Drawer Item based on the Item Title string being passed
     *
     * @param itemTitle The String which is the Title of the Drawer Item to be loaded
     */
    private void openNavItemByTitle(String itemTitle) {
        //Saving the Title value for future reloading of the Navigation Item
        mSelectedNavItemTitle = itemTitle;
        //Marking the Drawer Item as selected
        mNavRecyclerAdapter.setSelectedItemByTitle(mSelectedNavItemTitle);
        //Launching the Navigation Drawer Item
        openNavItem();
    }

    /**
     * Method that loads the {@link HeadlinesFragment} when the Headlines Navigation Menu is
     * clicked/selected.
     */
    private void openHeadlines() {
        //Replacing any Fragment shown at 'R.id.fragment_frame_id' with a TAG
        replaceFragment(HeadlinesFragment.newInstance(), HeadlinesFragment.NAV_FRAGMENT_TAG);
    }

    /**
     * Method that loads the {@link RandomNewsFragment} when the Random News Navigation Menu is
     * clicked/selected.
     */
    private void openRandomNews() {
        //Replacing any Fragment shown at 'R.id.fragment_frame_id' with a TAG
        replaceFragment(RandomNewsFragment.newInstance(), RandomNewsFragment.NAV_FRAGMENT_TAG);
    }

    /**
     * Method that loads the {@link BookmarksFragment} when the Bookmarked News Navigation Menu is
     * clicked/selected
     */
    private void openBookmarkedNews() {
        //Replacing any Fragment shown at 'R.id.fragment_frame_id' with a TAG
        replaceFragment(BookmarksFragment.newInstance(), BookmarksFragment.NAV_FRAGMENT_TAG);
    }

    /**
     * Method that loads the {@link FavoritesFragment} when the Favorited News Navigation Menu is
     * clicked/selected
     */
    private void openFavoritedNews() {
        //Replacing any Fragment shown at 'R.id.fragment_frame_id' with a TAG
        replaceFragment(FavoritesFragment.newInstance(), FavoritesFragment.NAV_FRAGMENT_TAG);
    }

    /**
     * Method that loads the {@link SettingsActivity} when the Settings Navigation Menu is
     * clicked/selected.
     */
    private void openSettings() {
        //Creating an explicit intent to SettingsActivity
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        //Launching the Activity with a Request Code
        startActivityForResult(settingsIntent, SettingsActivity.REQ_CODE);
    }

    /**
     * Method that loads the {@link AboutActivity} when the About Navigation Menu is
     * clicked/selected.
     */
    private void openAbout() {
        //Creating an explicit intent to AboutActivity
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        //Launching the Activity with a Request Code
        startActivityForResult(aboutIntent, AboutActivity.REQ_CODE);
    }

    /**
     * Method that replaces the Fragment at 'R.id.fragment_frame_id' with the Fragment and its Tag passed.
     * Prior to replacing, it checks whether the given Fragment is already present at 'R.id.fragment_frame_id'
     * or not.
     *
     * @param fragment is the instance of the Fragment that needs to be displayed at 'R.id.fragment_frame_id'
     * @param tag      is the String identifier used by the FragmentManager for checking the presence of the Fragment
     *                 prior to replacing the content with the Fragment
     */
    private void replaceFragment(Fragment fragment, String tag) {
        //Getting the Instance of the FragmentManager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag(tag) != null) {
            //If the Fragment is already being displayed, then close the Navigation Drawer
            mDrawerLayout.closeDrawers();
        } else {
            //When the Fragment given is not being displayed at 'R.id.fragment_frame_id'

            //Getting the FragmentTransaction
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            //Setting the animation for swapping out the Fragment
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            //Replacing the Fragment at 'R.id.fragment_frame_id' with the given Fragment and its Tag
            fragmentTransaction.replace(R.id.fragment_frame_id, fragment, tag).commit();
        }
    }

    /**
     * Called when an activity launched exits, giving the requestCode it was started with,
     * along with the resultCode it returned and any additional data from it passed as an Intent.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.REQ_CODE
                || requestCode == AboutActivity.REQ_CODE) {
            //When the request is for the Settings/About Activity
            if (resultCode == RESULT_OK) {
                //When the Result is OK

                //Copy the title of the last Fragment Drawer Item shown
                mSelectedNavItemTitle = mSelectedNavFragmentItemTitle;
                //Mark the Drawer Item as selected
                mNavRecyclerAdapter.setSelectedItemByTitle(mSelectedNavItemTitle);
                //If the Drawer is Open, then close the Drawer
                mDrawerLayout.closeDrawers();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            //If the Drawer is Open, then close the Drawer
            mDrawerLayout.closeDrawers();
            return;
        }

        if (!mSelectedNavItemTitle.equals(getString(R.string.headlines_title_str))) {
            //When the last Navigation Drawer Item shown is NOT the Headlines Navigation Menu,
            //then load the Headlines Fragment
            openNavItemByTitle(getString(R.string.headlines_title_str));
            return;
        }

        if (mSelectedNavItemTitle.equals(getString(R.string.headlines_title_str))) {
            //When the last Navigation Drawer Item shown is the Headlines Navigation Menu

            //Retrieving the fragment shown
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(HeadlinesFragment.NAV_FRAGMENT_TAG);
            if (fragment != null && fragment instanceof HeadlinesFragment) {
                //Casting to HeadlinesFragment
                HeadlinesFragment headlinesFragment = (HeadlinesFragment) fragment;

                //On Back Press, if the last page shown by the ViewPager of the Fragment
                //is not the first page (HighlightsFragment)
                //then reset the ViewPager to display the first page again
                if (headlinesFragment.getViewPager().getCurrentItem() > 0) {
                    headlinesFragment.getViewPager().setCurrentItem(0);
                    return;
                }
            }
        }

        super.onBackPressed();
    }
}
