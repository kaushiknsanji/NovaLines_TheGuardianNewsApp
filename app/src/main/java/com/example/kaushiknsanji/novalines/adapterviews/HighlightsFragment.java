package com.example.kaushiknsanji.novalines.adapterviews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.HighlightsAdapter;
import com.example.kaushiknsanji.novalines.drawerviews.HeadlinesFragment;
import com.example.kaushiknsanji.novalines.models.NewsSectionInfo;
import com.example.kaushiknsanji.novalines.workers.NewsHighlightsLoader;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
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

    //For the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeContainer;

    //For the Headline TextView
    private TextView mHeadlineTextView;

    //For the RecyclerView
    private RecyclerView mRecyclerView;

    //For the Adapter of RecyclerView
    private HighlightsAdapter mRecyclerAdapter;

    //Boolean that stores whether the Fragment was launched for the first time
    //Defaulted to False (meaning, Not Initial)
    private boolean mInitialLaunch = false;

    //For the Settings SharedPreferences
    private SharedPreferences mPreferences;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView: Started");
        //Inflating the layout 'R.layout.highlights_layout'
        View rootView = inflater.inflate(R.layout.highlights_layout, container, false);

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //Finding the headline text id
        mHeadlineTextView = rootView.findViewById(R.id.headline_text_id);
        //Setup the text on TextView
        setupHeadlineText();

        //Finding the SwipeRefreshLayout
        mSwipeContainer = rootView.findViewById(R.id.swipe_container_id);
        //Initializing the SwipeRefreshLayout
        setupSwipeRefresh();

        //Finding the RecyclerView
        mRecyclerView = mSwipeContainer.findViewById(R.id.news_recycler_view_id);
        //Initializing the RecyclerView
        setupRecyclerView();

        //Triggering the Data load
        triggerLoad(false);

        //Setting the launch flag based on whether there is saved state or not
        mInitialLaunch = (savedInstanceState == null);

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

        if (mInitialLaunch) {
            //On Initial launch of this Fragment

            //Recalculating and applying the Date setting if required
            enforceDateSetting();

        } else {
            //On subsequent launch of this Fragment

            //Triggering a new data load only if the Start date of the News has changed
            checkAndReloadData();
        }

        //Registering the Preference Change Listener
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to Activity.onPause of the containing Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause: Started");

        //UnRegistering the Preference Change Listener
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);

        //Resetting the launch flag to false
        //as it will be a subsequent launch when the Fragment is resumed
        mInitialLaunch = false;
    }

    /**
     * Method that checks the state from the "Start Period Preset/Manual" CheckBoxPreference Setting
     * and triggers the re-calculation of Start Date for the News if in Preset mode (False condition)
     */
    private void enforceDateSetting() {
        Log.d(LOG_TAG, "enforceDateSetting: Started");
        //Retrieving the state of the "Start Period Preset/Manual" CheckBoxPreference
        boolean state = mPreferences.getBoolean(getString(R.string.pref_start_period_manual_override_key), false);

        if (!state) {
            //When in Preset mode (False condition)

            //Creating an instance of today's date for comparison and as a default value
            Calendar todayCalendar = Calendar.getInstance();
            //Removing the time part from the current date
            pruneTimePart(todayCalendar);

            //Retrieving the Preference key that saves the current day's date
            String currentDayDateKeyStr = getString(R.string.pref_today_date_key);

            //Retrieving the current date stored in the preference
            long currentDatePrefInMillis = mPreferences.getLong(currentDayDateKeyStr, todayCalendar.getTimeInMillis());

            //Calculating the difference between the two dates in days: START
            long diffTimeInMillis = todayCalendar.getTimeInMillis() - currentDatePrefInMillis;
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffTimeInMillis);
            //Calculating the difference between the two dates in days: END

            if (diffInDays > 0) {
                //When today's date is different, trigger the preset start date calculation

                //Retrieving the Preference key that saves the start date for the News
                String fromDateKeyStr = getString(R.string.pref_start_period_manual_key);

                //Retrieving the start date value from the preference
                long fromDatePrefInMillis = mPreferences.getLong(fromDateKeyStr, todayCalendar.getTimeInMillis());

                //Opening the Editor to update the same value
                SharedPreferences.Editor prefEditor = mPreferences.edit();

                //Changing the start date value to trigger the change in 'from-date' setting
                prefEditor.putLong(fromDateKeyStr, fromDatePrefInMillis - 1);
                prefEditor.apply(); //applying the changes

                //Changing the start date value to its previous value,
                //to re-trigger the change in 'from-date' setting
                //forcing the calculation of Preset start date
                prefEditor.putLong(fromDateKeyStr, fromDatePrefInMillis);
                prefEditor.apply(); //applying the changes

                Log.d(LOG_TAG, "enforceDateSetting: Reapplied");

                //Updating the 'date-today' setting to reflect the current day's date
                prefEditor.putLong(currentDayDateKeyStr, todayCalendar.getTimeInMillis());
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
        mRecyclerAdapter = new HighlightsAdapter(getContext(), R.layout.highlights_item, newsSectionInfoList);

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
        pruneTimePart(todayCalendar);

        //Retrieving the start date value from the preference
        long fromDatePrefInMillis = mPreferences.getLong(getString(R.string.pref_start_period_manual_key), todayCalendar.getTimeInMillis());
        //Creating the calendar for the start date
        Calendar fromDateCalendar = Calendar.getInstance();
        fromDateCalendar.setTimeInMillis(fromDatePrefInMillis);
        //Removing the time part from the start date
        pruneTimePart(fromDateCalendar);

        //Calculating the difference between the two dates in days: START
        long diffTimeInMillis = todayCalendar.getTimeInMillis() - fromDateCalendar.getTimeInMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffTimeInMillis);
        //Calculating the difference between the two dates in days: END

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
    }

    /**
     * Method that removes/strips the time part from the calendar
     *
     * @param calendar is the Calendar instance on which the time part needs to be unset/cleared
     */
    private void pruneTimePart(Calendar calendar) {
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
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
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
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
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (position > RecyclerView.NO_POSITION) {
            //Scrolling to the item position passed when valid
            if (scrollImmediate) {
                //Scrolling to the item position immediately
                layoutManager.scrollToPosition(position);
            } else {
                //Scrolling to the item position naturally with default animation
                layoutManager.smoothScrollToPosition(mRecyclerView, null, position);
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
                return new NewsHighlightsLoader(getActivity());
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
    public void onLoadFinished(Loader<List<NewsSectionInfo>> loader, List<NewsSectionInfo> newsSectionInfos) {
        switch (loader.getId()) {
            case NewsHighlightsLoader.HIGHLIGHTS_LOADER:
                if (newsSectionInfos != null && newsSectionInfos.size() > 0) {
                    //Loading the data to the adapter when present
                    mRecyclerAdapter.swapItemData(newsSectionInfos);
                    //Updating the date on the Headline TextView
                    setupHeadlineText();
                } else {
                    //When the data returned is Empty or NULL
                    NewsHighlightsLoader highlightsLoader = (NewsHighlightsLoader) loader;

                    if (!highlightsLoader.getNetworkConnectivityStatus()) {
                        //Reporting Network Failure when False
                        Log.d(LOG_TAG, "onLoadFinished: Network Failure");
                    } else {
                        //When there is NO network issue and the current page has no data to be shown
                        Log.d(LOG_TAG, "onLoadFinished: NO DATA RETURNED");
                    }

                    //Hiding the Progress Indicator on failure
                    mSwipeContainer.setRefreshing(false);
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
    public void onLoaderReset(Loader<List<NewsSectionInfo>> loader) {
        //Creating an Empty list of NewsSectionInfo objects to clear the content in the Adapter
        ArrayList<NewsSectionInfo> newsSectionInfoList = new ArrayList<>();
        //Calling the Adapter's swap method to clear the data
        mRecyclerAdapter.swapItemData(newsSectionInfoList);
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
        Log.d(LOG_TAG, "onItemDataSwapped: News Highlights data loaded successfully");
        //Hiding the Progress Indicator after the data load completion
        mSwipeContainer.setRefreshing(false);
    }

    /**
     * Method invoked when an Item in the Adapter is clicked
     *
     * @param newsSectionInfo is the corresponding {@link NewsSectionInfo} object of the item view
     *                        clicked in the Adapter
     */
    @Override
    public void onItemClick(NewsSectionInfo newsSectionInfo) {
        Log.d(LOG_TAG, "onItemClick: Started");
        //Opening the News Category Tab for the News Section Title retrieved from the Item selected
        ((HeadlinesFragment) getParentFragment()).openNewsCategoryTabByTitle(newsSectionInfo.getSectionName());
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
        if (key.equals(getString(R.string.pref_start_period_manual_key))) {
            //When the Start Date of the News is changed
            Log.d(LOG_TAG, "onSharedPreferenceChanged: Updating " + key);

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
            Log.d(LOG_TAG, "checkAndReloadData: Started");
            //Retrieving the current loader of the Fragment
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            Loader<List<NewsSectionInfo>> loader = loaderManager.getLoader(NewsHighlightsLoader.HIGHLIGHTS_LOADER);
            if (loader != null) {
                //When loader was previously registered with the loader id used

                //Casting the loader to NewsHighlightsLoader
                NewsHighlightsLoader highlightsLoader = (NewsHighlightsLoader) loader;
                //Retrieving the start date value used by the loader
                long fromDateInMillis = highlightsLoader.getFromDateInMillis();
                //Retrieving the current start date value set in the preference
                long fromDatePrefInMillis = mPreferences.getLong(getString(R.string.pref_start_period_manual_key), Calendar.getInstance().getTimeInMillis());

                Log.d(LOG_TAG, "checkAndReloadData: fromDateInMillis " + fromDateInMillis);
                Log.d(LOG_TAG, "checkAndReloadData: fromDatePrefInMillis " + fromDatePrefInMillis);

                if (fromDateInMillis > 0 && fromDatePrefInMillis != fromDateInMillis) {
                    //When the start date value of the News are different, then refresh the content
                    Log.d(LOG_TAG, "checkAndReloadData: Reloading data");

                    //Dispatching the content changed event to the loader to reload the content
                    highlightsLoader.onContentChanged();
                    //Displaying the data load progress indicator
                    mSwipeContainer.setRefreshing(true);
                }
            }
        }
    }

}
