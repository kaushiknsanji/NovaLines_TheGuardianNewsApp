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

package com.example.kaushiknsanji.novalines.adapterviews;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.HighlightsAdapter;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.errorviews.NetworkErrorFragment;
import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;
import com.example.kaushiknsanji.novalines.utils.DateUtility;
import com.example.kaushiknsanji.novalines.utils.IntentUtility;
import com.example.kaushiknsanji.novalines.utils.Logger;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;
import com.example.kaushiknsanji.novalines.utils.RecyclerViewUtility;
import com.example.kaushiknsanji.novalines.workers.NewsHighlightsLoader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment that inflates the layout 'R.layout.highlights_layout'
 * containing the {@link RecyclerView} used in the {@link com.example.kaushiknsanji.novalines.adapters.HeadlinesPagerAdapter}
 * for the ViewPager shown in {@link HeadlinesFragment}.
 * <p>
 * Responsible for displaying the count of Articles for the News Feeds from the
 * subscribed News Categories/Sections.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class HighlightsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<NewsSectionInfo>>,
        SwipeRefreshLayout.OnRefreshListener,
        HighlightsAdapter.OnAdapterItemDataSwapListener,
        HighlightsAdapter.OnAdapterItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Constant used for logs
    private static final String LOG_TAG = HighlightsFragment.class.getSimpleName();

    //Bundle Key Constant to save/restore the value of the top visible Adapter Item position
    private static final String VISIBLE_ITEM_VIEW_POSITION_INT_KEY = "RecyclerView.TopItemPosition";
    //Bundle Key Constant to save/restore the value of the visibility of "Network Error Layout"
    private static final String NW_ERROR_VIEW_VISIBILITY_BOOL_KEY = "Visibility.NetworkErrorView";

    //For the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeContainer;

    //For the Headline TextView
    private TextView mHeadlineTextView;

    //For the content divider
    private View mHeadlineContentDivider;

    //For the RecyclerView
    private RecyclerView mRecyclerView;

    //For the Adapter of RecyclerView
    private HighlightsAdapter mRecyclerAdapter;

    //Boolean that stores whether the Fragment was launched for the first time
    //Defaulted to False (meaning, Not Initial)
    private boolean mInitialLaunch = false;

    //Saves whether the "Network Error Layout" should be visible/hidden
    private boolean mNetworkErrorViewVisible;

    //For the "Error Layout"
    private View mErrorView;

    //For the Settings SharedPreferences
    private SharedPreferences mPreferences;

    //Saves the top visible Adapter Item position
    private int mVisibleItemViewPosition;

    /**
     * Static constructor of the Fragment {@link HighlightsFragment}
     *
     * @return Instance of this Fragment {@link HighlightsFragment}
     */
    public static HighlightsFragment newInstance() {
        //Returning the instance of the Fragment
        return new HighlightsFragment();
    }

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Indicating that this fragment has menu options to show
        setHasOptionsMenu(true);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI ('R.layout.highlights_layout')
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.d(LOG_TAG, "onCreateView: Started");
        //Inflating the layout 'R.layout.highlights_layout'
        View rootView = inflater.inflate(R.layout.highlights_layout, container, false);

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        //Finding the headline text id
        mHeadlineTextView = rootView.findViewById(R.id.headline_text_id);
        //Setup the text on TextView
        setupHeadlineText();

        //Finding the content divider
        mHeadlineContentDivider = rootView.findViewById(R.id.content_div_id);

        //Finding the SwipeRefreshLayout
        mSwipeContainer = rootView.findViewById(R.id.swipe_container_id);
        //Initializing the SwipeRefreshLayout
        setupSwipeRefresh();

        //Finding the RecyclerView
        mRecyclerView = mSwipeContainer.findViewById(R.id.news_recycler_view_id);
        //Initializing the RecyclerView
        setupRecyclerView();

        //Finding the "Error View"
        mErrorView = rootView.findViewById(R.id.error_frame_id);

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Setting the launch flag to True as this is the initial launch
            mInitialLaunch = true;

            //Ensuring the "Error View" is hidden
            hideErrorView();

        } else {
            //On subsequent launch of this Fragment

            //Setting the launch flag to False
            mInitialLaunch = false;

            //Restoring the value of the position of the top Adapter item position previously visible
            mVisibleItemViewPosition = savedInstanceState.getInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY);

            //Restoring the visibility of "Network Error Layout"
            if (savedInstanceState.getBoolean(NW_ERROR_VIEW_VISIBILITY_BOOL_KEY)) {
                showNetworkErrorLayout();
            }

        }

        //Triggering the Data load
        triggerLoad(false);

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onResume of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        //Registering the Preference Change Listener
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        //Recalculating and applying the Date setting if required
        enforceDateSetting();

        if (!mInitialLaunch) {
            //On subsequent launch of this Fragment

            //Triggering a new data load only if the Start date of the News has changed
            checkAndReloadData();
        }

    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to Activity.onPause of the containing Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        Logger.d(LOG_TAG, "onPause: Started");

        //UnRegistering the Preference Change Listener
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);

        //Resetting the launch flag to false
        //as it will be a subsequent launch when the Fragment is resumed
        mInitialLaunch = false;
    }


    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process if
     * restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Saving the current position of the top Adapter item partially/completely visible
        outState.putInt(VISIBLE_ITEM_VIEW_POSITION_INT_KEY, RecyclerViewUtility.getFirstVisibleItemPosition(mRecyclerView));
        //Saving the visibility state of the "Network Error Layout"
        outState.putBoolean(NW_ERROR_VIEW_VISIBILITY_BOOL_KEY, mNetworkErrorViewVisible);

        super.onSaveInstanceState(outState);
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater The LayoutInflater object that can be used to inflate the Menu options
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflating the Menu options from highlights_menu.xml
        inflater.inflate(R.menu.highlights_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handling based on the Menu item selected
        switch (item.getItemId()) {
            case R.id.refresh_action_id:
                //For the refresh menu option
                triggerLoad(true);
                return true;
            case R.id.subscribe_action_id:
                //For the subscribe menu option

                //Navigating to the MoreNewsFragment
                if (getParentFragment() != null) {
                    ((HeadlinesFragment) getParentFragment()).openNewsCategoryTabByTitle(
                            getString(R.string.more_news_tab_title_text),
                            getString(R.string.more_news_tab_title_text)
                    );
                }
                return true;
            case R.id.settings_action_id:
                //For the settings menu option

                //Loading App's Settings
                IntentUtility.openAppSettings(getContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method that checks the state from the "Start Period Preset/Manual" CheckBoxPreference Setting
     * and triggers the re-calculation of Start Date for the News if in Preset mode (False condition)
     */
    private void enforceDateSetting() {
        Logger.d(LOG_TAG, "enforceDateSetting: Started");

        if (!PreferencesUtility.getStartPeriodOverrideValue(getContext(), mPreferences)) {
            //When in Preset mode (False condition)

            //Creating an instance of today's date for comparison and as a default value
            Calendar todayCalendar = Calendar.getInstance();

            //Retrieving the current day's date stored in the preference
            long currentDatePrefInMillis = PreferencesUtility.getCurrentDayDateValue(getContext(), mPreferences, 0);

            if (currentDatePrefInMillis > 0) {
                //When the value was previously stored in the 'date-today' setting

                if (!DateUtility.isSameDay(todayCalendar.getTimeInMillis(), currentDatePrefInMillis)) {
                    //When today's date is different, initiate the preset start date calculation

                    //Getting the current calendar instance
                    Calendar dateCalendar = Calendar.getInstance();

                    //Retrieving the "Preset Start Period" Preference value
                    String presetStartPeriodSelected = PreferencesUtility.getPresetStartPeriodValue(getContext(), mPreferences);

                    //Retrieving the list of values used in the "Preset Start Period" Preference
                    String[] availablePresets = PreferencesUtility.getPossiblePresetStartPeriodValues(requireContext());

                    if (presetStartPeriodSelected.equals(availablePresets[0])) {
                        //When the option selected was "Start of the Week"

                        //Setting the calendar to the locale's week's start day
                        dateCalendar.set(Calendar.DAY_OF_WEEK, dateCalendar.getFirstDayOfWeek());
                    } else if (presetStartPeriodSelected.equals(availablePresets[1])) {
                        //When the option selected was "Start of the Month"

                        //Setting the calendar to the start of the Month
                        dateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    }
                    //For the option "Start of Today", we are using the current day date AS-IS

                    //Retrieving the buffer value selected on the "Buffer to Start Period" Preference
                    int bufferDaysSelected = PreferencesUtility.getStartPeriodBufferValue(getContext(), mPreferences);

                    //Subtracting the calendar date by the Buffer value selected
                    dateCalendar.add(Calendar.DAY_OF_YEAR, -bufferDaysSelected);

                    //Updating the above value in the 'from-date' setting
                    PreferencesUtility.updateStartPeriodValue(getContext(), mPreferences, dateCalendar.getTimeInMillis());

                    Logger.d(LOG_TAG, "enforceDateSetting: Reapplied");

                    //Updating the 'date-today' setting to reflect the current day's date
                    PreferencesUtility.updateCurrentDayDateValue(getContext(), mPreferences, todayCalendar.getTimeInMillis());
                }

            } else {
                //When the value was NOT previously stored in the 'date-today' setting

                //Updating the 'date-today' setting to reflect the current day's date
                PreferencesUtility.updateCurrentDayDateValue(getContext(), mPreferences, todayCalendar.getTimeInMillis());
            }

        }

    }

    /**
     * Method that initializes the SwipeRefreshLayout
     * and its Listener
     */
    private void setupSwipeRefresh() {
        //Registering the refresh listener which triggers the new data loading
        mSwipeContainer.setOnRefreshListener(this);
        //Configuring the progress circle indicator colors
        mSwipeContainer.setColorSchemeResources(
                R.color.colorPink500,
                R.color.colorPurple500,
                R.color.colorIndigo500,
                R.color.colorGreen500
        );
    }

    /**
     * Method that initializes the Layout Manager, Adapter, Listener
     * and Decoration for the RecyclerView
     */
    private void setupRecyclerView() {
        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing an empty ArrayList of NewsSectionInfo Objects as the dataset for the Adapter
        ArrayList<NewsSectionInfo> newsSectionInfoList = new ArrayList<>();

        //Initializing the Adapter for the List view
        mRecyclerAdapter = new HighlightsAdapter(requireContext(), R.layout.highlights_item, getLoaderManager(), newsSectionInfoList);

        //Registering the OnAdapterItemDataSwapListener
        mRecyclerAdapter.setOnAdapterItemDataSwapListener(this);

        //Registering the OnAdapterItemClickListener
        mRecyclerAdapter.setOnAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    /**
     * Method that sets the Text on the TextView 'R.id.headline_text_id'
     * based on the start date selected in the preference setting
     */
    public void setupHeadlineText() {
        //Creating an instance of today's date for comparison and as a default value
        Calendar todayCalendar = Calendar.getInstance();
        //Removing the time part from the current date
        DateUtility.pruneTimePart(todayCalendar);

        //Creating the calendar for the start date
        Calendar fromDateCalendar = Calendar.getInstance();
        fromDateCalendar.setTimeInMillis(PreferencesUtility.getStartPeriodValue(getContext(), mPreferences, todayCalendar.getTimeInMillis()));
        //Removing the time part from the start date
        DateUtility.pruneTimePart(fromDateCalendar);

        //Calculating the difference between the two dates in days
        long diffInDays = DateUtility.getDifferenceInDays(todayCalendar, fromDateCalendar);

        //Setting the Text based on the difference in days between the start date and the current date
        if (diffInDays == 0) {
            //Difference is 0 for the current day being selected as the start date
            mHeadlineTextView.setText(getString(R.string.news_headline_text, "Today Morning"));
        } else if (diffInDays == 1) {
            //Difference is 1 for yesterday being selected as the start date
            mHeadlineTextView.setText(getString(R.string.news_headline_text, "Yesterday"));
        } else if (diffInDays > 1) {
            //Difference is more than 1 for older days being selected as the start date
            mHeadlineTextView.setText(getString(R.string.news_headline_text,
                    DateFormat.getDateInstance(DateFormat.LONG).format(fromDateCalendar.getTime())));
        }

        //Setting the Font
        mHeadlineTextView.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.gabriela), Typeface.BOLD);
    }

    /**
     * Method that triggers a data load
     *
     * @param forceLoad Boolean value that controls the nature of the trigger
     *                  <br/><b>TRUE</b> to start a new/existing load process
     *                  <br/><b>FALSE</b> to forcefully start a new load process
     */
    private void triggerLoad(boolean forceLoad) {
        if (getActivity() != null) {
            //Triggering only when attached to an Activity
            LoaderManager loaderManager = getLoaderManager();
            if (forceLoad) {
                //When forcefully triggered, restart the loader
                loaderManager.restartLoader(NewsHighlightsLoader.HIGHLIGHTS_LOADER, null, this);

            } else {
                //When triggered, start a new loader or load the existing loader
                loaderManager.initLoader(NewsHighlightsLoader.HIGHLIGHTS_LOADER, null, this);
            }
            //Displaying the data load progress indicator
            mSwipeContainer.setRefreshing(true);
        }
    }

    /**
     * Method that sets the Layout Manager's currently viewing position to the item position specified
     *
     * @param position        is the item position to which the Layout Manager needs to be set
     * @param scrollImmediate is a boolean which denotes the way in which the scroll to position
     *                        needs to be handled
     *                        <br/><b>TRUE</b> if the scroll to position needs to be set immediately
     *                        without any animation
     *                        <br/><b>FALSE</b> if the scroll to position needs to be done naturally
     *                        with the default animation
     */
    public void scrollToItemPosition(int position, boolean scrollImmediate) {
        //Retrieving the layout manager of RecyclerView
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (position > RecyclerView.NO_POSITION) {
            //Scrolling to the item position passed when valid

            //Validating the position passed is different from the top one to update if required
            if (RecyclerViewUtility.getFirstVisibleItemPosition(mRecyclerView) != position) {
                //Updating the item position reference
                mVisibleItemViewPosition = position;

                if (scrollImmediate) {
                    //Scrolling to the item position immediately
                    layoutManager.scrollToPositionWithOffset(mVisibleItemViewPosition, 0);
                } else {
                    //Scrolling to the item position naturally with smooth scroll
                    RecyclerViewUtility.smoothVScrollToPositionWithViewTop(mRecyclerView, mVisibleItemViewPosition);
                }
            }

        }
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<NewsSectionInfo>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case NewsHighlightsLoader.HIGHLIGHTS_LOADER:
                //Returning the instance of NewsHighlightsLoader
                return new NewsHighlightsLoader(requireActivity(),
                        getParentFragment() != null ? ((HeadlinesFragment) getParentFragment()).getSubscribedNewsSectionIdsList() : null
                );
            default:
                return null;
        }
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader           The Loader that has finished.
     * @param newsSectionInfos The List of {@link NewsSectionInfo} objects extracted by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsSectionInfo>> loader, List<NewsSectionInfo> newsSectionInfos) {
        switch (loader.getId()) {
            case NewsHighlightsLoader.HIGHLIGHTS_LOADER:
                if (getActivity() != null) {
                    //When attached to the Activity
                    if (newsSectionInfos != null && newsSectionInfos.size() > 0) {
                        //Loading the data to the adapter when present
                        mRecyclerAdapter.swapItemData(newsSectionInfos);
                        //Updating the date on the Headline TextView
                        setupHeadlineText();
                    } else {
                        //When the data returned is Empty or NULL

                        //Hiding the Progress Indicator on failure
                        mSwipeContainer.setRefreshing(false);

                        NewsHighlightsLoader highlightsLoader = (NewsHighlightsLoader) loader;

                        if (!highlightsLoader.getNetworkConnectivityStatus()) {
                            //Reporting Network Failure when False
                            Logger.d(LOG_TAG, "onLoadFinished: Network Failure");
                            //Displaying the "Network Error Layout"
                            showNetworkErrorLayout();
                        }

                    }
                }
                break;
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsSectionInfo>> loader) {
        //Creating an Empty list of NewsSectionInfo objects to clear the content in the Adapter
        ArrayList<NewsSectionInfo> newsSectionInfoList = new ArrayList<>();
        //Calling the Adapter's swap method to clear the data
        mRecyclerAdapter.swapItemData(newsSectionInfoList);
    }

    /**
     * Method that displays the "Network Error Layout".
     */
    private void showNetworkErrorLayout() {
        //Updating the visibility flag
        mNetworkErrorViewVisible = true;
        //When the layout needs to be shown
        mErrorView.setVisibility(View.VISIBLE);
        //Displaying the "Network Error Layout"
        replaceFragment(NetworkErrorFragment.newInstance(), NetworkErrorFragment.FRAGMENT_TAG);
        //Ensuring the Progress is always not shown in this case
        mSwipeContainer.setRefreshing(false);
        //Hiding other components
        enableDefaultComponents(false);
    }

    /**
     * Method that controls the visibility of the default view components in the layout.
     *
     * @param visibility <b>TRUE</b> to display the view components; <b>FALSE</b> to hide them.
     */
    private void enableDefaultComponents(boolean visibility) {
        if (visibility) {
            //Displaying the View components
            mHeadlineTextView.setVisibility(View.VISIBLE);
            mHeadlineContentDivider.setVisibility(View.VISIBLE);
            mSwipeContainer.setVisibility(View.VISIBLE);
        } else {
            //Hiding the View components
            mHeadlineTextView.setVisibility(View.GONE);
            mHeadlineContentDivider.setVisibility(View.GONE);
            mSwipeContainer.setVisibility(View.GONE);
            //Clearing the RecyclerView Pool if present : START
            RecyclerView.RecycledViewPool recycledViewPool = mRecyclerView.getRecycledViewPool();
            if (recycledViewPool != null) {
                recycledViewPool.clear();
            }
            //Clearing the RecyclerView Pool if present : END
        }
    }

    /**
     * Method that hides the "Error View"
     * and takes care of setting the dependent view visibility flags to false
     */
    private void hideErrorView() {
        //Hiding the "Error View"
        mErrorView.setVisibility(View.GONE);
        //Setting the "Network Error Layout" visibility flag to false
        mNetworkErrorViewVisible = false;
        //Displaying other components
        enableDefaultComponents(true);
    }

    /**
     * Method that replaces the Fragment at 'R.id.error_frame_id' with the Fragment and its Tag passed.
     * Prior to replacing, it checks whether the given Fragment is already present at 'R.id.error_frame_id'
     * or not.
     *
     * @param fragment is the instance of the Fragment that needs to be displayed at 'R.id.error_frame_id'
     * @param tag      is the String identifier used by the FragmentManager for checking the presence of the Fragment
     *                 prior to replacing the content with the Fragment
     */
    private void replaceFragment(Fragment fragment, String tag) {
        //Getting the Instance of the FragmentManager
        FragmentManager childFragmentManager = getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            //Getting the FragmentTransaction
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            //Replacing the Fragment at 'R.id.error_frame_id' with the given Fragment and its Tag
            fragmentTransaction.replace(R.id.error_frame_id, fragment, tag).commit();
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        //Forcefully triggering a new data load when pulled/swiped for refresh
        triggerLoad(true);
    }

    /**
     * Method invoked when the data on the RecyclerView's Adapter has been swapped successfully
     */
    @Override
    public void onItemDataSwapped() {
        Logger.d(LOG_TAG, "onItemDataSwapped: News Highlights data loaded successfully");
        //Ensuring the "Error View" is hidden
        hideErrorView();

        //Hiding the Progress Indicator after the data load completion
        mSwipeContainer.setRefreshing(false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //Scrolling over to first item after data load with a delay of 10ms
                scrollToItemPosition(mVisibleItemViewPosition, false);
            }
        }, 10); //This delay is for the animations to complete
    }

    /**
     * Method invoked when an Item in the Adapter is clicked
     *
     * @param newsSectionInfo is the corresponding {@link NewsSectionInfo} object of the item view
     *                        clicked in the Adapter
     */
    @Override
    public void onItemClick(NewsSectionInfo newsSectionInfo) {
        Logger.d(LOG_TAG, "onItemClick: Started");
        //Opening the News Category Tab for the News Section Title retrieved from the Item selected
        if (getParentFragment() != null) {
            ((HeadlinesFragment) getParentFragment()).openNewsCategoryTabByTitle(newsSectionInfo.getSectionName(), newsSectionInfo.getSectionId());
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or removed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesUtility.getStartPeriodKey(requireContext()))) {
            //When the Start Date of the News is changed
            Logger.d(LOG_TAG, "onSharedPreferenceChanged: Updating " + key);

            //Triggering a new data load only if the Start date value has changed
            //(This also prevents duplicate triggers)
            checkAndReloadData();
        }
        //NOTE: Other keys do not affect the data presented by this fragment
    }

    /**
     * Method that compares the start date used by an existing loader
     * with that of the value set in 'from-date' preference to trigger a new data load only if necessary
     */
    private void checkAndReloadData() {
        if (getActivity() != null) {
            //When attached to an Activity

            //Retrieving the current loader of the Fragment
            LoaderManager loaderManager = getLoaderManager();
            Loader<List<NewsSectionInfo>> loader = loaderManager.getLoader(NewsHighlightsLoader.HIGHLIGHTS_LOADER);
            if (loader != null) {
                //When loader was previously registered with the loader id used

                //Casting the loader to NewsHighlightsLoader
                NewsHighlightsLoader highlightsLoader = (NewsHighlightsLoader) loader;

                //Retrieving the start date value used by the loader
                long fromDateInMillis = highlightsLoader.getFromDateInMillis();
                //Retrieving the current start date value set in the preference
                long fromDatePrefInMillis = PreferencesUtility.getStartPeriodValue(getContext(), mPreferences, Calendar.getInstance().getTimeInMillis());

                //Retrieving the current list of Subscribed News Category Ids
                List<String> currSubsNewsSectionIds = getParentFragment() != null ? ((HeadlinesFragment) getParentFragment()).getSubscribedNewsSectionIdsList() : null;

                //Retrieving the current list of NewsSectionInfo objects from the Loader
                List<NewsSectionInfo> newsSectionInfoListFromLoader = highlightsLoader.getNewsSectionInfoList();

                if (fromDateInMillis > 0 && fromDatePrefInMillis != fromDateInMillis) {
                    //If the start date value of the News are different, then refresh the content
                    //Displaying the data load progress indicator
                    mSwipeContainer.setRefreshing(true);
                    //Dispatching the content changed event to the loader to reload the content
                    highlightsLoader.onContentChanged();

                } else if (newsSectionInfoListFromLoader != null && currSubsNewsSectionIds != null && newsSectionInfoListFromLoader.size() != currSubsNewsSectionIds.size()) {
                    //If the number of items previously loaded does not match with
                    //that of the current list of Subscribed News Categories, then reload the data
                    triggerLoad(true);
                }
            }
        }
    }

}
