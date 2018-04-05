package com.example.kaushiknsanji.novalines.drawerviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.example.kaushiknsanji.novalines.interfaces.IPaginationView;
import com.example.kaushiknsanji.novalines.presenters.PaginationPresenter;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Drawer Fragment that inflates the Coordinator layout 'R.layout.headlines_layout'
 * containing a ViewPager of Fragments loaded by the adapter {@link HeadlinesPagerAdapter}
 * and managed through the ChildFragmentManager.
 * <p>
 * Responsible for displaying the Fragments for the subscribed News Categories/Sections
 * and additionally subscribed News Feeds.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class HeadlinesFragment extends Fragment
        implements TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, IPaginationView {

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
    //For the Pagination Presenter
    private PaginationPresenter mPaginationPresenter;
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

        //Registering the Pagination Presenter
        mPaginationPresenter = new PaginationPresenter();
        mPaginationPresenter.attachView(this);

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
        mPaginationPresenter.resetStartPageIndex();
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
        mPaginationPresenter.resetEndPageIndex(endIndexValue);
    }

    /**
     * Method that updates the state of the Pagination Buttons
     * based on the current setting
     */
    public void updatePaginationButtonsState() {
        mPaginationPresenter.updatePaginationButtonsState();
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
                    mPaginationPresenter.resetLastViewedPageIndex(articlesFragment.getLastViewedPageIndex());
                    resetEndPageIndex(articlesFragment.getLastPageIndex());
                    //Show/Hide Pagination panel based on the position of the currently visible item at the top
                    articlesFragment.checkAndEnablePaginationPanel();
                } else {
                    //Hiding the Pagination Panel for a fragment without Paginated view
                    showPaginationPanel(fragment, false);
                }
                //Triggering a new data load only if any parameters have changed
                articlesFragment.checkAndReloadData();
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
        //Delegating to the Pagination Presenter to handle
        mPaginationPresenter.onPaginationButtonClick(view);
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
        if (key.equals(PreferencesUtility.getStartPageIndexKey(getContext())) || key.equals(PreferencesUtility.getLastPageIndexKey(getContext()))) {
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
                mPaginationPresenter.showPaginationPanel();
            } else {
                //Hiding the Pagination Panel when False
                mPaginationPresenter.hidePaginationPanel();
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
        //(Will launch an Activity to allow users to add/remove news feed subscriptions, in future implementation)

        //Currently, displaying a toast indicating that the feature will be available in future implementation
        Toast.makeText(getContext(), R.string.more_news_subscribe_acton_toast, Toast.LENGTH_LONG).show();
    }

    /**
     * Getter method for the {@link ViewPager} shown by this fragment
     *
     * @return the {@link ViewPager} shown by this fragment
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        //Unregistering the Pagination Presenter
        mPaginationPresenter.detachView();
        super.onDestroy();
    }

    /**
     * This method returns the {@link Context}
     * of the Activity/Fragment implementing {@link IPaginationView}
     *
     * @return {@link Context} of the Activity/Fragment
     */
    @Override
    public Context getViewContext() {
        return getContext();
    }

    /**
     * Method that returns the {@link View}
     * of the Pagination Panel
     *
     * @return {@link View} of the Pagination Panel
     */
    @Override
    public View getPaginationPanel() {
        return mPaginationPanel;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the very First Page
     *
     * @return The Pagination {@link ImageButton} for the First Page
     */
    @Override
    public ImageButton getPageFirstButton() {
        return mPageFirstButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Last page
     *
     * @return The Pagination {@link ImageButton} for the Last Page
     */
    @Override
    public ImageButton getPageLastButton() {
        return mPageLastButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Next page
     *
     * @return The Pagination {@link ImageButton} for the Next Page
     */
    @Override
    public ImageButton getPageNextButton() {
        return mPageNextButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Previous Page
     *
     * @return The Pagination {@link ImageButton} for the Previous Page
     */
    @Override
    public ImageButton getPagePreviousButton() {
        return mPagePreviousButton;
    }

    /**
     * Method that returns the {@link ImageButton}
     * which is a Pagination Button that takes the user
     * to the Page selected by the user in the corresponding dialog
     *
     * @return The Pagination {@link ImageButton} for the User Selected Page
     */
    @Override
    public ImageButton getPageMoreButton() {
        return mPageMoreButton;
    }

}
