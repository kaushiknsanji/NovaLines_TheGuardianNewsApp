package com.example.kaushiknsanji.novalines.drawerviews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.adapters.HeadlinesPagerAdapter;
import com.example.kaushiknsanji.novalines.adapterviews.ArticlesFragment;
import com.example.kaushiknsanji.novalines.adapterviews.HighlightsFragment;
import com.example.kaushiknsanji.novalines.adapterviews.MoreNewsFragment;
import com.example.kaushiknsanji.novalines.dialogs.PaginationNumberPickerDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Drawer Fragment that inflates the Coordinator layout 'R.layout.headlines_layout'
 * containing a ViewPager of Fragments loaded by the adapter {@link HeadlinesPagerAdapter}
 * and managed through the ChildFragmentManager.
 *
 * Responsible for displaying the Fragments for the subscribed News Categories/Sections
 * and additionally subscribed News Feeds.
 *
 * @author Kaushik N Sanji
 */
public class HeadlinesFragment extends Fragment
        implements TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //Constant used for logs
    private static final String LOG_TAG = HeadlinesFragment.class.getSimpleName();
    //Public Constant for use as Fragment's Tag
    public static final String NAV_FRAGMENT_TAG = LOG_TAG;
    //Bundle Key constants used for saving/restoring the state
    private static final String ACTIVE_TAB_POSITION_INT_KEY = "TabLayout.ActiveTabIndex";
    //Bundle key constant to save/restore the list of Subscribed News Category Names
    private static final String NEWS_SECTION_NAMES_LIST_KEY = "SubscribedNewsSectionNames";
    //Bundle key constant to save/restore the list of Subscribed News Category Ids
    private static final String NEWS_SECTION_IDS_LIST_KEY = "SubscribedNewsSectionIds";
    //For the custom Toolbar used as ActionBar
    private Toolbar mToolbar;
    //For the ViewPager
    private ViewPager mViewPager;
    //For the ViewPager's Adapter
    private HeadlinesPagerAdapter mViewPagerAdapter;
    //For the Tabs attached to ViewPager
    private TabLayout mTabLayout;
    //For the Pagination of content
    private View mPaginationPanel;
    //For the Settings SharedPreferences
    private SharedPreferences mPreferences;
    //For the Pagination buttons displayed at the bottom (when visible)
    private ImageButton mPageFirstButton;
    private ImageButton mPageLastButton;
    private ImageButton mPageNextButton;
    private ImageButton mPagePreviousButton;
    private ImageButton mPageMoreButton;
    //For managing the subscribed list of News categories
    private ArrayList<String> mSubscribedNewsSectionNamesList;
    private ArrayList<String> mSubscribedNewsSectionIdsList;

    /**
     * Constructor of {@link HeadlinesFragment}
     *
     * @return Instance of this Fragment {@link HeadlinesFragment}
     */
    public static HeadlinesFragment newInstance() {
        return new HeadlinesFragment();
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
     * @return Return the View for the fragment's UI ('R.layout.headlines_layout')
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the layout 'R.layout.headlines_layout'
        View rootView = inflater.inflate(R.layout.headlines_layout, container, false);

        //Finding the Toolbar
        mToolbar = rootView.findViewById(R.id.toolbar_id);
        //Initializing the Toolbar as ActionBar
        setupToolBar();

        //Retrieving the instance of SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        //ViewPager for swiping through the Fragments
        mViewPager = rootView.findViewById(R.id.view_pager_id);

        //Adapter for ViewPager to display the correct fragment at the active position
        mViewPagerAdapter = new HeadlinesPagerAdapter(getChildFragmentManager());

        //Loading the list of Subscribed News Categories
        loadSubscribedNewsSections(savedInstanceState);

        //Loading the ViewPager's Adapter with the Fragments
        loadViewPagerFragments();

        //Binding the Adapter to ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        //Tabs to be shown for the Fragments displayed in the ViewPager
        mTabLayout = rootView.findViewById(R.id.sliding_tabs_id);

        //Binding the TabLayout to the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);

        //Finding the Pagination Panel
        mPaginationPanel = rootView.findViewById(R.id.pagination_panel_id);

        //Finding the Pagination Buttons
        mPageFirstButton = rootView.findViewById(R.id.page_first_button_id);
        mPageLastButton = rootView.findViewById(R.id.page_last_button_id);
        mPageNextButton = rootView.findViewById(R.id.page_next_button_id);
        mPagePreviousButton = rootView.findViewById(R.id.page_previous_button_id);
        mPageMoreButton = rootView.findViewById(R.id.page_more_button_id);

        //Registering click listener on the Pagination buttons
        mPageFirstButton.setOnClickListener(this);
        mPageLastButton.setOnClickListener(this);
        mPageNextButton.setOnClickListener(this);
        mPagePreviousButton.setOnClickListener(this);
        mPageMoreButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Making the first tab as selected
            mViewPager.setCurrentItem(0);

            //Resetting the 'page' (Page to Display) setting to 1 on initial launch
            resetStartPageIndex();

        } else {
            //On subsequent launch of this Fragment

            //Restoring the active fragment tab
            mViewPager.setCurrentItem(savedInstanceState.getInt(ACTIVE_TAB_POSITION_INT_KEY));
        }

        //Returning the prepared layout
        return rootView;
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process if
     * restarted.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Saving the current tab position
        outState.putInt(ACTIVE_TAB_POSITION_INT_KEY, mViewPager.getCurrentItem());
        //Saving the subscribed list of News Categories
        outState.putStringArrayList(NEWS_SECTION_NAMES_LIST_KEY, mSubscribedNewsSectionNamesList);
        outState.putStringArrayList(NEWS_SECTION_IDS_LIST_KEY, mSubscribedNewsSectionIdsList);

        super.onSaveInstanceState(outState);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onResume of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();

        //Registering the Listener on TabLayout
        mTabLayout.addOnTabSelectedListener(this);

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

        //UnRegistering the Listener on TabLayout
        mTabLayout.removeOnTabSelectedListener(this);

        //UnRegistering the Preference Change Listener
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);

    }

    /**
     * Method that resets the 'page' (Page to Display) setting to 1, when not 1
     */
    public void resetStartPageIndex() {
        //Retrieving the preference key string of 'page'
        String startIndexPrefKeyStr = getString(R.string.pref_page_index_key);
        //Retrieving the default value of 'page' setting
        int startIndexPrefKeyDefaultValue = getResources().getInteger(R.integer.pref_page_index_default_value);
        //Retrieving the current value of 'page' setting
        int startIndex = mPreferences.getInt(startIndexPrefKeyStr, startIndexPrefKeyDefaultValue);

        if (startIndex != 1) {
            //When the 'page' setting value is not equal to 1

            //Opening the Editor to update the value
            SharedPreferences.Editor prefEditor = mPreferences.edit();
            //Setting to its default value, which is 1
            prefEditor.putInt(startIndexPrefKeyStr, startIndexPrefKeyDefaultValue);
            prefEditor.apply(); //applying the changes
        }
    }

    /**
     * Method that resets the 'page' (Page to Display) setting of a particular fragment to 1, when not 1
     *
     * @param lastViewedPageIndex is the index of the last page viewed by the user
     *                            which is specific to the fragment and not stored as a SharedPreference
     */
    public void resetLastViewedPageIndex(int lastViewedPageIndex) {
        //Retrieving the preference key string of 'page'
        String startIndexPrefKeyStr = getString(R.string.pref_page_index_key);
        //Retrieving the default value of 'page' setting
        int startIndexPrefKeyDefaultValue = getResources().getInteger(R.integer.pref_page_index_default_value);
        //Retrieving the current value of 'page' setting
        int startIndex = mPreferences.getInt(startIndexPrefKeyStr, startIndexPrefKeyDefaultValue);

        if (lastViewedPageIndex > 1) {
            //When the index of the last page viewed is greater than 1

            //Opening the Editor to update the value
            SharedPreferences.Editor prefEditor = mPreferences.edit();
            //Setting the 'page' setting value to lastViewedPageIndex
            prefEditor.putInt(startIndexPrefKeyStr, lastViewedPageIndex);
            prefEditor.apply(); //applying the changes
            //Setting the 'page' setting value to its default value, which is 1
            //(This resets the start page to the first page for the current fragment)
            prefEditor.putInt(startIndexPrefKeyStr, startIndexPrefKeyDefaultValue);
            prefEditor.apply(); //applying the changes

        } else if (startIndex > 1) {
            //When the 'page' (Page to Display) setting is greater than 1 but not the last page viewed

            //Resetting the 'page' (Page to Display) setting value to 1
            resetStartPageIndex();
        }
    }

    /**
     * Method that resets/reapplies the value for the 'endIndex' preference setting
     *
     * @param endIndexValue Integer value of the last page index (specific to the fragment)
     *                      to be applied to the 'endIndex' preference setting.
     *                      When the value is <=0, the default value of 'page' preference setting
     *                      will be applied to 'endIndex' preference setting
     */
    public void resetEndPageIndex(int endIndexValue) {
        //Retrieving the preference key string of 'endIndex'
        String endIndexPrefKeyStr = getString(R.string.pref_page_max_value_key);
        //Retrieving the default value of 'page' setting
        int startIndexPrefKeyDefaultValue = getResources().getInteger(R.integer.pref_page_index_default_value);
        //Flag to check whether the state of Pagination Buttons needs an update
        boolean updateButtonsStateReqd = false;

        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = mPreferences.edit();
        if (endIndexValue <= 0) {
            //Defaulting the 'endIndex' setting to the default value of 'page' setting when
            //the value passed is 0 (or less)
            prefEditor.putInt(endIndexPrefKeyStr, startIndexPrefKeyDefaultValue);
        } else {
            //When the endIndex value specific to the fragment is more than 0

            //Retrieving the current value of endIndex from the preference
            int endIndexPrefValue = mPreferences.getInt(endIndexPrefKeyStr, startIndexPrefKeyDefaultValue);

            if (endIndexPrefValue == endIndexValue) {
                //When the endIndex values are same, there will be no change in Preference value
                //Hence manual update of Pagination Buttons state is required
                updateButtonsStateReqd = true;
            }

            //honouring the value passed when greater than 0
            prefEditor.putInt(endIndexPrefKeyStr, endIndexValue);
        }

        prefEditor.apply(); //applying the changes

        if (updateButtonsStateReqd) {
            //When set to true, updating the state of Pagination Buttons
            updatePaginationButtonsState();
        }
    }

    /**
     * Method that updates the state of the Pagination Buttons
     * based on the current setting
     */
    public void updatePaginationButtonsState() {

        //Retrieving the 'page' (Page to Display) setting value
        int startIndex = mPreferences.getInt(getString(R.string.pref_page_index_key),
                getResources().getInteger(R.integer.pref_page_index_default_value));

        //Retrieving the 'endIndex' preference value
        int endIndex = mPreferences.getInt(getString(R.string.pref_page_max_value_key),
                startIndex);

        Log.d(LOG_TAG, "updatePaginationButtonsState: startIndex " + startIndex);
        Log.d(LOG_TAG, "updatePaginationButtonsState: endIndex " + endIndex);

        if (startIndex == endIndex && startIndex != 1) {
            //When the last page is reached

            //Disabling the page-last and page-next buttons
            mPageLastButton.setEnabled(false);
            mPageNextButton.setEnabled(false);

            //Enabling the rest
            mPageFirstButton.setEnabled(true);
            mPagePreviousButton.setEnabled(true);
            mPageMoreButton.setEnabled(true);

            Log.d(LOG_TAG, "updatePaginationButtonsState: last buttons disabled");

        }
        if (startIndex == endIndex && startIndex == 1) {
            //When the first and last page is same, and only one page is existing

            //Disabling all the buttons
            mPageLastButton.setEnabled(false);
            mPageNextButton.setEnabled(false);
            mPageFirstButton.setEnabled(false);
            mPagePreviousButton.setEnabled(false);
            mPageMoreButton.setEnabled(false);

            Log.d(LOG_TAG, "updatePaginationButtonsState: all buttons disabled");

        } else if (startIndex != endIndex && startIndex == 1) {
            //When the first page is reached, and last page is not same as first page

            //Disabling the page-first and page-previous buttons
            mPageFirstButton.setEnabled(false);
            mPagePreviousButton.setEnabled(false);

            //Enabling the rest
            mPageMoreButton.setEnabled(true);
            mPageLastButton.setEnabled(true);
            mPageNextButton.setEnabled(true);

            Log.d(LOG_TAG, "updatePaginationButtonsState: first buttons disabled");

        } else if (startIndex != endIndex) {
            //Enabling all the buttons when first and last page are different
            mPageFirstButton.setEnabled(true);
            mPagePreviousButton.setEnabled(true);
            mPageMoreButton.setEnabled(true);
            mPageLastButton.setEnabled(true);
            mPageNextButton.setEnabled(true);

            Log.d(LOG_TAG, "updatePaginationButtonsState: all buttons enabled");

        }

    }

    /**
     * Method that loads the list of Subscribed News Categories.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state which will contain
     *                           the last Subscribed News Categories.
     */
    private void loadSubscribedNewsSections(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            //On initial launch of this Fragment

            //Initializing the lists to store the Subscribed News Categories
            mSubscribedNewsSectionIdsList = new ArrayList<>();
            mSubscribedNewsSectionNamesList = new ArrayList<>();

            //Retrieving the initial list of Subscribed News Categories
            mSubscribedNewsSectionIdsList.addAll(Arrays.asList(getResources().getStringArray(R.array.news_fixed_section_ids)));
            mSubscribedNewsSectionNamesList.addAll(Arrays.asList(getResources().getStringArray(R.array.news_fixed_section_names)));

        } else {
            //On subsequent launch of this Fragment

            //Retrieving the last saved list of Subscribed News Categories
            mSubscribedNewsSectionIdsList = savedInstanceState.getStringArrayList(NEWS_SECTION_IDS_LIST_KEY);
            mSubscribedNewsSectionNamesList = savedInstanceState.getStringArrayList(NEWS_SECTION_NAMES_LIST_KEY);
        }
    }

    /**
     * Method that returns the list of Subscribed News Category Ids
     *
     * @return Arraylist of Strings containing the IDs of the Subscribed News Categories
     */
    public ArrayList<String> getSubscribedNewsSectionIdsList() {
        return mSubscribedNewsSectionIdsList;
    }

    /**
     * Method that adds the Subscribed News Category details to the list maintained by the Fragment
     *
     * @param newsCategoryTitleStr The Title of the News Category that has been subscribed
     * @param newsCategoryIdStr    The ID of the News Category Tab that has been subscribed
     */
    private void addNewsSectionSubscribed(String newsCategoryTitleStr, String newsCategoryIdStr) {
        //Adding the Subscribed News Category details to the lists
        mSubscribedNewsSectionIdsList.add(newsCategoryIdStr);
        mSubscribedNewsSectionNamesList.add(newsCategoryTitleStr);
    }

    /**
     * Method that initializes the ViewPager's Adapter with the Fragments to be displayed
     */
    private void loadViewPagerFragments() {
        //Loading the "Highlights" as the first tab content
        mViewPagerAdapter.addFragment(
                HighlightsFragment.newInstance(),
                getString(R.string.highlights_title_str), 0);

        //Retrieving the count of Subscribed News Categories
        int noOfNewsTopics = mSubscribedNewsSectionIdsList.size();

        //Iterating and loading the list of Fragments for the News Categories Subscribed
        for (int index = 0; index < noOfNewsTopics; index++) {
            mViewPagerAdapter.addFragment(
                    ArticlesFragment.newInstance(mSubscribedNewsSectionIdsList.get(index), index + 1),
                    mSubscribedNewsSectionNamesList.get(index),
                    index + 1
            );
        }

        //Loading the "More News" Fragment as the last tab content
        appendMoreNewsFragment();
    }

    /**
     * Method that appends the instance of {@link MoreNewsFragment}
     * at the end of the list maintained by the {@link HeadlinesPagerAdapter}
     */
    private void appendMoreNewsFragment() {
        mViewPagerAdapter.addFragment(
                MoreNewsFragment.newInstance(),
                getString(R.string.more_news_tab_title_text),
                mViewPagerAdapter.getCount());
    }

    /**
     * Method that initializes the Toolbar as ActionBar
     * and sets the Title
     */
    private void setupToolBar() {
        //Setting the Toolbar as the ActionBar
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        //Retrieving the Action Bar
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            //Removing the default title text
            supportActionBar.setDisplayShowTitleEnabled(false);
            //Enabling home button to be used for Up navigation
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //Enabling home button
            supportActionBar.setHomeButtonEnabled(true);
        }

        //Finding the TextView to set the Title
        TextView titleTextView = mToolbar.findViewById(R.id.toolbar_title_text_id);
        titleTextView.setText(getString(R.string.headlines_title_str));
    }

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(LOG_TAG, "onTabSelected: Started");
        //Fix added to correct the Position pointed by the ViewPager: START
        //(Directly touching the tab instead of swiping can result in this)
        int newPosition = tab.getPosition();
        if (mViewPager.getCurrentItem() != newPosition) {
            //When position is incorrect, restore the position using the tab's position
            mViewPager.setCurrentItem(newPosition);
        }
        //Fix added to correct the Position pointed by the ViewPager: END

        //Retrieving the Current Fragment from ViewPager
        Fragment fragment = mViewPagerAdapter.getRegisteredFragment(newPosition);
        Log.d(LOG_TAG, "onTabSelected: Current Fragment is " + fragment);
        if (fragment != null) {
            //When child fragment is attached

            //Marking the current fragment as visible to the user
            fragment.setUserVisibleHint(true);

            if (fragment instanceof ArticlesFragment) {
                //For ArticlesFragment instances
                ArticlesFragment articlesFragment = (ArticlesFragment) fragment;
                Log.d(LOG_TAG, "onTabSelected: Current ArticlesFragment is for " + articlesFragment.getNewsTopicId());
                if (articlesFragment.isPaginatedView()) {
                    //Resetting the 'page' setting and 'endIndex' setting when the fragment is
                    //for News Topics with Paginated results
                    resetLastViewedPageIndex(articlesFragment.getLastViewedPageIndex());
                    resetEndPageIndex(articlesFragment.getLastPageIndex());
                    //Show/Hide Pagination panel based on the position of the currently visible item at the top
                    articlesFragment.checkAndEnablePaginationPanel();
                } else {
                    //Hiding the Pagination Panel for a fragment without Paginated view
                    showPaginationPanel(fragment, false);
                }
            } else {
                //For other Fragment instances

                //Hiding the Pagination Panel
                showPaginationPanel(fragment, false);

                if (fragment instanceof MoreNewsFragment) {
                    //For MoreNewsFragment instance

                    //Initializing the content of MoreNewsFragment
                    MoreNewsFragment moreNewsFragment = (MoreNewsFragment) fragment;
                    moreNewsFragment.showDefaultInfo();
                }
            }
        }
    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected
     */
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.d(LOG_TAG, "onTabUnselected: Started");
        //Retrieving the tab position
        int oldPosition = tab.getPosition();
        //Retrieving the Old Fragment from ViewPager
        Fragment fragment = mViewPagerAdapter.getRegisteredFragment(oldPosition);
        if (fragment != null) {
            //When child fragment is attached

            //Marking the previous fragment as NOT visible to the user
            fragment.setUserVisibleHint(false);

            if (fragment instanceof HighlightsFragment) {
                //For HighlightsFragment instances
                HighlightsFragment highlightsFragment = (HighlightsFragment) fragment;
                //Resetting to first item
                highlightsFragment.scrollToItemPosition(0, true);
            } else if (fragment instanceof ArticlesFragment) {
                //For ArticlesFragment instances
                ArticlesFragment articlesFragment = (ArticlesFragment) fragment;
                //Resetting to first item
                articlesFragment.scrollToItemPosition(0, true);
            }
        }
    }

    /**
     * Called when a tab that is already selected is chosen again by the user. Some applications
     * may use this action to return to the top level of a category.
     *
     * @param tab The tab that was reselected.
     */
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //Scroll to Top when the current tab is reselected

        //Retrieving the Current Fragment from ViewPager
        Fragment fragment = mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        if (fragment instanceof HighlightsFragment) {
            //For HighlightsFragment instances
            HighlightsFragment highlightsFragment = (HighlightsFragment) fragment;
            //Scrolling to first item
            highlightsFragment.scrollToItemPosition(0, false);
        } else if (fragment instanceof ArticlesFragment) {
            //For ArticlesFragment instances
            ArticlesFragment articlesFragment = (ArticlesFragment) fragment;
            //Scrolling to first item
            articlesFragment.scrollToItemPosition(0, false);
        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        //Retrieving the preference key string of 'Page to Display' setting, that is, the 'page'
        String startIndexPrefKeyStr = getString(R.string.pref_page_index_key);
        //Retrieving the 'page' (Page to Display) setting value
        int startIndex = mPreferences.getInt(startIndexPrefKeyStr,
                getResources().getInteger(R.integer.pref_page_index_default_value));
        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = mPreferences.edit();

        //Executing the click action based on the view's id
        switch (view.getId()) {
            case R.id.page_first_button_id:
                //On Page First action, updating the 'page' setting to 1
                prefEditor.putInt(startIndexPrefKeyStr, 1);
                prefEditor.apply(); //applying the changes
                //Displaying a Toast Message
                Toast.makeText(getContext(), getString(R.string.navigate_page_first_msg), Toast.LENGTH_SHORT).show();
                break;

            case R.id.page_previous_button_id:
                //On Page Previous action, updating the 'page' setting
                //to a value less than itself by 1
                startIndex = startIndex - 1;
                prefEditor.putInt(startIndexPrefKeyStr, startIndex);
                prefEditor.apply(); //applying the changes
                //Displaying a Toast Message
                Toast.makeText(getContext(), getString(R.string.navigate_page_x_msg, startIndex), Toast.LENGTH_SHORT).show();
                break;

            case R.id.page_more_button_id:
                //On Page More action, displaying a Number Picker Dialog
                //to allow the user to make the choice of viewing a random page

                //Retrieving the Minimum and Maximum values for the NumberPicker
                int minValue = getResources().getInteger(R.integer.pref_page_index_default_value);
                int maxValue = mPreferences.getInt(getString(R.string.pref_page_max_value_key),
                        minValue);

                //Retrieving the instance of the Dialog to be shown through the FragmentManager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                PaginationNumberPickerDialogFragment numberPickerDialogFragment
                        = (PaginationNumberPickerDialogFragment) fragmentManager.findFragmentByTag(PaginationNumberPickerDialogFragment.PAGN_DIALOG_FRAGMENT_TAG);

                if (numberPickerDialogFragment == null) {
                    //When there is no instance attached, that is the dialog is not active

                    //Creating the DialogFragment Instance
                    numberPickerDialogFragment = PaginationNumberPickerDialogFragment.newInstance(minValue, maxValue);

                    //Displaying the DialogFragment
                    numberPickerDialogFragment.show(fragmentManager,
                            PaginationNumberPickerDialogFragment.PAGN_DIALOG_FRAGMENT_TAG);
                }
                break;

            case R.id.page_next_button_id:
                //On Page Next action, updating the 'page' setting
                //to a value greater than itself by 1
                startIndex = startIndex + 1;
                prefEditor.putInt(startIndexPrefKeyStr, startIndex);
                prefEditor.apply(); //applying the changes
                //Displaying a Toast Message
                Toast.makeText(getContext(), getString(R.string.navigate_page_x_msg, startIndex), Toast.LENGTH_SHORT).show();
                break;

            case R.id.page_last_button_id:
                //On Page Last action, updating the 'page' setting to
                //a value equal to that of the predetermined 'endIndex' preference value
                prefEditor.putInt(startIndexPrefKeyStr,
                        mPreferences.getInt(getString(R.string.pref_page_max_value_key),
                                startIndex));
                prefEditor.apply(); //applying the changes
                //Displaying a Toast Message
                Toast.makeText(getContext(), getString(R.string.navigate_page_last_msg), Toast.LENGTH_SHORT).show();
                break;

        }

    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or removed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_page_index_key)) || key.equals(getString(R.string.pref_page_max_value_key))) {
            //When the 'page' (Page to Display) setting  or the 'endIndex' setting value is changed
            Log.d(LOG_TAG, "onSharedPreferenceChanged: Updating " + key);

            //Retrieving the current Tab's Fragment
            Fragment fragment = mViewPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
            if (fragment instanceof ArticlesFragment) {
                //Casting to ArticlesFragment
                ArticlesFragment articlesFragment = (ArticlesFragment) fragment;
                Log.d(LOG_TAG, "onSharedPreferenceChanged: Updating for " + articlesFragment.getNewsTopicId());
                Log.d(LOG_TAG, "onSharedPreferenceChanged: isVisible " + articlesFragment.getUserVisibleHint());
                if (articlesFragment.isPaginatedView() && articlesFragment.getUserVisibleHint()) {
                    //When the fragment is having paginated results
                    //and is the one currently being viewed by the user

                    //Updating the state of Pagination Buttons
                    updatePaginationButtonsState();
                }
            }

        }
    }

    /**
     * Method that displays/hides the Pagination Panel
     * based on the value of visibility passed.
     * This method is also invoked by {@link ArticlesFragment}.
     *
     * @param fragment The fragment that is requesting to hide/show the Pagination Panel
     * @param visibility <b>TRUE</b> to display the Pagination Panel; <b>FALSE</b> otherwise
     */
    public void showPaginationPanel(Fragment fragment, boolean visibility) {

        if (fragment.getUserVisibleHint()) {
            if (visibility) {
                //Displaying the Pagination Panel when True
                mPaginationPanel.setVisibility(View.VISIBLE);
            } else {
                //Hiding the Pagination Panel when False
                mPaginationPanel.setVisibility(View.GONE);
            }
        }

    }

    /**
     * Method invoked by the Child ViewPager Fragments of this Parent Fragment
     * when a particular News Category Tab is requested to be opened,
     * either through the More options of a News Article from {@link ArticlesFragment}
     * or the News Section Item clicked on the {@link HighlightsFragment}
     *
     * @param newsCategoryTitleStr The Title of the News Category Tab that is requested to be opened
     * @param newsCategoryIdStr The ID of the News Category Tab that is requested to be opened
     */
    public void openNewsCategoryTabByTitle(String newsCategoryTitleStr, String newsCategoryIdStr) {
        //Retrieving the position of the News Category Fragment in the ViewPager
        int fragmentPosition = mViewPagerAdapter.getItemPositionByTitle(newsCategoryTitleStr);
        if (fragmentPosition > 0) {
            //Setting the ViewPager to load the requested Fragment when present
            mViewPager.setCurrentItem(fragmentPosition, true);
        } else {
            //When the requested Fragment is not present

            //Loading the "More News" Fragment asking the user to subscribe the News Category requested
            //Finding the MoreNewsFragment Position
            int moreNewsFragmentPosition = mViewPagerAdapter.getItemPositionByTitle(getString(R.string.more_news_tab_title_text));
            //Getting the Fragment at the Position
            Fragment fragment = mViewPagerAdapter.getItem(moreNewsFragmentPosition);
            if (fragment instanceof MoreNewsFragment) {
                //Casting to MoreNewsFragment
                MoreNewsFragment moreNewsFragment = (MoreNewsFragment) fragment;
                //Scrolling over to the Fragment Tab
                mViewPager.setCurrentItem(moreNewsFragmentPosition, true);
                //Loading the Fragment with the info specific to the News Category requested
                moreNewsFragment.showSpecificInfo(newsCategoryTitleStr, newsCategoryIdStr);
            }
        }
    }

    /**
     * Method invoked by the Child ViewPager Fragment {@link MoreNewsFragment} of this Parent Fragment
     * when a specific News Category Feed is is requested to be subscribed and launched.
     *
     * @param newsCategoryTitleStr The Title of the News Category that is requested to be subscribed and launched
     * @param newsCategoryIdStr    The ID of the News Category Tab that is requested to be subscribed and launched
     */
    public void subscribeAndLaunchNewsCategory(String newsCategoryTitleStr, String newsCategoryIdStr) {
        Log.d(LOG_TAG, "subscribeAndLaunchNewsCategory: Started");
        //Retrieving the current tab position of MoreNewsFragment
        int currentMoreFragmentPosition = mViewPagerAdapter.getItemPositionByTitle(getString(R.string.more_news_tab_title_text));
        //Creating the instance of the News Fragment for the News Category subscribed
        ArticlesFragment subscribedFragment = ArticlesFragment.newInstance(newsCategoryIdStr, currentMoreFragmentPosition);
        //Adding to the list maintained by the HeadlinesPagerAdapter
        mViewPagerAdapter.addFragment(
                subscribedFragment,
                newsCategoryTitleStr,
                currentMoreFragmentPosition //inserting at the same position of the MoreNewsFragment
        );
        //Resetting 'page' setting value to 1
        resetStartPageIndex();
        //Triggering the adapter to reload the stuff which invokes the adapter's getItemPosition method
        mViewPagerAdapter.notifyDataSetChanged();
        //Launching the News Category Tab added
        mViewPager.setCurrentItem(currentMoreFragmentPosition, true);
        //Adding to the Subscribed News Categories lists
        addNewsSectionSubscribed(newsCategoryTitleStr, newsCategoryIdStr);
    }

    /**
     * Method invoked by the Child ViewPager Fragment {@link MoreNewsFragment} of this Parent Fragment
     * when the User wants to subscribe to the News Feeds of their choice
     */
    public void subscribeMoreNews() {
        Log.d(LOG_TAG, "subscribeMoreNews: Started");
    }

}
